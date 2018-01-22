/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bloc97.javaplotter;

import java.util.LinkedList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.ContourChart;
import org.jzy3d.chart.factories.ContourChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.javafx.JavaFXChartFactory;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

/**
 *
 * @author bowen
 * 

    Interesting libraries
    https://github.com/mzur/pretty-formula
    http://www.japisoft.com/formula/
    http://jeval.sourceforge.net/
    https://stackoverflow.com/questions/11117158/java-formula-evaluation-library-with-out-of-order-variables-feature

* 
*   Use Textarea and parse one function per line instead of individual textfields
*/

public class JavaPlotter extends Application {
    
    private final List<Label> labelList = new LinkedList<>();
    private final List<TextField> textFieldList = new LinkedList<>();
    
    private final GridPane grid = new GridPane();
    private final TextField minField, maxField, nField;
                    
    private final JavaFXChartFactory factory = new JavaFXChartFactory();
    
    private Chart chart;
    private ImageView chartView;
    
    public JavaPlotter() {
        minField = new TextField("-3");
        maxField = new TextField("3");
        nField = new TextField("50");
        
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Text scenetitle = new Text("3D Function Plotter");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
        
        Label minLabel = new Label("Min :");
        grid.add(minLabel, 4, 0);

        minField.setPrefWidth(40);
        grid.add(minField, 5, 0);
        
        Label maxLabel = new Label("Max :");
        grid.add(maxLabel, 6, 0);

        maxField.setPrefWidth(40);
        grid.add(maxField, 7, 0);
        
        Label nLabel = new Label("N :");
        grid.add(nLabel, 11, 0);

        nField.setPrefWidth(40);
        grid.add(nField, 12, 0);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Multiple Plotter");
        
        
        Button btn = new Button("Plot");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 21, 0);
        
        
        btn.setOnAction((ActionEvent e) -> {
            try {
                if (chart != null) {
                    chart.dispose();
                }
                boolean isChartNull = chartView == null;
                if (chartView == null) {
                    chartView = new ImageView();
                }
                chart = factory.newChart(Quality.Nicest, IChartComponentFactory.Toolkit.offscreen);   
                factory.bind(chartView, (AWTChart) chart);             
                float min = -10;
                float max = 10;
                int n = 40;
                try {
                    min = Float.parseFloat(minField.getText());
                    max = Float.parseFloat(maxField.getText());
                    n = Integer.parseInt(nField.getText());
                } catch (NumberFormatException nex) {
                    
                }
                //ContourChart ch = (ContourChart) ContourChartComponentFactory.chart(Quality.Nicest, IChartComponentFactory.Toolkit.offscreen);
                for (TextField textField : textFieldList) {
                    chart.add(ChartPlotter.getShape(textField.getText(), min, max, n));
                }
                
                if (isChartNull) {
                    chart.setViewMode(ViewPositionMode.FREE);
                    grid.add(chartView, 1, labelList.size() + 1, 20, 1);
                    primaryStage.sizeToScene();
                }

                //AWTChart chart = ChartPlotter.get3DAWTChart(factory, stringList, min, max, n);
                //ChartLauncher.openChart(c, stringList.get(0));
                //c.open(stringList.get(0), 800, 600);
            } catch (Exception ex) {
            }
        });
        
        addNewRow();
        textFieldList.get(0).setText("cos(x+y) + x^2/6 - y^2/6");

        Button moreBtn = new Button("More");
        HBox hbMoreBtn = new HBox(10);
        hbMoreBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbMoreBtn.getChildren().add(moreBtn);
        grid.add(hbMoreBtn, 21, 1);
        
        moreBtn.setOnAction((ActionEvent e) -> {
            try {
                addNewRow();
                primaryStage.sizeToScene();
            } catch (Exception ex) {
                
            }
        });
        
        Button removeBtn = new Button("Remove");
        HBox hbRemoveBtn = new HBox(10);
        hbRemoveBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbRemoveBtn.getChildren().add(removeBtn);
        grid.add(hbRemoveBtn, 21, 2);
        removeBtn.setOnAction((ActionEvent e) -> {
            try {
                removeRow();
                primaryStage.sizeToScene();
            } catch (Exception ex) {

            }
        });
        
        Scene scene = new Scene(grid);
        
        primaryStage.setScene(scene);
        
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
        
    }
    
    private void addNewRow() {
        
        Label functionName = new Label("Function " + (labelList.size() + 1) + ":");
        grid.add(functionName, 0, labelList.size() + 1);
        labelList.add(functionName);

        TextField textField = new TextField();
        grid.add(textField, 1, textFieldList.size() + 1, 20, 1);
        textFieldList.add(textField);
    }
    
    private void removeRow() {
        if (labelList.size() > 1) {
            Label label = labelList.get(labelList.size()-1);
            TextField textField = textFieldList.get(textFieldList.size()-1);
            grid.getChildren().remove(textField);
            grid.getChildren().remove(label);

            textFieldList.remove(textField);
            labelList.remove(label);
            
        }
        
    }
    
    
    
}