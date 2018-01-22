/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bloc97.javaplotter;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.colors.Color;
import org.jzy3d.javafx.JavaFXChartFactory;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 * @author bowen
 */
public class ChartPlotter {
    
    private static Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.GRAY};
    
    private static ExprEvaluator eval = new ExprEvaluator(new EvalEngine(true), true, 20);
    
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
    public static AWTChart get3DAWTChart(JavaFXChartFactory factory, List<String> expressions, float min, float max, int steps) {
        
        AWTChart chart = (AWTChart) factory.newChart(Quality.Nicest, "offscreen");
        
        int i = 0;
        
        for (String expression : expressions) {
            eval.clearVariables();
            ISymbol xSymbol = eval.defineVariable("x");
            ISymbol ySymbol = eval.defineVariable("y");
            IExpr preExpr = eval.evaluateWithTimeout(expression, 10, TimeUnit.SECONDS, true);
            if (expression.trim().length() > 0) {

                Mapper mapper = new Mapper() {
                    @Override
                    public double f(double x, double y) {
                        ISymbol vxSymbol = eval.defineVariable(xSymbol, x);
                        ISymbol vySymbol = eval.defineVariable(ySymbol, y);
                        return eval.evalf(preExpr);
                        /*
                        IExpr newExpr = preExpr.copy();
                        newExpr.replaceAll(expr -> {
                            if (expr.equalTo(xSymbol).isTrue()) {
                                return vxSymbol;
                            } else if (expr.equalTo(ySymbol).isTrue()) {
                                return vySymbol;
                            }
                            return expr;
                        });
                        return newExpr.evalDouble();*/
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
        
        //chart.open("Chart", 800, 600);
        chart.setViewMode(ViewPositionMode.FREE);
        
        //chart.addMouseCameraController();
        //chart.addKeyboardCameraController();
        
        return chart;
    }
    public static Shape getShape(String expression, float min, float max, int steps) {

        eval.clearVariables();
        ISymbol xSymbol = eval.defineVariable("x");
        ISymbol ySymbol = eval.defineVariable("y");
        IExpr preExpr = eval.evaluateWithTimeout(expression, 10, TimeUnit.SECONDS, true);
        if (expression.trim().length() > 0) {

            Mapper mapper = new Mapper() {
                @Override
                public double f(double x, double y) {
                    eval.defineVariable(xSymbol, x);
                    eval.defineVariable(ySymbol, y);
                    return eval.evalf(preExpr);
                }

            };

            Shape shape = Builder.buildOrthonormal(mapper, new Range(min, max), steps);
            //shape.setColorMapper(new ColorMapper(new ColorMapGrayscale(), shape.getBoundingBoxColor()));
            shape.setFaceDisplayed(false);
            shape.setWireframeColor(Color.BLUE);
            //shape.setWireframeColor(colors[i % colors.length]);
            shape.setWireframeDisplayed(true);
            return shape;
        }
        return null;
    
    }
}
