/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bloc97.javaplotter;

import java.util.LinkedList;
import java.util.List;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

/**
 *
 * @author bowen
 */
public class ChartPlotter {
    
    private static Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.GRAY};
    
    public static Chart get3DChart(List<String> expressions, float min, float max, int steps) {
        
        Chart chart = new AWTChart(Quality.Advanced);
        
        int i = 0;
        
        for (String expression : expressions) {
            if (expression.trim().length() > 0) {
                //Expression e = new ExpressionBuilder(expression).variables("x","y","e","pi").build();
                Expression e = new ExpressionBuilder(expression).implicitMultiplication(true).variables("x","y").build();
                if (e.validate(false).isValid()) {
                    Mapper mapper = new Mapper() {
                        @Override
                        public double f(double x, double y) {
                            try {
                                if (e.setVariable("x", x).setVariable("y", y).validate(true).isValid()) {
                                    return e.evaluate();
                                } else {
                                    return 0;
                                }
                            } catch (Exception ex) {
                                    return 0;
                            }
                            //return e.setVariable("x", x).setVariable("y", y).setVariable("e", Math.E).setVariable("pi", Math.PI).evaluate();
                        }

                    };

                    Shape shape = Builder.buildOrthonormal(mapper, new Range(min, max), steps);
                    //shape.setColorMapper(new ColorMapper(new ColorMapGrayscale(), shape.getBoundingBoxColor()));
                    shape.setFaceDisplayed(false);
                    shape.setWireframeColor(colors[i % colors.length]);
                    shape.setWireframeDisplayed(true);
                    chart.add(shape);
                    i++;
                }
            }
        }
        
        //chart.open("Chart", 800, 600);
        chart.setViewMode(ViewPositionMode.FREE);
        
        chart.addMouseCameraController();
        //chart.addKeyboardCameraController();
        
        return chart;
    }
}
