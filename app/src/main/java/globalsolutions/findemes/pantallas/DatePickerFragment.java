package globalsolutions.findemes.pantallas;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

import globalsolutions.findemes.database.util.Constantes;

/**
 * Created by manuel.molero on 10/02/2015.
 */
public class DatePickerFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if(getArguments().getString("movimiento").equals(Constantes.TIPO_MOVIMIENTO_GASTO.toString()))
            return new DatePickerDialog(getActivity(), (GastoActivity)getActivity(), year, month, day);
        else
            return new DatePickerDialog(getActivity(), (IngresoActivity)getActivity(), year, month, day);
    }

}
