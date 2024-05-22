package ru.ac.uniyar.artgallery.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ac.uniyar.artgallery.model.Polygon;
import ru.ac.uniyar.artgallery.model.Triangle;
import ru.ac.uniyar.artgallery.model.Vertex;

import java.util.ArrayList;
import java.util.List;

public class AutoSolving {

    private static final Logger logger = LoggerFactory.getLogger(AutoSolving.class);

    //todo save all colors of vertexes to get most efficient camera places
    public static List<Vertex> invoke(Polygon polygon) {

        ArrayList<Vertex> skipped = new ArrayList<>();
        ArrayList<Vertex> cams = new ArrayList<>();
        ArrayList<Triangle> done = new ArrayList<>();
        ArrayList<Triangle> trianglesToCheck = new ArrayList<>(polygon.getTriangles());
        int index = 0;

        Triangle check = trianglesToCheck.get(index);
        done.add(check);
        cams.add(check.getVertex1());
        skipped.add(check.getVertex3());
        skipped.add(check.getVertex2());

        check = getNextTriangleToCheck(check, trianglesToCheck, done);
        if (check == null) return cams;

        while (done.size() < trianglesToCheck.size()) {
            logger.info("index: " + index);

            if (cams.contains(check.getVertex1())) {
                skipped.add(check.getVertex2());
                skipped.add(check.getVertex3());
            } else if (cams.contains(check.getVertex2())) {
                skipped.add(check.getVertex1());
                skipped.add(check.getVertex3());
            } else if (cams.contains(check.getVertex3())) {
                skipped.add(check.getVertex1());
                skipped.add(check.getVertex2());
            } else {
                Vertex tobeRed;
                if (!skipped.contains(check.getVertex1()))
                    tobeRed = check.getVertex1();
                else if (!skipped.contains(check.getVertex2()))
                    tobeRed = check.getVertex2();
                else if (!skipped.contains(check.getVertex3()))
                    tobeRed = check.getVertex3();
                else {
                    logger.info("moving backwards to index: " + (index - 1));
                    if (index == 0) {
                        break;
                    }
                    --index;
                    check = done.get(index);
                    continue;
                }
                cams.add(tobeRed);
            }
            if (!done.contains(check)) {
                done.add(check);
            }
            check = getNextTriangleToCheck(check, trianglesToCheck, done);

            if (check == null && index > 0) {
                logger.info("moving backwards to index: " + (index - 1));
                check = done.get(index);
                --index;
                continue;
            }
            if (check == null && index == 0) {
                break;
            }
            index = done.size() - 1;
            logger.info("moving forward to index: " + index);
        }
        return cams;
    }

    public static Triangle getNextTriangleToCheck(Triangle last, ArrayList<Triangle> allTriangles,
                                           ArrayList<Triangle> doneTriangles) {

        for (Triangle triangle : allTriangles) {
            if (last != triangle && triangle.isNextTo(last) && !doneTriangles.contains(triangle))
                return triangle;
        }
        return null;
    }
}
