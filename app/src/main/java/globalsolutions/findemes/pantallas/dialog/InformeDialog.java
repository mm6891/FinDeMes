package globalsolutions.findemes.pantallas.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.GrupoGastoDAO;
import globalsolutions.findemes.database.dao.GrupoIngresoDAO;
import globalsolutions.findemes.database.dao.RegistroDAO;
import globalsolutions.findemes.database.model.Registro;
import globalsolutions.findemes.database.util.Constantes;
import globalsolutions.findemes.pantallas.util.Util;

/**
 * Created by manuel.molero on 16/02/2015.
 */
public class InformeDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.informe_dialog, container, false);

        //se cargan las propiedades del item seleccionado
        String periodo = getArguments().getString("periodo");
        String tipo = getArguments().getString("tipo");
        String ingresos = getArguments().getString("ingresos");
        String gastos = getArguments().getString("gastos");
        String total = getArguments().getString("total");

        ((TextView) view.findViewById(R.id.tvDetalleInformePeriodo)).setText(periodo);
        ((TextView) view.findViewById(R.id.tvDetalleTipoInforme)).setText(tipo);
        ((TextView) view.findViewById(R.id.tvDetalleIngresosInforme)).setText(ingresos);
        ((TextView) view.findViewById(R.id.tvDetalleGastosInforme)).setText(gastos);
        ((TextView) view.findViewById(R.id.tvDetalleTotalInforme)).setText(total);

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
