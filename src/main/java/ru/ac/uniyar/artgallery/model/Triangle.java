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

    public boolean hasVertex(Vertex v) {
        return vertex1.isEqualTo(v) || vertex2.isEqualTo(v) || vertex3.isEqualTo(v);
    }

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

    public boolean isNextTo(Triangle triangle) {
        return this.contains(triangle.getVertex1(), triangle.getVertex2()) ||
                this.contains(triangle.getVertex1(), triangle.getVertex3()) ||
                this.contains(triangle.getVertex2(), triangle.getVertex3());
    }

    public boolean contains(Vertex vertex1, Vertex vertex2) {
        return checkIfTwoOfThreeVertexesAreEqual(this.vertex1, this.vertex2, this.vertex3, vertex1, vertex2) ||
                checkIfTwoOfThreeVertexesAreEqual(this.vertex1, this.vertex3, this.vertex2, vertex1, vertex2) ||
                checkIfTwoOfThreeVertexesAreEqual(this.vertex2, this.vertex1, this.vertex3, vertex1, vertex2) ||
                checkIfTwoOfThreeVertexesAreEqual(this.vertex2, this.vertex3, this.vertex1, vertex1, vertex2) ||
                checkIfTwoOfThreeVertexesAreEqual(this.vertex3, this.vertex1, this.vertex2, vertex1, vertex2) ||
                checkIfTwoOfThreeVertexesAreEqual(this.vertex3, this.vertex2, this.vertex1, vertex1, vertex2);
    }

    public boolean checkIfTwoOfThreeVertexesAreEqual(Vertex v1, Vertex v2, Vertex v3, Vertex c1, Vertex c2) {
        if (v1.isEqualTo(c1) || v1.isEqualTo(c2)) {
            if (v2.isEqualTo(c1) || v2.isEqualTo(c2)) {
                return true;
            } else return v3.isEqualTo(c1) || v3.isEqualTo(c2);
        }
        return false;
    }

    public boolean isValid() {
        return !(new Line(vertex1, vertex3)).checkIfContainsVertex(vertex2);
    }
}
