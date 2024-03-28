package ru.ac.uniyar.artgallery.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Triangle {
    private final double eps = 0.00000001;

    private Vertex vertex1;
    private Vertex vertex2;
    private Vertex vertex3;

    public double getField() {
        double a = new Line(vertex1, vertex2).getLength();
        double b = new Line(vertex2, vertex3).getLength();
        double c = new Line(vertex1, vertex3).getLength();
        double p = (a + b + c) / 2;

        return Math.sqrt(p * (p - a) * (p - b) * (p - c));
    }

    public boolean checkIfVertexIsInside(Vertex checkVertex) {
        Triangle smallTriangle1 = new Triangle(checkVertex, this.getVertex1(), this.getVertex2());
        Triangle smallTriangle2 = new Triangle(checkVertex, this.getVertex2(), this.getVertex3());
        Triangle smallTriangle3 = new Triangle(checkVertex, this.getVertex1(), this.getVertex3());

        return Math.abs(this.getField() -
                (smallTriangle2.getField() + smallTriangle1.getField() + smallTriangle3.getField())) < eps;
    }

    public ArrayList<Line> getListOfLines() {
        ArrayList<Line> lines = new ArrayList<>();
        lines.add(new Line(vertex1, vertex2));
        lines.add(new Line(vertex1, vertex3));
        lines.add(new Line(vertex3, vertex2));

        return lines;
    }
}
