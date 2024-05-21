package ru.ac.uniyar.artgallery.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.pekkam.Canvas;
import org.vaadin.pekkam.CanvasRenderingContext2D;
import ru.ac.uniyar.artgallery.model.Camera;
import ru.ac.uniyar.artgallery.model.Line;
import ru.ac.uniyar.artgallery.model.Polygon;
import ru.ac.uniyar.artgallery.model.Vertex;

import java.util.*;

public class CameraAdding {

    private static final Logger logger = LoggerFactory.getLogger(CameraAdding.class);

    public static void addCam(double x, double y, Polygon polygon, Canvas canvas) {
        Camera camera = new Camera(x, y);
        polygon.addCamera(camera);
        CanvasRenderingContext2D context = canvas.getContext();
        if (polygon.checkIfPointIsInside(new Vertex(x, y))) {
            addCamVisibilityField(camera, context, polygon);
            drawCameras(context, polygon);
        }
    }

    //todo исправить момент с выходом прямой из многоугольника
    public static void addCamVisibilityField(Camera camera, CanvasRenderingContext2D context, Polygon polygon) {
        ArrayList<Vertex> vertexesToDrawLinesTo = new ArrayList<>();
        for (Vertex vertex : polygon.getVertexes()) {
            logger.info("current vertex: (" + vertex.getX() + "," + vertex.getY() + ")");
            Line lineToDraw = new Line(vertex, camera);
            if (lineToDraw.canBeDrawn(polygon.getLines())) {
                logger.info("line can be drawn");
                if (!vertexesToDrawLinesTo.contains(vertex)) {
                    vertexesToDrawLinesTo.add(vertex);
                    Vertex vertexPlus = getCrossingVertexOfExtendedLine(lineToDraw.extendInOneWayPlus(), vertex, polygon.getLines());
                    if (vertexPlus != null && new Line(camera, vertexPlus).canBeDrawnExceptVertex(polygon.getLines(), vertex)) {
                        if (!vertexesToDrawLinesTo.contains(vertexPlus)) {
                            logger.info("vertexPlus added");
                            vertexesToDrawLinesTo.add(vertexPlus);
                        }
                    }
                    Vertex vertexMinus = getCrossingVertexOfExtendedLine(lineToDraw.extendInOneWayMinus(), vertex, polygon.getLines());
                    if (vertexMinus != null && new Line(camera, vertexMinus).canBeDrawnExceptVertex(polygon.getLines(), vertex)) {
                        if (!vertexesToDrawLinesTo.contains(vertexMinus)) {
                            logger.info("vertexMinus added");
                            vertexesToDrawLinesTo.add(vertexMinus);
                        }
                    }
                }
            }
        }

        ArrayList<Vertex> resultVertexes = getOrderedVertexes(vertexesToDrawLinesTo, polygon.getLines());

        context.moveTo(resultVertexes.get(0).getX(), resultVertexes.get(0).getY());
        context.setFillStyle("green");
        context.beginPath();

        for (Vertex vertex : resultVertexes) {
            context.lineTo(vertex.getX(), vertex.getY());
        }
        context.lineTo(resultVertexes.get(0).getX(), resultVertexes.get(0).getY());

        context.closePath();
        context.fill();

        //раскомментировать для отрисовки вершин
//        for (Vertex vertex : resultVertexes) {
//            context.setFillStyle("red");
//            context.fillRect(vertex.getX(), vertex.getY(), 5, 5);
//        }
    }

    public static Vertex getCrossingVertexOfExtendedLine(Line line, Vertex vertex, List<Line> lines) {
        for (Line linePolygon : lines) {
            if (linePolygon.crossesExceptVertex(line, vertex)) {
                return line.getLinesCrossVertex(linePolygon);
            }
        }
        return null;
    }

    public static ArrayList<Vertex> getOrderedVertexes(ArrayList<Vertex> vertexesWithNoOrder, List<Line> lines) {
        ArrayList<Vertex> orderedVertexes = new ArrayList<>();
        for (Line line : lines) {
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


    public static void drawCameras(CanvasRenderingContext2D context, Polygon polygon) {
        for (Camera camera : polygon.getCameras()) {
            context.setFillStyle("red");
            context.fillRect(camera.getX(), camera.getY(), 4, 4);
        }
    }
}
