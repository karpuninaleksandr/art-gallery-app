package ru.ac.uniyar.artgallery.processing;

import ru.ac.uniyar.artgallery.CopyOnWriteUtils;
import ru.ac.uniyar.artgallery.model.Line;
import ru.ac.uniyar.artgallery.model.Polygon;
import ru.ac.uniyar.artgallery.model.Triangle;
import ru.ac.uniyar.artgallery.model.Vertex;

import java.util.ArrayList;
import java.util.List;

public class PolygonGeneration {

    private static int width;
    private static int height;

    /* базовый метод вызова */
    public static Polygon invoke(int numberOfVertexes, int height, int width) {
        PolygonGeneration.width = width;
        PolygonGeneration.height = height;

        List<Line> segments = generateTriangle();

        for (int i = 0; i < numberOfVertexes - 3; ++i)
            addPoint(segments);

        sortSegments(segments);

        return createPolygonFromSegments(segments);
    }

    /* генерация треугольника */
    private static ArrayList<Line> generateTriangle() {
        Vertex a = new Vertex(Math.random() * width, Math.random() * height);
        Vertex b = new Vertex(Math.random() * width, Math.random() * height);
        Vertex c = new Vertex(Math.random() * width, Math.random() * height);

        if ((new Triangle(a, b, c)).isValid()) {
            Line ab = new Line(a, b);
            Line bc = new Line(b, c);
            Line ca = new Line(c, a);
            if (checkCoefficient(ab,  bc, ca)) {
                return generateTriangle();
            } else {
                return new ArrayList<>(List.of(ab, bc, ca));
            }
        } else return generateTriangle();
    }

    /* проверка "красоты" треугольника */
    private static boolean checkCoefficient(Line a, Line b, Line c) {
        return createCoefficient(a, b, c) >= 1.05 && createCoefficient(b, c, a) >= 1.05 && createCoefficient(c, a, b) >= 1.05;
    }

    /* создание коэффицента "красоты" треугольника */
    private static double createCoefficient(Line a, Line b, Line c) {
        return (a.getLength() + b.getLength()) / c.getLength();
    }

    /* добавление вершины к многоугольнику */
    private static void addPoint(List<Line> segments) {
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

    /* сортировка сторон многоугольника */
    private static void sortSegments(List<Line> segments) {
        List<Line> sortedSegments = new ArrayList<>(List.of(segments.get(0)));
        for (Line line : segments) {
            if (line.getStart().isEqualTo(sortedSegments.get(sortedSegments.size() - 1).getEnd())) {
                sortedSegments = CopyOnWriteUtils.addToList(sortedSegments, List.of(line));
            }
        }
    }

    /* создание многоугольника из полученных сторон */
    private static Polygon createPolygonFromSegments(List<Line> segments) {
        Polygon polygon = new Polygon();
        polygon.addVertexes(segments.stream().map(Line::getStart).toList());

        return polygon;
    }
}
