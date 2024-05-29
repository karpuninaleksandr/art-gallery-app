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
import java.util.concurrent.atomic.AtomicBoolean;

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

    private int level;
    private double points;

    public MainPageView(@Value("${canvas.height}") int height, @Value("${canvas.width}") int width) {
        logger.info("main page initialization");

        this.canvasHeight = height;
        this.canvasWidth = width;

        this.level = 4;
        this.points = 0;

        init();
    }

    public void init() {
        ++level;

        removeAll();
        canvas = new Canvas(canvasWidth, canvasHeight);

        Text pointsText = new Text(String.format("You have %s points!", points));
        AtomicBoolean autoSolved = new AtomicBoolean(false);

        canvas.addMouseClickListener(it -> {
            logger.info("clicked on " + it.getOffsetX() + " " + it.getOffsetY());
            Polygon camVisibilityField = CameraAdding.getCamVisibilityField(new Vertex(it.getOffsetX(), it.getOffsetY()), polygon);
            if (camVisibilityField != null) {
                logger.info("is not null");
                camVisibilityFields.add(camVisibilityField);
                logger.info("started triangulation");
                Triangulation.invoke(camVisibilityField);
                logger.info("ended triangulation");
                drawCamVisibilityField(canvas.getContext(), camVisibilityField, "green");
                drawCameras(canvas.getContext(), polygon.getCameras());
            }

            if (polygon.isFullyCovered(camVisibilityFields)) {
                Text endOfLevel = new Text("Level is finished! Let's move to the next one");
                if (!autoSolved.get()) {
                    int auto = AutoSolving.invoke(polygon).size();
                    if (auto >= polygon.getCameras().size()) {
                        points += (auto - polygon.getCameras().size() + 1) * 100 * (1 + ((double) (level - 5)) / 100);
                        pointsText.setText(String.format("You have %s points!", points));
                    }
                }
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
            autoSolved.set(true);
        });

        add(refresh, autoSolve, pointsText, canvas);
    }

    public void addWalls(CanvasRenderingContext2D context) {
        logger.info("adding walls");
        drawCamVisibilityField(context, polygon, "red");
    }

    public void drawCamVisibilityField(CanvasRenderingContext2D context, Polygon field, String color) {
        context.moveTo(field.getVertexes().get(0).getX(), field.getVertexes().get(0).getY());
        context.setFillStyle(color);
        context.beginPath();

        for (Vertex vertex : field.getVertexes()) {
            context.lineTo(vertex.getX(), vertex.getY());
        }
        context.lineTo(field.getVertexes().get(0).getX(), field.getVertexes().get(0).getY());

        context.closePath();
        context.fill();
    }

    public void drawCameras(CanvasRenderingContext2D context, List<Vertex> cameras) {
        for (Vertex camera : cameras) {
            context.setFillStyle("black");
            context.fillRect(camera.getX(), camera.getY(), 4, 4);
        }
    }

    public void createPolygon() {
        logger.info("creating polygon");

        camVisibilityFields = new ArrayList<>();

//        polygon = new Polygon();
//        polygon.addVertexes(List.of(
//                new Vertex(886.6090037617421,166.68579670516175),
//                new Vertex(891.0504515039829,641.3138984515487),
//                new Vertex(260.9452077759111,692.2727341295405),
//                new Vertex(535.0749634526961,655.19205169337),
//                new Vertex(367.8615169290765,365.09512961265125),
//                new Vertex(191.39542998619174,162.19611518864028),
//                new Vertex(531.1834292564066,25.82542784544637)
//                new Vertex(932.2465585713729,242.6982989178366),
//                new Vertex(626.9840423723022,83.84466514479922),
//                new Vertex(351.67026580693084,228.8566923805847),
//                new Vertex(552.1604920325107,202.61444716391918),
//                new Vertex(19.33015932779092,616.1884610322708),
//                new Vertex(361.87827024581475,436.54720066452046),
//                new Vertex(363.84640731855256,562.8331569692725),
//                new Vertex(262.5856324001448,623.6466830987562),
//                new Vertex(184.64201900710864,687.9319585990779),
//                new Vertex(594.732860251488,603.7142679249515),
//                new Vertex(545.8448834607615,548.8699215737292),
//                new Vertex(483.95349613553384,475.9176434808329),
//                new Vertex(556.5700875590073,418.7508164052168)
//        ));

        polygon = PolygonGeneration.invoke(level, canvasHeight, canvasWidth);
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
