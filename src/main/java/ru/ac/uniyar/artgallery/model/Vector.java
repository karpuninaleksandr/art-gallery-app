package ru.ac.uniyar.artgallery.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Vector {

    private double x;
    private double y;

    public double cross(Vector v) {
        return x * v.getY() - y * v.getX();
    }
}
