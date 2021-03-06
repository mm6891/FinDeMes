package globalsolutions.findemes.pantallas.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import globalsolutions.findemes.R;
import globalsolutions.findemes.pantallas.util.Util;

/**
 * Created by manuel.molero on 16/02/2015.
 */
public class FormatoFechaDialog extends DialogFragment {

    private Spinner spFormatoFecha;
    private Button btnFormatoFecha;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.formato_dialog, container, false);

        spFormatoFecha = (Spinner) view.findViewById(R.id.spFormatoFecha);
        List<String> formatos = new ArrayList<String>();
        formatos.add("dd/MM/yyyy kk:mm");
        formatos.add("EEE, d MMM yyyy HH:mm");
        formatos.add("yyyy-MM-dd HH:mm");
       /* formatos.add("dd/MM/yyyy");
        formatos.add("EEE, d MMM yyyy");
        formatos.add("yyyy-MM-dd");*/


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, formatos);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFormatoFecha.setAdapter(dataAdapter);

        btnFormatoFecha = (Button) view.findViewById(R.id.btnFormatoFecha);

        btnFormatoFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs =
                        view.getContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("formato");
                editor.putString("formato", (String)spFormatoFecha.getSelectedItem());
                editor.commit();
                Util.showToast(view.getContext(),view.getResources().getString(R.string.Modificado));
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
}
