package globalsolutions.findemes.pantallas.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
/*
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
*/

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;

import java.util.Arrays;

import globalsolutions.findemes.R;


/**
 * Created by Manuel on 23/02/2015.
 */
public class OptionActivityBarChart extends Activity {




    //private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linegraph);

        String periodo = getIntent().getExtras().getString("periodo");
        double[] ingresos = getIntent().getExtras().getDoubleArray("ingresos");
        double[] gastos = getIntent().getExtras().getDoubleArray("gastos");
        String[] ejeX = getIntent().getExtras().getStringArray("ejeX");

        double max = 0.00;
        double size =  ingresos.length;

        for(int i = 0 ; i < size; i++){
            if(ingresos[i] > max){
                max = ingresos[i];
            }
            if(gastos[i] > max){
                max = gastos[i];
            }
        }

        final double MAX_VALUE = max;
        double dist = MAX_VALUE/size;

        Line lingresos = new Line();
        Line lgastos = new Line();
        lingresos.setColor(Color.GREEN);
        lgastos.setColor(Color.RED);
        float distancia = 0;
        for(int i = 0 ; i < size; i++){
            LinePoint p = new LinePoint();
            p.setX(distancia);
            p.setY(new Float(ingresos[i]));
            lingresos.addPoint(p);

            LinePoint p2 = new LinePoint();
            p2.setX(distancia);
            p2.setY(new Float(gastos[i]));
            lgastos.addPoint(p2);
            distancia += dist;
        }

        LineGraph li = (LineGraph)findViewById(R.id.linegraphid);
        li.addLine(lgastos);
        li.addLine(lingresos);
        li.setRangeY(0, new Float(MAX_VALUE));
        li.setLineToFill(0);
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
