package globalsolutions.findemes.pantallas.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.BarChart;
import com.googlecode.charts4j.BarChartPlot;
import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.DataUtil;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.LinearGradientFill;
import com.googlecode.charts4j.Plots;
import com.googlecode.charts4j.Color;

import java.util.Arrays;

import globalsolutions.findemes.R;


/**
 * Created by Manuel on 23/02/2015.
 */
public class OptionActivityBarChart extends Activity {




    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_bar);

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

        // Defining data series.
        final double MAX_VALUE = max;
        Data ingresosData= DataUtil.scaleWithinRange(0, MAX_VALUE, ingresos);
        Data gastosData= DataUtil.scaleWithinRange(0, MAX_VALUE, gastos);
        BarChartPlot ingresosPlot = Plots.newBarChartPlot(ingresosData, Color.GREEN, getResources().getString(R.string.MENU_INGRESOS));
        BarChartPlot gastosPlot = Plots.newBarChartPlot(gastosData, Color.RED, getResources().getString(R.string.MENU_GASTOS));
        BarChart chart = GCharts.newBarChart(ingresosPlot, gastosPlot);

        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.BLACK, 13, AxisTextAlignment.CENTER);
        AxisLabels periodo = AxisLabelsFactory.newAxisLabels(getIntent().getExtras().getString("anyo"), 50.0);
        periodo.setAxisStyle(axisStyle);
        AxisLabels periodos = AxisLabelsFactory.newAxisLabels(ejeX);
        periodos.setAxisStyle(axisStyle);
        AxisLabels efectivo = AxisLabelsFactory.newAxisLabels(getResources().getString(R.string.Grafica_ejex), 50.0);
        efectivo.setAxisStyle(axisStyle);
        AxisLabels valueCount = AxisLabelsFactory.newNumericRangeAxisLabels(0, MAX_VALUE);
        valueCount.setAxisStyle(axisStyle);


        // Adding axis info to chart.
        chart.addXAxisLabels(valueCount);
        chart.addXAxisLabels(efectivo);
        chart.addYAxisLabels(periodos);
        chart.addYAxisLabels(periodo);
        chart.addTopAxisLabels(valueCount);
        chart.setHorizontal(true);
        chart.setSize(450, 650);
        chart.setSpaceBetweenGroupsOfBars(30);

        chart.setTitle(getResources().getString(R.string.Grafica_titulo), Color.BLACK, 16);

        chart.setGrid((50.0/MAX_VALUE)*20, 600, 3, 2);
        chart.setBackgroundFill(Fills.newSolidFill(Color.LIGHTGREY));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.newColor("E37600"), 100);
        fill.addColorAndOffset(Color.WHITE, 0);
        chart.setAreaFill(fill);
        String url = chart.toURLString();

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
