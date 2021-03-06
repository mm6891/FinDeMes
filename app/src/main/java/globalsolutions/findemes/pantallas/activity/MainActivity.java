package globalsolutions.findemes.pantallas.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.GrupoGastoDAO;
import globalsolutions.findemes.database.dao.GrupoIngresoDAO;
import globalsolutions.findemes.database.dao.MovimientoDAO;
import globalsolutions.findemes.database.model.GrupoGasto;
import globalsolutions.findemes.database.model.GrupoIngreso;
import globalsolutions.findemes.database.model.MovimientoItem;
import globalsolutions.findemes.database.util.MyDatabaseHelper;
import globalsolutions.findemes.pantallas.dialog.NuevaVersionDialog;
import globalsolutions.findemes.pantallas.util.Util;


public class MainActivity extends Activity {

    //botonera menu
    private Button btnGasto;
    private Button btnIngreso;
    private Button btnMovimientos;
    private Button btnInformes;
    private Button btnMovimientosFrecuentes;
    private Button btnOpciones;
    private GridLayout gv;

    private ImageButton btnCalc;

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

        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;

        for (int i = 0; i < gv.getChildCount(); i++)
        {
            //anchura
            Button row = (Button)gv.getChildAt(i);
            row.setWidth(anchoBoton);
        }

        //load resumen
        //recuperamos movimientos
        final ArrayList<MovimientoItem> movs = new MovimientoDAO().cargaMovimientos(getApplicationContext());

        DecimalFormat df = new DecimalFormat("#.00");
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(false);

        int mesActual = Calendar.getInstance().get(Calendar.MONTH);
        int anyoActal = Calendar.getInstance().get(Calendar.YEAR);
        BigDecimal ingresos = new BigDecimal(0.00);
        ingresos = ingresos.setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal gastos = new BigDecimal(0.00);
        gastos = gastos.setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal saldo = new BigDecimal(0.00);

        for(MovimientoItem mov : movs){
            String fecha = mov.getFecha();
            Calendar cal  = Calendar.getInstance();
            try {
                cal.setTime(Util.formatoFechaActual().parse(fecha));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            int mesMovimiento = cal.get(Calendar.MONTH);
            int anyoMovimiento = cal.get(Calendar.YEAR);
            if (mov.getTipoMovimiento().equals(getResources().getString(R.string.TIPO_MOVIMIENTO_GASTO))
                    && mesMovimiento == mesActual && anyoActal == anyoMovimiento) {
                BigDecimal bdGasto;
                try {
                    bdGasto = new BigDecimal(df.parse(mov.getValor()).toString());
                    gastos = gastos.add(bdGasto);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else if (mov.getTipoMovimiento().equals(getResources().getString(R.string.TIPO_MOVIMIENTO_INGRESO))
                    && mesMovimiento == mesActual && anyoActal == anyoMovimiento) {
                BigDecimal bdIngreso;
                try {
                    bdIngreso =  new BigDecimal(df.parse(mov.getValor()).toString());
                    ingresos = ingresos.add(bdIngreso);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        tvIngresosValor = (TextView) findViewById(R.id.tvIngresosValor);
        tvIngresosValor.setText(df.format(ingresos) + Util.formatoMoneda(getApplicationContext()));
        tvGastosValor = (TextView) findViewById(R.id.tvGastosValor);
        tvGastosValor.setText(df.format(gastos) + Util.formatoMoneda(getApplicationContext()));
        saldo = ingresos.subtract(gastos);
        saldo = saldo.setScale(2, BigDecimal.ROUND_DOWN);
        tvSaldo = (TextView) findViewById(R.id.tvSaldoValor);
        tvSaldo.setText(df.format(saldo) + Util.formatoMoneda(getApplicationContext()));
        tvMes = (TextView) findViewById(R.id.tvMesResumen);
        tvMes.setText(new DateFormatSymbols().getMonths()[mesActual].toUpperCase());

        //enlazamos calculadora
        btnCalc = (ImageButton) findViewById(R.id.btnCalc);
        btnCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCalculator();
            }
        });

        //novedades de cada version
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        String version = prefs.getString("version102",null);
        if(version == null){
            SharedPreferences.Editor edit = prefs.edit();
            edit.remove("version102");
            edit.putString("version102", "version102");
            edit.commit();
            //mostramos el dialog
            DialogFragment newFragment = new NuevaVersionDialog();
            newFragment.show(getFragmentManager(), "NUEVA VERSION");
        }
    }

    public void CreaRegistros(){
        if(!MyDatabaseHelper.checkDataBase(getApplicationContext())) {
            //insercion BBDD
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(getApplicationContext());
            //GASTOS Y CATEGORIAS DE GASTOS
            GrupoGasto facturas = new GrupoGasto();
            facturas.setGrupo(getResources().getString(R.string.Grupo_facturas));
            GrupoGasto alimentacion = new GrupoGasto();
            alimentacion.setGrupo(getResources().getString(R.string.Grupo_alimentacion));
            GrupoGasto hipoteca = new GrupoGasto();
            hipoteca.setGrupo(getResources().getString(R.string.Grupo_hipoteca));
            GrupoGasto alquiler = new GrupoGasto();
            alquiler.setGrupo(getResources().getString(R.string.Grupo_alquiler));
            GrupoGasto automocion = new GrupoGasto();
            automocion.setGrupo(getResources().getString(R.string.Grupo_automocion));
            GrupoGasto vacaciones = new GrupoGasto();
            vacaciones.setGrupo(getResources().getString(R.string.Grupo_vacaciones));
            GrupoGasto familia = new GrupoGasto();
            familia.setGrupo(getResources().getString(R.string.Grupo_familia));
            GrupoGasto extra = new GrupoGasto();
            extra.setGrupo(getResources().getString(R.string.Grupo_extra));

            GrupoGastoDAO grupoGastoDAO = new GrupoGastoDAO(getApplicationContext());
            grupoGastoDAO.createRecords(facturas);
            grupoGastoDAO.createRecords(alimentacion);
            grupoGastoDAO.createRecords(hipoteca);
            grupoGastoDAO.createRecords(alquiler);
            grupoGastoDAO.createRecords(automocion);
            grupoGastoDAO.createRecords(vacaciones);
            grupoGastoDAO.createRecords(familia);
            grupoGastoDAO.createRecords(extra);

            //INGRESOS Y CATEGORIAS DE INGRESOS
            GrupoIngreso nomina = new GrupoIngreso();
            nomina.setGrupo(getResources().getString(R.string.Grupo_nomina));
            GrupoIngreso prestamo = new GrupoIngreso();
            prestamo.setGrupo(getResources().getString(R.string.Grupo_prestamo));
            GrupoIngreso ventas = new GrupoIngreso();
            ventas.setGrupo(getResources().getString(R.string.Grupo_ventas));
            GrupoIngreso iextra = new GrupoIngreso();
            iextra.setGrupo(getResources().getString(R.string.Grupo_extra));
            GrupoIngresoDAO grupoIngresoDAO = new GrupoIngresoDAO(getApplicationContext());
            grupoIngresoDAO.createRecords(nomina);
            grupoIngresoDAO.createRecords(prestamo);
            grupoIngresoDAO.createRecords(ventas);
            grupoIngresoDAO.createRecords(iextra);

            // Don't forget to close database connection
            dbHelper.close();
        }
    }

    private static final String CALCULATOR_PACKAGE_NAME = "com.android.calculator2";
    private static final String CALCULATOR_CLASS_NAME = "com.android.calculator2.Calculator";

    public void launchCalculator() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(CALCULATOR_PACKAGE_NAME,
                CALCULATOR_CLASS_NAME));
        try {
            this.startActivity(intent);
        } catch (ActivityNotFoundException noSuchActivity) {
            // handle exception where calculator intent filter is not registered
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.Salir))
                .setMessage(getResources().getString(R.string.Abandonar))
                .setNegativeButton(getResources().getString(R.string.NEGACION), null)
                .setPositiveButton(getResources().getString(R.string.AFIRMACION), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(MainActivity.this, Password.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                    }
                }).create().show();
    }
}
