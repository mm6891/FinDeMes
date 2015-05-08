package globalsolutions.findemes.pantallas.activity;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.GastoDAO;
import globalsolutions.findemes.database.dao.GrupoGastoDAO;
import globalsolutions.findemes.database.dao.GrupoIngresoDAO;
import globalsolutions.findemes.database.dao.IngresoDAO;
import globalsolutions.findemes.database.dao.MovimientoDAO;
import globalsolutions.findemes.database.model.MovimientoItem;
import globalsolutions.findemes.database.util.ArrayAdapterWithIcon;
import globalsolutions.findemes.database.util.Constantes;
import globalsolutions.findemes.pantallas.adapter.MovimientoAdapter;
import globalsolutions.findemes.pantallas.dialog.GastoDialog;
import globalsolutions.findemes.pantallas.dialog.IngresoDialog;
import globalsolutions.findemes.pantallas.util.Util;

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
            ((MovimientoAdapter)listViewMovs.getAdapter()).setAnyoSeleccionado(devuelveAnyo());

            if(!((CheckBox) findViewById(R.id.cbIconMinus)).isChecked() && ((CheckBox) findViewById(R.id.cbIconPlus)).isChecked())
                ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(getResources().getString(R.string.TIPO_MOVIMIENTO_INGRESO));
            else if(((CheckBox) findViewById(R.id.cbIconMinus)).isChecked() && !((CheckBox) findViewById(R.id.cbIconPlus)).isChecked())
                ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(getResources().getString(R.string.TIPO_MOVIMIENTO_GASTO));
            else
                ((MovimientoAdapter)listViewMovs.getAdapter()).getFilter().filter(getResources().getString(R.string.TIPO_FILTRO_RESETEO));
        }
    }

    public void actualizarFiltroCategoria() {
        if(!((CheckBox) findViewById(R.id.cbIconMinus)).isChecked() && ((CheckBox) findViewById(R.id.cbIconPlus)).isChecked())
            ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(getResources().getString(R.string.TIPO_MOVIMIENTO_INGRESO));
        else if(((CheckBox) findViewById(R.id.cbIconMinus)).isChecked() && !((CheckBox) findViewById(R.id.cbIconPlus)).isChecked())
            ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(getResources().getString(R.string.TIPO_MOVIMIENTO_GASTO));
    }

    private ListView listViewMovs;
    private Spinner spFiltroMes;
    private Spinner spFitroAnyo;
    private Spinner spFiltroCategoria;

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
            Util.showToast(getApplicationContext(), getResources().getString(R.string.No_Movimientos));
        /*else{*/
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
        anyos.add(getResources().getString(R.string.TIPO_FILTRO_RESETEO));
        spFitroAnyo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, anyos));
        mSpinnerCount++;
        spFitroAnyo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSpinnerInitializedCount < mSpinnerCount)
                {
                    mSpinnerInitializedCount++;
                }
                else {
                    filtraMesAnyo(view, spFiltroMes.getSelectedItemPosition(), devuelveAnyo());
                }
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
        String[] meses = creaMeses();
        spFiltroMes.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, meses));
        mSpinnerCount++;
        spFiltroMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSpinnerInitializedCount < mSpinnerCount)
                {
                    mSpinnerInitializedCount++;
                }
                else {
                    filtraMesAnyo(view, position, devuelveAnyo());
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                int month = Calendar.getInstance().get(Calendar.MONTH);
                spFiltroMes.setSelection(month);
            }
        });

        //filtro categoria del movimiento
        spFiltroCategoria = (Spinner) findViewById(R.id.spCategoriaMovimiento);
        spFiltroCategoria.setEnabled(false);

        spFiltroMes.setSelection(meses.length - 1);
        spFitroAnyo.setSelection(anyos.size() - 1);

        listViewMovs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position,
                                    long id) {

                final MovimientoItem movSeleccionado = (MovimientoItem) listViewMovs.getItemAtPosition(position);
                if (!movSeleccionado.isEsFrecuente()) {
                    final String[] items = {getResources().getString(R.string.Modificar), getResources().getString(R.string.Eliminar)};

                    AlertDialog.Builder builder = new AlertDialog.Builder(MovimientosActivity.this);

                    ListAdapter adapter = new ArrayAdapterWithIcon(getApplicationContext(), items, Util.prgmImagesOption);
                    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    //Eliminar Movimiento
                                    String accion = (String) items[item];
                                    boolean realizado;

                                    if (accion.equals(getResources().getString(R.string.Eliminar))) {
                                        if (movSeleccionado.getTipoMovimiento().trim().equals(getResources().getString(R.string.TIPO_MOVIMIENTO_GASTO))) {
                                            GastoDAO gastoDAO = new GastoDAO(MovimientosActivity.this);
                                            realizado = gastoDAO.deleteGasto(movSeleccionado.get_id());
                                            if (realizado) {
                                                Util.showToast(getApplicationContext(), getResources().getString(R.string.Eliminado));
                                                actualizarFiltro(String.valueOf(Activity.RESULT_OK));
                                            } else
                                                Util.showToast(getApplicationContext(), getResources().getString(R.string.No_Eliminado));
                                        }
                                        if (movSeleccionado.getTipoMovimiento().trim().equals(getResources().getString(R.string.TIPO_MOVIMIENTO_INGRESO))) {
                                            IngresoDAO ingresoDAO = new IngresoDAO(MovimientosActivity.this);
                                            realizado = ingresoDAO.deleteIngreso(movSeleccionado.get_id());
                                            if (realizado) {
                                                Util.showToast(getApplicationContext(), getResources().getString(R.string.Eliminado));
                                                actualizarFiltro(String.valueOf(Activity.RESULT_OK));
                                            } else
                                                Util.showToast(getApplicationContext(), getResources().getString(R.string.No_Eliminado));
                                        }
                                    }
                                    if (accion.equals(getResources().getString(R.string.Modificar))) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("_id", String.valueOf(movSeleccionado.get_id()));
                                        bundle.putString("valor", movSeleccionado.getValor());
                                        bundle.putString("descripcion", movSeleccionado.getDescripcion());
                                        bundle.putString("categoria", movSeleccionado.getCategoria());
                                        bundle.putString("fecha", movSeleccionado.getFecha());

                                        if (movSeleccionado.getTipoMovimiento().trim().equals(getResources().getString(R.string.TIPO_MOVIMIENTO_GASTO))) {
                                            // Create an instance of the dialog fragment and show it*/
                                            showGastoDialog(view, bundle);
                                        } else if (movSeleccionado.getTipoMovimiento().trim().equals(getResources().getString(R.string.TIPO_MOVIMIENTO_INGRESO))) {
                                            // Create an instance of the dialog fragment and show it*/
                                            showIngresoDialog(view, bundle);
                                        }
                                    }
                                }
                            }
                    ).show();
                }
                else
                    Util.showToast(getApplicationContext(), getResources().getString(R.string.MovimientoFrecuente));
            }
        });
        //}
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
        //activamos combo categorias
        mSpinnerCount++;
        spFiltroCategoria.setOnItemSelectedListener(new categoriaOnItemSelectedListener());
        ((MovimientoAdapter) listViewMovs.getAdapter()).setCategoriaSeleccionada(null);
        GrupoGastoDAO grupoGastoDAO = new GrupoGastoDAO(getApplicationContext());
        String[] categoriasGastos = grupoGastoDAO.selectGruposFilter(getApplicationContext());
        List<String> listCategorias = Arrays.asList(categoriasGastos);
        ArrayAdapter<String> dataAdapterCat = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item, listCategorias);
        dataAdapterCat.setDropDownViewResource(R.layout.spinner_item);
        spFiltroCategoria.setAdapter(dataAdapterCat);
        spFiltroCategoria.setSelection(listCategorias.size() - 1);

        int mes = spFiltroMes != null ? spFiltroMes.getSelectedItemPosition() : Constantes.NUMERO_ANYO_TODO;
        int anyo = devuelveAnyo();
        ((MovimientoAdapter)listViewMovs.getAdapter()).setMesSeleccionado(mes);
        ((MovimientoAdapter)listViewMovs.getAdapter()).setAnyoSeleccionado(anyo);

        ((CheckBox) findViewById(R.id.cbIconPlus)).setChecked(false);
        spFiltroCategoria.setEnabled(((CheckBox) findViewById(R.id.cbIconMinus)).isChecked());
        if(!((CheckBox) findViewById(R.id.cbIconMinus)).isChecked())
            ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(getResources().getString(R.string.TIPO_FILTRO_RESETEO));
        else
            ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(getResources().getString(R.string.TIPO_MOVIMIENTO_GASTO));
    }
    public void filtraIngreso(View v){
        //activamos combo categorias
        mSpinnerCount++;
        spFiltroCategoria.setOnItemSelectedListener(new categoriaOnItemSelectedListener());
        ((MovimientoAdapter) listViewMovs.getAdapter()).setCategoriaSeleccionada(null);
        GrupoIngresoDAO grupoIngresoDAO = new GrupoIngresoDAO(getApplicationContext());
        String[] categoriasIngresos = grupoIngresoDAO.selectGruposFilter(getApplicationContext());
        List<String> listCategorias = Arrays.asList(categoriasIngresos);
        ArrayAdapter<String> dataAdapterCat = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item, listCategorias);
        dataAdapterCat.setDropDownViewResource(R.layout.spinner_item);
        spFiltroCategoria.setAdapter(dataAdapterCat);
        spFiltroCategoria.setSelection(listCategorias.size() - 1);

        int mes = spFiltroMes != null ? spFiltroMes.getSelectedItemPosition() : Constantes.NUMERO_ANYO_TODO;
        int anyo = devuelveAnyo();
        ((MovimientoAdapter)listViewMovs.getAdapter()).setMesSeleccionado(mes);
        ((MovimientoAdapter)listViewMovs.getAdapter()).setAnyoSeleccionado(anyo);

        ((CheckBox) findViewById(R.id.cbIconMinus)).setChecked(false);
        spFiltroCategoria.setEnabled(((CheckBox) findViewById(R.id.cbIconPlus)).isChecked());
        if(!((CheckBox) findViewById(R.id.cbIconPlus)).isChecked())
            ((MovimientoAdapter)listViewMovs.getAdapter()).getFilter().filter(getResources().getString(R.string.TIPO_FILTRO_RESETEO));
        else
            ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(getResources().getString(R.string.TIPO_MOVIMIENTO_INGRESO));
    }

    public void filtraMesAnyo(View v, int mes, int anyo){
        ((MovimientoAdapter)listViewMovs.getAdapter()).setMesSeleccionado(mes);
        ((MovimientoAdapter)listViewMovs.getAdapter()).setAnyoSeleccionado(anyo);

        if(!((CheckBox) findViewById(R.id.cbIconMinus)).isChecked() && ((CheckBox) findViewById(R.id.cbIconPlus)).isChecked())
            ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(getResources().getString(R.string.TIPO_MOVIMIENTO_INGRESO));
        else if(((CheckBox) findViewById(R.id.cbIconMinus)).isChecked() && !((CheckBox) findViewById(R.id.cbIconPlus)).isChecked())
            ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(getResources().getString(R.string.TIPO_MOVIMIENTO_GASTO));
        else
            ((MovimientoAdapter)listViewMovs.getAdapter()).getFilter().filter(getResources().getString(R.string.TIPO_FILTRO_RESETEO));
    }

    public String[] creaMeses(){
        String[] meses = new String[Constantes.NUMERO_MES_TODO + 1];
        int indiceFinal = meses.length - 1;
        for(int i = 0 ; i < Constantes.NUMERO_MES_TODO ; i++){
            meses[i] = new DateFormatSymbols().getMonths()[i].toUpperCase();
        }
        meses[indiceFinal] = getResources().getString(R.string.TIPO_FILTRO_RESETEO);
        return meses;
    }

    public int devuelveAnyo(){
        int anyoSpinner = Constantes.NUMERO_ANYO_TODO;
        try {
            anyoSpinner = spFitroAnyo.getSelectedItem() != null ? new Integer((String) spFitroAnyo.getSelectedItem()).intValue() : Constantes.NUMERO_ANYO_TODO;
        }
        catch (Exception ex){
            //filtro all
            anyoSpinner = Constantes.NUMERO_ANYO_TODO;
        }
        return anyoSpinner;
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

    private class categoriaOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int pos,
                                   long id) {
            if (mSpinnerInitializedCount < mSpinnerCount)
            {
                mSpinnerInitializedCount++;
            }
            else {
                if(((String) spFiltroCategoria.getSelectedItem()).equals(getResources().getString(R.string.TIPO_FILTRO_RESETEO)))
                    ((MovimientoAdapter) listViewMovs.getAdapter()).setCategoriaSeleccionada(null);
                else
                    ((MovimientoAdapter) listViewMovs.getAdapter()).setCategoriaSeleccionada((String) spFiltroCategoria.getSelectedItem());

                actualizarFiltroCategoria();
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
}
