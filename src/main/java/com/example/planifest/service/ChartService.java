package com.example.planifest.service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

import com.example.planifest.entity.Supply;

@Service
public class ChartService {

    // Gráfico de barras: Reporte Administración
    public String generarGraficoAdministracion(long empleados, long eventos, long tareas, long insumos)
            throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(empleados, "Empleados", "Empleados");
        dataset.addValue(eventos, "Eventos", "Eventos");
        dataset.addValue(tareas, "Tareas", "Tareas");
        dataset.addValue(insumos, "Insumos", "Insumos");

        JFreeChart chart = ChartFactory.createBarChart(
                "Resumen Administración",
                "Métricas",
                "Cantidad",
                dataset);

        return convertirGraficoABase64(chart, 500, 300);
    }

    public String generarGraficoInventario(Map<String, Integer> stockPorTipo) throws Exception {
        DefaultPieDataset dataset = new DefaultPieDataset();
        stockPorTipo.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                "Distribución de Stock por Tipo",
                dataset,
                true, true, false);

        return convertirGraficoABase64(chart, 500, 300);
    }

    public String generarGraficoStockMinimo(List<Supply> supplies) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Supply s : supplies) {
            dataset.addValue(s.getCurrentStock(), "Stock Actual", s.getName());
            dataset.addValue(s.getMinStock(), "Stock Mínimo", s.getName());
            dataset.addValue(s.getMaxStock(), "Stock Máximo", s.getName());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Stock Actual vs. Stock Mínimo y Máximo",
                "Insumos",
                "Cantidad",
                dataset);

        return convertirGraficoABase64(chart, 700, 400);
    }

    private String convertirGraficoABase64(JFreeChart chart, int width, int height) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, width, height);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
