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
public class EditCategoriaIngresoDialog extends DialogFragment {

    private EditText txtDecripcionCatIng;
    private Button btnModCatIng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.edit_category_ingreso_dialog, container, false);

        txtDecripcionCatIng = (EditText) view.findViewById(R.id.txtDecripcionCatIng);
        txtDecripcionCatIng.setText(getArguments().getString("nameCategory"));

        final GrupoIngreso aMod = new GrupoIngreso();
        aMod.setGrupo(getArguments().getString("nameCategory"));

        btnModCatIng = (Button) view.findViewById(R.id.btnModCatIng);

        btnModCatIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descripcion = (String)((EditText) view.findViewById(R.id.txtDecripcionCatIng)).getText().toString();
                if(descripcion == null || descripcion.isEmpty()) {
                    ((EditText) view.findViewById(R.id.txtDecripcionCatIng)).setError("Debe incluir el nombre de la categoría");
                    return;
                }

                GrupoIngreso nuevoGrupo = new GrupoIngreso();
                nuevoGrupo.setGrupo(descripcion);

                GrupoIngresoDAO grupoDAO = new GrupoIngresoDAO(v.getContext());
                boolean actualizado = grupoDAO.updateGrupoIngreso(aMod,nuevoGrupo);
                if(actualizado){
                    showToast(view.getContext(),"¡Categoria de Grupo actualizada!");
                    ((CategoriasIngresosDialog)getTargetFragment()).OnCategoriaIngresoDialogSubmit(String.valueOf(Activity.RESULT_OK));
                    dismiss();
                }
                else
                    showToast(view.getContext(),"¡No se ha podido actualizar!");
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