package globalsolutions.findemes.pantallas;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
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
import globalsolutions.findemes.database.dao.GrupoIngresoDAO;
import globalsolutions.findemes.database.dao.IngresoDAO;
import globalsolutions.findemes.database.model.GrupoIngreso;
import globalsolutions.findemes.database.model.Ingreso;
import globalsolutions.findemes.database.util.Constantes;

public class IngresoActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);

        //cargamos el combo de categorias
        Spinner categoria = (Spinner) findViewById(R.id.spCategoriaIngreso);

        List<String> list = new ArrayList<String>();
        GrupoIngresoDAO grupoIngresoDAO = new GrupoIngresoDAO(getApplicationContext());
        String[] categoriasIngresos = grupoIngresoDAO.selectGrupos();
        list = Arrays.asList(categoriasIngresos);

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
        bundle.putString("movimiento", Constantes.TIPO_MOVIMIENTO_INGRESO.toString());
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(),"Fecha");
    }

    public void showGastoDialog(View v, Bundle bundle) {
        DialogFragment newFragment = new GastoDialog();
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(),"MODIFICACION");
    }

    public void guardarIngreso(View view) {
        //descripcion , valor , fecha
        String valor = (String)((EditText) findViewById(R.id.txtIngreso)).getText().toString();
        if(valor == null || valor.isEmpty()) {
            ((EditText) findViewById(R.id.txtIngreso)).setError("Debe incluir la cantidad del movimiento");
            return;
        }
        String descripcion = (String)((EditText) findViewById(R.id.txtDecripcion)).getText().toString();
        if(descripcion == null || descripcion.isEmpty()) {
            ((EditText) findViewById(R.id.txtDecripcion)).setError("Debe incluir una breve descripción del movimiento");
            return;
        }
        //obtenemos categoria de ingreso
        String categoriaIngreso = (String)((Spinner) findViewById(R.id.spCategoriaIngreso)).getSelectedItem();
        if(categoriaIngreso != null && !categoriaIngreso.isEmpty()) {
            Ingreso nuevoIngreso = new Ingreso();
            nuevoIngreso.setDescripcion(descripcion);
            nuevoIngreso.setValor(valor);
            String fecha = (String) ((TextView) findViewById(R.id.tvDia)).getText();
            String hora = (String) ((TextView) findViewById(R.id.tvHora)).getText();
            nuevoIngreso.setFecha(fecha + " " + hora);

            GrupoIngreso grupo = new GrupoIngreso();
            grupo.setGrupo(categoriaIngreso);
            nuevoIngreso.setGrupoIngreso(grupo);
            IngresoDAO ingresoDAO = new IngresoDAO(getApplicationContext());
            boolean existeIngeso = ingresoDAO.existeIngreso(nuevoIngreso);
            if(existeIngeso)
                ingresoDAO.updateIngreso(nuevoIngreso);
            else
                ingresoDAO.createRecords(nuevoIngreso);
            showToast("¡Ingreso guardado!");
        }
        else{
            showToast("Debe seleccionar una categoría");
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
