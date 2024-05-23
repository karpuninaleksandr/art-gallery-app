package ru.ac.uniyar.artgallery.ui;

import com.vaadin.flow.component.Text;
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
import ru.ac.uniyar.artgallery.processing.AutoSolving;
import ru.ac.uniyar.artgallery.processing.CameraAdding;
import ru.ac.uniyar.artgallery.processing.PolygonGeneration;
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
    private Polygon polygon;
    private List<Polygon> camVisibilityFields;

    private Canvas canvas;

    public MainPageView(@Value("${canvas.height}") int height, @Value("${canvas.width}") int width) {
        logger.info("main page initialization");

        this.canvasHeight = height;
        this.canvasWidth = width;

        init();
    }

    public void init() {
        removeAll();
        canvas = new Canvas(canvasWidth, canvasHeight);

        canvas.addMouseClickListener(it -> {
            logger.info("clicked on " + it.getOffsetX() + " " + it.getOffsetY());
            Polygon camVisibilityField = CameraAdding.getCamVisibilityField(new Vertex(it.getOffsetX(), it.getOffsetY()), polygon);
//            Polygon camVisibilityField = CameraAdding.getCamVisibilityField(new Vertex(246, 618), polygon);
            if (camVisibilityField != null) {
                camVisibilityFields.add(camVisibilityField);
                Triangulation.invoke(camVisibilityField);
                drawCamVisibilityField(canvas.getContext(), camVisibilityField);
                drawCameras(canvas.getContext(), polygon.getCameras());
            }

            if (polygon.isFullyCovered(camVisibilityFields)) {
                //todo add statistics, show autosolving, compare and give points
                Text endOfLevel = new Text("Level is finished! Let's move to the next one");
                add(endOfLevel);
            }
        });

        createPolygon();
        addWalls(canvas.getContext());

        Button refresh = new Button("NEXT LEVEL");
        refresh.addClickListener(it -> init());

        Button autoSolve = new Button("SOLVE");
        autoSolve.addClickListener(it -> {
            List<Vertex> cameras = AutoSolving.invoke(polygon);
            drawCameras(canvas.getContext(), cameras);
        });
        //todo if "SOLVE" button was pushed flag is needed to check that user did not solve task by himself,
        // of course no points are given in that situation


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

    public void drawCamVisibilityField(CanvasRenderingContext2D context, Polygon field) {
        context.moveTo(field.getVertexes().get(0).getX(), field.getVertexes().get(0).getY());
        context.setFillStyle("green");
        context.beginPath();

        for (Vertex vertex : field.getVertexes()) {
            context.lineTo(vertex.getX(), vertex.getY());
        }
        context.lineTo(field.getVertexes().get(0).getX(), field.getVertexes().get(0).getY());

        context.closePath();
        context.fill();

        //раскомментировать для отрисовки вершин
//        for (Vertex vertex : resultVertexes) {
//            context.setFillStyle("red");
//            context.fillRect(vertex.getX(), vertex.getY(), 5, 5);
//        }
    }

    public void drawCameras(CanvasRenderingContext2D context, List<Vertex> cameras) {
        for (Vertex camera : cameras) {
            context.setFillStyle("red");
            context.fillRect(camera.getX(), camera.getY(), 4, 4);
        }
    }

    public void createPolygon() {
        logger.info("creating polygon");

        camVisibilityFields = new ArrayList<>();

        polygon = new Polygon();
//        polygon.addVertexes(List.of(
//                new Vertex(614.7416773348622,104.29977497886385),
//                new Vertex(539.3231136228142,117.89462707832206),
//                new Vertex(653.6058117961721,214.88664206744875),
//                new Vertex(290.85976208875974,1.836442275572181),
//                new Vertex(129.21778467466072,107.35675948311123),
//                new Vertex(259.398207739972,39.829442034007215),
//                new Vertex(358.26168639047415,161.09589351819835),
//                new Vertex(598.3673934192761,473.9760002542412),
//                new Vertex(665.2526187166119,618.0796464752599),
//                new Vertex(594.1795224563176,559.0777820144898),
//                new Vertex(507.9657664111401,453.80370274367425),
//                new Vertex(39.32180677149199,266.81961717904886),
//                new Vertex(248.11090976691185,423.73909217273416),
//                new Vertex(318.5735667753201,548.2441570593526),
//                new Vertex(362.73818256507707,613.3990016851842),
//                new Vertex(190.20009384813662,656.0276688066103),
//                new Vertex(360.3655948294926,654.2240010821811),
//                new Vertex(826.504145166809,605.2642199093323),
//                new Vertex(958.6401257320492,448.87153029640217),
//                new Vertex(942.3369286195543,351.7165598057472)
//        ));

//        polygon.addVertexes(List.of(
//                new Vertex(100, 500),
//                new Vertex(100, 650),
//                new Vertex(700, 650),
//                new Vertex(700, 600),
//                new Vertex(500, 600),
//                new Vertex(500, 300),
//                new Vertex(300, 300),
//                new Vertex(300, 100),
//                new Vertex(200, 100),
//                new Vertex(200, 500)
//        ));
        polygon = PolygonGeneration.invoke(20, canvasHeight, canvasWidth);
        polygon.clearCams();

        Triangulation.invoke(polygon);

        //раскомментить для логирования сгенерированного полигона
//        logger.info("generated polygon: \n");
//        for (Line line : polygon.getLines()) {
//            logger.info("line from (" + line.getStart().getX() + "," + line.getStart().getY()
//                    + ") to (" + line.getEnd().getX() + "," + line.getEnd().getY() + ")");
//        }


        //раскомментить для отображения триангуляции
//        CanvasRenderingContext2D context = canvas.getContext();
//        for (Triangle triangle : polygon.getTriangles()) {
//            for (Line line : triangle.getListOfLines()) {
//                context.setStrokeStyle("black");
//                context.moveTo(line.getStart().getX(), line.getStart().getY());
//                context.lineTo(line.getEnd().getX(), line.getEnd().getY());
//                context.stroke();
//            }
//        }
    }
}
