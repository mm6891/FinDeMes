package globalsolutions.findemes.pantallas.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.util.MyDatabaseHelper;
import globalsolutions.findemes.pantallas.util.Util;


/**
 * Created by Manuel on 23/02/2015.
 */
public class OptionActivityDatabase extends Activity {




    private Button guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_activity_database);

        //boton retroceder
        ImageButton btnReturn = (ImageButton) findViewById(R.id.btnBackButton);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                backActivity();
            }
        });

        guardar = (Button) findViewById(R.id.btnGuardarDB);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        File sd = Environment.getExternalStorageDirectory();

                        if (sd.canWrite()) {
                            String currentDBPath = MyDatabaseHelper.DB_PATH + MyDatabaseHelper.DATABASE_NAME;
                            String backupDBPath = "/findemes/" + MyDatabaseHelper.DATABASE_NAME;
                            File currentDB = new File(currentDBPath);
                            File backupDB = new File(sd, backupDBPath);
                            if(!backupDB.exists())
                                backupDB.getParentFile().mkdirs();

                            if (currentDB.exists()) {
                                FileChannel src = new FileInputStream(currentDB).getChannel();
                                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                                dst.transferFrom(src, 0, src.size());
                                src.close();
                                dst.close();
                                Util.showToast(getApplicationContext(), getResources().getString(R.string.Creado));
                            }
                        }
                        else
                            Util.showToast(getApplicationContext(), getResources().getString(R.string.No_Creado));
                    }
                    else {
                        Util.showToast(getApplicationContext(), getResources().getString(R.string.No_Creado));
                    }

                }catch (IOException ex){
                    Util.showToast(getApplicationContext(), getResources().getString(R.string.No_Creado));
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    private void backActivity(){
        Intent in = new Intent(OptionActivityDatabase.this, OpcionesActivity.class);
        startActivity(in);
        setResult(RESULT_OK);
        finish();
    }
}
