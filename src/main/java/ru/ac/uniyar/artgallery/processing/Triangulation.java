package ru.ac.uniyar.artgallery.processing;

import ru.ac.uniyar.artgallery.model.*;

import java.util.ArrayList;
import java.util.List;

public class Triangulation {

    public static void invoke(Polygon polygon) {
        ArrayList<Vertex> vertexes = new ArrayList<>(polygon.getVertexes());
        boolean wayToGo = wayToGo(vertexes);
        int index = 0;

        while (vertexes.size() > 2) {
            Vertex vertex1 = vertexes.get(Math.abs(index % vertexes.size()));
            Vertex vertex2 = vertexes.get(Math.abs((index + 1) % vertexes.size()));
            Vertex vertex3 = vertexes.get(Math.abs((index + 2) % vertexes.size()));

            Vector vector1 = new Vector(vertex2.getX() - vertex1.getX(), vertex2.getY() - vertex1.getY());
            Vector vector2 = new Vector(vertex3.getX() - vertex1.getX(), vertex3.getY() - vertex1.getY());

            double cross = vector1.cross(vector2);

            Triangle triangle = new Triangle(vertex1, vertex2, vertex3);

            if ((!wayToGo && cross >= 0) || (wayToGo && cross <= 0)) {
                if (validTriangle(triangle, vertexes)) {
                    vertexes.remove(Math.abs((index + 1) % vertexes.size()));
                    polygon.addTriangle(triangle);
                    index = 0;
                } else ++index;
            } else ++index;
        }
    }

    private static boolean validTriangle(Triangle triangle, List<Vertex> vertexes) {
        for (Vertex vertex : vertexes) {
            if (vertex != triangle.getVertex1() && vertex != triangle.getVertex2() && vertex != triangle.getVertex3()
                    && triangle.checkIfVertexIsInside(vertex))
                return false;
        }
        return true;
    }

    public static boolean wayToGo(List<Vertex> vertexes) {
        double sum = 0;
        for (int i = 0; i < vertexes.size(); i++) {
            Vertex vertex1 = vertexes.get(i);
            Vertex vertex2 = vertexes.get((i + 1) % vertexes.size());
            sum += (vertex2.getX() - vertex1.getX()) * (vertex2.getY() + vertex1.getY());
        }
        return sum >= 0;
    }
}
