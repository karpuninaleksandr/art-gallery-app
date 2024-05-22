package ru.ac.uniyar.artgallery.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.pekkam.Canvas;
import org.vaadin.pekkam.CanvasRenderingContext2D;
import ru.ac.uniyar.artgallery.model.Polygon;
import ru.ac.uniyar.artgallery.model.Triangle;
import ru.ac.uniyar.artgallery.model.Vertex;

import java.util.ArrayList;

public class AutoSolving {

    private static final Logger logger = LoggerFactory.getLogger(AutoSolving.class);

    //todo save all colors of vertexes to get most efficient camera places
    public static void invoke(Canvas canvas, Polygon polygon) {
        CanvasRenderingContext2D context = canvas.getContext();
        context.setStrokeStyle("black");

        ArrayList<Vertex> skipped = new ArrayList<>();
        ArrayList<Vertex> cams = new ArrayList<>();
        ArrayList<Triangle> done = new ArrayList<>();
        ArrayList<Triangle> trianglesToCheck = new ArrayList<>(polygon.getTriangles());
        int index = 0;

        Triangle check = trianglesToCheck.get(index);
        context.setFillStyle("red");
        context.fillRect(check.getVertex1().getX(), check.getVertex1().getY(), 4, 4);
        done.add(check);
        cams.add(check.getVertex1());
        skipped.add(check.getVertex3());
        skipped.add(check.getVertex2());

        check = getNextTriangleToCheck(check, trianglesToCheck, done);
        if (check == null) return;

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
                context.fillRect(tobeRed.getX(), tobeRed.getY(), 4, 4);
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
