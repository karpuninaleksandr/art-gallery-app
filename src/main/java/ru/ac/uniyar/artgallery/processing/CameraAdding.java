package ru.ac.uniyar.artgallery.processing;

import ru.ac.uniyar.artgallery.Utils;
import ru.ac.uniyar.artgallery.model.Line;
import ru.ac.uniyar.artgallery.model.Polygon;
import ru.ac.uniyar.artgallery.model.Vertex;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class CameraAdding {

    /* получение области видимости камеры */
    public static Polygon getCamVisibilityField(Vertex camera, Polygon polygon) {
        if (polygon.checkIfPointIsInside(camera)) {
            polygon.addCamera(camera);
            return createCamVisibilityField(camera, polygon);
        } else return null;
    }

    /* создание области видимости камеры */
    public static Polygon createCamVisibilityField(Vertex camera, Polygon polygon) {
        List<Vertex> vertexesToDrawLinesTo = new ArrayList<>();
        for (Vertex vertex : polygon.getVertexes()) {
            Line lineToDraw = new Line(vertex, camera);
            if (lineToDraw.canBeDrawn(polygon.getLines())) {
                if (!vertexesToDrawLinesTo.contains(vertex)) {
                    vertexesToDrawLinesTo = Utils.addToList(vertexesToDrawLinesTo, List.of(vertex));
                    addIfVertexIsPartOfVisibilityField(1000, camera, polygon, vertexesToDrawLinesTo, lineToDraw, vertex);
                    addIfVertexIsPartOfVisibilityField(0, camera, polygon, vertexesToDrawLinesTo, lineToDraw, vertex);
                }
            }
        }

        Polygon result = new Polygon();
        result.addVertexes(getOrderedVertexes(vertexesToDrawLinesTo, polygon.getLines()));
        return result;
    }

    public static void addIfVertexIsPartOfVisibilityField(int parameter, Vertex camera, Polygon polygon, List<Vertex> field, Line line, Vertex vertex) {
        Vertex extendedVertex = getCrossingVertexOfExtendedLine(line.extend(parameter), vertex, polygon);

        if (extendedVertex != null && new Line(camera, extendedVertex).canBeDrawnExceptVertex(polygon.getLines(), vertex)
                && polygon.checkIfLineIsInsideExceptVertex(new Line(camera, extendedVertex), vertex) && !field.contains(extendedVertex)) {
            field.add(extendedVertex);
        }
    }

    /* получение точки пересечения расширенной линии */
    public static Vertex getCrossingVertexOfExtendedLine(Line line, Vertex vertex, Polygon polygon) {
        Vertex crossVertex = new Vertex(100000, 100000);
        for (Line linePolygon : polygon.getLines()) {
            if (linePolygon.crossesExceptVertex(line, vertex)) {
                if (line.getLinesCrossVertex(linePolygon).getDistanceToVertex(vertex) <
                        crossVertex.getDistanceToVertex(vertex)) {
                    crossVertex = line.getLinesCrossVertex(linePolygon);
                }
            }
        }
        return crossVertex.isEqualTo(new Vertex(100000, 100000)) ? null : crossVertex;
    }

    /* упорядочивание вершин */
    public static List<Vertex> getOrderedVertexes(List<Vertex> vertexesWithNoOrder, List<Line> lines) {
        AtomicReference<List<Vertex>> orderedVertexes = new AtomicReference<>(new ArrayList<>());
        lines.forEach(it -> {
            ArrayList<Vertex> vertexesOnTheLine = new ArrayList<>();
            for (Vertex vertex : vertexesWithNoOrder) {
                if (orderedVertexes.get().contains(vertex)) {
                    continue;
                }
                if (it.checkIfContainsVertex(vertex)) {
                    vertexesOnTheLine.add(vertex);
                }
            }
            orderedVertexes.set(Utils.addToList(orderedVertexes.get(), vertexesOnTheLine.stream().sorted((v1, v2) ->
                    (int) (v1.getDistanceToVertex(it.getStart()) - v2.getDistanceToVertex(it.getStart()))).toList()));
        });
        return orderedVertexes.get();
    }
}
