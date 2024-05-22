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
import ru.ac.uniyar.artgallery.processing.AutoSolving;
import ru.ac.uniyar.artgallery.processing.CameraAdding;
import ru.ac.uniyar.artgallery.processing.PolygonGeneration;
import ru.ac.uniyar.artgallery.processing.Triangulation;

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
            //todo recreate addCam method and do not transfer canvas to CameraAdding
            CameraAdding.addCam(it.getOffsetX(), it.getOffsetY(), polygon, canvas);
        });
        //todo after adding camera check if polygon is fully seen by placed cameras

        //todo after polygon is fully covered with visibility fields sout autosolving and compare number of
        // suggested cams to number of placed ones and give some points


        createPolygon();
        addWalls(canvas.getContext());

        Button refresh = new Button("NEXT LEVEL");
        refresh.addClickListener(it -> init());

        //todo recreate invoke method and do not transfer canvas to AutoSolving
        Button autoSolve = new Button("SOLVE");
        autoSolve.addClickListener(it -> AutoSolving.invoke(canvas, polygon));
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

    public void createPolygon() {
        logger.info("creating polygon");

//        polygon = new Polygon();
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

        polygon = PolygonGeneration.invoke(20, canvasHeight, canvasWidth, canvas.getContext());
        polygon.clearCams();

        Triangulation.invoke(polygon);

        //раскомментить для отображения триангуляции
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
