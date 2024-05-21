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
            CameraAdding.addCam(it.getOffsetX(), it.getOffsetY(), polygon, canvas);
        });

        createPolygon();
        addWalls(canvas.getContext());

        Button refresh = new Button("REFRESH");
        refresh.addClickListener(it -> init());

        Button autoSolve = new Button("SOLVE");
        autoSolve.addClickListener(it -> AutoSolving.invoke(canvas, polygon));

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

        polygon = PolygonGeneration.invoke(15, canvasHeight, canvasWidth, canvas.getContext());
        polygon.clearCams();

        Triangulation.invoke(polygon, canvas.getContext());
    }
}
