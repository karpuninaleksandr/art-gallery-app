package ru.ac.uniyar.artgallery.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ac.uniyar.artgallery.model.Line;
import ru.ac.uniyar.artgallery.model.Polygon;
import ru.ac.uniyar.artgallery.model.Triangle;
import ru.ac.uniyar.artgallery.model.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PolygonGeneration {
    private static final List<Line> segments = new ArrayList<>();

    private static int width;
    private static int height;

    private static final Logger logger = LoggerFactory.getLogger(PolygonGeneration.class);

    public static Polygon invoke(int numberOfVertexes, int height, int width) {
        PolygonGeneration.width = width;
        PolygonGeneration.height = height;

        generateTriangle();

        for (int i = 0; i < numberOfVertexes - 3; ++i)
            addPoint();

        sortSegments();

        return createPolygonFromSegments();
    }

    private static void generateTriangle() {
        Vertex a = new Vertex(Math.random() * width, Math.random() * height);
        Vertex b = new Vertex(Math.random() * width, Math.random() * height);
        Vertex c = new Vertex(Math.random() * width, Math.random() * height);

        if ((new Triangle(a, b, c)).isValid()) {
            Line ab = new Line(a, b);
            Line bc = new Line(b, c);
            Line ca = new Line(c, a);
            if (checkCoefficient(ab,  bc, ca)) {
                generateTriangle();
            } else {
                segments.addAll(Arrays.asList(ab, bc, ca));
            }
        } else generateTriangle();
    }

    private static boolean checkCoefficient(Line a, Line b, Line c) {
        System.out.println(createCoefficient(a, b, c) + " " + createCoefficient(b, c, a) + " " + createCoefficient(c, a, b));
        return createCoefficient(a, b, c) >= 1.05 && createCoefficient(b, c, a) >= 1.05 && createCoefficient(c, a, b) >= 1.05;
    }

    private static double createCoefficient(Line a, Line b, Line c) {
        return (a.getLength() + b.getLength()) / c.getLength();
    }

    private static void addPoint() {
        Polygon polygon = new Polygon();
        polygon.addLines(segments);
        Vertex randomVertex;
        while (true) {
            randomVertex = new Vertex(Math.random() * width, Math.random() * height);
            Vertex finalRandomVertex = randomVertex;
            if (polygon.getVertexes().stream().anyMatch(it -> it.isEqualTo(finalRandomVertex)))
                continue;
            Vertex v1 = polygon.getVertexes().get(0);
            for (Vertex vertex : polygon.getVertexes()) {
                if (vertex.getDistanceToVertex(randomVertex) < v1.getDistanceToVertex(randomVertex)) {
                    v1 = vertex;
                }
            }
            Line l1 = new Line(v1, randomVertex);
            if (!l1.canBeDrawn(segments))
                continue;
            Vertex finalV = v1;
            List<Line> linesWithV1 = segments.stream().filter(it ->
                    it.getStart().isEqualTo(finalV) || it.getEnd().isEqualTo(finalV)).toList();
            boolean toBreak = false;
            for (Line line : linesWithV1) {
                if ((line.getEnd().isEqualTo(v1) && new Line(line.getStart(), randomVertex).canBeDrawn(segments)) ||
                        (line.getStart().isEqualTo(v1) && new Line(randomVertex, line.getEnd()).canBeDrawn(segments))) {
                    Line startRandom = new Line(line.getStart(), randomVertex);
                    Line randomEnd = new Line(randomVertex, line.getEnd());
                    if (checkCoefficient(startRandom, randomEnd, line)) {
                        int segmentId = segments.indexOf(line);
                        int prevId = segmentId == 0 ? segments.size() - 1 : segmentId - 1;
                        int nextId = segmentId == segments.size() - 1 ? 0 : segmentId + 1;
                        Line prevSegment = segments.get(prevId);
                        Line nextSegment = segments.get(nextId);
                        if (checkCoefficient(prevSegment, startRandom, new Line(prevSegment.getStart(), randomVertex)) &&
                                checkCoefficient(nextSegment, randomEnd, new Line(nextSegment.getEnd(), randomVertex))) {
                            segments.remove(line);
                            segments.add(segmentId, startRandom);
                            segments.add(segmentId + 1, randomEnd);
                            toBreak = true;
                            break;
                        }
                    }
                }
            }
            if (toBreak)
                break;
        }
    }

    private static void sortSegments() {
        List<Line> sortedSegments = new ArrayList<>();
        sortedSegments.add(segments.get(0));
        for (Line line : segments) {
            if (line.getStart().isEqualTo(sortedSegments.get(sortedSegments.size() - 1).getEnd())) {
                sortedSegments.add(line);
            }
        }
    }

    private static Polygon createPolygonFromSegments() {
        Polygon polygon = new Polygon();
        polygon.addVertexes(segments.stream().map(Line::getStart).toList());
        segments.clear();

        return polygon;
    }
}
