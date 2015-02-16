package globalsolutions.findemes.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Timestamp;

import globalsolutions.findemes.database.model.Gasto;
import globalsolutions.findemes.database.model.GrupoGasto;
import globalsolutions.findemes.database.util.MyDatabaseHelper;

/**
 * Created by manuel.molero on 03/02/2015.
 */
public class GastoDAO {
    private MyDatabaseHelper dbHelper;

    private SQLiteDatabase database;

    //tables´s name
    public final static String GASTOS_TABLA="Gastos";

    //columns table Gastos
    public final static String GASTOS_ID="_id"; // id value for gasto
    public final static String GASTOS_DESC="descripcion";  // nombre del gasto
    public final static String GASTOS_VALOR="valor";  // valor del gasto
    public final static String GASTOS_FECHA="fecha";  // fecha del gasto
    public final static String GASTOS_GRUPO="grupogasto";  // grupo al que pertenece el gasto, referencia

    /**
     *
     * @param context
     */
    public GastoDAO(Context context){
        dbHelper = new MyDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long createRecords(Gasto gasto){
        ContentValues values = new ContentValues();
        values.put(GASTOS_DESC, gasto.getDescripcion());
        values.put(GASTOS_VALOR, gasto.getValor());
        values.put(GASTOS_GRUPO, gasto.getGrupoGasto().getGrupo());

        return database.insert(GASTOS_TABLA, null, values);
    }

    public Gasto[] selectGastos() {
        Gasto[] ret;
        String[] cols = new String[] {GASTOS_GRUPO, GASTOS_DESC, GASTOS_VALOR,GASTOS_FECHA};
        Cursor mCursor = database.query(true, GASTOS_TABLA,cols,null
                , null, null, null, null, null);
        ret = new Gasto[mCursor.getCount()];
        int i = 0;
        mCursor.moveToFirst();
        while (mCursor.isAfterLast() == false) {
            Gasto nuevoGasto = new Gasto();
            GrupoGasto categoria = new GrupoGasto();
            categoria.setGrupo(mCursor.getString(0));
            nuevoGasto.setGrupoGasto(categoria);
            nuevoGasto.setDescripcion(mCursor.getString(1));
            nuevoGasto.setValor(mCursor.getString(2));
            nuevoGasto.setFecha(mCursor.getString(3));

            ret[i] = nuevoGasto;
            i++;
            mCursor.moveToNext();
        }
        return ret; // iterate to get each value.
    }

    public boolean deleteGasto(String descripcion, String valor, String fecha){

        return  database.delete(GASTOS_TABLA,
                GASTOS_DESC + "='" + descripcion +"' AND " + GASTOS_VALOR + "='" + valor+ "' AND " +
                        GASTOS_FECHA + "='" + fecha + "'", null) > 0;
    }

}
