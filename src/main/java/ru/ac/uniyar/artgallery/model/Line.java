package ru.ac.uniyar.artgallery.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Line {
    private Vertex start;
    private Vertex end;

    private final double eps = 0.005;

    /* проверка пересечения линий */
    public boolean crosses(Line line) {
        Vertex crossVertex = getLinesCrossVertex(line);
        return crossVertex != null && start.isNotEqualTo(crossVertex) && end.isNotEqualTo(crossVertex);
    }

    /* проверка пересечения линий кроме переданной вершины */
    public boolean crossesExceptVertex(Line line, Vertex vertex) {
        Vertex crossVertex = getLinesCrossVertex(line);
        return crossVertex != null && start.isNotEqualTo(crossVertex) && end.isNotEqualTo(crossVertex) && vertex.isNotEqualTo(crossVertex);
    }

    /* получение точки пересечения линий */
    public Vertex getLinesCrossVertex(Line line) {
        double k1, b1, k2, b2, xCross, yCross;

        if (start.getX() == end.getX()) {
            if (line.getStart().getX() == line.getEnd().getX()) {
                if (start.getX() != line.getStart().getX())
                    return null;
                if (checkIfVertexIsOnTheLine(line.getStart(), line.getEnd(), start) ||
                        checkIfVertexIsOnTheLine(line.getStart(), line.getEnd(), end)) {
                    return end;
                } else {
                    return null;
                }
            }
            k2 = (line.getEnd().getY() - line.getStart().getY()) / (line.getEnd().getX() - line.getStart().getX());
            b2 = line.getStart().getY() - k2 * line.getStart().getX();
            xCross = start.getX();
            yCross = k2 * start.getX() + b2;
            Vertex checkVertex = new Vertex(xCross, yCross);
            if (checkIfVertexIsOnTheLine(start, end, checkVertex) &&
                    checkIfVertexIsOnTheLine(line.getStart(), line.getEnd(), checkVertex)) {
                return new Vertex(xCross, yCross);
            }
            return null;
        }

        if (line.getStart().getX() == line.getEnd().getX()) {
            k1 = (end.getY() - start.getY()) / (end.getX() - start.getX());
            b1 = start.getY() - k1 * start.getX();
            xCross = line.getStart().getX();
            yCross = k1 * line.getStart().getX() + b1;
            Vertex checkVertex = new Vertex(xCross, yCross);
            if (checkIfVertexIsOnTheLine(start, end, checkVertex) && checkIfVertexIsOnTheLine(line.getStart(), line.getEnd(), checkVertex)) {
                return new Vertex(xCross, yCross);
            }
            return null;
        }

        k1 = (end.getY() - start.getY()) / (end.getX() - start.getX());
        k2 = (line.getEnd().getY() - line.getStart().getY()) / (line.getEnd().getX() - line.getStart().getX());

        b1 = start.getY() - k1 * start.getX();
        b2 = line.getStart().getY() - k2 * line.getStart().getX();

        if (k1 == k2) {
            if (b1 == b2) {
                if (checkIfVertexIsOnTheLine(line.getStart(), line.getEnd(), start) ||
                        checkIfVertexIsOnTheLine(line.getStart(), line.getEnd(), end)) {
                    return end;
                }
                return null;
            }
            return null;
        }

        xCross = (b2 - b1) / (k1 - k2);
        yCross = k1 * xCross + b1;
        Vertex checkVertex = new Vertex(xCross, yCross);

        if (checkIfVertexIsOnTheLine(start, end, checkVertex) && checkIfVertexIsOnTheLine(line.getStart(), line.getEnd(), checkVertex))
            return new Vertex(xCross, yCross);
        return null;
    }

    /* получение длины линии */
    public double getLength() {
        return Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow((start.getY() - end.getY()), 2));
    }

    /* проверка отсутствия пересечения линий */
    public boolean canBeDrawn(List<Line> lines) {
        for (Line line : lines) {
            if (this.crosses(line))
                return false;
        }
        return true;
    }

    /* проверка отсутствия пересечения линий кроме переданной вершины */
    public boolean canBeDrawnExceptVertex(Polygon polygon, Vertex vertex) {
        for (Line line : polygon.getLines()) {
            if (this.crossesExceptVertex(line, vertex))
                return false;
        }
        return true;
    }

    /* расширение линии */
    public Line extendInOneWayPlus() {
        double k, b;

        k = (end.getY() - start.getY()) / (end.getX() - start.getX());
        b = start.getY() - k * start.getX();

        Vertex newEnd = new Vertex(1000, k * 1000 + b);

        return new Line(start, newEnd);
    }

    /* расширение линии */
    public Line extendInOneWayMinus() {
        double k, b;

        k = (end.getY() - start.getY()) / (end.getX() - start.getX());
        b = start.getY() - k * start.getX();

        Vertex newStart = new Vertex(0, b);

        return new Line(newStart, end);
    }

    /* проверка принадлежности вершины линии */
    public boolean checkIfVertexIsOnTheLine(Vertex startCheck, Vertex endCheck, Vertex check) {
        return startCheck.getDistanceToVertex(check) + endCheck.getDistanceToVertex(check) <= startCheck.getDistanceToVertex(endCheck) + eps &&
                startCheck.getDistanceToVertex(check) + endCheck.getDistanceToVertex(check) >= startCheck.getDistanceToVertex(endCheck) - eps;
    }

    /* проверка принадлежности вершины линии */
    public boolean checkIfContainsVertex(Vertex check) {
        return checkIfVertexIsOnTheLine(start, end, check);
    }

    /* преобразование линии в список вершин */
    public List<Vertex> getAsListOfDots() {
        List<Vertex> dots = new ArrayList<>();
        double xDiff = end.getX() - start.getX(), yDiff = end.getY() - start.getY(),
                xCurrent = start.getX(), yCurrent = start.getY();
        boolean end = false;
        while (!end) {
            Vertex newDot = new Vertex(xCurrent, yCurrent);
            if (!checkIfContainsVertex(newDot)) {
                end = true;
            } else {
                dots.add(newDot);
                xCurrent += (xDiff / 75);
                yCurrent += (yDiff / 75);
            }
        }
        return dots;
    }
}
