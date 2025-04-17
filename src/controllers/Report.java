package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.geometry.Side;

import java.net.URL;
import java.util.ResourceBundle;

public class Report implements Initializable {

    @FXML
    private HBox chartContainer;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // First chart
        CategoryAxis xAxis1 = new CategoryAxis();
        NumberAxis yAxis1 = new NumberAxis();
        xAxis1.setLabel("Vehicle");
        yAxis1.setLabel("Income");
        BarChart<String, Number> chart1 = new BarChart<>(xAxis1, yAxis1);
        chart1.setLegendVisible(false);
        chart1.setTitle("Income per Vehicle");
        chart1.setCategoryGap(10);
        chart1.setBarGap(3);
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.getData().add(new XYChart.Data<>("Car AC", 1000));
        series1.getData().add(new XYChart.Data<>("Car AB", 1000));
        series1.getData().add(new XYChart.Data<>("Car A", 1000));
        series1.getData().add(new XYChart.Data<>("Car B", 1500));
        series1.getData().add(new XYChart.Data<>("Car C", 800));
        chart1.getData().add(series1);
    
        // Set the chart's width and height to be 50% of the container's size
        chart1.setPrefHeight(Region.USE_COMPUTED_SIZE); // This allows the chart to resize based on container height
        chart1.setPrefWidth(Region.USE_COMPUTED_SIZE);  // This allows the chart to resize based on container width
    
        // Second chart
        CategoryAxis xAxis2 = new CategoryAxis();
        NumberAxis yAxis2 = new NumberAxis();
        xAxis2.setLabel("Vehicle");
        yAxis2.setLabel("Number of Rents");
        yAxis2.setSide(Side.LEFT);
        BarChart<String, Number> chart2 = new BarChart<>(xAxis2, yAxis2);
        chart2.setLegendVisible(false);
        chart2.setTitle("Number of Rentals");
        chart2.setCategoryGap(10);
        chart2.setBarGap(3);
        chart2.setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);
        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.getData().add(new XYChart.Data<>("Car A", 5));
        series2.getData().add(new XYChart.Data<>("Car B", 8));
        series2.getData().add(new XYChart.Data<>("Car C", 3));
        chart2.getData().add(series2);
    
        // Set the chart's width and height to be 50% of the container's size
        chart2.setPrefHeight(Region.USE_COMPUTED_SIZE); // Allow resizing based on parent container
        chart2.setPrefWidth(Region.USE_COMPUTED_SIZE);  // Allow resizing based on parent container
    
        // Add the charts to the container
        chartContainer.getChildren().addAll(chart1, chart2);
    
        // Make sure the charts fill the container's size
        chartContainer.setStyle("-fx-border-color: black;"); // For debugging the layout
    }
    
    

    
    
}
