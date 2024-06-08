package ru.ac.uniyar.artgallery.processing;

import ru.ac.uniyar.artgallery.model.*;

import java.util.ArrayList;
import java.util.List;

public class Triangulation {

    /* базовый метод вызова */
    public static void invoke(Polygon polygon) {
        int n = polygon.getVertexes().size();
        if (n < 3) return;

        List<Integer> indexes = new ArrayList<>();
        if (area(polygon) > 0)
            for (int i = 0; i < n; ++i) {
                indexes.add(i);
            }
        else
            for (int i = 0; i < n; ++i) {
                indexes.add((n - 1) - i);
            }

        int count = n * 2;
        int v = n - 1;
        while (n >= 3) {
            if ((--count) <= 0) return;

            int u = (v >= n) ? 0 : v;
            v = (u + 1 >= n) ? 0 : u + 1;
            int w = (v + 1 >= n) ? 0 : v + 1;

            if (canBeCut(polygon, u, v, w, n, indexes)) {
                int a = indexes.get(u);
                int b = indexes.get(v);
                int c = indexes.get(w);
                polygon.addTriangle(new Triangle(polygon.getVertexes().get(a),
                        polygon.getVertexes().get(b), polygon.getVertexes().get(c)));
                for (int s = v, t = v + 1; t < n; ++s, ++t) {
                    indexes.remove(s);
                    indexes.add(s, indexes.get(t - 1));
                }
                --n;
                count = n * 2;
            }
        }
    }

    /* площадь многоугольника на основе определителя */
    private static double area(Polygon polygon) {
        int n = polygon.getVertexes().size();
        double A = 0.0;
        for (int p = n - 1, q = 0; q < n; p = q++) {
            Vertex pval = polygon.getVertexes().get(p);
            Vertex qval = polygon.getVertexes().get(q);
            A += pval.getX() * qval.getY() - qval.getX() * pval.getY();
        }
        return (A * 0.5);
    }

    /* проверка возможности отрезания "уха" многоугольника */
    private static boolean canBeCut(Polygon polygon, int u, int v, int w, int n, List<Integer> indexes) {
        Vertex A = polygon.getVertexes().get(indexes.get(u));
        Vertex B = polygon.getVertexes().get(indexes.get(v));
        Vertex C = polygon.getVertexes().get(indexes.get(w));
        if ((((B.getX() - A.getX()) * (C.getY() - A.getY())) - ((B.getY() - A.getY()) * (C.getX() - A.getX()))) < 0.0000000001)
            return false;
        for (int p = 0; p < n; ++p) {
            if ((p != u) && (p != v) && (p != w) &&
                    new Triangle(A, B, C).checkIfVertexIsInside(polygon.getVertexes().get(indexes.get(p))))
                return false;
        }
        return true;
    }
}
