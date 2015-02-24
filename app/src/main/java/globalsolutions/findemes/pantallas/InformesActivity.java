package globalsolutions.findemes.pantallas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

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
        tiposMovimientos.add(Constantes.TIPO_FILTRO_RESETEO.toString());
        tiposMovimientos.add(Constantes.TIPO_MOVIMIENTO_INGRESO.toString());
        tiposMovimientos.add(Constantes.TIPO_MOVIMIENTO_GASTO.toString());
        spPeriodo = (Spinner) findViewById(R.id.spPeriodo);
        spTipoMovimiento.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, periodos));

        //recuperamos movimientos
        final ArrayList<MovimientoItem> movs = new MovimientoDAO().cargaMovimientos(getApplicationContext());
        listViewMovsInforme = (ListView) findViewById(R.id.listViewMov);
        listViewMovsInforme.setAdapter(new MovimientoAdapter(getApplicationContext(), movs));
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
