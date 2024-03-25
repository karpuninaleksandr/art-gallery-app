package ru.ac.uniyar.artgallery.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.vaadin.pekkam.Canvas;
import org.vaadin.pekkam.CanvasRenderingContext2D;
import ru.ac.uniyar.artgallery.model.Polygon;
import ru.ac.uniyar.artgallery.model.Vertex;
import ru.ac.uniyar.artgallery.model.Wall;

import java.util.List;

@Component
@Route("")
public class MainPageView extends VerticalLayout {

    @Value("${canvas.height}")
    private int canvasHeight = 500;

    @Value("${canvas.width}")
    private int canvasWidth = 500;

    private final Logger logger = LoggerFactory.getLogger(MainPageView.class);
    private final Polygon polygon = new Polygon();
    private final CanvasRenderingContext2D context;

    public MainPageView() {
        logger.info("main page initialization");

        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        canvas.addMouseClickListener(it -> {
            logger.info("clicked on " + it.getOffsetX() + " " + it.getOffsetY());

            
        });

        context = canvas.getContext();

        createPolygonExample();
        addWalls();

        add(canvas);
    }

    public void addWalls() {
        logger.info("adding walls");

        for (Wall wall: polygon.getListOfWalls()) {
            context.setStrokeStyle("black");
            context.moveTo(wall.getStart().getX(), wall.getStart().getY());
            context.lineTo(wall.getEnd().getX(), wall.getEnd().getY());
            context.stroke();
        }
    }

    public void createPolygonExample() {
        logger.info("creating polygon");

        polygon.addVertexes(List.of(
                new Vertex(10, 30),
                new Vertex(50, 50),
                new Vertex(100, 50),
                new Vertex(100, 30)
        ));
    }
}
