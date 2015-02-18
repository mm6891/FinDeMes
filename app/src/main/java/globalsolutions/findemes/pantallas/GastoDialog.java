package globalsolutions.findemes.pantallas;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.GastoDAO;
import globalsolutions.findemes.database.dao.GrupoGastoDAO;
import globalsolutions.findemes.database.model.Gasto;
import globalsolutions.findemes.database.model.GrupoGasto;

/**
 * Created by manuel.molero on 16/02/2015.
 */
public class GastoDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_gasto, container, false);

        //cargamos el combo de categorias
        Spinner categoria = (Spinner) view.findViewById(R.id.spCategoriaGasto);

        List<String> list = new ArrayList<String>();
        GrupoGastoDAO grupoGastoDAO = new GrupoGastoDAO(view.getContext());
        String[] categoriasGastos = grupoGastoDAO.selectGrupos();
        list = Arrays.asList(categoriasGastos);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(dataAdapter);

        //se cargan las propiedades del item seleccionado
        ((EditText) view.findViewById(R.id.txtGasto)).setText(getArguments().getString("valor"));
        ((EditText) view.findViewById(R.id.txtDecripcion)).setText(getArguments().getString("descripcion"));
         int spinnerPostion = dataAdapter.getPosition(getArguments().getString("categoria"));
        categoria.setSelection(spinnerPostion);
        ((TextView) view.findViewById(R.id.tvDia)).setText(getArguments().getString("fecha").split(" ")[0]);
        ((TextView) view.findViewById(R.id.tvHora)).setText(getArguments().getString("fecha").split(" ")[1]);

        Button btnModificar = (Button) view.findViewById(R.id.btnGuardarGasto);

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //descripcion , valor , fecha
                String valor = (String)((EditText) view.findViewById(R.id.txtGasto)).getText().toString();
                if(valor == null || valor.isEmpty()) {
                    ((EditText) v.findViewById(R.id.txtGasto)).setError("Debe incluir la cantidad del movimiento");
                    return;
                }
                String descripcion = (String)((EditText) view.findViewById(R.id.txtDecripcion)).getText().toString();
                if(descripcion == null || descripcion.isEmpty()) {
                    ((EditText) view.findViewById(R.id.txtDecripcion)).setError("Debe incluir una breve descripción del movimiento");
                    return;
                }
                //obtenemos categoria de gasto
                String categoriaGasto = (String)((Spinner) view.findViewById(R.id.spCategoriaGasto)).getSelectedItem();
                if(categoriaGasto != null && !categoriaGasto.isEmpty()) {
                    Gasto nuevoGasto = new Gasto();
                    nuevoGasto.setDescripcion(descripcion);
                    nuevoGasto.setValor(valor);
                    String fecha = (String) ((TextView) view.findViewById(R.id.tvDia)).getText();
                    String hora = (String) ((TextView) view.findViewById(R.id.tvHora)).getText();
                    nuevoGasto.setFecha(fecha + " " + hora);

                    GrupoGasto grupo = new GrupoGasto();
                    grupo.setGrupo(categoriaGasto);
                    nuevoGasto.setGrupoGasto(grupo);
                    GastoDAO gastoDAO = new GastoDAO(view.getContext());
                    boolean existeGasto = gastoDAO.existeGasto(nuevoGasto);
                    if(existeGasto)
                        gastoDAO.updateGasto(nuevoGasto);
                    else
                        gastoDAO.createRecords(nuevoGasto);
                    showToast(view.getContext(),"¡Gasto guardado!");
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

}
