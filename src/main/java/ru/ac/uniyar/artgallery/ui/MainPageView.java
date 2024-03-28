package ru.ac.uniyar.artgallery.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vaadin.pekkam.Canvas;
import org.vaadin.pekkam.CanvasRenderingContext2D;
import ru.ac.uniyar.artgallery.model.*;
import ru.ac.uniyar.artgallery.processing.Triangulation;

import java.util.ArrayList;
import java.util.List;

@Service
@PreserveOnRefresh
@Route("")
public class MainPageView extends VerticalLayout {

    @Value("${canvas.height}")
    private int canvasHeight;

    @Value("${canvas.width}")
    private int canvasWidth;

    private final Logger logger = LoggerFactory.getLogger(MainPageView.class);
    private final Polygon polygon = new Polygon();

    private Canvas canvas;

    public MainPageView(@Value("${canvas.height}") int height, @Value("${canvas.width}") int width) {
        logger.info("main page initialization");

        createPolygonExample();
        this.canvasHeight = height;
        this.canvasWidth = width;
        init();
    }

    public void init() {
        removeAll();
        canvas = new Canvas(canvasWidth, canvasHeight);
        canvas.addMouseClickListener(it -> {
            logger.info("clicked on " + it.getOffsetX() + " " + it.getOffsetY());
            addCam(it.getOffsetX(), it.getOffsetY());
        });

        addWalls(canvas.getContext());

        Button refresh = new Button("REFRESH");
        refresh.addClickListener(it -> init());

        add(refresh, canvas);
    }

    public void addWalls(CanvasRenderingContext2D context) {
        logger.info("adding walls");
        for (Line line : polygon.getLines()) {
            context.setStrokeStyle("black");
            context.moveTo(line.getStart().getX(), line.getStart().getY());
            context.lineTo(line.getEnd().getX(), line.getEnd().getY());
            context.stroke();
        }
    }

    public void addCam(double x, double y) {
        Camera camera = new Camera(x, y);
        polygon.addCamera(camera);
        CanvasRenderingContext2D context = canvas.getContext();
        if (polygon.checkIfPointIsInside(new Vertex(x, y))) {
            addCamVisibilityField(camera, context);
            drawCameras(context);
        }
    }

    public void addCamVisibilityField(Camera camera, CanvasRenderingContext2D context) {
        ArrayList<Line> complexLines = new ArrayList<>();
        for (Line line : polygon.getLines()) {
            context.moveTo(camera.getX(), camera.getY());
            context.setFillStyle("green");
            context.beginPath();

            if (camTriangleCanBeDrawn(line, camera)) {
                createSimpleCamVisibilityToLine(line, camera, context);
            } else {
                complexLines.add(line);
            }
            context.closePath();
            context.fill();
        }
        for (Line line : complexLines) {
            context.moveTo(camera.getX(), camera.getY());
            context.setFillStyle("green");
            context.beginPath();

            createComplexCamVisibilityToLine(line, camera, context);

            context.closePath();
            context.fill();
        }
        polygon.setSuccessfulVertexForAll();
    }

    public boolean camTriangleCanBeDrawn(Line line, Camera camera) {
        Line lineToStart = new Line(camera, line.getStart());
        Line lineToEnd = new Line(camera, line.getEnd());
        return lineToStart.canBeDrawn(polygon) && lineToEnd.canBeDrawn(polygon);
    }

    public void createSimpleCamVisibilityToLine(Line line, Camera camera, CanvasRenderingContext2D context) {
        context.lineTo(line.getStart().getX(), line.getStart().getY());
        context.lineTo(line.getEnd().getX(), line.getEnd().getY());
        context.lineTo(camera.getX(), camera.getY());

        polygon.setSuccessfulVertex(line.getStart());
        polygon.setSuccessfulVertex(line.getEnd());
    }

    public void createComplexCamVisibilityToLine(Line line, Camera camera, CanvasRenderingContext2D context) {
        Line lineToDraw = new Line();

//        if (!(new Line(line.getStart(), camera).canBeDrawn(polygon)) && !(new Line(line.getStart(), camera).canBeDrawn(polygon))) {
////            lineToDraw = new Line(createLineToDrawLineTo(new Line(line.getStart(), line.getMiddleVertex()), camera).getStart(),
////                    createLineToDrawLineTo(new Line(line.getEnd(), line.getMiddleVertex()), camera).getStart());
//        } else {
            lineToDraw = createLineToDrawLineTo(line, camera);
//        }
        if (lineToDraw == null || lineToDraw.getStart() == null || lineToDraw.getEnd() == null) return;

        context.lineTo(lineToDraw.getStart().getX(), lineToDraw.getStart().getY());
        context.lineTo(lineToDraw.getEnd().getX(), lineToDraw.getEnd().getY());
        context.lineTo(camera.getX(), camera.getY());
    }

    public Line createLineToDrawLineTo(Line line, Camera camera) {
        Vertex problemVertex = new Line(line.getStart(), camera).canBeDrawn(polygon) ? line.getEnd() : line.getStart();
        Vertex closestSuccessfulVertex = problemVertex == line.getEnd() ? line.getStart() : getClosestSuccessfulVertex(problemVertex);

        Line lineThatCrosses;

        try {
            lineThatCrosses = polygon.getLines().get(new Line(camera, problemVertex).getLineThatCrosses(polygon));
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        Vertex vertexToFormLine = closestSuccessfulVertex.getDistanceToVertex(lineThatCrosses.getStart()) >
                closestSuccessfulVertex.getDistanceToVertex(lineThatCrosses.getEnd()) ? lineThatCrosses.getEnd() :
                lineThatCrosses.getStart();

        Line lineToDrawWith = new Line(camera, vertexToFormLine);

        Vertex newVertex = line.getLinesCrossVertex(lineToDrawWith.extend());

        return new Line(newVertex, closestSuccessfulVertex);
    }

    //todo rework
    public Vertex getClosestSuccessfulVertex(Vertex problemVertex) {
        int problemPosition = 0, minDiff = 1000;
        Vertex closestVertex = new Vertex();
        for (int i = 1; i < polygon.getVertexes().size(); ++i) {
            if (polygon.getVertexes().get(i) == problemVertex) {
                problemPosition = i;
                break;
            }
        }
        for (int i = 0; i < polygon.getVertexes().size(); ++i) {
            if (polygon.getVertexes().get(i).isSuccessful() && Math.abs(problemPosition - i) < minDiff) {
                minDiff = Math.abs(problemPosition - i);
                closestVertex = polygon.getVertexes().get(i);
            }
        }
        return closestVertex;
    }

    public void drawCameras(CanvasRenderingContext2D context) {
        for (Camera camera : polygon.getCameras()) {
            context.setFillStyle("red");
            context.fillRect(camera.getX(), camera.getY(), 4, 4);
        }
    }

    public void createPolygonExample() {
        logger.info("creating polygon");

        polygon.addVertexes(List.of(
                new Vertex(100, 500),
                new Vertex(100, 800),
                new Vertex(700, 800),
                new Vertex(700, 600),
                new Vertex(500, 600),
                new Vertex(500, 300),
                new Vertex(300, 300),
                new Vertex(300, 100),
                new Vertex(200, 100),
                new Vertex(200, 500)
        ));
        polygon.mapToOtherFormats();

        Triangulation.invoke(polygon);
    }
}
