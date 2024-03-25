package ru.ac.uniyar.artgallery.model;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor
public class Polygon {
    private ArrayList<Vertex> vertexes = new ArrayList<>();

    public void addVertex(Vertex vertex) {
        vertexes.add(vertex);
    }

    public void addVertexes(Collection<Vertex> vertexes) {
        this.vertexes.addAll(vertexes);
    }

    public ArrayList<Wall> getListOfWalls() {
        ArrayList<Wall> walls = new ArrayList<>();
        for (int i = 0; i < vertexes.size() - 1;)
            walls.add(new Wall(vertexes.get(i), vertexes.get(++i)));
        walls.add(new Wall(vertexes.get(vertexes.size() - 1), vertexes.get(0)));
        return walls;
    }
}
