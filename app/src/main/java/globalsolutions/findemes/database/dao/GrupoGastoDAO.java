package globalsolutions.findemes.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import globalsolutions.findemes.database.model.GrupoGasto;
import globalsolutions.findemes.database.util.MyDatabaseHelper;

/**
 * Created by manuel.molero on 03/02/2015.
 */
public class GrupoGastoDAO {
    private MyDatabaseHelper dbHelper;

    private SQLiteDatabase database;

    public final static String GRUPO_GASTOS_TABLA="Grupo_Gastos";

    //columns table Grupo_Gastos
    public final static String GRUPO_GASTOS_ID="_id"; // id value for grupo_gastos
    public final static String GRUPO_GASTOS_DESC="grupo";  // nombre del grupo

    /**
     *
     * @param context
     */
    public GrupoGastoDAO(Context context){
        dbHelper = new MyDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long createRecords(GrupoGasto grupoGasto){
        ContentValues values = new ContentValues();
        values.put(GRUPO_GASTOS_DESC, grupoGasto.getGrupo());

        return database.insert(GRUPO_GASTOS_TABLA, null, values);
    }

    public String[] selectGrupos() {
        String[] ret;
        String[] cols = new String[] {GRUPO_GASTOS_DESC};
        Cursor mCursor = database.query(true, GRUPO_GASTOS_TABLA,cols,null
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
