package globalsolutions.findemes.database.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by manuel.molero on 03/02/2015.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DBFinDeMes";

    private static final int DATABASE_VERSION = 2;
    private static String DB_PATH = "";

    // Database creation sql statement
    private static final String CREATE_TABLE_GASTOS =
            "create table Gastos( _id integer primary key," +
                    "descripcion text not null," +
                    "valor text not null," +
                    "fecha TIMESTAMP NOT NULL DEFAULT current_timestamp," +
                    "grupogasto text not null," +
                    "  FOREIGN KEY(grupogasto) REFERENCES Grupo_Gastos(_id));";

    private static final String CREATE_TABLE_GRUPO_GASTOS =
            "create table Grupo_Gastos( _id integer primary key," +
                    "grupo text not null);";

    private static final String CREATE_TABLE_INGRESOS =
            "create table Ingresos( _id integer primary key," +
                    "descripcion text not null," +
                    "valor text not null," +
                    "fecha TIMESTAMP NOT NULL DEFAULT current_timestamp," +
                    "grupoingreso text not null," +
                    "  FOREIGN KEY(grupoingreso) REFERENCES Grupo_Ingresos(_id));";

    private static final String CREATE_TABLE_GRUPO_INGRESOS =
            "create table Grupo_Ingresos( _id integer primary key," +
                    "grupo text not null);";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
            database.execSQL(CREATE_TABLE_GASTOS);
            database.execSQL(CREATE_TABLE_GRUPO_GASTOS);
            database.execSQL(CREATE_TABLE_INGRESOS);
            database.execSQL(CREATE_TABLE_GRUPO_INGRESOS);

    }

    // Method is called during an upgrade of the database,
    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){
        Log.w(MyDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS Gastos");
        database.execSQL("DROP TABLE IF EXISTS Grupo_Gastos");
        database.execSQL("DROP TABLE IF EXISTS Ingresos");
        database.execSQL("DROP TABLE IF EXISTS Grupo_Ingresos");

        onCreate(database);
    }

    /**
     * Check if the database exist
     *
     * @return true if it exists, false if it doesn't
     */
    public static boolean checkDataBase(Context context) {
        SQLiteDatabase checkDB = null;
        int count = 0;
        try {
            if(android.os.Build.VERSION.SDK_INT >= 17){
                DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
            }
            else
            {
                DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
            }
            checkDB = SQLiteDatabase.openDatabase(DB_PATH + DATABASE_NAME, null,
                    SQLiteDatabase.OPEN_READONLY);

            String selectRegistros = "SELECT COUNT(*) FROM Grupo_Gastos";

            Cursor mCursor = checkDB.query(true, "Grupo_Gastos",new String[]{"_id"},null
                    , null, null, null, null, null);
            count = mCursor.getCount();

            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
            return  false;
        }
        return count > 0;
    }
}
