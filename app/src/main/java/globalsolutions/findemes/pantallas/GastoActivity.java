package globalsolutions.findemes.pantallas;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.GastoDAO;
import globalsolutions.findemes.database.dao.GrupoGastoDAO;
import globalsolutions.findemes.database.model.Gasto;
import globalsolutions.findemes.database.model.GrupoGasto;
import globalsolutions.findemes.database.util.Constantes;

public class GastoActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasto);

        //cargamos el combo de categorias
        Spinner categoria = (Spinner) findViewById(R.id.spCategoriaGasto);

        List<String> list = new ArrayList<String>();
        GrupoGastoDAO grupoGastoDAO = new GrupoGastoDAO(getApplicationContext());
        String[] categoriasGastos = grupoGastoDAO.selectGrupos();
        list = Arrays.asList(categoriasGastos);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(dataAdapter);

        //cargamos el modal para seleccionar fecha
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdfDia = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("kk:mm");
        String mTimeText = sdfDia.format(date);
        String mTimeHora = sdfHora.format(date);

        ((TextView) findViewById(R.id.tvDia)).setText(mTimeText);
        ((TextView) findViewById(R.id.tvHora)).setText(mTimeHora);

        ImageButton datePicker = (ImageButton) findViewById(R.id.myDatePickerButton);

        datePicker.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
    }


    //dialogos
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("movimiento", Constantes.TIPO_MOVIMIENTO_GASTO.toString());
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(),"Fecha");
    }

    public void showGastoDialog(View v, Bundle bundle) {
        DialogFragment newFragment = new GastoDialog();
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(),"MODIFICACION");
    }

    //eventos botones guardar gasto e ingreso
    public void guardarGasto(View view) {
        //descripcion , valor , fecha
        String valor = (String)((EditText) findViewById(R.id.txtGasto)).getText().toString();
        if(valor == null || valor.isEmpty()) {
            ((EditText) findViewById(R.id.txtGasto)).setError("Debe incluir la cantidad del movimiento");
            return;
        }
        String descripcion = (String)((EditText) findViewById(R.id.txtDecripcion)).getText().toString();
        if(descripcion == null || descripcion.isEmpty()) {
            ((EditText) findViewById(R.id.txtDecripcion)).setError("Debe incluir una breve descripción del movimiento");
            return;
        }
        //obtenemos categoria de gasto
        String categoriaGasto = (String)((Spinner) findViewById(R.id.spCategoriaGasto)).getSelectedItem();
        if(categoriaGasto != null && !categoriaGasto.isEmpty()) {
            Gasto nuevoGasto = new Gasto();
            nuevoGasto.setDescripcion(descripcion);
            nuevoGasto.setValor(valor);
            String fecha = (String) ((TextView) findViewById(R.id.tvDia)).getText();
            String hora = (String) ((TextView) findViewById(R.id.tvHora)).getText();
            nuevoGasto.setFecha(fecha + " " + hora);

            GrupoGasto grupo = new GrupoGasto();
            grupo.setGrupo(categoriaGasto);
            nuevoGasto.setGrupoGasto(grupo);
            GastoDAO gastoDAO = new GastoDAO(getApplicationContext());
            boolean existeGasto = gastoDAO.existeGasto(nuevoGasto);
            if(existeGasto)
                gastoDAO.updateGasto(nuevoGasto);
            else
                gastoDAO.createRecords(nuevoGasto);
            showToast("¡Gasto guardado!");
        }
        else{
            return;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        final Calendar c = Calendar.getInstance();
        c.set(year,month,day);

        Date date = new Date(c.getTimeInMillis());
        SimpleDateFormat sdfDia = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("kk:mm");
        String mTimeText = sdfDia.format(date);
        String mTimeHora = sdfHora.format(date);

        ((TextView) findViewById(R.id.tvDia)).setText(mTimeText);
        ((TextView) findViewById(R.id.tvHora)).setText(mTimeHora);
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
