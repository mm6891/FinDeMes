package globalsolutions.findemes.pantallas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.MovimientoDAO;
import globalsolutions.findemes.database.model.MovimientoItem;
import globalsolutions.findemes.database.util.Constantes;

/**
 * Created by Manuel on 23/02/2015.
 */
public class InformesActivity extends Activity{

    /*public enum Periodo {
        PORDÍA = "Por día",
        PORPERIODO = "Por período"
    }*/

    private Spinner spTipoMovimiento;
    private Spinner spPeriodo;
    private Spinner spAnyosInforme;

    private ListView listViewMovsInforme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos);

        //boton retroceder
        ImageButton btnReturn = (ImageButton) findViewById(R.id.btnBackButton);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                backActivity();
            }
        });

        //spinner movimiento
        ArrayList<String> tiposMovimientos = new ArrayList<String>();
        tiposMovimientos.add(Constantes.TIPO_FILTRO_RESETEO.toString());
        tiposMovimientos.add(Constantes.TIPO_MOVIMIENTO_INGRESO.toString());
        tiposMovimientos.add(Constantes.TIPO_MOVIMIENTO_GASTO.toString());
        spTipoMovimiento = (Spinner) findViewById(R.id.spTipoMovimiento);
        spTipoMovimiento.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tiposMovimientos));

        //spinner periodo
        ArrayList<String> periodos = new ArrayList<String>();
        periodos.add("MENSUAL");
        periodos.add("ANUAL");
        spPeriodo = (Spinner) findViewById(R.id.spPeriodo);
        spPeriodo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, periodos));

        spAnyosInforme = (Spinner) findViewById(R.id.spAnyos);
        //lista movimientos
        //recuperamos movimientos
        final ArrayList<MovimientoItem> movs = new MovimientoDAO().cargaMovimientos(getApplicationContext());
        //cargamos anyos
        ArrayList<String> anyos = new ArrayList<String>();
        if(movs.size() <= 0 )
            showToast("NO HAY MOVIMIENTOS ACTUALMENTE");
        else {
            for (MovimientoItem mov : movs) {
                String fecha = mov.getFecha();
                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date d1 = null;
                Calendar tdy1;
                try {
                    d1 = formato.parse(fecha);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                tdy1 = Calendar.getInstance();
                int year = tdy1.get(Calendar.YEAR);
                if (!anyos.contains(String.valueOf(new Integer(year))))
                    anyos.add(String.valueOf(new Integer(year)));
            }
            spAnyosInforme.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, anyos));

            listViewMovsInforme = (ListView) findViewById(R.id.listViewMovInforme);
            listViewMovsInforme.setAdapter(new MovimientoAdapter(getApplicationContext(), movs));
        }

    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    private void backActivity(){
        Intent in = new Intent(InformesActivity.this, MainActivity.class);
        startActivity(in);
        setResult(RESULT_OK);
        finish();
    }

}
