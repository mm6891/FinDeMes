package globalsolutions.findemes.pantallas.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.MovimientoDAO;
import globalsolutions.findemes.database.model.InformeItem;
import globalsolutions.findemes.database.model.MovimientoItem;
import globalsolutions.findemes.database.util.ArrayAdapterWithIcon;
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

        //String[] periodos = new String[]{getResources().getString(R.string.TIPO_FILTRO_INFORME_DIARIO,getResources().getString(R.string.TIPO_FILTRO_INFORME_SEMANAL),getResources().getString(R.string.TIPO_FILTRO_INFORME_QUINCENAL),getResources().getString(R.string.TIPO_FILTRO_INFORME_MENSUAL),getResources().getString(R.string.TIPO_FILTRO_INFORME_TRIMESTRAL))};
        List<String> periodos = new ArrayList<String>();
        periodos.add(getResources().getString(R.string.TIPO_FILTRO_INFORME_DIARIO));
        periodos.add(getResources().getString(R.string.TIPO_FILTRO_INFORME_SEMANAL));
        periodos.add(getResources().getString(R.string.TIPO_FILTRO_INFORME_QUINCENAL));
        periodos.add(getResources().getString(R.string.TIPO_FILTRO_INFORME_MENSUAL));
        periodos.add(getResources().getString(R.string.TIPO_FILTRO_INFORME_TRIMESTRAL));
        periodos.add(getResources().getString(R.string.TIPO_FILTRO_INFORME_ANUAL));

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
            Calendar cal  = Calendar.getInstance();
            try {
                cal.setTime(Util.formatoFechaActual().parse(fecha));
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

        if (savedInstanceState != null) {
            spTipoMovimiento.setSelection(savedInstanceState.getInt("spTipoMovimiento", 0));
            spPeriodo.setSelection(savedInstanceState.getInt("spPeriodo", 0));
            spPeriodoFiltro.setSelection(savedInstanceState.getInt("spPeriodoFiltro", 0));
        }

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
        //btnGraficar.setEnabled(false);

        btnGraficar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {getResources().getString(R.string.OpcionGrafica_Lineal), getResources().getString(R.string.OpcionGrafica_Barra)};
                AlertDialog.Builder builder = new AlertDialog.Builder(InformesActivity.this);

                ListAdapter adapter = new ArrayAdapterWithIcon(getApplicationContext(), items, Util.prgmImagesOption);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                Intent intent = new Intent(InformesActivity.this, OptionActivityBarChart.class);
                                String accion = (String) items[item];

                                ArrayList<InformeItem> informes = ((InformeAdapter)listViewMovsInforme.getAdapter()).getItemsActuales();
                                int count = informes.size();
                                double[] valoresIngresos = new double[count];
                                double[] valoresGastos = new double[count];
                                String[] ejeX = new String[informes.size()];
                                for(int i = 0 ; i < count ; i++){
                                    valoresIngresos[i] = Double.valueOf(informes.get(i).getIngresoValor());
                                    valoresGastos[i] = Double.valueOf(informes.get(i).getGastoValor());
                                    int valorX = count - i;
                                    ejeX[valorX-1] = informes.get(i).getPeriodoDesc();
                                }

                                intent.putExtra("tipoGrafica" , accion);
                                intent.putExtra("anyo" , (String)spPeriodoFiltro.getSelectedItem());
                                intent.putExtra("periodo" , (String) spPeriodo.getSelectedItem());
                                intent.putExtra("ingresos" , valoresIngresos);
                                intent.putExtra("gastos" , valoresGastos);
                                intent.putExtra("ejeX" , ejeX);
                                startActivity(intent);
                            }
                        }
                ).show();
            }
        });

          listViewMovsInforme.setAdapter(new InformeAdapter(getApplicationContext(), new ArrayList<InformeItem>()));
        ((InformeAdapter)listViewMovsInforme.getAdapter()).setOnDataChangeListener(new InformeAdapter.OnDataChangeListener() {
            @Override
            public void onDataChanged(final ArrayList<InformeItem> informes) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //antes de cargar el resumen comprobamos boton informes
                        if(informes.size() > 0) {
                          /*  if (((String) spPeriodo.getSelectedItem()).equals(getResources().getString(R.string.TIPO_FILTRO_INFORME_TRIMESTRAL))
                                    && ((String) spTipoMovimiento.getSelectedItem()).equals(getResources().getString(R.string.TIPO_FILTRO_RESETEO))) {
                                btnGraficar.setEnabled(true);
                            } else
                                btnGraficar.setEnabled(false);*/

                            //load resumen
                            Double ingresos = new Double(0.00);
                            Double gastos = new Double(0.00);
                            Double saldo = new Double(0.00);

                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            //todos
                            if (((String) spTipoMovimiento.getSelectedItem()).equals(getResources().getString(R.string.TIPO_FILTRO_RESETEO))) {
                                for (InformeItem inf : informes) {
                                    ingresos += Double.valueOf(inf.getIngresoValor());
                                    gastos += Double.valueOf(inf.getGastoValor());
                                }
                                saldo = ingresos - gastos;

                                ((TextView) findViewById(R.id.tvIngresosInformesValor)).setText(String.valueOf(ingresos) + Util.formatoMoneda(getApplicationContext()));
                                TextView tvGastosTotal = (TextView) findViewById(R.id.tvGastosInformesValor);
                                tvGastosTotal.setText(String.valueOf(gastos) + Util.formatoMoneda(getApplicationContext()));
                                TextView tvSaldoTotal = (TextView) findViewById(R.id.tvSaldoInformesValor);
                                tvSaldoTotal.setText(String.valueOf(saldo) + Util.formatoMoneda(getApplicationContext()));
                            }
                            //tipo gasto
                            else if (((String) spTipoMovimiento.getSelectedItem()).equals(getResources().getString(R.string.TIPO_MOVIMIENTO_GASTO))) {
                                for (InformeItem inf : informes) {
                                    gastos += Double.valueOf(inf.getGastoValor());
                                }
                                saldo = ingresos - gastos;

                                ((TextView) findViewById(R.id.tvIngresosInformesValor)).setText(String.valueOf(ingresos));
                                TextView tvGastosTotal = (TextView) findViewById(R.id.tvGastosInformesValor);
                                tvGastosTotal.setText(String.valueOf(gastos) + Util.formatoMoneda(getApplicationContext()));
                                TextView tvSaldoTotal = (TextView) findViewById(R.id.tvSaldoInformesValor);
                                tvSaldoTotal.setText(String.valueOf(saldo) + Util.formatoMoneda(getApplicationContext()));
                            }
                            //tipo ingreso
                            else {
                                for (InformeItem inf : informes) {
                                    ingresos += Double.valueOf(inf.getIngresoValor());
                                }
                                saldo = ingresos - gastos;

                                ((TextView) findViewById(R.id.tvIngresosInformesValor)).setText(String.valueOf(ingresos) + Util.formatoMoneda(getApplicationContext()));
                                TextView tvGastosTotal = (TextView) findViewById(R.id.tvGastosInformesValor);
                                tvGastosTotal.setText(String.valueOf(gastos) + Util.formatoMoneda(getApplicationContext()));
                                TextView tvSaldoTotal = (TextView) findViewById(R.id.tvSaldoInformesValor);
                                tvSaldoTotal.setText(String.valueOf(saldo) + Util.formatoMoneda(getApplicationContext()));
                            }
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
        //finish();
    }

    public void showInformeDialog(Bundle bundle) {
        DialogFragment newFragment = new InformeDialog();
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "INFORME");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.remove("spTipoMovimiento");
        outState.remove("spPeriodo");
        outState.remove("spPeriodoFiltro");
        outState.putInt("spTipoMovimiento", spTipoMovimiento.getSelectedItemPosition());
        outState.putInt("spPeriodo", spPeriodo.getSelectedItemPosition());
        outState.putInt("spPeriodoFiltro", spPeriodoFiltro.getSelectedItemPosition());
        super.onSaveInstanceState(outState);
    }
}
