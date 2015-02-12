package globalsolutions.findemes.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import globalsolutions.findemes.database.model.GrupoGasto;
import globalsolutions.findemes.database.model.GrupoIngreso;
import globalsolutions.findemes.database.util.MyDatabaseHelper;

/**
 * Created by manuel.molero on 03/02/2015.
 */
public class GrupoIngresoDAO {
    private MyDatabaseHelper dbHelper;

    private SQLiteDatabase database;

    public final static String GRUPO_INGRESOS_TABLA="Grupo_Ingresos";

    //columns table Grupo_Ingresos
    public final static String GRUPO_INGRESOS_ID="_id"; // id value for grupo_gastos
    public final static String GRUPO_INGRESOS_DESC="grupo";  // nombre del grupo

    /**
     *
     * @param context
     */
    public GrupoIngresoDAO(Context context){
        dbHelper = new MyDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long createRecords(GrupoIngreso grupoIngreso){
        ContentValues values = new ContentValues();
        values.put(GRUPO_INGRESOS_DESC, grupoIngreso.getGrupo());

        return database.insert(GRUPO_INGRESOS_TABLA, null, values);
    }

    public String[] selectGrupos() {
        String[] ret;
        String[] cols = new String[] {GRUPO_INGRESOS_DESC};
        Cursor mCursor = database.query(true, GRUPO_INGRESOS_TABLA,cols,null
                , null, null, null, null, null);
        ret = new String[mCursor.getCount()];
        int i = 0;
        mCursor.moveToFirst();
        while (mCursor.isAfterLast() == false) {
            ret[i] = mCursor.getString(0);
            i++;
            mCursor.moveToNext();
        }

        return ret; // iterate to get each value.
    }
}
