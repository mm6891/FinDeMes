package globalsolutions.findemes.pantallas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.GrupoGastoDAO;
import globalsolutions.findemes.database.dao.GrupoIngresoDAO;
import globalsolutions.findemes.database.model.GrupoGasto;
import globalsolutions.findemes.database.model.GrupoIngreso;
import globalsolutions.findemes.database.util.MyDatabaseHelper;


public class MainActivity extends Activity {

    //botonera menu
    private Button btnGasto;
    private Button btnIngreso;
    private Button btnMovimientos;
    private Button btnInformes;
    private Button btnMovimientosFrecuentes;
    private Button btnOpciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //GASTO
        btnGasto = (Button) findViewById(R.id.imgBtn00);
        btnGasto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GastoActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //INGRESO
        btnIngreso = (Button) findViewById(R.id.imgBtn01);
        btnIngreso.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IngresoActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //MOVIMIENTOS
        btnMovimientos = (Button) findViewById(R.id.imgBtn10);
        btnMovimientos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MovimientosActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void CreaRegistros(){
        if(!MyDatabaseHelper.checkDataBase(getApplicationContext())) {
            //insercion BBDD
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(getApplicationContext());
            //GASTOS Y CATEGORIAS DE GASTOS
            GrupoGasto facturas = new GrupoGasto();
            facturas.setGrupo("Facturas");
            GrupoGasto alimentacion = new GrupoGasto();
            alimentacion.setGrupo("Alimentacion");
            GrupoGasto inmueble = new GrupoGasto();
            inmueble.setGrupo("Inmuebles");
            GrupoGasto automocion = new GrupoGasto();
            automocion.setGrupo("Automoción");
            GrupoGasto familia = new GrupoGasto();
            familia.setGrupo("Familia");
            GrupoGasto extra = new GrupoGasto();
            extra.setGrupo("Extra");

            GrupoGastoDAO grupoGastoDAO = new GrupoGastoDAO(getApplicationContext());
            grupoGastoDAO.createRecords(facturas);
            grupoGastoDAO.createRecords(alimentacion);
            grupoGastoDAO.createRecords(inmueble);
            grupoGastoDAO.createRecords(automocion);
            grupoGastoDAO.createRecords(familia);
            grupoGastoDAO.createRecords(extra);

            //INGRESOS Y CATEGORIAS DE INGRESOS
            GrupoIngreso nomina = new GrupoIngreso();
            nomina.setGrupo("Nómina");
            GrupoIngreso prestamo = new GrupoIngreso();
            prestamo.setGrupo("Prestamo");
            GrupoIngreso iextra = new GrupoIngreso();
            iextra.setGrupo("Extra");
            GrupoIngresoDAO grupoIngresoDAO = new GrupoIngresoDAO(getApplicationContext());
            grupoIngresoDAO.createRecords(nomina);
            grupoIngresoDAO.createRecords(prestamo);
            grupoIngresoDAO.createRecords(iextra);

            // Don't forget to close database connection
            dbHelper.close();
        }
    }
}
