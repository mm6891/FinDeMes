package globalsolutions.findemes.pantallas.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.BarChart;
import com.googlecode.charts4j.BarChartPlot;
import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.LinearGradientFill;
import com.googlecode.charts4j.Plots;
import com.googlecode.charts4j.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import globalsolutions.findemes.R;


/**
 * Created by Manuel on 23/02/2015.
 */
public class OptionActivityBarChart extends Activity {




    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chartexample);

        webView =(WebView) findViewById(R.id.chart);
        webView.getSettings().setJavaScriptEnabled(true);

        double[] ingresos = getIntent().getExtras().getDoubleArray("ingresos");
        double[] gastos = getIntent().getExtras().getDoubleArray("gastos");
        String[] ejeX = getIntent().getExtras().getStringArray("ejeX");

        double max = 0.00;
        for(int i = 0 ; i < ingresos.length; i++){
            if(ingresos[i] > max){
                max = ingresos[i];
            }
        }
        for(int i = 0 ; i < gastos.length; i++){
            if(gastos[i] > max){
                max = gastos[i];
            }
        }

        // Defining data plots.
        BarChartPlot barraIngreso = Plots.newBarChartPlot(Data.newData(ingresos), Color.BLUEVIOLET, "Ingreso");
        BarChartPlot barraGasto = Plots.newBarChartPlot(Data.newData(gastos), Color.ORANGERED, "Gasto");

        // Instantiating chart.
        BarChart chart = GCharts.newBarChart(barraIngreso,barraGasto);

        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.BLACK, 13, AxisTextAlignment.CENTER);
        AxisLabels money = AxisLabelsFactory.newAxisLabels("Efectivo", 50.0);
        money.setAxisStyle(axisStyle);
        AxisLabels year = AxisLabelsFactory.newAxisLabels(getIntent().getExtras().getString("anyo"), 50.0);
        year.setAxisStyle(axisStyle);

        // Adding axis info to chart.
        chart.addXAxisLabels(AxisLabelsFactory.newAxisLabels(ejeX));
        chart.addYAxisLabels(AxisLabelsFactory.newNumericRangeAxisLabels(0, max));
        chart.addYAxisLabels(money);
        chart.addXAxisLabels(year);

        chart.setSize(600, 450);
        chart.setBarWidth(100);
        chart.setSpaceWithinGroupsOfBars(20);
        chart.setDataStacked(true);
        chart.setTitle("Grafico de Informes", Color.BLACK, 16);
        chart.setGrid(100, 10, 3, 2);
        chart.setBackgroundFill(Fills.newSolidFill(Color.ALICEBLUE));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.LAVENDER, 100);
        fill.addColorAndOffset(Color.WHITE, 0);
        chart.setAreaFill(fill);
        String url = chart.toURLString();
        //ejemplo
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    private void backActivity(){
        Intent in = new Intent(OptionActivityBarChart.this, InformesActivity.class);
        startActivity(in);
        setResult(RESULT_OK);
        finish();
    }
}
