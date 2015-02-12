package globalsolutions.findemes.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import globalsolutions.findemes.database.model.Gasto;
import globalsolutions.findemes.database.model.GrupoIngreso;
import globalsolutions.findemes.database.model.Ingreso;
import globalsolutions.findemes.database.util.MyDatabaseHelper;

/**
 * Created by manuel.molero on 03/02/2015.
 */
public class IngresoDAO {
    private MyDatabaseHelper dbHelper;

    private SQLiteDatabase database;

    //tables´s name
    public final static String INGRESOS_TABLA="Ingresos";

    //columns table Ingresos
    public final static String INGRESOS_ID="_id"; // id value for gasto
    public final static String INGRESOS_DESC="descripcion";  // nombre del gasto
    public final static String INGRESOS_VALOR="valor";  // valor del gasto
    public final static String INGRESOS_FECHA="fecha";  // fecha del gasto
    public final static String INGRESOS_GRUPO="grupoingreso";  // grupo al que pertenece el gasto, referencia

    /**
     *
     * @param context
     */
    public IngresoDAO(Context context){
        dbHelper = new MyDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long createRecords(Ingreso ingreso){
        ContentValues values = new ContentValues();
        values.put(INGRESOS_DESC, ingreso.getDescripcion());
        values.put(INGRESOS_VALOR, ingreso.getValor());
        values.put(INGRESOS_GRUPO, ingreso.getGrupoIngreso().getGrupo());

        return database.insert(INGRESOS_TABLA, null, values);
    }

    public Ingreso[] selectIngresos() {
        Ingreso[] ret;
        String[] cols = new String[] {INGRESOS_GRUPO, INGRESOS_DESC, INGRESOS_VALOR,INGRESOS_FECHA};
        Cursor mCursor = database.query(true, INGRESOS_TABLA,cols,null
                , null, null, null, null, null);
        ret = new Ingreso[mCursor.getCount()];
        int i = 0;
        mCursor.moveToFirst();
        while (mCursor.isAfterLast() == false) {
            Ingreso nuevoIngreso = new Ingreso();
            GrupoIngreso categoria = new GrupoIngreso();
            categoria.setGrupo(mCursor.getString(0));
            nuevoIngreso.setGrupoIngreso(categoria);
            nuevoIngreso.setDescripcion(mCursor.getString(1));
            nuevoIngreso.setValor(mCursor.getString(2));
            nuevoIngreso.setFecha(mCursor.getString(3));

            ret[i] = nuevoIngreso;
            i++;
            mCursor.moveToNext();
        }
        return ret; // iterate to get each value.
    }

    public boolean deleteIngreso(String descripcion, String valor, String fecha){

        return  database.delete(INGRESOS_TABLA,
                INGRESOS_DESC + "='" + descripcion +"' AND " + INGRESOS_VALOR + "='" + valor+ "' AND " +
                        INGRESOS_FECHA + "='" + fecha + "'", null) > 0;
    }
}
