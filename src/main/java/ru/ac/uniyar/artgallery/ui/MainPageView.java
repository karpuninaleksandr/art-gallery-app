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
            if (camVisibilityField != null) {
                logger.info("is not null");
                camVisibilityFields.add(camVisibilityField);
//                logger.info("started triangulation");
//                Triangulation.invoke(camVisibilityField);
//                logger.info("ended triangulation");
                drawCamVisibilityField(canvas.getContext(), camVisibilityField);
                drawCameras(canvas.getContext(), polygon.getCameras());
            }

//            if (polygon.isFullyCovered(camVisibilityFields)) {
//                //todo add statistics, show autosolving, compare and give points
//                Text endOfLevel = new Text("Level is finished! Let's move to the next one");
//                add(endOfLevel);
//            }
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

//        polygon = new Polygon();
//        polygon.addVertexes(List.of(
//                new Vertex(695.5581175986968,130.0403855826727),
//                new Vertex(790.5483075402233,381.9220578203906),
//                new Vertex(927.8144094413664,605.0986196880497),
//                new Vertex(737.1279304457148,220.97031305547657),
//                new Vertex(819.1460791721355,212.27462325308807),
//                new Vertex(993.2119411835444,277.2947523227727),
//                new Vertex(972.8666537831008,44.63558420247373),
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
