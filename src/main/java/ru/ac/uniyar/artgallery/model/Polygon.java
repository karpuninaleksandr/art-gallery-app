package ru.ac.uniyar.artgallery.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor
@Getter
public class Polygon {
    private final double eps = 0.00000001;

    private final ArrayList<Vertex> vertexes = new ArrayList<>();

    private ArrayList<Camera> cameras = new ArrayList<>();

    private final ArrayList<Line> lines = new ArrayList<>();

    private final ArrayList<Triangle> triangles = new ArrayList<>();

    public void addVertexes(Collection<Vertex> vertexes) {
        this.vertexes.addAll(vertexes);
    }

    public void addTriangle(Triangle triangle) {
        this.triangles.add(triangle);
    }

    public void mapToOtherFormats() {
        for (int i = 0; i < vertexes.size() - 1;)
            lines.add(new Line(vertexes.get(i), vertexes.get(++i)));
        lines.add(new Line(vertexes.get(vertexes.size() - 1), vertexes.get(0)));
    }

    public void addCamera(Camera camera) {
        this.cameras.add(camera);
    }

    public boolean checkIfPointIsInside(Vertex checkVertex) {
        for (Triangle triangle : triangles) {
            double biqField = triangle.getField();
            Triangle smallTriangle1 = new Triangle(checkVertex, triangle.getVertex1(), triangle.getVertex2());
            Triangle smallTriangle2 = new Triangle(checkVertex, triangle.getVertex2(), triangle.getVertex3());
            Triangle smallTriangle3 = new Triangle(checkVertex, triangle.getVertex1(), triangle.getVertex3());

            if (Math.abs(biqField - (smallTriangle2.getField() + smallTriangle1.getField() + smallTriangle3.getField())) < eps)
                return true;
        }
        return false;
    }

    public void clearCams() {
        this.cameras = new ArrayList<>();
    }
}
