package globalsolutions.findemes.pantallas;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.GrupoGastoDAO;
import globalsolutions.findemes.database.dao.GrupoIngresoDAO;
import globalsolutions.findemes.database.dao.RegistroDAO;
import globalsolutions.findemes.database.model.Registro;
import globalsolutions.findemes.database.util.Constantes;

/**
 * Created by manuel.molero on 16/02/2015.
 */
public class NuevoRegistroDialog extends DialogFragment {

    private ONuevoRegistroDialogListener callback;

    public interface ONuevoRegistroDialogListener {
        public void ONuevoRegistroDialogSubmit(String result);
    }

    private Spinner categoria;
    private Spinner tipoMovimiento;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            callback = (ONuevoRegistroDialogListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement ONuevoRegistroDialogListener");
        }

        final View view = inflater.inflate(R.layout.nuevo_registro_dialog, container, false);

        //cargamos el combo de periodicidad
        Spinner periodicidad = (Spinner) view.findViewById(R.id.spPeriodicidad);
        List<String> listPeriod = new ArrayList<String>();
        listPeriod.add(getResources().getString(R.string.PERIODICIDAD_REGISTRO_MENSUAL));
        listPeriod.add(getResources().getString(R.string.PERIODICIDAD_REGISTRO_ANUAL));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, listPeriod);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodicidad.setAdapter(dataAdapter);

        //cargamos el combo tipo (gasto o ingreso)
        tipoMovimiento = (Spinner) view.findViewById(R.id.spTipo);
        List<String> listTipos = new ArrayList<String>();
        listTipos.add(getResources().getString(R.string.TIPO_MOVIMIENTO_GASTO));
        listTipos.add(getResources().getString(R.string.TIPO_MOVIMIENTO_INGRESO));
        ArrayAdapter<String> dataAdapterTM = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, listTipos);
        dataAdapterTM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoMovimiento.setAdapter(dataAdapterTM);
        tipoMovimiento.setOnItemSelectedListener(new tipoMovimientoOnClickListener());

        //cargamos el combo de categorias
        categoria = (Spinner) view.findViewById(R.id.spCategoria);
        categoria.setEnabled(false);

        Button btnNuevoRegistro = (Button) view.findViewById(R.id.btnCrearRegistro);

        btnNuevoRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //nombre , valor
                String valor = (String)((EditText) view.findViewById(R.id.txtValor)).getText().toString();
                if(valor == null || valor.isEmpty()) {
                    ((EditText) v.findViewById(R.id.txtValor)).setError("Debe incluir la cantidad del registro");
                    return;
                }
                String descripcion = (String)((EditText) view.findViewById(R.id.txtRegistro)).getText().toString();
                if(descripcion == null || descripcion.isEmpty()) {
                    ((EditText) view.findViewById(R.id.txtRegistro)).setError("Debe incluir un nombre");
                    return;
                }

                //periodicidad
                String periodicidad = (String)((Spinner) view.findViewById(R.id.spPeriodicidad)).getSelectedItem();
                String tipoMovimiento = (String)((Spinner) view.findViewById(R.id.spPeriodicidad)).getSelectedItem();
                String categoria = (String)((Spinner) view.findViewById(R.id.spCategoria)).getSelectedItem();

                if(categoria != null && !categoria.isEmpty()) {
                    Registro nuevoRegistro = new Registro();
                    nuevoRegistro.setDescripcion(descripcion);
                    nuevoRegistro.setPeriodicidad(periodicidad);
                    nuevoRegistro.setTipo(tipoMovimiento);
                    nuevoRegistro.setGrupo(categoria);
                    Integer valueActivo = ((RadioButton)view.findViewById(R.id.rbActivo)).isChecked()
                            ? Integer.valueOf(Constantes.REGISTRO_ACTIVO.toString()) :
                            Integer.valueOf(Constantes.REGISTRO_INACTIVO.toString());
                    nuevoRegistro.setActivo(valueActivo);
                    nuevoRegistro.setValor(valor);

                    RegistroDAO registroDAO = new RegistroDAO(view.getContext());
                    boolean actualizado = registroDAO.createRecords(nuevoRegistro) > 0;
                    if(actualizado){
                        showToast(view.getContext(),"¡Registro creado!");
                        callback.ONuevoRegistroDialogSubmit(String.valueOf(Activity.RESULT_OK));
                        dismiss();
                    }
                    else
                        showToast(view.getContext(),"¡No se ha podido crear!");
                }
                else{
                    showToast(view.getContext(),"Debe seleccionar una categoría");
                    return;
                }
            }
        });

        // Inflate the layout to use as dialog or embedded fragment
        return view;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    public void showToast(Context c, String message){
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    private class tipoMovimientoOnClickListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int pos,
                                   long id) {

            parent.getItemAtPosition(pos);
            categoria.setEnabled(true);
            List<String> list = new ArrayList<String>();

            if(((String)(tipoMovimiento.getSelectedItem())).equals(getString(R.string.TIPO_MOVIMIENTO_GASTO))) {
                GrupoGastoDAO grupoGastoDAO = new GrupoGastoDAO(getView().getContext());
                String[] categoriasGastos = grupoGastoDAO.selectGrupos();
                list = Arrays.asList(categoriasGastos);
            }
            else {
                GrupoIngresoDAO grupoIngresoDAO = new GrupoIngresoDAO(getView().getContext());
                String[] categoriasIngresos = grupoIngresoDAO.selectGrupos();
                list = Arrays.asList(categoriasIngresos);
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getView().getContext(),
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categoria.setAdapter(dataAdapter);
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }
}
