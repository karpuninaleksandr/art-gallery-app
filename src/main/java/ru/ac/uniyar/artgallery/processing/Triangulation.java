package ru.ac.uniyar.artgallery.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ac.uniyar.artgallery.model.*;

public class Triangulation {

    private static final Logger logger = LoggerFactory.getLogger(Triangulation.class);

    public static void invoke(Polygon polygon) {
        int n = polygon.getVertexes().size();
        if (n < 3) return;

        int[] indices = new int[n];
        if (area(polygon) > 0) {
            for (int i = 0; i < n; i++) {
                indices[i] = i;
            }
        } else {
            for (int i = 0; i < n; i++) {
                indices[i] = (n - 1) - i;
            }
        }

        int count = 2 * n;
        int v = n - 1;
        while (n > 2) {
            if ((count--) <= 0) return;

            int u = v;
            if (n <= u) u = 0;
            v = u + 1;
            if (n <= v) v = 0;
            int w = v + 1;
            if (n <= w) w = 0;

            if (canBeCut(polygon, u, v, w, n, indices)) {
                int a = indices[u];
                int b = indices[v];
                int c = indices[w];
                polygon.addTriangle(new Triangle(polygon.getVertexes().get(a), polygon.getVertexes().get(b), polygon.getVertexes().get(c)));
                for (int s = v, t = v + 1; t < n; s++, t++) {
                    indices[s] = indices[t];
                }
                n--;
                count = 2 * n;
            }
        }
    }

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

    private static boolean canBeCut(Polygon polygon, int u, int v, int w, int n, int[] indices) {
        Vertex A = polygon.getVertexes().get(indices[u]);
        Vertex B = polygon.getVertexes().get(indices[v]);
        Vertex C = polygon.getVertexes().get(indices[w]);
        if (1.0E-10 > (((B.getX() - A.getX()) * (C.getY() - A.getY())) - ((B.getY() - A.getY()) * (C.getX() - A.getX())))) return false;
        for (int p = 0; p < n; p++) {
            if ((p == u) || (p == v) || (p == w)) continue;
            if (new Triangle(A, B, C).checkIfVertexIsInside(polygon.getVertexes().get(indices[p]))) return false;
        }
        return true;
    }
}
