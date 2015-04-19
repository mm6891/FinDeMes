package globalsolutions.findemes.pantallas.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.PasswordDAO;
import globalsolutions.findemes.database.model.Password;
import globalsolutions.findemes.database.util.Constantes;
import globalsolutions.findemes.pantallas.util.Util;


/**
 * Created by Manuel on 23/02/2015.
 */
public class OptionActivityPassword extends Activity {



    private EditText txtPassword;
    private EditText txtMail;
    private Button guardar;
    private RadioButton rbPassActivo;
    private RadioButton rbPassInActivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_activity_password);

        //boton retroceder
        ImageButton btnReturn = (ImageButton) findViewById(R.id.btnBackButton);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                backActivity();
            }
        });

        txtPassword = (EditText) findViewById(R.id.txtContrasena);
        txtMail = (EditText) findViewById(R.id.txtMail);
        rbPassActivo = (RadioButton) findViewById(R.id.rbPassActivo);
        rbPassInActivo = (RadioButton) findViewById(R.id.rbPassInActivo);

        PasswordDAO passwordDAO = new PasswordDAO(getApplicationContext());
        final globalsolutions.findemes.database.model.Password password = passwordDAO.selectPassword();
        final String pass = password.getPassword();
        if (pass != null && !pass.isEmpty()) {
            txtPassword.setText(pass);
            txtMail.setText(password.getMail());
            rbPassActivo.setChecked(password.getActivo().equals(Constantes.REGISTRO_ACTIVO.toString()));
            rbPassInActivo.setChecked(password.getActivo().equals(Constantes.REGISTRO_INACTIVO.toString()));
        }

        guardar = (Button) findViewById(R.id.btnGuardarPass);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validaCampos()) {
                    //se guarda en base de datos y se envia al correo
                    Password nuevaPass = new Password();
                    nuevaPass.setPassword(txtPassword.getText().toString());
                    nuevaPass.setMail(txtMail.getText().toString());
                    String valueActivo = ((RadioButton) findViewById(R.id.rbPassActivo)).isChecked()
                            ? String.valueOf(Constantes.REGISTRO_ACTIVO.toString()) :
                            String.valueOf(Constantes.REGISTRO_INACTIVO.toString());
                    nuevaPass.setActivo(valueActivo);

                    PasswordDAO passwordDAO = new PasswordDAO(getApplicationContext());
                    if (pass != null && !pass.isEmpty()) {
                        boolean actualizado = passwordDAO.updatePassword(password,nuevaPass);
                        if(actualizado)
                            Util.showToast(getApplicationContext(), getResources().getString(R.string.Modificado));
                    }
                    else {
                        boolean insertada = passwordDAO.createRecords(nuevaPass) > 0;
                        if(insertada)
                            Util.showToast(getApplicationContext(), getResources().getString(R.string.Creado));
                    }

                   /* Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{txtMail.getText().toString()});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Contraseña app Fin de Mes");
                    i.putExtra(Intent.EXTRA_TEXT, txtPassword.getText().toString());
                    try {
                        startActivity(Intent.createChooser(i, "Enviando correo"));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(OptionActivityPassword.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }*/
                }
            }
        });

    }

    public boolean validaCampos(){
        //obtenemos password y mail
        String pass = txtPassword.getText().toString();
        if(pass == null || pass.isEmpty()) {
            ((EditText) findViewById(R.id.txtContrasena)).setError(getResources().getString(R.string.Validacion_PIN));
            return false;
        }
        /*String mail = txtMail.getText().toString();
        if(mail == null || mail.isEmpty()) {
            ((EditText) findViewById(R.id.txtMail)).setError("Debe incluir un correo electronico para enviarle su contraseña");
            return false;
        }*/

        return true;
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    private void backActivity(){
        Intent in = new Intent(OptionActivityPassword.this, OpcionesActivity.class);
        startActivity(in);
        setResult(RESULT_OK);
        finish();
    }
}