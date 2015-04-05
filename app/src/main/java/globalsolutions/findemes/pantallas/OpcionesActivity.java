package globalsolutions.findemes.pantallas;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.GrupoGastoDAO;
import globalsolutions.findemes.database.model.GrupoGasto;
import globalsolutions.findemes.database.model.OptionItem;


/**
 * Created by Manuel on 23/02/2015.
 */
public class OpcionesActivity extends Activity{

    public static final String CATEGORIAS_INGRESOS = "Categorías de ingresos";
    public static final String CATEGORIAS_GASTOS = "Categorías de gastos";
    public static final String FORMATO_MONEDA = "Formato de moneda";
    public static final String FORMATO_FECHA = "Formato de fecha y hora";
    public static final String BASEDATOS = "Base de datos";
    public static final String CONTRASEÑA = "Contraseña";
    public static final String CALIFICAR = "Calificar aplicación";
    public static final String ACERCA_APLICACION = "Acerca de la aplicación";
    public static final String FUNCIONES_PRO = "Acerca de las funciones PRO";
    public static final String DESARROLLO_CONTACTO = "Desarrollo y contacto";
    public static final String AYUDA = "Ayuda";

   public String[] options = {CATEGORIAS_INGRESOS, CATEGORIAS_GASTOS, FORMATO_MONEDA,FORMATO_FECHA, BASEDATOS,
           CONTRASEÑA,CALIFICAR,ACERCA_APLICACION,FUNCIONES_PRO,DESARROLLO_CONTACTO,AYUDA};

    public static int [] prgmImages={R.drawable.pluscategory,R.drawable.minusoption,R.drawable.dollaroption,
            R.drawable.calendaroption,R.drawable.databaseoption,R.drawable.padlockicon,R.drawable.staroption,
            R.drawable.appoption,R.drawable.prooption,R.drawable.developeroption,R.drawable.interrogationoption};

    private ListView listViewOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        //boton retroceder
        ImageButton btnReturn = (ImageButton) findViewById(R.id.btnBackButton);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                backActivity();
            }
        });
        ArrayList<OptionItem> opcionesArray = new ArrayList<OptionItem>(options.length);
        for(String op : options){
            OptionItem nuevo = new OptionItem();
            nuevo.setDescripcion(op);
            opcionesArray.add(nuevo);
        }

        // cargamos adaptador de opciones
        listViewOptions = (ListView) findViewById(R.id.listViewOptions);
        listViewOptions.setAdapter(new OptionAdapter(getApplicationContext(),opcionesArray,prgmImages));

        listViewOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position,long id) {
                OptionItem optSelected = (OptionItem) listViewOptions.getItemAtPosition(position);

                switch (optSelected.getDescripcion()){
                    case CATEGORIAS_INGRESOS:
                        showCategoriasIngresosDialog();
                        break;
                    case CATEGORIAS_GASTOS:
                        showCategoriasGastosDialog();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    private void backActivity(){
        Intent in = new Intent(OpcionesActivity.this, MainActivity.class);
        startActivity(in);
        setResult(RESULT_OK);
        finish();
    }

    public void anyadeGasto(String value){
        GrupoGasto nuevo = new GrupoGasto();
        nuevo.setGrupo(value);
        GrupoGastoDAO grupoGastoDAO = new GrupoGastoDAO(getApplicationContext());
        grupoGastoDAO.createRecords(nuevo);
    }

    public void showCategoriasIngresosDialog() {
        DialogFragment newFragment = new CategoriasIngresosDialog();
        newFragment.show(getFragmentManager(),"INGRESOS");
    }

    public void showCategoriasGastosDialog() {
        DialogFragment newFragment = new CategoriasIngresosDialog();
        newFragment.show(getFragmentManager(),"INGRESOS");
    }

}
