package globalsolutions.findemes.pantallas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.GastoDAO;
import globalsolutions.findemes.database.dao.IngresoDAO;
import globalsolutions.findemes.database.dao.MovimientoDAO;
import globalsolutions.findemes.database.model.MovimientoItem;
import globalsolutions.findemes.database.util.Constantes;

public class MovimientosActivity extends Activity {

    public enum Meses {
        ENERO, FEBRERO, MARZO, ABRIL,
        MAYO, JUNIO, JULIO, AGOSTO, SEPTIEMBRE, OCTUBRE, NOVIEMBRE, DICIEMBRE
    }

    private ListView listViewMovs;
    private Spinner spFiltroMes;
    private Spinner spFitroAnyo;

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
                java.util.Date d1 = null;
                Calendar tdy1;
                try {
                    d1 = formato.parse(fecha);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                tdy1 = Calendar.getInstance();
                int year = tdy1.get(Calendar.YEAR);
                if(!anyos.contains(String.valueOf(new Integer(year))))
                    anyos.add(String.valueOf(new Integer(year)));
            }
            spFitroAnyo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, anyos));
        }
        listViewMovs = (ListView) findViewById(R.id.listViewMov);
        listViewMovs.setAdapter(new MovimientoAdapter(getApplicationContext(), movs));
        //cargamos meses
        spFiltroMes = (Spinner) findViewById(R.id.spMeses);
        spFiltroMes.setAdapter(new ArrayAdapter<Meses>(this, android.R.layout.select_dialog_singlechoice, Meses.values()));
        spFiltroMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtraMes(view, position);
            }
            public void onNothingSelected(AdapterView<?> parent) {
                int month = Calendar.getInstance().get(Calendar.MONTH);
                //String mes = new DateFormatSymbols().getMonths()[month-1];
                spFiltroMes.setSelection(month);
            }
        });
        int month = Calendar.getInstance().get(Calendar.MONTH);
        spFiltroMes.setSelection(month);

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
                                    if (movSeleccionado.getTipoMovimiento().trim().equals("GASTO")) {
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
                                    if (movSeleccionado.getTipoMovimiento().trim().equals("INGRESO")) {
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
                                        /*ArrayList<MovimientoItem> newList = new MovimientoDAO().cargaMovimientos(getApplicationContext());
                                        ((MovimientoAdapter) listViewMovs.getAdapter()).updateReceiptsList(newList);*/
                                    }
                                    else if (movSeleccionado.getTipoMovimiento().trim().equals(Constantes.TIPO_MOVIMIENTO_INGRESO)) {
                                        // Create an instance of the dialog fragment and show it*/
                                        showIngresoDialog(view, bundle);
                                        /*ArrayList<MovimientoItem> newList = new MovimientoDAO().cargaMovimientos(getApplicationContext());
                                        ((MovimientoAdapter) listViewMovs.getAdapter()).updateReceiptsList(newList);*/
                                    }
                                }
                            }
                        }
                ).show();
            }
        });
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
        ((CheckBox) findViewById(R.id.cbIconPlus)).setChecked(false);
        if(!((CheckBox) findViewById(R.id.cbIconMinus)).isChecked())
            ((MovimientoAdapter)listViewMovs.getAdapter()).getFilter().filter(Constantes.TIPO_FILTRO_RESETEO);
        else
            ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(Constantes.TIPO_MOVIMIENTO_GASTO);
    }
    public void filtraIngreso(View v){
        ((CheckBox) findViewById(R.id.cbIconMinus)).setChecked(false);
        if(!((CheckBox) findViewById(R.id.cbIconPlus)).isChecked())
            ((MovimientoAdapter)listViewMovs.getAdapter()).getFilter().filter(Constantes.TIPO_FILTRO_RESETEO);
        else
            ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(Constantes.TIPO_MOVIMIENTO_INGRESO);
    }

    public void filtraMes(View v, int mes){
         ((MovimientoAdapter)listViewMovs.getAdapter()).setMesSeleccionado(mes);
        ((MovimientoAdapter)listViewMovs.getAdapter()).getFilter().filter(Constantes.TIPO_FILTRO_MES);
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
