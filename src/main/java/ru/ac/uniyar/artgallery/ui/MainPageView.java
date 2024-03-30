package ru.ac.uniyar.artgallery.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.CompletableToListenableFutureAdapter;
import org.vaadin.pekkam.Canvas;
import org.vaadin.pekkam.CanvasRenderingContext2D;
import ru.ac.uniyar.artgallery.model.*;
import ru.ac.uniyar.artgallery.processing.Triangulation;

import java.util.ArrayList;
import java.util.Collections;
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

        Button autoSolve = new Button("SOLVE");
        autoSolve.addClickListener(it -> autoSolve());

        add(refresh, autoSolve, canvas);
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
        ArrayList<Vertex> vertexesToDrawLinesTo = new ArrayList<>();
        for (Vertex vertex : polygon.getVertexes()) {
            Line lineToDraw = new Line(vertex, camera);
            if (lineToDraw.canBeDrawn(polygon)) {
                if (!vertexesToDrawLinesTo.contains(vertex)) {
                    vertexesToDrawLinesTo.add(vertex);
                    Vertex vertexPlus = getCrossingVertexOfExtendedLine(lineToDraw.extendInOneWayPlus(), vertex);
                    if (vertexPlus != null && new Line(camera, vertexPlus).canBeDrawnExceptVertex(polygon, vertex)) {
                        if (!vertexesToDrawLinesTo.contains(vertexPlus)) {
                            vertexesToDrawLinesTo.add(vertexPlus);
                        }
                    }
                    Vertex vertexMinus = getCrossingVertexOfExtendedLine(lineToDraw.extendInOneWayMinus(), vertex);
                    if (vertexMinus != null && new Line(camera, vertexMinus).canBeDrawnExceptVertex(polygon, vertex)) {
                        if (!vertexesToDrawLinesTo.contains(vertexMinus)) {
                            vertexesToDrawLinesTo.add(vertexMinus);
                        }
                    }
                }
            }
        }

        context.moveTo(camera.getX(), camera.getY());
        context.setFillStyle("green");
        context.beginPath();

        for (Vertex vertex : getOrderedVertexes(vertexesToDrawLinesTo)) {
            context.lineTo(vertex.getX(), vertex.getY());
        }

        context.closePath();
        context.fill();

//        for (Vertex vertex : vertexesToDrawLinesTo) {
//            context.setFillStyle("red");
//            context.fillRect(vertex.getX(), vertex.getY(), 4, 4);
//        }
//
//        for (Vertex vertex : getOrderedVertexes(vertexesToDrawLinesTo)) {
//            context.setFillStyle("yellow");
//            context.fillRect(vertex.getX(), vertex.getY(), 2, 2);
//        }
    }

    public Vertex getCrossingVertexOfExtendedLine(Line line, Vertex vertex) {
        for (Line linePolygon : polygon.getLines()) {
            if (linePolygon.crossesExceptVertex(line, vertex)) {
                return line.getLinesCrossVertex(linePolygon);
            }
        }
        return null;
    }

    public ArrayList<Vertex> getOrderedVertexes(ArrayList<Vertex> vertexesWithNoOrder) {
        ArrayList<Vertex> orderedVertexes = new ArrayList<>();
        for (Line line : polygon.getLines()) {
            ArrayList<Vertex> vertexesOnTheLine = new ArrayList<>();
            for (Vertex vertex : vertexesWithNoOrder) {
                if (orderedVertexes.contains(vertex)) {
                    continue;
                }
                if (line.checkIfContainsVertex(vertex)) {
                    vertexesOnTheLine.add(vertex);
                }
            }
            orderedVertexes.addAll(vertexesOnTheLine.stream().sorted((v1, v2) ->
                    (int)(v1.getDistanceToVertex(line.getStart()) - v2.getDistanceToVertex(line.getStart()))).toList());
        }
        return orderedVertexes;
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

    //todo add solving
    public void autoSolve() {
        CanvasRenderingContext2D context = canvas.getContext();
        context.setStrokeStyle("black");
        for (Triangle triangle : polygon.getTriangles()) {
            for (Line line : triangle.getListOfLines()) {
                context.setStrokeStyle("black");
                context.moveTo(line.getStart().getX(), line.getStart().getY());
                context.lineTo(line.getEnd().getX(), line.getEnd().getY());
                context.stroke();
            }
        }
    }
}
