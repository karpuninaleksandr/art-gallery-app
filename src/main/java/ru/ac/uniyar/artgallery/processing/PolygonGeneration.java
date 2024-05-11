package ru.ac.uniyar.artgallery.processing;

import ru.ac.uniyar.artgallery.model.Line;
import ru.ac.uniyar.artgallery.model.Polygon;
import ru.ac.uniyar.artgallery.model.Triangle;
import ru.ac.uniyar.artgallery.model.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//todo add clear function
public class PolygonGeneration {
    private static final List<Line> segments = new ArrayList<>();

    private static int width;
    private static int height;

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
            segments.addAll(Arrays.asList(new Line(a, b), new Line(a, c), new Line(b, c)));
        } else generateTriangle();
    }

    private static void addPoint() {
        int segmentToRemove = Math.round((float) Math.random() * segments.size());
        Vertex v1 = segments.get(segmentToRemove).getStart();
        Vertex v2 = segments.get(segmentToRemove).getEnd();
        segments.remove(segmentToRemove);

        //todo create visibility field for v1 and v2 vertexes, randomly get 1 point from it and add it to polygon
    }

    private static void sortSegments() {
        //todo sort segments somehow
    }

    private static Polygon createPolygonFromSegments() {
        List<Vertex> vertexesToFromPolygon = new ArrayList<>();
        for (Line line : segments)
            vertexesToFromPolygon.add(line.getStart());

        Polygon polygon = new Polygon();
        polygon.addVertexes(vertexesToFromPolygon);
        return polygon;
    }
}
