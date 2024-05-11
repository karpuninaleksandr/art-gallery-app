package ru.ac.uniyar.artgallery.processing;

import org.vaadin.pekkam.Canvas;
import org.vaadin.pekkam.CanvasRenderingContext2D;
import ru.ac.uniyar.artgallery.model.Camera;
import ru.ac.uniyar.artgallery.model.Line;
import ru.ac.uniyar.artgallery.model.Polygon;
import ru.ac.uniyar.artgallery.model.Vertex;

import java.util.ArrayList;

public class CameraAdding {

    public static void addCam(double x, double y, Polygon polygon, Canvas canvas) {
        Camera camera = new Camera(x, y);
        polygon.addCamera(camera);
        CanvasRenderingContext2D context = canvas.getContext();
        if (polygon.checkIfPointIsInside(new Vertex(x, y))) {
            addCamVisibilityField(camera, context, polygon);
            drawCameras(context, polygon);
        }
    }

    public static void addCamVisibilityField(Camera camera, CanvasRenderingContext2D context, Polygon polygon) {
        ArrayList<Vertex> vertexesToDrawLinesTo = new ArrayList<>();
        for (Vertex vertex : polygon.getVertexes()) {
            Line lineToDraw = new Line(vertex, camera);
            if (lineToDraw.canBeDrawn(polygon)) {
                if (!vertexesToDrawLinesTo.contains(vertex)) {
                    vertexesToDrawLinesTo.add(vertex);
                    Vertex vertexPlus = getCrossingVertexOfExtendedLine(lineToDraw.extendInOneWayPlus(), vertex, polygon);
                    if (vertexPlus != null && new Line(camera, vertexPlus).canBeDrawnExceptVertex(polygon, vertex)) {
                        if (!vertexesToDrawLinesTo.contains(vertexPlus)) {
                            vertexesToDrawLinesTo.add(vertexPlus);
                        }
                    }
                    Vertex vertexMinus = getCrossingVertexOfExtendedLine(lineToDraw.extendInOneWayMinus(), vertex, polygon);
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

        ArrayList<Vertex> resultVertexes = getOrderedVertexes(vertexesToDrawLinesTo, polygon);

        for (Vertex vertex : resultVertexes) {
            context.lineTo(vertex.getX(), vertex.getY());
        }
        context.lineTo(resultVertexes.get(0).getX(), resultVertexes.get(0).getY());

        context.closePath();
        context.fill();

        //раскомментировать для отрисовки вершин
//        for (Vertex vertex : resultVertexes) {
//            context.setFillStyle("red");
//            context.fillRect(vertex.getX(), vertex.getY(), 9, 9);
//        }
    }

    public static Vertex getCrossingVertexOfExtendedLine(Line line, Vertex vertex, Polygon polygon) {
        for (Line linePolygon : polygon.getLines()) {
            if (linePolygon.crossesExceptVertex(line, vertex)) {
                return line.getLinesCrossVertex(linePolygon);
            }
        }
        return null;
    }

    public static ArrayList<Vertex> getOrderedVertexes(ArrayList<Vertex> vertexesWithNoOrder, Polygon polygon) {
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

    public static void drawCameras(CanvasRenderingContext2D context, Polygon polygon) {
        for (Camera camera : polygon.getCameras()) {
            context.setFillStyle("red");
            context.fillRect(camera.getX(), camera.getY(), 4, 4);
        }
    }
}
