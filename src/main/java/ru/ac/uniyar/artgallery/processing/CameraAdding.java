package ru.ac.uniyar.artgallery.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ac.uniyar.artgallery.model.Line;
import ru.ac.uniyar.artgallery.model.Polygon;
import ru.ac.uniyar.artgallery.model.Vertex;

import java.util.*;

public class CameraAdding {

    private static final Logger logger = LoggerFactory.getLogger(CameraAdding.class);

    public static Polygon getCamVisibilityField(Vertex camera, Polygon polygon) {
        if (polygon.checkIfPointIsInside(camera)) {
            polygon.addCamera(camera);
            return createCamVisibilityField(camera, polygon);
        } else return null;
    }

    public static Polygon createCamVisibilityField(Vertex camera, Polygon polygon) {
        ArrayList<Vertex> vertexesToDrawLinesTo = new ArrayList<>();
        for (Vertex vertex : polygon.getVertexes()) {
            Line lineToDraw = new Line(vertex, camera);
            if (lineToDraw.canBeDrawn(polygon.getLines())) {
                if (!vertexesToDrawLinesTo.contains(vertex)) {
                    vertexesToDrawLinesTo.add(vertex);
                    Vertex vertexPlus = getCrossingVertexOfExtendedLine(lineToDraw.extendInOneWayPlus(), vertex, polygon);
                    if (vertexPlus != null && new Line(camera, vertexPlus).canBeDrawnExceptVertex(polygon, vertex)
                            && polygon.checkIfLineIsInsideExceptVertex(new Line(camera, vertexPlus), vertex)) {
                        if (!vertexesToDrawLinesTo.contains(vertexPlus)) {
                            vertexesToDrawLinesTo.add(vertexPlus);
                        }
                    }
                    Vertex vertexMinus = getCrossingVertexOfExtendedLine(lineToDraw.extendInOneWayMinus(), vertex, polygon);
                    if (vertexMinus != null && new Line(camera, vertexMinus).canBeDrawnExceptVertex(polygon, vertex)
                            && polygon.checkIfLineIsInsideExceptVertex(new Line(camera, vertexMinus), vertex)) {
                        if (!vertexesToDrawLinesTo.contains(vertexMinus)) {
                            vertexesToDrawLinesTo.add(vertexMinus);
                        }
                    }
                }
            }
        }

        Polygon result = new Polygon();
        result.addVertexes(getOrderedVertexes(vertexesToDrawLinesTo, polygon.getLines()));
        return result;
    }

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

    public static List<Vertex> getOrderedVertexes(ArrayList<Vertex> vertexesWithNoOrder, List<Line> lines) {
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
}
