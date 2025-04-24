package controllers;

import database.DatabaseConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

public class Report {

    @FXML
    private NumberAxis monthlyRentAxis;

    @FXML
    private CategoryAxis vehicleModelAxis;

    @FXML
    private BarChart<String, Number> vehicleRentalChart1;

    @FXML
    private BarChart<String, Number> vehicleRentalChart2;

    @FXML
    private NumberAxis quantityAxis;

    @FXML
    private CategoryAxis vehicleModelAxis2;

    @FXML
    private PieChart vehicleCategoryPieChart; // Optional – not used in this version

    public void initialize() {
        updateCharts();
    }

    private void updateCharts() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        String pieSql = "SELECT category, SUM(totalRent) as totalRents FROM performance_reports GROUP BY category";

        try (var conn = DatabaseConnection.connect();
            var stmt = conn.prepareStatement(pieSql);
            var rs = stmt.executeQuery()) {

            while (rs.next()) {
                String category = rs.getString("category");
                int totalRents = rs.getInt("totalRents");
                pieChartData.add(new PieChart.Data(category, totalRents));
            }

            vehicleCategoryPieChart.setData(pieChartData);
            vehicleCategoryPieChart.setLabelsVisible(true);
            vehicleCategoryPieChart.setLegendVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String query = "SELECT carbrand, carModel, totalRent, TotalIncome FROM performance_reports";

        XYChart.Series<String, Number> rentSeries = new XYChart.Series<>();
        rentSeries.setName("Total Rent");

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Total Income");

        try (var conn = DatabaseConnection.connect();
             var stmt = conn.prepareStatement(query);
             var rs = stmt.executeQuery()) {

            while (rs.next()) {
                String brand = rs.getString("carbrand");
                String model = rs.getString("carModel");
                int totalRent = rs.getInt("totalRent");
                double totalIncome = rs.getDouble("TotalIncome");

                String label = brand + " " + model;

                rentSeries.getData().add(new XYChart.Data<>(label, totalRent));
                incomeSeries.getData().add(new XYChart.Data<>(label, totalIncome));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        vehicleRentalChart1.getData().setAll(rentSeries);
        vehicleRentalChart2.getData().setAll(incomeSeries);

        styleCharts(rentSeries, incomeSeries);
        for (PieChart.Data data : pieChartData) {
            String text = data.getName() + ": " + (int) data.getPieValue();
            javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(text);
            javafx.scene.control.Tooltip.install(data.getNode(), tooltip);
        }
        vehicleCategoryPieChart.setData(pieChartData);
        
    }

    private void styleCharts(XYChart.Series<String, Number> rentSeries, XYChart.Series<String, Number> incomeSeries) {
        // Disable legends and set axis label styles
        vehicleRentalChart1.setLegendVisible(false);
        vehicleRentalChart1.setPrefHeight(500);
        vehicleRentalChart2.setPrefHeight(500);
        vehicleRentalChart2.setLegendVisible(false);
        vehicleCategoryPieChart.setLegendVisible(false);
        vehicleCategoryPieChart.setLabelsVisible(false);
        vehicleCategoryPieChart.setTitle("Most Category Rented ");


        Platform.runLater(() -> {
            rentSeries.getData().forEach(data -> {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-bar-fill: #FF4C4C;"); // Red for Rent
                }
            });

            incomeSeries.getData().forEach(data -> {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-bar-fill: #4A90E2;"); // Blue for Income
                }
            });

            // Background styling
            Node background1 = vehicleRentalChart1.lookup(".chart-plot-background");
            if (background1 != null) background1.setStyle("-fx-background-color: #001FBB;");

            Node background2 = vehicleRentalChart2.lookup(".chart-plot-background");
            if (background2 != null) background2.setStyle("-fx-background-color: #001FBB;");

            // Axis label colors
            monthlyRentAxis.setLabel("Total Rent");
            monthlyRentAxis.setTickLabelFill(Color.WHITE);
            quantityAxis.setLabel("Total Income");
            quantityAxis.setTickLabelFill(Color.WHITE);

            vehicleModelAxis.setTickLabelFill(Color.WHITE);
            vehicleModelAxis2.setTickLabelFill(Color.WHITE);
        });
    }
}
