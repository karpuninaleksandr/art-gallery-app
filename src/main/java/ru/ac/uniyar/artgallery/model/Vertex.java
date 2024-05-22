package ru.ac.uniyar.artgallery.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vertex {
    private final double eps = 0.00000001;

    private double x;
    private double y;

    public double getDistanceToVertex(Vertex vertex) {
        return Math.sqrt(Math.pow(x - vertex.getX(), 2) + Math.pow(y - vertex.getY(), 2));
    }

    public boolean isNotEqualTo(Vertex vertex) {
        if (vertex == null) return true;
        return !(Math.abs(x - vertex.getX()) < eps) || !(Math.abs(y - vertex.getY()) < eps);
    }

    public boolean isEqualTo(Vertex vertex) {
        return !isNotEqualTo(vertex);
    }
}
