package ru.ac.uniyar.artgallery.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Line {
    private Vertex start;
    private Vertex end;

    private final double eps = 0.00001;

    public boolean crosses(Line line) {
        Vertex crossVertex = getLinesCrossVertex(line);
        return crossVertex != null && start.isNotEqualTo(crossVertex) && end.isNotEqualTo(crossVertex);
    }

    public boolean crossesExceptVertex(Line line, Vertex vertex) {
        Vertex crossVertex = getLinesCrossVertex(line);
        return crossVertex != null && start.isNotEqualTo(crossVertex) && end.isNotEqualTo(crossVertex) && vertex.isNotEqualTo(crossVertex);
    }

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

    public double getLength() {
        return Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow((start.getY() - end.getY()), 2));
    }

    public boolean canBeDrawn(List<Line> lines) {
        for (Line line : lines) {
            if (this.crosses(line))
                return false;
        }
        return true;
    }

    public boolean canBeDrawnExceptVertex(List<Line> lines, Vertex vertex) {
        for (Line line : lines) {
            if (this.crossesExceptVertex(line, vertex))
                return false;
        }
        return true;
    }

    public Line extendInOneWayPlus() {
        double k, b;

        k = (end.getY() - start.getY()) / (end.getX() - start.getX());
        b = start.getY() - k * start.getX();

        Vertex newEnd = new Vertex(1000, k * 1000 + b);

        return new Line(start, newEnd);
    }

    public Line extendInOneWayMinus() {
        double k, b;

        k = (end.getY() - start.getY()) / (end.getX() - start.getX());
        b = start.getY() - k * start.getX();

        Vertex newStart = new Vertex(0, b);

        return new Line(newStart, end);
    }

    public boolean checkIfVertexIsOnTheLine(Vertex startCheck, Vertex endCheck, Vertex check) {
        return startCheck.getDistanceToVertex(check) + endCheck.getDistanceToVertex(check) <= startCheck.getDistanceToVertex(endCheck) + eps &&
                startCheck.getDistanceToVertex(check) + endCheck.getDistanceToVertex(check) >= startCheck.getDistanceToVertex(endCheck) - eps;
    }

    public boolean checkIfContainsVertex(Vertex check) {
        return checkIfVertexIsOnTheLine(start, end, check);
    }

}
