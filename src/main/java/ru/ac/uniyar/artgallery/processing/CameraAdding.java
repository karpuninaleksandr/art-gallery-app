package ru.ac.uniyar.artgallery.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ac.uniyar.artgallery.model.Line;
import ru.ac.uniyar.artgallery.model.Polygon;
import ru.ac.uniyar.artgallery.model.Vertex;

import java.util.*;

public class CameraAdding {

    private static final Logger logger = LoggerFactory.getLogger(CameraAdding.class);

    public static List<Vertex> addCam(Vertex camera, Polygon polygon) {
        if (polygon.checkIfPointIsInside(camera)) {
            polygon.addCamera(camera);
            return addCamVisibilityField(camera, polygon);
        } else return new ArrayList<>();
    }

    //todo still some problems with camVisibility constructing
    public static List<Vertex> addCamVisibilityField(Vertex camera, Polygon polygon) {
        ArrayList<Vertex> vertexesToDrawLinesTo = new ArrayList<>();
        for (Vertex vertex : polygon.getVertexes()) {
            logger.info("current vertex: (" + vertex.getX() + "," + vertex.getY() + ")");
            Line lineToDraw = new Line(vertex, camera);
            if (lineToDraw.canBeDrawn(polygon.getLines())) {
                logger.info("line can be drawn");
                if (!vertexesToDrawLinesTo.contains(vertex)) {
                    vertexesToDrawLinesTo.add(vertex);
                    Vertex vertexPlus = getCrossingVertexOfExtendedLine(lineToDraw.extendInOneWayPlus(), vertex, polygon);
                    if (vertexPlus != null && new Line(camera, vertexPlus).canBeDrawnExceptVertex(polygon, vertex)
                            && polygon.checkIfLineIsInsideExceptVertex(new Line(camera, vertexPlus), vertex)) {
                        if (!vertexesToDrawLinesTo.contains(vertexPlus)) {
                            logger.info("vertexPlus added");
                            vertexesToDrawLinesTo.add(vertexPlus);
                        }
                    }
                    Vertex vertexMinus = getCrossingVertexOfExtendedLine(lineToDraw.extendInOneWayMinus(), vertex, polygon);
                    if (vertexMinus != null && new Line(camera, vertexMinus).canBeDrawnExceptVertex(polygon, vertex)
                            && polygon.checkIfLineIsInsideExceptVertex(new Line(camera, vertexMinus), vertex)) {
                        if (!vertexesToDrawLinesTo.contains(vertexMinus)) {
                            logger.info("vertexMinus added");
                            vertexesToDrawLinesTo.add(vertexMinus);
                        }
                    }
                }
            }
        }

        return getOrderedVertexes(vertexesToDrawLinesTo, polygon.getLines());
    }

    public static Vertex getCrossingVertexOfExtendedLine(Line line, Vertex vertex, Polygon polygon) {
        for (Line linePolygon : polygon.getLines()) {
            if (linePolygon.crossesExceptVertex(line, vertex)) {
                return line.getLinesCrossVertex(linePolygon);
            }
        }
        return null;
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
