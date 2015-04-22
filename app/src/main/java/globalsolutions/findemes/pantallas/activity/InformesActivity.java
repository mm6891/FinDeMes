package globalsolutions.findemes.pantallas.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.MovimientoDAO;
import globalsolutions.findemes.database.model.InformeItem;
import globalsolutions.findemes.database.model.MovimientoItem;
import globalsolutions.findemes.pantallas.adapter.InformeAdapter;
import globalsolutions.findemes.pantallas.dialog.InformeDialog;
import globalsolutions.findemes.pantallas.util.Util;

import static android.app.PendingIntent.getActivity;

/**
 * Created by Manuel on 23/02/2015.
 */
public class InformesActivity extends Activity {

    /*public enum Periodos{
        MENSUAL, TRIMESTRAL, QUINCENAL
    }*/


    private Spinner spTipoMovimiento;
    private Spinner spPeriodo;
    private Spinner spPeriodoFiltro;

    private ListView listViewMovsInforme;
    private ImageButton btnGraficar;

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

        String[] periodos = new String[]{getResources().getString(R.string.TIPO_FILTRO_INFORME_MENSUAL),
                getResources().getString(R.string.TIPO_FILTRO_INFORME_TRIMESTRAL),getResources().getString(R.string.TIPO_FILTRO_INFORME_QUINCENAL)};

        //spinner periodo
        spPeriodo = (Spinner) findViewById(R.id.spPeriodo);
        spPeriodo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, periodos));

        mSpinnerCount++;
        spPeriodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSpinnerInitializedCount < mSpinnerCount)
                {
                    mSpinnerInitializedCount++;
                }
                else {
                    String periodoFiltro = spPeriodoFiltro.getSelectedItem() != null ? (String) spPeriodoFiltro.getSelectedItem() : "-1";
                    filtraInforme(view, (String) spTipoMovimiento.getSelectedItem(), (String) spPeriodo.getSelectedItem(), periodoFiltro);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spPeriodoFiltro = (Spinner) findViewById(R.id.spPeriodoFiltro);
        //lista movimientos
        //recuperamos movimientos
        final ArrayList<MovimientoItem> movs = new MovimientoDAO().cargaMovimientos(getApplicationContext());
        //cargamos anyos
        ArrayList<String> anyos = new ArrayList<String>();
        if(movs.size() <= 0 )
            Util.showToast(getApplicationContext(), getResources().getString(R.string.No_Informes));

        for(MovimientoItem mov : movs){
            String fecha = mov.getFecha();
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cal  = Calendar.getInstance();
            try {
                cal.setTime(formato.parse(fecha));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            int year = cal.get(Calendar.YEAR);
            if(!anyos.contains(String.valueOf(new Integer(year))))
                anyos.add(String.valueOf(new Integer(year)));
        }
        spPeriodoFiltro.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, anyos));

        mSpinnerCount++;
        spPeriodoFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSpinnerInitializedCount < mSpinnerCount)
                {
                    mSpinnerInitializedCount++;
                }
                else {
                    String periodoFiltro = spPeriodoFiltro.getSelectedItem() != null ? (String) spPeriodoFiltro.getSelectedItem() : "-1";
                    filtraInforme(view, (String) spTipoMovimiento.getSelectedItem(), (String) spPeriodo.getSelectedItem(), periodoFiltro);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //spinner movimiento
        final ArrayList<String> tiposMovimientos = new ArrayList<String>();
        tiposMovimientos.add(getResources().getString(R.string.TIPO_FILTRO_RESETEO));
        tiposMovimientos.add(getResources().getString(R.string.TIPO_MOVIMIENTO_INGRESO));
        tiposMovimientos.add(getResources().getString(R.string.TIPO_MOVIMIENTO_GASTO));
        spTipoMovimiento = (Spinner) findViewById(R.id.spTipoMovimiento);
        spTipoMovimiento.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tiposMovimientos));
        spTipoMovimiento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String periodoFiltro = spPeriodoFiltro.getSelectedItem() != null ? (String) spPeriodoFiltro.getSelectedItem() : "-1";
                filtraInforme(view, (String)spTipoMovimiento.getSelectedItem(), (String) spPeriodo.getSelectedItem(), periodoFiltro);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //cargamos adaptador de informes
        listViewMovsInforme = (ListView) findViewById(R.id.listViewMovInforme);
        listViewMovsInforme.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position,
                                        long id) {
                    final InformeItem itemSeleccionado = (InformeItem) listViewMovsInforme.getItemAtPosition(position);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("informe", (android.os.Parcelable) itemSeleccionado);

                    showInformeDialog(bundle);
                }
            });
        btnGraficar = (ImageButton) findViewById(R.id.btnGraficar);
        btnGraficar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InformesActivity.this, OptionActivityBarChart.class);
                intent.putExtra("anyo" , (String)spPeriodoFiltro.getSelectedItem());

                ArrayList<InformeItem> informes = ((InformeAdapter)listViewMovsInforme.getAdapter()).getItemsActuales();
                double[] valoresIngresos = new double[informes.size()];
                double[] valoresGastos = new double[informes.size()];
                String[] ejeX = new String[informes.size()];
                for(int i = 0 ; i < informes.size() ; i++){
                    valoresIngresos[i] = Double.valueOf(informes.get(i).getIngresoValor());
                    valoresGastos[i] = Double.valueOf(informes.get(i).getGastoValor());
                    ejeX[i] = informes.get(i).getPeriodoDesc();
                }

                intent.putExtra("ingresos" , valoresIngresos);
                intent.putExtra("gastos" , valoresGastos);
                intent.putExtra("ejeX" , ejeX);
                startActivity(intent);
                finish();
            }
        });

          listViewMovsInforme.setAdapter(new InformeAdapter(getApplicationContext(), new ArrayList<InformeItem>()));
        ((InformeAdapter)listViewMovsInforme.getAdapter()).setOnDataChangeListener(new InformeAdapter.OnDataChangeListener() {
            @Override
            public void onDataChanged(final ArrayList<InformeItem> informes) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //load resumen
                        Double ingresos = new Double(0.00);
                        Double gastos = new Double(0.00);
                        Double saldo = new Double(0.00);

                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                        //todos
                        if(((String)spTipoMovimiento.getSelectedItem()).equals(getResources().getString(R.string.TIPO_FILTRO_RESETEO))){
                            for(InformeItem inf : informes){
                                ingresos += Double.valueOf(inf.getIngresoValor());
                                gastos += Double.valueOf(inf.getGastoValor());
                            }
                            saldo = ingresos - gastos;

                            ((TextView) findViewById(R.id.tvIngresosInformesValor)).setText(String.valueOf(ingresos));
                            TextView tvGastosTotal = (TextView) findViewById(R.id.tvGastosInformesValor);
                            tvGastosTotal.setText(String.valueOf(gastos));
                            TextView tvSaldoTotal = (TextView) findViewById(R.id.tvSaldoInformesValor);
                            tvSaldoTotal.setText(String.valueOf(saldo));
                        }
                        //tipo gasto
                        else if(((String)spTipoMovimiento.getSelectedItem()).equals(getResources().getString(R.string.TIPO_MOVIMIENTO_GASTO))){
                            for(InformeItem inf : informes){
                                gastos += Double.valueOf(inf.getGastoValor());
                            }
                            saldo = ingresos - gastos;

                            ((TextView) findViewById(R.id.tvIngresosInformesValor)).setText(String.valueOf(ingresos));
                            TextView tvGastosTotal = (TextView) findViewById(R.id.tvGastosInformesValor);
                            tvGastosTotal.setText(String.valueOf(gastos));
                            TextView tvSaldoTotal = (TextView) findViewById(R.id.tvSaldoInformesValor);
                            tvSaldoTotal.setText(String.valueOf(saldo));
                        }
                        //tipo ingreso
                        else {
                            for(InformeItem inf : informes){
                                ingresos += Double.valueOf(inf.getIngresoValor());
                            }
                            saldo = ingresos - gastos;

                            ((TextView) findViewById(R.id.tvIngresosInformesValor)).setText(String.valueOf(ingresos));
                            TextView tvGastosTotal = (TextView) findViewById(R.id.tvGastosInformesValor);
                            tvGastosTotal.setText(String.valueOf(gastos));
                            TextView tvSaldoTotal = (TextView) findViewById(R.id.tvSaldoInformesValor);
                            tvSaldoTotal.setText(String.valueOf(saldo));
                        }
                    }
                });
            }
        });
    }

    public void filtraInforme(View v, String tipoMovimiento, String periodo, String periodoFiltro){
        String filtro = tipoMovimiento + ";" + periodo + ";" + periodoFiltro;
        ((InformeAdapter)listViewMovsInforme.getAdapter()).getFilter().filter(filtro);
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

    public void showInformeDialog(Bundle bundle) {
        DialogFragment newFragment = new InformeDialog();
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(),"INFORME");
    }

}
