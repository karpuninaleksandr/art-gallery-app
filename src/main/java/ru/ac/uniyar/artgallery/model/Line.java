package ru.ac.uniyar.artgallery.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.ac.uniyar.artgallery.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Line {
    private Vertex start;
    private Vertex end;

    private final double eps = 0.005;

    /* проверка пересечения линий кроме переданной вершины */
    public boolean crossesExceptVertex(Line line, Vertex vertex) {
        Vertex crossVertex = getLinesCrossVertex(line);
        return crossVertex != null && start.isNotEqualTo(crossVertex) && end.isNotEqualTo(crossVertex) && vertex.isNotEqualTo(crossVertex);
    }

    /* получение точки пересечения линий */
    public Vertex getLinesCrossVertex(Line line) {
        double k1, b1, k2, b2, xCross;

        k1 = calculateK(start, end);
        k2 = calculateK(line.getStart(), line.getEnd());

        b1 = calculateB(start, k1);
        b2 = calculateB(line.getStart(), k2);

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
            xCross = start.getX();
            return getCrossing(xCross, calculateYCross(k2, xCross, b2), line);
        }

        if (line.getStart().getX() == line.getEnd().getX()) {
            xCross = line.getStart().getX();
            return getCrossing(xCross, calculateYCross(k1, xCross, b1), line);
        }

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
        return getCrossing(xCross, calculateYCross(k1, xCross, b1), line);
    }

    private double calculateK(Vertex start, Vertex end) {
        return (end.getY() - start.getY()) / (end.getX() - start.getX());
    }

    private double calculateB(Vertex vertex, double k) {
        return vertex.getY() - k * vertex.getX();
    }

    private double calculateYCross(double k, double x, double b) {
        return k * x + b;
    }

    /* получение длины линии */
    public double getLength() {
        return Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow((start.getY() - end.getY()), 2));
    }

    public boolean canBeDrawn(List<Line> lines) {
        return isDrawable(lines, line -> {
            Vertex crossVertex = getLinesCrossVertex(line);
            return crossVertex != null && start.isNotEqualTo(crossVertex) && end.isNotEqualTo(crossVertex);
        });
    }

    /* проверка отсутствия пересечения линий кроме переданной вершины */
    public boolean canBeDrawnExceptVertex(List<Line> lines, Vertex vertex) {
        return isDrawable(lines, line -> {
            Vertex crossVertex = getLinesCrossVertex(line);
            return crossVertex != null && start.isNotEqualTo(crossVertex) && end.isNotEqualTo(crossVertex) && vertex.isNotEqualTo(crossVertex);
        });
    }

    public boolean isDrawable(List<Line> lines, Predicate<Line> condition) {
        return Utils.filter(lines, condition).isEmpty();
    }

    public Line extend(int x) {
        double k, b;

        k = (end.getY() - start.getY()) / (end.getX() - start.getX());
        b = start.getY() - k * start.getX();

        Vertex newEnd = new Vertex(x, k * x + b);

        return new Line(start, newEnd);
    }

    public Vertex getCrossing(double x, double y, Line line) {
        Vertex checkVertex = new Vertex(x, y);
        if (checkIfVertexIsOnTheLine(start, end, checkVertex) && checkIfVertexIsOnTheLine(line.getStart(), line.getEnd(), checkVertex)) {
            return new Vertex(x, y);
        }
        return null;
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
