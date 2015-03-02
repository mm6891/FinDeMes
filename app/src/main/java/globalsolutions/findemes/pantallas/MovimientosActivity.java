package globalsolutions.findemes.pantallas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.GastoDAO;
import globalsolutions.findemes.database.dao.IngresoDAO;
import globalsolutions.findemes.database.dao.MovimientoDAO;
import globalsolutions.findemes.database.model.MovimientoItem;
import globalsolutions.findemes.database.util.Constantes;

public class MovimientosActivity extends FragmentActivity implements GastoDialog.OnGastoDialogListener, IngresoDialog.OnIngresoDialogListener{

    @Override
    public void onGastoDialogSubmit(String result) {
        actualizarFiltro(result);
    }

    @Override
    public void onIngresoDialogSubmit(String result) {
        actualizarFiltro(result);
    }

    public void actualizarFiltro(String result) {
        if(result.equals(String.valueOf(Activity.RESULT_OK))){
            ((MovimientoAdapter)listViewMovs.getAdapter()).setMesSeleccionado(spFiltroMes.getSelectedItemPosition());
            ((MovimientoAdapter)listViewMovs.getAdapter()).setAnyoSeleccionado(new Integer((String) spFitroAnyo.getSelectedItem()).intValue());

            if(!((CheckBox) findViewById(R.id.cbIconMinus)).isChecked() && ((CheckBox) findViewById(R.id.cbIconPlus)).isChecked())
                ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(Constantes.TIPO_MOVIMIENTO_INGRESO);
            if(((CheckBox) findViewById(R.id.cbIconMinus)).isChecked() && !((CheckBox) findViewById(R.id.cbIconPlus)).isChecked())
                ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(Constantes.TIPO_MOVIMIENTO_GASTO);
            else
                ((MovimientoAdapter)listViewMovs.getAdapter()).getFilter().filter(Constantes.TIPO_FILTRO_RESETEO);
        }
    }

    public enum Meses {
        ENERO, FEBRERO, MARZO, ABRIL,
        MAYO, JUNIO, JULIO, AGOSTO, SEPTIEMBRE, OCTUBRE, NOVIEMBRE, DICIEMBRE
    }

    private ListView listViewMovs;
    private Spinner spFiltroMes;
    private Spinner spFitroAnyo;

    //this counts how many Spinner's are on the UI
    private int mSpinnerCount=0;

    //this counts how many Spinner's have been initialized
    private int mSpinnerInitializedCount=0;

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

        spFitroAnyo = (Spinner) findViewById(R.id.spAnyos);

        //recuperamos movimientos
        final ArrayList<MovimientoItem> movs = new MovimientoDAO().cargaMovimientos(getApplicationContext());
        //cargamos anyos
        ArrayList<String> anyos = new ArrayList<String>();
        if(movs.size() <= 0 )
            showToast("NO HAY MOVIMIENTOS ACTUALMENTE");
        else{
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
            spFitroAnyo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, anyos));
            mSpinnerCount++;
            spFitroAnyo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (mSpinnerInitializedCount < mSpinnerCount)
                    {
                        mSpinnerInitializedCount++;
                    }
                    else
                        filtraMesAnyo(view, spFiltroMes.getSelectedItemPosition(), new Integer((String) spFitroAnyo.getSelectedItem()).intValue());
                }
                public void onNothingSelected(AdapterView<?> parent) {
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    spFitroAnyo.setSelection(year);
                }
            });

            listViewMovs = (ListView) findViewById(R.id.listViewMov);
            listViewMovs.setAdapter(new MovimientoAdapter(getApplicationContext(), movs));
            //cargamos meses
            spFiltroMes = (Spinner) findViewById(R.id.spMeses);
            spFiltroMes.setAdapter(new ArrayAdapter<Meses>(this, android.R.layout.simple_spinner_dropdown_item, Meses.values()));

            spFiltroMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    filtraMesAnyo(view, position, new Integer((String) spFitroAnyo.getSelectedItem()).intValue());
                }
                public void onNothingSelected(AdapterView<?> parent) {
                    int month = Calendar.getInstance().get(Calendar.MONTH);
                    spFiltroMes.setSelection(month);
                }
            });
            spFiltroMes.setSelection(Calendar.getInstance().get(Calendar.MONTH));
            spFitroAnyo.setSelection(spFitroAnyo.getSelectedItemPosition());

            listViewMovs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position,
                                        long id) {

                    final MovimientoItem movSeleccionado = (MovimientoItem) listViewMovs.getItemAtPosition(position);
                    final CharSequence[] items = {Constantes.ACCION_MODIFICAR, Constantes.ACCION_ELIMINAR};

                    AlertDialog.Builder builder = new AlertDialog.Builder(MovimientosActivity.this);
                    builder.setTitle("OPCIONES");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    //Eliminar Movimiento
                                    String accion = (String) items[item];
                                    boolean realizado;

                                    if (accion.equals(Constantes.ACCION_ELIMINAR)) {
                                        if (movSeleccionado.getTipoMovimiento().trim().equals(Constantes.TIPO_MOVIMIENTO_GASTO)) {
                                            GastoDAO gastoDAO = new GastoDAO(MovimientosActivity.this);
                                            realizado = gastoDAO.deleteGasto(movSeleccionado.getDescripcion(), movSeleccionado.getValor(),
                                                    movSeleccionado.getFecha());
                                            if (realizado) {
                                                showToast("¡Gasto eliminado!");
                                                ArrayList<MovimientoItem> newList = new MovimientoDAO().cargaMovimientos(getApplicationContext());
                                                ((MovimientoAdapter) listViewMovs.getAdapter()).updateReceiptsList(newList);
                                            } else
                                                showToast("No se ha podido eliminar el gasto");
                                        }
                                        if (movSeleccionado.getTipoMovimiento().trim().equals(Constantes.TIPO_MOVIMIENTO_INGRESO)) {
                                            IngresoDAO ingresoDAO = new IngresoDAO(MovimientosActivity.this);
                                            realizado = ingresoDAO.deleteIngreso(movSeleccionado.getDescripcion(), movSeleccionado.getValor(),
                                                    movSeleccionado.getFecha());
                                            if (realizado) {
                                                showToast("¡Ingreso eliminado!");
                                                ArrayList<MovimientoItem> newList = new MovimientoDAO().cargaMovimientos(getApplicationContext());
                                                ((MovimientoAdapter) listViewMovs.getAdapter()).updateReceiptsList(newList);
                                            } else
                                                showToast("No se ha podido eliminar el ingreso");
                                        }
                                    }
                                    if (accion.equals(Constantes.ACCION_MODIFICAR)) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("valor", movSeleccionado.getValor());
                                        bundle.putString("descripcion", movSeleccionado.getDescripcion());
                                        bundle.putString("categoria", movSeleccionado.getCategoria());
                                        bundle.putString("fecha", movSeleccionado.getFecha());

                                        if (movSeleccionado.getTipoMovimiento().trim().equals(Constantes.TIPO_MOVIMIENTO_GASTO)){
                                            // Create an instance of the dialog fragment and show it*/
                                            showGastoDialog(view, bundle);
                                        }
                                        else if (movSeleccionado.getTipoMovimiento().trim().equals(Constantes.TIPO_MOVIMIENTO_INGRESO)) {
                                            // Create an instance of the dialog fragment and show it*/
                                            showIngresoDialog(view, bundle);
                                        }
                                    }
                                }
                            }
                    ).show();
                }
            });
        }
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showGastoDialog(View v, Bundle bundle) {
        DialogFragment newFragment = new GastoDialog();
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(),"MODIFICACION");
    }

    public void showIngresoDialog(View v, Bundle bundle) {
        DialogFragment newFragment = new IngresoDialog();
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(),"MODIFICACION");
    }

    //eventos click filtro gasto e ingreso
    public void filtraGasto(View v){
        int mes = spFiltroMes.getSelectedItemPosition();
        int anyo = new Integer((String) spFitroAnyo.getSelectedItem()).intValue();
        ((MovimientoAdapter)listViewMovs.getAdapter()).setMesSeleccionado(mes);
        ((MovimientoAdapter)listViewMovs.getAdapter()).setAnyoSeleccionado(anyo);

        ((CheckBox) findViewById(R.id.cbIconPlus)).setChecked(false);
        if(!((CheckBox) findViewById(R.id.cbIconMinus)).isChecked())
            ((MovimientoAdapter)listViewMovs.getAdapter()).getFilter().filter(Constantes.TIPO_FILTRO_RESETEO);
        else
            ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(Constantes.TIPO_MOVIMIENTO_GASTO);
    }
    public void filtraIngreso(View v){
         int mes = spFiltroMes.getSelectedItemPosition();
         int anyo = new Integer((String) spFitroAnyo.getSelectedItem()).intValue();
        ((MovimientoAdapter)listViewMovs.getAdapter()).setMesSeleccionado(mes);
        ((MovimientoAdapter)listViewMovs.getAdapter()).setAnyoSeleccionado(anyo);

        ((CheckBox) findViewById(R.id.cbIconMinus)).setChecked(false);
        if(!((CheckBox) findViewById(R.id.cbIconPlus)).isChecked())
            ((MovimientoAdapter)listViewMovs.getAdapter()).getFilter().filter(Constantes.TIPO_FILTRO_RESETEO);
        else
            ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(Constantes.TIPO_MOVIMIENTO_INGRESO);
    }

    public void filtraMesAnyo(View v, int mes, int anyo){
        ((MovimientoAdapter)listViewMovs.getAdapter()).setMesSeleccionado(mes);
        ((MovimientoAdapter)listViewMovs.getAdapter()).setAnyoSeleccionado(anyo);

        if(!((CheckBox) findViewById(R.id.cbIconMinus)).isChecked() && ((CheckBox) findViewById(R.id.cbIconPlus)).isChecked())
            ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(Constantes.TIPO_MOVIMIENTO_INGRESO);
        if(((CheckBox) findViewById(R.id.cbIconMinus)).isChecked() && !((CheckBox) findViewById(R.id.cbIconPlus)).isChecked())
            ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(Constantes.TIPO_MOVIMIENTO_GASTO);
        else
            ((MovimientoAdapter)listViewMovs.getAdapter()).getFilter().filter(Constantes.TIPO_FILTRO_RESETEO);
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    private void backActivity(){
        Intent in = new Intent(MovimientosActivity.this, MainActivity.class);
        startActivity(in);
        setResult(RESULT_OK);
        finish();
    }
}
