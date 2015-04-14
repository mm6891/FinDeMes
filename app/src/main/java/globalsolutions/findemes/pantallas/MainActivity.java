package globalsolutions.findemes.pantallas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.GrupoGastoDAO;
import globalsolutions.findemes.database.dao.GrupoIngresoDAO;
import globalsolutions.findemes.database.dao.MovimientoDAO;
import globalsolutions.findemes.database.model.GrupoGasto;
import globalsolutions.findemes.database.model.GrupoIngreso;
import globalsolutions.findemes.database.model.MovimientoItem;
import globalsolutions.findemes.database.util.Constantes;
import globalsolutions.findemes.database.util.MyDatabaseHelper;


public class MainActivity extends Activity {

    //botonera menu
    private Button btnGasto;
    private Button btnIngreso;
    private Button btnMovimientos;
    private Button btnInformes;
    private Button btnMovimientosFrecuentes;
    private Button btnOpciones;
    private GridLayout gv;

    //resumen
    private TextView tvIngresosValor;
    private TextView tvGastosValor;
    private TextView tvSaldo;
    private TextView tvMes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        CreaRegistros();

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
        btnMovimientos = (Button) findViewById(R.id.imgBtn02);
        btnMovimientos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MovimientosActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //INFORMES
        btnInformes = (Button) findViewById(R.id.imgBtn10);
        btnInformes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InformesActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //OPCIONES
        btnOpciones = (Button) findViewById(R.id.imgBtn12);
        btnOpciones.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OpcionesActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //REGISTROS FRECUENTES
        btnMovimientosFrecuentes = (Button) findViewById(R.id.imgBtn11);
        btnMovimientosFrecuentes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrosActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //tamanyo de gridlayout segun pantalla en pixeles
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        //margen establecido en main.xml como dp
        int margen = 10;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(margen * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        gv = (GridLayout) findViewById(R.id.glMenu);
        int anchoBoton = (width/2)  - (px*3);

        for (int i = 0; i < gv.getChildCount(); i++)
        {
            Button row = (Button)gv.getChildAt(i);
            row.setWidth(anchoBoton);
        }

        //load resumen
        //recuperamos movimientos
        final ArrayList<MovimientoItem> movs = new MovimientoDAO().cargaMovimientos(getApplicationContext());

        int mesActual = Calendar.getInstance().get(Calendar.MONTH);
        int anyoActal = Calendar.getInstance().get(Calendar.YEAR);
        Double ingresos = new Double(0.00);
        Double gastos = new Double(0.00);
        Double saldo = new Double(0.00);

        for(MovimientoItem mov : movs){
            String fecha = mov.getFecha();
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cal  = Calendar.getInstance();
            try {
                cal.setTime(formato.parse(fecha));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            int mesMovimiento = cal.get(Calendar.MONTH);
            int anyoMovimiento = cal.get(Calendar.YEAR);
            if (mov.getTipoMovimiento().equals(Constantes.TIPO_MOVIMIENTO_GASTO)
                    && mesMovimiento == mesActual && anyoActal == anyoMovimiento)
                gastos += Double.valueOf(mov.getValor());
            else if (mov.getTipoMovimiento().equals(Constantes.TIPO_MOVIMIENTO_INGRESO)
                    && mesMovimiento == mesActual && anyoActal == anyoMovimiento)
                ingresos += Double.valueOf(mov.getValor());
        }

        tvIngresosValor = (TextView) findViewById(R.id.tvIngresosValor);
        tvIngresosValor.setText(String.valueOf(ingresos));
        tvGastosValor = (TextView) findViewById(R.id.tvGastosValor);
        tvGastosValor.setText(String.valueOf(gastos));
        saldo = ingresos - gastos;
        tvSaldo = (TextView) findViewById(R.id.tvSaldoValor);
        tvSaldo.setText(String.valueOf(saldo));
        tvMes = (TextView) findViewById(R.id.tvMesResumen);
        tvMes.setText(new DateFormatSymbols().getMonths()[mesActual].toUpperCase());
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("¿Salir?")
                .setMessage("¿Esta seguro de abandonar la aplicación?")
                .setNegativeButton("NO", null)
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(MainActivity.this, Password.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                    }
                }).create().show();
    }
}
