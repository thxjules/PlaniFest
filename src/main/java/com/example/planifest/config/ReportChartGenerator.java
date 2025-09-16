package com.example.planifest.config;
import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class ReportChartGenerator {

    public static void generarGraficoEmpleados(int admins, int empleados, int otros) throws IOException {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Admins", admins);
        dataset.setValue("Empleados", empleados);
        dataset.setValue("Otros", otros);

        JFreeChart chart = ChartFactory.createPieChart(
                "Distribución de Empleados",
                dataset,
                true, true, false
        );

        ChartUtils.saveChartAsPNG(new File("src/main/resources/static/images/graficos/empleadosChart.png"), chart, 400, 300);
    }

    public static void generarGraficoTareas(int pendientes, int completadas) throws IOException {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Pendientes", pendientes);
        dataset.setValue("Completadas", completadas);

        JFreeChart chart = ChartFactory.createPieChart(
                "Tareas",
                dataset,
                true, true, false
        );

        ChartUtils.saveChartAsPNG(new File("src/main/resources/static/images/graficos/tareasChart.png"), chart, 400, 300);
    }

}
