package ru.ac.uniyar.artgallery.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
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
        Text top = new Text("Охрана картинной галереи");

        Text pointsText = new Text(String.format("У Вас %s очков!", (int) points));
        Text levelEnded = new Text("");
        Text levelNumber = new Text("Уровень №" + (level - 4));
        AtomicBoolean ended = new AtomicBoolean(false);
        Text levelStatistics = new Text("");
        Text autoStatistics = new Text("");
        AtomicBoolean autoSolved = new AtomicBoolean(false);

        Div startStatistics = new Div();
        Div topDiv = new Div(top);
        topDiv.getStyle().set("font-size", "30px");
        topDiv.getStyle().set("margin-bottom", "30px");
        Div levelNumberDiv = new Div(levelNumber);
        levelNumberDiv.getStyle().set("font-weight", "bold");
        startStatistics.add(topDiv, levelNumberDiv, new Div(pointsText));
        startStatistics.getStyle().set("text-align", "center");

        Div endStatistics = new Div();
        Div levelEndedDiv = new Div(levelEnded);
        levelEndedDiv.getStyle().set("font-weight", "bold");
        endStatistics.add(levelEndedDiv, new Div(levelStatistics), new Div(autoStatistics));
        endStatistics.getStyle().set("text-align", "center");
        endStatistics.getStyle().set("margin-top", "20px");

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

            if (polygon.isFullyCovered(camVisibilityFields) && !ended.get()) {
                ended.set(true);
                levelEnded.setText("Уровень пройден!");
                int auto = AutoSolving.invoke(polygon).size();
                levelStatistics.setText("Вы использовали следуюшее количество камер: " + camVisibilityFields.size() + ".");
                autoStatistics.setText("Автоматическое решение использует " + auto + ".");
                if (!autoSolved.get()) {
                    if (auto >= polygon.getCameras().size()) {
                        points += 100 + (auto - polygon.getCameras().size()) * 100 * (1 + ((double) (level - 5)) / 100);
                    } else {
                        points += 100;
                    }
                    pointsText.setText(String.format("У Вас %s очков!", (int) points));
                }
            }
        });

        canvas.getStyle().set("border-style", "solid");

        createPolygon();
        addWalls(canvas.getContext());

        Button next = new Button("Перейти к следующему уровню");
        next.addClickListener(it -> init());

        Button autoSolve = new Button("Решить уровень");
        autoSolve.addClickListener(it -> {
            List<Vertex> cameras = AutoSolving.invoke(polygon);
            drawCameras(canvas.getContext(), cameras);
            autoSolved.set(true);
        });
        autoSolve.getStyle().set("margin-right", "20px");

        Div buttons = new Div();
        buttons.add(autoSolve, next);
        buttons.getStyle().set("display", "flex");
        buttons.getStyle().set("justify-content", "center");
        buttons.getStyle().set("margin-top", "20px");

        Div canvasDiv = new Div(canvas);
        canvasDiv.getStyle().set("display", "flex");
        canvasDiv.getStyle().set("justify-content", "center");
        canvasDiv.getStyle().set("margin-top", "20px");

        Div formLayout = new Div();
        formLayout.add(startStatistics, canvasDiv, endStatistics, buttons);
        formLayout.getStyle().set("width", "100%");

        add(formLayout);
    }

    public void addWalls(CanvasRenderingContext2D context) {
        logger.info("adding walls");
        drawCamVisibilityField(context, polygon, "red");

//        раскомментить для отображения триангуляции
//        for (Triangle triangle : polygon.getTriangles()) {
//            for (Line line : triangle.getListOfLines()) {
//                context.setStrokeStyle("black");
//                context.moveTo(line.getStart().getX(), line.getStart().getY());
//                context.lineTo(line.getEnd().getX(), line.getEnd().getY());
//                context.stroke();
//            }
//        }
        //раскомментить для логирования сгенерированного полигона
//        logger.info("generated polygon: \n");
//        for (Line line : polygon.getLines()) {
//            logger.info("line from (" + line.getStart().getX() + "," + line.getStart().getY()
//                    + ") to (" + line.getEnd().getX() + "," + line.getEnd().getY() + ")");
//        }
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

        polygon = PolygonGeneration.invoke(level, canvasHeight, canvasWidth);
        polygon.clearCams();

        for (Vertex vertex : polygon.getVertexes()) {
            System.out.println("new Vertex(" + vertex.getX() + "," + vertex.getY() + "),");
        }

        Triangulation.invoke(polygon);
    }
}
