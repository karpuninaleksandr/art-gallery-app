package ru.ac.uniyar.artgallery.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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

@Component
@PreserveOnRefresh
@Route("")
@UIScope
@JsModule("./scripts/prevent-reload.js")
public class MainPageView extends VerticalLayout {

    @Value("${canvas.height}")
    private int canvasHeight;

    @Value("${canvas.width}")
    private int canvasWidth;

    private Polygon polygon;
    private List<Polygon> camVisibilityFields;

    private Canvas canvas;

    private int level;
    private double points;

    public MainPageView(@Value("${canvas.height}") int height, @Value("${canvas.width}") int width) {
        this.canvasHeight = height;
        this.canvasWidth = width;

        this.level = 4;
        this.points = 0;
        init();
    }

    /* инициализация уровня */
    public void init() {
        removeAll();

        ++level;
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
            System.out.println(it.getOffsetX() + " " + it.getOffsetY());
            boolean end = addCam(ended.get(), it.getOffsetX(), it.getOffsetY());
            if (end) {
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

        Button next = new Button("Перейти к следующему уровню");
        next.addClickListener(it -> init());

        Button autoSolve = new Button("Решить уровень");
        autoSolve.addClickListener(it -> autoSolve(autoSolved));
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

        createPolygon();
        drawPolygon(canvas.getContext());
    }

    public boolean addCam(boolean ended, double x, double y) {
        Polygon camVisibilityField = CameraAdding.getCamVisibilityField(new Vertex(x, y), polygon);
        if (camVisibilityField != null) {
            camVisibilityFields.add(camVisibilityField);
            Triangulation.invoke(camVisibilityField);
            drawCamVisibilityField(canvas.getContext(), camVisibilityField,
                    getCamVisibilityColor(polygon.getCameras().size() - 1));
            drawCameras(canvas.getContext(), polygon.getCameras(), "black");
        }

        return polygon.isFullyCovered() && !ended;
    }

    public void autoSolve(AtomicBoolean autoSolved) {
        List<Vertex> cameras = AutoSolving.invoke(polygon);
        drawCameras(canvas.getContext(), cameras, "#FFD500");
        autoSolved.set(true);
    }

    /* визуализация многоугольника */
    public void drawPolygon(CanvasRenderingContext2D context) {
        drawCamVisibilityField(context, polygon, "#FF5D40");
        drawWalls(context);
    }

    /* визуализация сторон многоугольника */
    public void drawWalls(CanvasRenderingContext2D context) {
        for (Line wall : polygon.getLines()) {
            context.setStrokeStyle("black");
            context.moveTo(wall.getStart().getX(), wall.getStart().getY());
            context.lineTo(wall.getEnd().getX(), wall.getEnd().getY());
            context.stroke();
        }
    }

    /* визуализация области видимости камеры */
    public void drawCamVisibilityField(CanvasRenderingContext2D context, Polygon field, String color) {
        context.setStrokeStyle(color);
        context.moveTo(field.getVertexes().get(0).getX(), field.getVertexes().get(0).getY());
        context.setFillStyle(color);
        context.beginPath();

        field.getVertexes().forEach(it -> context.lineTo(it.getX(), it.getY()));
        context.lineTo(field.getVertexes().get(0).getX(), field.getVertexes().get(0).getY());

        context.closePath();
        context.fill();
    }

    /* визуализация камер */
    public void drawCameras(CanvasRenderingContext2D context, List<Vertex> cameras, String color) {
        for (Vertex camera : cameras) {
            context.setFillStyle(color);
            context.fillRect(camera.getX() - 1, camera.getY() - 1, 4, 4);
            context.setStrokeStyle("black");
            context.moveTo(camera.getX() - 2, camera.getY() - 2);
            context.lineTo(camera.getX() + 3, camera.getY() - 2);
            context.lineTo(camera.getX() + 3, camera.getY() + 3);
            context.lineTo(camera.getX() - 2, camera.getY() + 3);
            context.lineTo(camera.getX() - 2, camera.getY() - 2);
            context.stroke();
        }
    }

    /* создание многоугольника */
    public void createPolygon() {
        camVisibilityFields = new ArrayList<>();

        polygon = PolygonGeneration.invoke(level, canvasHeight, canvasWidth);

        Triangulation.invoke(polygon);
    }

    /* выбор цвета области видимости камеры */
    public String getCamVisibilityColor(int camNumber) {
        return List.of("#62DA97", "#37DA7E", "#00B64F", "#22884F", "#007633",
                "#B5F36D", "#9FF33D", "#71AD2B", "#7CE700", "#519600").get(camNumber % 10);
    }
}
