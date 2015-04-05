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
import globalsolutions.findemes.database.dao.GrupoIngresoDAO;
import globalsolutions.findemes.database.model.GrupoIngreso;

/**
 * Created by manuel.molero on 16/02/2015.
 */
public class NewCategoriaIngresoDialog extends DialogFragment {

    private OnCategoriaIngresoDialogListener callback;

    public interface OnCategoriaIngresoDialogListener {
        public void OnCategoriaIngresoDialogSubmit(String result);
    }

    private Button btnNewCatIng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            callback = (OnCategoriaIngresoDialogListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnCategoriaIngresoDialogListener");
        }

        View view = inflater.inflate(R.layout.new_category_ingreso_dialog, container, false);

        btnNewCatIng = (Button) view.findViewById(R.id.btnNewCatIng);

        btnNewCatIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descripcion = (String)((EditText) v.findViewById(R.id.txtNuevaCatIng)).getText().toString();
                if(descripcion == null || descripcion.isEmpty()) {
                    ((EditText) v.findViewById(R.id.txtNuevaCatIng)).setError("Debe incluir el nombre de la nueva categoría");
                    return;
                }

                GrupoIngreso nuevoGrupo = new GrupoIngreso();
                nuevoGrupo.setGrupo(descripcion);

                GrupoIngresoDAO grupoDAO = new GrupoIngresoDAO(v.getContext());
                boolean actualizado = grupoDAO.createRecords(nuevoGrupo) > 0;

                if(actualizado){
                    showToast(v.getContext(),"¡Categoria de Grupo creada!");
                    callback.OnCategoriaIngresoDialogSubmit(String.valueOf(Activity.RESULT_OK));
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
