package ru.ac.uniyar.artgallery.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Getter
public class Polygon {
    private final double eps = 0.001;

    private final ArrayList<Vertex> vertexes = new ArrayList<>();

    private List<Vertex> cameras = new ArrayList<>();

    private final ArrayList<Line> lines = new ArrayList<>();

    private final ArrayList<Triangle> triangles = new ArrayList<>();

    public void addVertexes(Collection<Vertex> vertexes) {
        this.vertexes.addAll(vertexes);
        this.mapToOtherFormats();
    }

    public void mapToOtherFormats() {
        for (int i = 0; i < vertexes.size() - 1;)
            lines.add(new Line(vertexes.get(i), vertexes.get(++i)));
        lines.add(new Line(vertexes.get(vertexes.size() - 1), vertexes.get(0)));
    }

    public void addLines(Collection<Line> lines) {
        this.lines.addAll(lines);
        List<Vertex> vertexesToAdd = new ArrayList<>();
        for (Line line : lines) {
            if (!vertexesToAdd.contains(line.getStart())) {
                vertexesToAdd.add(line.getStart());
            }
            if (!vertexesToAdd.contains(line.getEnd())) {
                vertexesToAdd.add(line.getEnd());
            }
        }
        this.addVertexes(vertexesToAdd);
    }

    public void addTriangle(Triangle triangle) {
        this.triangles.add(triangle);
    }

    public void addCamera(Vertex camera) {
        this.cameras.add(camera);
    }

    public boolean checkIfPointIsInside(Vertex checkVertex) {
        for (Triangle triangle : triangles) {
            Triangle smallTriangle1 = new Triangle(checkVertex, triangle.getVertex1(), triangle.getVertex2());
            Triangle smallTriangle2 = new Triangle(checkVertex, triangle.getVertex2(), triangle.getVertex3());
            Triangle smallTriangle3 = new Triangle(checkVertex, triangle.getVertex1(), triangle.getVertex3());

            if (Math.abs(triangle.getField() - (smallTriangle2.getField() + smallTriangle1.getField() + smallTriangle3.getField())) < eps
                || Double.isNaN(Math.abs(triangle.getField() - (smallTriangle2.getField() + smallTriangle1.getField() + smallTriangle3.getField()))))
                return true;
        }
        return false;
    }

    public boolean checkIfLineIsInsideExceptVertex(Line line, Vertex except) {
        Vertex currentCheck = new Vertex(line.getStart().getX(), line.getStart().getY());
        double xDiff = line.getEnd().getX() - line.getStart().getX(),
                yDiff = line.getEnd().getY() - line.getStart().getY();
        while (line.checkIfContainsVertex(currentCheck) && (currentCheck).isNotEqualTo(line.getEnd())) {
            if (currentCheck.isNotEqualTo(except) && !checkIfPointIsInside(currentCheck) && checkIfBordersDoNotContainVertex(currentCheck)) {
                return false;
            }
            System.out.println("currentCheck is (" + currentCheck.getX() + "," + currentCheck.getY() + ")");
            currentCheck.setX(currentCheck.getX() + xDiff / 75);
            currentCheck.setY(currentCheck.getY() + yDiff / 75);
        }
        return true;
    }

    public boolean checkIfBordersDoNotContainVertex(Vertex vertex) {
        for (Line line : lines) {
            if (line.checkIfContainsVertex(vertex)) return false;
        }
        return true;
    }

    public void clearCams() {
        this.cameras = new ArrayList<>();
    }

    //todo some problems here ->
    public boolean isFullyCovered(List<Polygon> camVisibilityFields) {
        for (Triangle triangle : this.triangles) {
            boolean isInside = false;
            for (Polygon field : camVisibilityFields) {
                if (field.hasTriangleInside(triangle)) {
                    isInside = true;
                    break;
                }
            }
            if (!isInside) {
//                System.out.println("triangle that is not inside any of fields : " +
//                        "(" + triangle.getVertex1().getX() + "," + triangle.getVertex1().getY() + ")" +
//                        "(" + triangle.getVertex2().getX() + "," + triangle.getVertex2().getY() + ")" +
//                        "(" + triangle.getVertex3().getX() + "," + triangle.getVertex3().getY() + ")");
                return false;
            }
        }
        return true;
    }

    public boolean hasTriangleInside(Triangle triangle) {
        return this.checkIfPointIsInside(triangle.getVertex1()) && this.checkIfPointIsInside(triangle.getVertex2())
                && this.checkIfPointIsInside(triangle.getVertex3()) && this.checkIfLineIsInsideExceptVertex(triangle.getListOfLines().get(0), null)
                && this.checkIfLineIsInsideExceptVertex(triangle.getListOfLines().get(1), null)
                && this.checkIfLineIsInsideExceptVertex(triangle.getListOfLines().get(2), null);
    }
}
