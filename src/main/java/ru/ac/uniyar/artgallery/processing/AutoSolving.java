package ru.ac.uniyar.artgallery.processing;

import ru.ac.uniyar.artgallery.CopyOnWriteUtils;
import ru.ac.uniyar.artgallery.model.Polygon;
import ru.ac.uniyar.artgallery.model.Triangle;
import ru.ac.uniyar.artgallery.model.Vertex;

import java.util.ArrayList;
import java.util.List;

public class AutoSolving {

    /* основной метод вызова */
    public static List<Vertex> invoke(Polygon polygon) {

        List<Vertex> reds = new ArrayList<>();
        List<Vertex> greens = new ArrayList<>();
        List<Vertex> blues = new ArrayList<>();

        List<Triangle> done = new ArrayList<>();
        List<Triangle> trianglesToCheck = new ArrayList<>(polygon.getTriangles());
        int index = 0;

        Triangle check = trianglesToCheck.get(0);
        done = CopyOnWriteUtils.addToList(done, List.of(check));
        reds = CopyOnWriteUtils.addToList(reds, List.of(check.getVertex2()));
        greens = CopyOnWriteUtils.addToList(greens, List.of(check.getVertex1()));
        blues = CopyOnWriteUtils.addToList(blues, List.of(check.getVertex3()));

        check = getNextTriangleToCheck(check, trianglesToCheck, done);
        if (check == null) return reds;

        while (done.size() < trianglesToCheck.size()) {
            if (!done.contains(check))
                done = CopyOnWriteUtils.addToList(done, List.of(check));
            if (!paintTriangle(reds, greens, blues, check.getVertex1(), check.getVertex2(), check.getVertex3())) {
                int curr = done.indexOf(check);
                check = getNextTriangleToCheck(check, trianglesToCheck, done);
                if (check == null) {
                    if (index == 0) break;
                    check = done.get(index);
                    --index;
                } else {
                    index = curr;
                }
            } else {
                check = getNextTriangleToCheck(check, trianglesToCheck, done);
                if (check == null) {
                    if (index == 0) break;
                    check = done.get(index);
                    --index;
                } else {
                    index = done.size() - 1;
                }
            }
        }
        if (reds.size() <= greens.size()) {
            return reds.size() <= blues.size() ? reds : blues;
        } else {
            return greens.size() <= blues.size() ? greens : blues;
        }
    }

    /* раскрашивание треугольника */
    public static boolean paintTriangle(List<Vertex> reds, List<Vertex> greens, List<Vertex> blues, Vertex v1, Vertex v2, Vertex v3) {
        List<Vertex> allColoredVertexes = new ArrayList<>(reds);
        allColoredVertexes = CopyOnWriteUtils.addToList(allColoredVertexes, greens);
        allColoredVertexes = CopyOnWriteUtils.addToList(allColoredVertexes, blues);
        boolean v1isColored = allColoredVertexes.contains(v1);
        boolean v2isColored = allColoredVertexes.contains(v2);
        boolean v3isColored = allColoredVertexes.contains(v3);
        if (v1isColored && v2isColored && v3isColored)
            return false;
        String v1Color = getVertexColor(reds, greens, blues, v1);
        String v2Color = getVertexColor(reds, greens, blues, v2);
        String v3Color = getVertexColor(reds, greens, blues, v3);

        switch (v1Color) {
            case "red": {
                switch (v2Color) {
                    case "green": {
                        blues.add(v3);
                        break;
                    }
                    case "blue": {
                        greens.add(v3);
                        break;
                    }
                    case "no_color": {
                        switch (v3Color) {
                            case "green": {
                                blues.add(v2);
                                break;
                            }
                            case "blue": {
                                greens.add(v2);
                                break;
                            }
                            case "no_color": {
                                greens.add(v2);
                                blues.add(v3);
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
            case "green": {
                switch (v2Color) {
                    case "red": {
                        blues.add(v3);
                        break;
                    }
                    case "blue": {
                        reds.add(v3);
                        break;
                    }
                    case "no_color": {
                        switch (v3Color) {
                            case "red": {
                                blues.add(v2);
                                break;
                            }
                            case "blue": {
                                reds.add(v2);
                                break;
                            }
                            case "no_color": {
                                reds.add(v2);
                                blues.add(v3);
                            }
                        }
                        break;
                    }
                }
                break;
            }
            case "blue": {
                switch (v2Color) {
                    case "red": {
                        greens.add(v3);
                        break;
                    }
                    case "green": {
                        reds.add(v3);
                        break;
                    }
                    case "no_color": {
                        switch (v3Color) {
                            case "red": {
                                greens.add(v2);
                                break;
                            }
                            case "green": {
                                reds.add(v2);
                                break;
                            }
                            case "no_color": {
                                reds.add(v2);
                                greens.add(v3);
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
            case "no_color": {
                switch (v2Color) {
                    case "red": {
                        switch (v3Color) {
                            case "green": {
                                blues.add(v1);
                                break;
                            }
                            case "blue": {
                                greens.add(v1);
                                break;
                            }
                            case "no_color": {
                                greens.add(v1);
                                blues.add(v3);
                                break;
                            }
                        }
                        break;
                    }
                    case "green": {
                        switch (v3Color) {
                            case "red": {
                                blues.add(v1);
                                break;
                            }
                            case "blue": {
                                reds.add(v1);
                                break;
                            }
                            case "no_color": {
                                reds.add(v1);
                                blues.add(v3);
                                break;
                            }
                        }
                        break;
                    }
                    case "blue": {
                        switch (v3Color) {
                            case "red": {
                                greens.add(v1);
                                break;
                            }
                            case "green": {
                                reds.add(v1);
                                break;
                            }
                            case "no_color": {
                                reds.add(v1);
                                greens.add(v3);
                                break;
                            }
                        }
                        break;
                    }
                    case "no_color": {
                        switch (v3Color) {
                            case "red": {
                                greens.add(v1);
                                blues.add(v2);
                                break;
                            }
                            case "green": {
                                reds.add(v1);
                                blues.add(v2);
                                break;
                            }
                            case "blue": {
                                reds.add(v1);
                                greens.add(v2);
                                break;
                            }
                            case "no_color": {
                                reds.add(v1);
                                greens.add(v2);
                                blues.add(v3);
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
        return true;
    }

    /* получение цвета вершины */
    public static String getVertexColor(List<Vertex> reds, List<Vertex> greens, List<Vertex> blues, Vertex check) {
        if (reds.contains(check)) return "red";
        if (greens.contains(check)) return "green";
        if (blues.contains(check)) return "blue";
        return "no_color";
    }

    /* получение следующего к рассмотрению треугольника */
    public static Triangle getNextTriangleToCheck(Triangle last, List<Triangle> allTriangles,
                                           List<Triangle> doneTriangles) {
        for (Triangle triangle : allTriangles) {
            if (last != triangle && triangle.isNextTo(last) && !doneTriangles.contains(triangle))
                return triangle;
        }
        return null;
    }
}
