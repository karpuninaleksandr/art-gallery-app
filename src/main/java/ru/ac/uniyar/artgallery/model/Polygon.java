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

    private final ArrayList<Camera> cameras = new ArrayList<>();

    private final ArrayList<Line> lines = new ArrayList<>();

    private final ArrayList<Triangle> triangles = new ArrayList<>();

    public void addVertex(Vertex vertex) {
        vertexes.add(vertex);
    }

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

//        for (int i = 0; i < lines.size() - 3; ++i) {
//            triangles.add(new Triangle(lines.get(i).getStart(), lines.get(i + 1).getStart(),
//                    lines.get(i + 2).getStart()));
//        }
//        if (lines.size() != 3) {
//            triangles.add(new Triangle(lines.get(lines.size() - 3).getStart(), lines.get(lines.size() - 2).getStart(),
//                    lines.get(lines.size() - 1).getStart()));
//        }
//        triangles.add(new Triangle(lines.get(lines.size() - 2).getStart(), lines.get(lines.size() - 1).getStart(),
//                lines.get(0).getStart()));
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

    public void setSuccessfulVertex(Vertex vertex) {
        for (Vertex pVertex : vertexes) {
            if (pVertex.isEqualTo(vertex)) {
                pVertex.setSuccessful(true);
                break;
            }
        }
    }
}
