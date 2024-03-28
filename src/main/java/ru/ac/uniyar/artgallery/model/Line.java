package ru.ac.uniyar.artgallery.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Line {
    private final double eps = 0.00000001;

    private Vertex start;
    private Vertex end;

    public boolean crosses(Line line) {
        Vertex crossVertex = getLinesCrossVertex(line);
        return crossVertex != null && !start.isEqualTo(crossVertex) && !end.isEqualTo(crossVertex);
    }

    public Vertex getLinesCrossVertex(Line line) {
        double k1, b1, k2, b2, xCross, yCross;

        if (start.getX() == end.getX()) {
            if (line.getStart().getX() == line.getEnd().getX()) {
                if (start.getX() != line.getStart().getX())
                    return null;
                if (start.getY() >= Math.min(line.getStart().getY(), line.getEnd().getY())
                        && start.getY() <= Math.max(line.getStart().getY(), line.getEnd().getY()) ||
                        end.getY() >= Math.min(line.getStart().getY(), line.getEnd().getY()) && end.getY()
                                <= Math.max(line.getStart().getY(), line.getEnd().getY())) {
                    return end;
                } else {
                    return null;
                }
            }
            k2 = (line.getEnd().getY() - line.getStart().getY()) / (line.getEnd().getX() - line.getStart().getX());
            b2 = line.getStart().getY() - k2 * line.getStart().getX();
            xCross = start.getX();
            yCross = k2 * start.getX() + b2;
            //todo this if needs to be fixed
            if ((yCross >= Math.min(start.getY(), end.getY()) && yCross <= Math.max(start.getY(), end.getY()))
                    &&
                (xCross >= Math.min(line.getStart().getX(), line.getEnd().getX()) && xCross <= Math.max(line.getStart().getX(), line.getEnd().getX()))
            ) {
                return new Vertex(xCross, yCross);
            }
            return null;
        }

        if (line.getStart().getX() == line.getEnd().getX()) {
            k1 = (end.getY() - start.getY()) / (end.getX() - start.getX());
            b1 = start.getY() - k1 * start.getX();
            xCross = line.getStart().getX();
            yCross = k1 * line.getStart().getX() + b1;
            if (yCross >= Math.min(start.getY(), end.getY()) && yCross <= Math.max(start.getY(), end.getY()) && xCross >=
                    Math.min(start.getX(), end.getX()) && xCross <= Math.max(start.getX(), end.getX())) {
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
                if ((start.getX() <= Math.max(line.getStart().getX(), line.getEnd().getX()) && start.getX() >=
                        Math.min(line.getStart().getX(), line.getEnd().getX())) ||
                        (end.getX() <= Math.max(line.getStart().getX(), line.getEnd().getX()) && start.getX() >=
                        Math.min(line.getStart().getX(), line.getEnd().getX()))) {
                    return end;
                }
                return null;
            }
            return null;
        }

        xCross = (b2 - b1) / (k1 - k2);
        yCross = k1 * xCross + b1;

        if (xCross >= Math.min(start.getX(), end.getX()) && xCross <= Math.max(start.getX(), end.getX()) &&
                yCross >= Math.min(start.getY(), end.getY()) && yCross <= Math.max(start.getY(), end.getY()))
            if (xCross >= Math.min(line.getStart().getX(), line.getEnd().getX()) && xCross <=
                    Math.max(line.getStart().getX(), line.getEnd().getX()) && yCross >=
                    Math.min(line.getStart().getY(), line.getEnd().getY()) && yCross <=
                    Math.max(line.getStart().getY(), line.getEnd().getY())) {
                return new Vertex(xCross, yCross);
            }
        return null;
    }

    public Vertex getMiddleVertex() {
        return new Vertex((start.getX() + end.getX()) / 2, (start.getY() + end.getY()) / 2);
    }

    public double getLength() {
        return Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow((start.getY() - end.getY()), 2));
    }

    public boolean canBeDrawn(Polygon polygon) {
        for (Line line : polygon.getLines()) {
            if (line.crosses(this))
                return false;
        }
        return true;
    }

    public int getLineThatCrosses(Polygon polygon) {
        for (int i = 0; i < polygon.getLines().size(); ++i) {
            if (polygon.getLines().get(i).crosses(this))
                return i;
        }
        return -1;
    }

    public boolean isEqualTo(Line line) {
        return Math.abs(start.getX() - line.getStart().getX()) < eps &&
                Math.abs(start.getY() - line.getStart().getY()) < eps &&
                Math.abs(end.getX() - line.getEnd().getX()) < eps &&
                Math.abs(end.getY() - line.getEnd().getY()) < eps;
    }
}
