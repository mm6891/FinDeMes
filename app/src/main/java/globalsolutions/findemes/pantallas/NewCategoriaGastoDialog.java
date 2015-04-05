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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.GrupoGastoDAO;
import globalsolutions.findemes.database.model.GrupoGasto;

/**
 * Created by manuel.molero on 16/02/2015.
 */
public class NewCategoriaGastoDialog extends DialogFragment {

    private OnCategoriaGastoDialogListener callback;

    public interface OnCategoriaGastoDialogListener {
        public void OnCategoriaGastoDialogSubmit(String result);
    }

    private Button btnNewCatGas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            callback = (OnCategoriaGastoDialogListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnCategoriaGastoDialogListener");
        }

        View view = inflater.inflate(R.layout.new_category_gasto_dialog, container, false);

        btnNewCatGas = (Button) view.findViewById(R.id.btnNewCatGas);

        btnNewCatGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descripcion = (String)((EditText) v.findViewById(R.id.txtNuevaCatGas)).getText().toString();
                if(descripcion == null || descripcion.isEmpty()) {
                    ((EditText) v.findViewById(R.id.txtNuevaCatGas)).setError("Debe incluir el nombre de la nueva categoría");
                    return;
                }

                GrupoGasto nuevoGrupo = new GrupoGasto();
                nuevoGrupo.setGrupo(descripcion);

                GrupoGastoDAO grupoDAO = new GrupoGastoDAO(v.getContext());
                boolean actualizado = grupoDAO.createRecords(nuevoGrupo) > 0;

                if(actualizado){
                    showToast(v.getContext(),"¡Categoria de Grupo creada!");
                    callback.OnCategoriaGastoDialogSubmit(String.valueOf(Activity.RESULT_OK));
                    dismiss();
                }
                else
                    showToast(v.getContext(),"¡No se ha podido crear!");
            }
        });

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