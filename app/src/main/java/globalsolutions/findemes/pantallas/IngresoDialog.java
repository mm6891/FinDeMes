package globalsolutions.findemes.pantallas;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import globalsolutions.findemes.R;

/**
 * Created by manuel.molero on 16/02/2015.
 */
public class IngresoDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ingreso_list_item, container, false);

        //se cargan las propiedades del item seleccionado
        ((EditText) view.findViewById(R.id.txtIngreso)).setText(getArguments().getString("valor"));
        ((EditText) view.findViewById(R.id.txtDecripcion)).setText(getArguments().getString("descripcion"));
        //((Spinner) view.findViewById(R.id.spCategoriaGasto)).setText(getArguments().getString("descripcion"));
        //int spinnerPostion = dataAdapter.getPosition(movSeleccionado.getCategoria());
        ((TextView) view.findViewById(R.id.tvDia)).setText(getArguments().getString("fecha").split(" ")[0]);
        ((TextView) view.findViewById(R.id.tvHora)).setText(getArguments().getString("fecha").split(" ")[1]);

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

}
