package globalsolutions.findemes.pantallas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.PasswordDAO;
import globalsolutions.findemes.database.model.Password;
import globalsolutions.findemes.database.util.Constantes;
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
                final String inFileName = MyDatabaseHelper.DB_PATH + MyDatabaseHelper.DATABASE_NAME;

                File dbFile = new File(inFileName);
                try {
                    FileInputStream fis = new FileInputStream(dbFile);
                    String outFileName = Environment.getExternalStorageDirectory() + "/findemes/DBFinDeMes.db";

                    // Open the empty db as the output stream
                    OutputStream output = new FileOutputStream(outFileName);
                    // Transfer bytes from the inputfile to the outputfile
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }

                    // Close the streams
                    output.flush();
                    output.close();
                    fis.close();
                    Util.showToast(getApplicationContext(), getResources().getString(R.string.Creado));
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
