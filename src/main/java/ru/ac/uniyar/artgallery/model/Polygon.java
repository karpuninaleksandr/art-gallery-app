package ru.ac.uniyar.artgallery.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Getter
public class Polygon {
    private List<Vertex> vertexes = new ArrayList<>();

    private List<Vertex> cameras = new ArrayList<>();

    private List<Line> lines = new ArrayList<>();

    private List<Triangle> triangles = new ArrayList<>();

    /* добавление вершин */
    public void addVertexes(Collection<Vertex> vertexes) {
        this.vertexes = copyOnWrite(this.vertexes, vertexes.stream().toList());
        this.mapToOtherFormats();
    }

    /* маппинг от вершин к сторонам */
    public void mapToOtherFormats() {
        List<Line> newList = new ArrayList<>();
        for (int i = 0; i < vertexes.size() - 1;)
            newList.add(new Line(vertexes.get(i), vertexes.get(++i)));
        newList.add(new Line(vertexes.get(vertexes.size() - 1), vertexes.get(0)));
        this.lines = newList;
    }

    /* добавление сторон */
    public void addLines(Collection<Line> lines) {
        this.lines = copyOnWrite(this.lines, lines.stream().toList());
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
        this.triangles = copyOnWrite(this.triangles, List.of(triangle));
    }

    /* добавление камеры */
    public void addCamera(Vertex camera) {
        this.cameras = copyOnWrite(this.cameras, List.of(camera));
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

    /* проверка полного покрытия многоугольника */
    public boolean isFullyCovered() {
        for (Triangle triangle : this.triangles) {
            int notCovered = 0;
            List<Vertex> vertexesToCheck = new ArrayList<>();
            vertexesToCheck.add(triangle.getVertex1());
            vertexesToCheck.add(triangle.getVertex2());
            vertexesToCheck.add(triangle.getVertex3());
            for (Line line : triangle.getListOfLines())
                vertexesToCheck.addAll(line.getAsListOfDots());
            for (Vertex currentCheck : vertexesToCheck) {
                boolean isInside = false;
                for (Vertex camera : this.cameras) {
                    if (new Line(currentCheck, camera).canBeDrawnExceptVertex(this, currentCheck)) {
                        isInside = true;
                    }
                }
                if (!isInside) ++notCovered;
                if (notCovered > (double) vertexesToCheck.size() / 50) {
                    return false;
                }
            }
        }
        return true;
    }

    private <T> List<T> copyOnWrite(List<T> list, List<T> addedObjects) {
        List<T> newList = new ArrayList<>(list);
        newList.addAll(addedObjects);
        return newList;
    }
}
