package globalsolutions.findemes.pantallas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.MovimientoDAO;
import globalsolutions.findemes.database.model.InformeItem;
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

    //this counts how many Spinner's are on the UI
    private int mSpinnerCount=0;

    //this counts how many Spinner's have been initialized
    private int mSpinnerInitializedCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informes);

        //boton retroceder
        ImageButton btnReturn = (ImageButton) findViewById(R.id.btnBackButton);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                backActivity();
            }
        });

        //spinner periodo
        ArrayList<String> periodos = new ArrayList<String>();
        periodos.add("MENSUAL");
        periodos.add("ANUAL");
        spPeriodo = (Spinner) findViewById(R.id.spPeriodo);
        spPeriodo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, periodos));

        spAnyosInforme = (Spinner) findViewById(R.id.spAnyosInforme);
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
                Calendar cal  = Calendar.getInstance();
                try {
                    cal.setTime(formato.parse(fecha));
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                int year = cal.get(Calendar.YEAR);
                if (!anyos.contains(String.valueOf(new Integer(year))))
                    anyos.add(String.valueOf(new Integer(year)));
            }
            spAnyosInforme.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, anyos));

            mSpinnerCount++;
            spAnyosInforme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (mSpinnerInitializedCount < mSpinnerCount)
                    {
                        mSpinnerInitializedCount++;
                    }
                    else
                        filtraInformeMesAnyo(view, (String)spTipoMovimiento.getSelectedItem(), new Integer((String) spAnyosInforme.getSelectedItem()).intValue());
                }
                public void onNothingSelected(AdapterView<?> parent) {
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    spAnyosInforme.setSelection(year);
                }
            });

            //spinner movimiento
            ArrayList<String> tiposMovimientos = new ArrayList<String>();
            tiposMovimientos.add(Constantes.TIPO_FILTRO_RESETEO.toString());
            tiposMovimientos.add(Constantes.TIPO_MOVIMIENTO_INGRESO.toString());
            tiposMovimientos.add(Constantes.TIPO_MOVIMIENTO_GASTO.toString());
            spTipoMovimiento = (Spinner) findViewById(R.id.spTipoMovimiento);
            spTipoMovimiento.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tiposMovimientos));
            mSpinnerCount++;
            spTipoMovimiento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (mSpinnerInitializedCount < mSpinnerCount)
                    {
                        mSpinnerInitializedCount++;
                    }
                    else
                    filtraInformeMesAnyo(view, (String)spTipoMovimiento.getSelectedItem(), new Integer((String) spAnyosInforme.getSelectedItem()).intValue());
                }
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            //cargamos adaptador de informes
            listViewMovsInforme = (ListView) findViewById(R.id.listViewMovInforme);

            //load resumen
            int mesActual = Calendar.getInstance().get(Calendar.MONTH);
            int anyoActal = Calendar.getInstance().get(Calendar.YEAR);
            Double ingresos = new Double(0.00);
            Double gastos = new Double(0.00);
            Double saldo = new Double(0.00);

            for(MovimientoItem mov : movs){
                String fecha = mov.getFecha();
                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                Calendar cal  = Calendar.getInstance();
                try {
                    cal.setTime(formato.parse(fecha));
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                int mesMovimiento = cal.get(Calendar.MONTH);
                int anyoMovimiento = cal.get(Calendar.YEAR);
                if (mov.getTipoMovimiento().equals(Constantes.TIPO_MOVIMIENTO_GASTO)
                        && mesMovimiento == mesActual && anyoActal == anyoMovimiento)
                    gastos += Double.valueOf(mov.getValor());
                else if (mov.getTipoMovimiento().equals(Constantes.TIPO_MOVIMIENTO_INGRESO)
                        && mesMovimiento == mesActual && anyoActal == anyoMovimiento)
                    ingresos += Double.valueOf(mov.getValor());
            }

            ArrayList<InformeItem> informes = new ArrayList<InformeItem>();

            InformeItem informe = new InformeItem();
            informe.setIngresoValor(String.valueOf(ingresos));
            informe.setGastoValor(String.valueOf(gastos));
            saldo = ingresos - gastos;
            informe.setTotalValor(String.valueOf(saldo));
            informe.setPeriodoDesc(new DateFormatSymbols().getMonths()[mesActual]);

            informes.add(informe);

            listViewMovsInforme.setAdapter(new InformeAdapter(getApplicationContext(), informes));
        }

    }

    public void filtraInformeMesAnyo(View v, String tipoFiltro, int anyo){
        ((MovimientoAdapter)listViewMovsInforme.getAdapter()).setAnyoSeleccionado(anyo);
        ((MovimientoAdapter)listViewMovsInforme.getAdapter()).getFilter().filter(tipoFiltro);
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
