package ru.ac.uniyar.artgallery.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.ac.uniyar.artgallery.Utils;

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
        this.vertexes = Utils.addToList(this.vertexes, vertexes.stream().toList());
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
    public void addLines(List<Line> lines) {
        this.lines = Utils.addToList(this.lines, lines.stream().toList());
        List<Vertex> check = Utils.map(lines, Line::getStart);
        check = Utils.addToList(check, Utils.map(lines, Line::getEnd));
        this.addVertexes(check.stream().distinct().toList());
    }

    /* добавление треугольника */
    public void addTriangle(Triangle triangle) {
        this.triangles = Utils.addToList(this.triangles, List.of(triangle));
    }

    /* добавление камеры */
    public void addCamera(Vertex camera) {
        this.cameras = Utils.addToList(this.cameras, List.of(camera));
    }

    /* проверка принадлежности переданной вершины внутреннему пространству многоугольника */
    public boolean checkIfPointIsInside(Vertex checkVertex) {
        return !Utils.filter(triangles, it -> it.checkIfVertexIsInside(checkVertex)).isEmpty();
    }

    /* проверка принадлежности переданной стороны внутреннему пространству многоугольника */
    public boolean checkIfLineIsInsideExceptVertex(Line line, Vertex except) {
        return Utils.filter(line.getAsListOfDots(), it -> it.isNotEqualTo(except) && !checkIfPointIsInside(it) &&
                checkIfBordersDoNotContainVertex(it)).isEmpty();
    }

    /* проверка непринадлежности переданной вершины сторонам многоугольника */
    public boolean checkIfBordersDoNotContainVertex(Vertex vertex) {
        return Utils.filter(lines, it -> it.checkIfContainsVertex(vertex)).isEmpty();
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
                    if (new Line(currentCheck, camera).canBeDrawnExceptVertex(this.getLines(), currentCheck)) {
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
}
