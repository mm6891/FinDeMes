package globalsolutions.findemes.pantallas;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import globalsolutions.findemes.database.dao.GrupoIngresoDAO;
import globalsolutions.findemes.database.dao.IngresoDAO;
import globalsolutions.findemes.database.model.Gasto;
import globalsolutions.findemes.database.model.GrupoGasto;
import globalsolutions.findemes.database.model.GrupoIngreso;
import globalsolutions.findemes.database.model.Ingreso;
import globalsolutions.findemes.database.model.MovimientoItem;
import globalsolutions.findemes.database.util.MyDatabaseHelper;


public class MainActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener {

    private final String MENU_GASTOS = "Gasto";
    private final String MENU_INGRESOS = "Ingreso";
    private final String MENU_MOVIMIENTOS = "Movimientos";
    private final String MENU_INFORMES = "Informes";
    private final String MENU_REGISTROS = "Movimientos frecuentes";
	private final String MENU_OPCIONES = "Opciones";

    public final String TIPO_MOVIMIENTO_GASTO = "GASTO";
    public final String TIPO_MOVIMIENTO_INGRESO = "INGRESO";

    public final String ACCION_ELIMINAR = "Eliminar";
    public final String ACCION_MODIFICAR = "Modificar";

    private ListView listView;
    private RelativeLayout relativeLayout;
    private Spinner categoria;
    private Button btnGuardar;
    private ListView listViewMovs;
    private ImageButton datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //CreaRegistros();
        listView = (ListView) findViewById(R.id.listView);

        List items = new ArrayList();
        items.add(new ClipData.Item(MENU_GASTOS));
        items.add(new ClipData.Item(MENU_INGRESOS));
        items.add(new ClipData.Item(MENU_MOVIMIENTOS));
        items.add(new ClipData.Item(MENU_INFORMES));
        items.add(new ClipData.Item(MENU_REGISTROS));
		items.add(new ClipData.Item(MENU_OPCIONES));

        // Sets the data behind this ListView
        listView.setAdapter(new ItemAdapter(this, items));

        //cargamos categorias gasto inicialmente, al iniciar la app
        addCategoriasGasto();
        setFechaHora();

        // Register a callback to be invoked when an item in this AdapterView
        // has been clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                ClipData.Item itemSeleccionado = (ClipData.Item) listView.getItemAtPosition(position);
                if(itemSeleccionado.getText().equals(MENU_INGRESOS)){
                    int optionId = R.layout.ingreso_list_item;
                    modificaMenu(optionId);
                    //cargamos categorias ingreso
                    addCategoriasIngreso();
                    btnGuardar = (Button) findViewById(R.id.btnGuardarIngreso);
                    setFechaHora();
                }

                if(itemSeleccionado.getText().equals(MENU_GASTOS)){
                    int optionId = R.layout.gasto_list_item;
                    modificaMenu(optionId);
                    //cargamos categorias gasto
                    addCategoriasGasto();
                    btnGuardar = (Button) findViewById(R.id.btnGuardarGasto);
                    setFechaHora();
                }

                if(itemSeleccionado.getText().equals(MENU_MOVIMIENTOS)){
                    int optionId = R.layout.movimientos_list_item;
                    modificaMenu(optionId);
                    //recuperamos movimientos
                    final ArrayList<MovimientoItem> movs = cargaMovimientos();
                    if(movs.size() <= 0 )
                        showToast("NO HAY MOVIMIENTOS ACTUALMENTE");
                    listViewMovs = (ListView) findViewById(R.id.listViewMov);
                    listViewMovs.setAdapter(new MovimientoAdapter(getApplicationContext(), movs));
                    listViewMovs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                                long id) {

                            final MovimientoItem movSeleccionado = (MovimientoItem) listViewMovs.getItemAtPosition(position);
                            final CharSequence[] items = {ACCION_MODIFICAR, ACCION_ELIMINAR};

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("OPCIONES");
                            builder.setItems(items, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    //Eliminar Movimiento
                                    String accion = (String)items[item];
									boolean realizado;
                                    if(accion.equals(ACCION_ELIMINAR)) {
                                        if(movSeleccionado.getTipoMovimiento().trim().equals("GASTO")){
                                            GastoDAO gastoDAO = new GastoDAO(MainActivity.this);
                                            realizado = gastoDAO.deleteGasto(movSeleccionado.getDescripcion(), movSeleccionado.getValor(),
                                                    movSeleccionado.getFecha());
											if(realizado) {
                                                showToast("¡Gasto eliminado!");
                                                ArrayList<MovimientoItem> newList = cargaMovimientos();
                                                ((MovimientoAdapter)listViewMovs.getAdapter()).updateReceiptsList(newList);
                                            }
											else
                                                showToast("No se ha podido eliminar el gasto");
                                        }
                                        if(movSeleccionado.getTipoMovimiento().trim().equals("INGRESO")){
                                            IngresoDAO ingresoDAO = new IngresoDAO(MainActivity.this);
                                            realizado = ingresoDAO.deleteIngreso(movSeleccionado.getDescripcion(), movSeleccionado.getValor(),
                                                    movSeleccionado.getFecha());
											if(realizado) {
                                                showToast("¡Ingreso eliminado!");
                                                ArrayList<MovimientoItem> newList = cargaMovimientos();
                                                ((MovimientoAdapter)listViewMovs.getAdapter()).updateReceiptsList(newList);
                                            }
											else
                                                showToast("No se ha podido eliminar el ingreso");
                                        }
                                    }
                                }
                            }).show();
                        }
                    });
                }
            }
        });

    }

    public void showToast(String message){
        Toast.makeText(this, message,  Toast.LENGTH_SHORT).show();
    }

    public void setFechaHora(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdfDia = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("kk:mm");
        String mTimeText = sdfDia.format(date);
        String mTimeHora = sdfHora.format(date);

        ((TextView) findViewById(R.id.tvDia)).setText(mTimeText);
        ((TextView) findViewById(R.id.tvHora)).setText(mTimeHora);

        datePicker = (ImageButton) findViewById(R.id.myDatePickerButton);

        datePicker.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
    }

    public void modificaMenu(int option){
        View C = findViewById(R.id.relativeLayout);
        ViewGroup parentvg = (ViewGroup) C.getParent();
        int index = parentvg.indexOfChild(C);
        parentvg.removeView(C);
        C = getLayoutInflater().inflate(option, parentvg, false);
        parentvg.addView(C, index);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // add items into spinner dynamically
    public void addCategoriasGasto() {

        categoria = (Spinner) findViewById(R.id.spCategoriaGasto);

        List<String> list = new ArrayList<String>();
        GrupoGastoDAO grupoGastoDAO = new GrupoGastoDAO(getApplicationContext());
        String[] categoriasGastos = grupoGastoDAO.selectGrupos();
        list = Arrays.asList(categoriasGastos);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(dataAdapter);
    }

    // add items into spinner dynamically
    public void addCategoriasIngreso() {

        categoria = (Spinner) findViewById(R.id.spCategoriaIngreso);

        List<String> list = new ArrayList<String>();
        GrupoIngresoDAO grupoIngresoDAO = new GrupoIngresoDAO(getApplicationContext());
        String[] categoriasIngresos = grupoIngresoDAO.selectGrupos();
        list = Arrays.asList(categoriasIngresos);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(dataAdapter);
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
            GrupoGasto grupo = new GrupoGasto();
            grupo.setGrupo(categoriaGasto);
            nuevoGasto.setGrupoGasto(grupo);
            GastoDAO gastoDAO = new GastoDAO(getApplicationContext());
            gastoDAO.createRecords(nuevoGasto);
            showToast("¡Gasto guardado!");
        }
        else{
            return;
        }
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
            GrupoIngreso grupo = new GrupoIngreso();
            grupo.setGrupo(categoriaIngreso);
            nuevoIngreso.setGrupoIngreso(grupo);
            IngresoDAO ingresoDAO = new IngresoDAO(getApplicationContext());
            ingresoDAO.createRecords(nuevoIngreso);
            showToast("¡Ingreso guardado!");
        }
        else{
            showToast("Debe seleccionar una categoría");
            return;
        }
    }

    public ArrayList<MovimientoItem> cargaMovimientos(){
        MovimientoItem[] movsArray;
        Gasto[] gastos = new GastoDAO(this.getApplicationContext()).selectGastos();
        Ingreso[] ingresos = new IngresoDAO(this.getApplicationContext()).selectIngresos();
        movsArray = new MovimientoItem[gastos.length + ingresos.length];
        for(int i = 0 ; i < gastos.length ; i++){
            MovimientoItem m = new MovimientoItem();
            m.setValor(gastos[i].getValor());
            m.setDescripcion(gastos[i].getDescripcion());
            m.setFecha(gastos[i].getFecha());
            m.setCategoria(gastos[i].getGrupoGasto().getGrupo());
            m.setTipoMovimiento(TIPO_MOVIMIENTO_GASTO);
            movsArray[i] = m;
        }

        for(int j = 0 ; j < ingresos.length ; j++){
            MovimientoItem m = new MovimientoItem();
            m.setValor(ingresos[j].getValor());
            m.setDescripcion(ingresos[j].getDescripcion());
            m.setFecha(ingresos[j].getFecha());
            m.setCategoria(ingresos[j].getGrupoIngreso().getGrupo());
            m.setTipoMovimiento(TIPO_MOVIMIENTO_INGRESO);
            movsArray[gastos.length + j] = m;
        }

        return new ArrayList(Arrays.asList(movsArray));
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(),"Fecha");
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

    //eventos click filtro gasto e ingreso
    public void filtraGasto(View v){
        //si no esta chequeado mostramos todos los gastos
        boolean isChecked = ((CheckBox) findViewById(R.id.cbIconPlus)).isChecked();

        ((MovimientoAdapter) listViewMovs.getAdapter()).getFilter().filter(TIPO_MOVIMIENTO_GASTO);
    }
    public void filtraIngreso(View v){
        //si no esta chequeado mostramos todos los ingresos
        boolean isChecked = ((CheckBox) findViewById(R.id.cbIconMinus)).isChecked();

        ((MovimientoAdapter)listViewMovs.getAdapter()).getFilter().filter(TIPO_MOVIMIENTO_INGRESO);
    }
}
