package ru.ac.uniyar.artgallery.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Getter
public class Polygon {
    private final ArrayList<Vertex> vertexes = new ArrayList<>();

    private List<Vertex> cameras = new ArrayList<>();

    private final ArrayList<Line> lines = new ArrayList<>();

    private final ArrayList<Triangle> triangles = new ArrayList<>();

    /* добавление вершин */
    public void addVertexes(Collection<Vertex> vertexes) {
        this.vertexes.addAll(vertexes);
        this.mapToOtherFormats();
    }

    /* маппинг от вершин к сторонам */
    public void mapToOtherFormats() {
        for (int i = 0; i < vertexes.size() - 1;)
            lines.add(new Line(vertexes.get(i), vertexes.get(++i)));
        lines.add(new Line(vertexes.get(vertexes.size() - 1), vertexes.get(0)));
    }

    /* добавление сторон */
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

    /* добавление треугольника */
    public void addTriangle(Triangle triangle) {
        this.triangles.add(triangle);
    }

    /* добавление камеры */
    public void addCamera(Vertex camera) {
        this.cameras.add(camera);
    }

    /* проверка принадлежности переданной вершины внутреннему пространству многоугольника */
    public boolean checkIfPointIsInside(Vertex checkVertex) {
        for (Triangle triangle : triangles)
            if (triangle.checkIfVertexIsInside(checkVertex))
                return true;
        return false;
    }

    /* проверка принадлежности переданной стороны внутреннему пространству многоугольника */
    public boolean checkIfLineIsInsideExceptVertex(Line line, Vertex except) {
        for (Vertex vertex : line.getAsListOfDots()) {
            if (vertex.isNotEqualTo(except) && !checkIfPointIsInside(vertex) && checkIfBordersDoNotContainVertex(vertex))
                return false;
        }
        return true;
    }

    /* проверка непринадлежности переданной вершины сторонам многоугольника */
    public boolean checkIfBordersDoNotContainVertex(Vertex vertex) {
        for (Line line : lines) {
            if (line.checkIfContainsVertex(vertex)) return false;
        }
        return true;
    }

    /* очистка списка камер */
    public void clearCams() {
        this.cameras = new ArrayList<>();
    }

    /* проверка полного покрытия многоугольника */
    public boolean isFullyCovered(List<Polygon> camVisibilityFields) {
        int notCovered = 0;
        List<Vertex> vertexesToCheck = new ArrayList<>();
        for (Triangle triangle : this.triangles) {
            vertexesToCheck.add(triangle.getVertex1());
            vertexesToCheck.add(triangle.getVertex2());
            vertexesToCheck.add(triangle.getVertex3());
            for (Line line : triangle.getListOfLines()) {
                vertexesToCheck.addAll(line.getAsListOfDots());
            }
        }
        for (Vertex currentCheck : vertexesToCheck) {
            boolean isInside = false;
            for (Polygon camField : camVisibilityFields) {
                if (camField.checkIfPointIsInside(currentCheck)) {
                    isInside = true;
                }
            }
            if (!isInside) {
                System.out.println("(" + currentCheck.getX() + "," + currentCheck.getY() +")");
                ++notCovered;
            }
        }
        System.out.println(notCovered + " " + vertexesToCheck.size());
        return notCovered < (double) vertexesToCheck.size() / 100;
    }
}
