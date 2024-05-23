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
            segments.addAll(Arrays.asList(new Line(a, b), new Line(b, c), new Line(c, a)));
        } else generateTriangle();
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
                    int segmentId = segments.indexOf(line);
                    segments.remove(line);
                    segments.add(segmentId, new Line(line.getStart(), randomVertex));
                    segments.add(segmentId + 1, new Line(randomVertex, line.getEnd()));
                    toBreak = true;
                    break;
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
