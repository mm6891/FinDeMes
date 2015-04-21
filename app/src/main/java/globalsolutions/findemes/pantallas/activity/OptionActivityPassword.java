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
import globalsolutions.findemes.pantallas.util.GMailSender;
import globalsolutions.findemes.pantallas.util.Util;


/**
 * Created by Manuel on 23/02/2015.
 */
public class OptionActivityPassword extends Activity {



    private EditText txtPassword;
    private EditText txtUserFom;
    private EditText txtPasswordFrom;
    private EditText txtMailTo;
    private RadioButton rbPassActivo;
    private RadioButton rbPassInActivo;

    private Button guardar;
    private Button enviar;
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
        txtMailTo = (EditText) findViewById(R.id.txtMailTo);
        txtUserFom = (EditText) findViewById(R.id.txtUserFrom);
        txtPasswordFrom = (EditText) findViewById(R.id.txtPasswordFrom);
        rbPassActivo = (RadioButton) findViewById(R.id.rbPassActivo);
        rbPassInActivo = (RadioButton) findViewById(R.id.rbPassInActivo);

        PasswordDAO passwordDAO = new PasswordDAO(getApplicationContext());
        final globalsolutions.findemes.database.model.Password password = passwordDAO.selectPassword();
        final String pass = password.getPassword();
        if (pass != null && !pass.isEmpty()) {
            txtPassword.setText(pass);
            txtUserFom.setText(password.getMail());
            rbPassActivo.setChecked(password.getActivo().equals(Constantes.REGISTRO_ACTIVO.toString()));
            rbPassInActivo.setChecked(password.getActivo().equals(Constantes.REGISTRO_INACTIVO.toString()));
        }

        guardar = (Button) findViewById(R.id.btnGuardarPass);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validaCamposPasswordLocal()) {
                    //objeto password
                    Password nuevaPass = new Password();
                    nuevaPass.setPassword(txtPassword.getText().toString());
                    nuevaPass.setMail(txtUserFom.getText().toString());
                    String valueActivo = ((RadioButton) findViewById(R.id.rbPassActivo)).isChecked()
                            ? String.valueOf(Constantes.REGISTRO_ACTIVO.toString()) :
                            String.valueOf(Constantes.REGISTRO_INACTIVO.toString());
                    nuevaPass.setActivo(valueActivo);

                    PasswordDAO passwordDAO = new PasswordDAO(getApplicationContext());
                    if (pass != null && !pass.isEmpty()) {
                        boolean actualizado = passwordDAO.updatePassword(password, nuevaPass);
                        if (actualizado)
                            Util.showToast(getApplicationContext(), getResources().getString(R.string.Modificado));
                    } else {
                        boolean insertada = passwordDAO.createRecords(nuevaPass) > 0;
                        if (insertada)
                            Util.showToast(getApplicationContext(), getResources().getString(R.string.Creado));
                    }
                }
            }
        });

        enviar = (Button) findViewById(R.id.btnEnviarCorreo);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validaCamposPasswordLocal() && validaCamposEnvioCorreo()) {
                    //Envio de correo
                    try {
                        GMailSender sender = new GMailSender(txtUserFom.getText().toString(), txtPasswordFrom.getText().toString());
                        sender.sendMail(getResources().getString(R.string.Asunto),
                                getResources().getString(R.string.Cuerpo) + " " + txtPassword.getText().toString(),
                                txtUserFom.getText().toString(),
                                txtMailTo.getText().toString(), getApplicationContext());
                    } catch (Exception e) {
                        Util.showToast(getApplicationContext(), getResources().getString(R.string.Validacion_Correo_envio));
                        return;
                    }
                }
            }
        });
    }

    public boolean validaCamposPasswordLocal(){
        //password
        String pass = txtPassword.getText().toString();
        if(pass == null || pass.isEmpty()) {
            ((EditText) findViewById(R.id.txtContrasena)).setError(getResources().getString(R.string.Validacion_PIN));
            return false;
        }

        return true;
    }

    public boolean validaCamposEnvioCorreo(){
        String userFrom = txtUserFom.getText().toString();
        if(userFrom == null || userFrom.isEmpty() || !userFrom.contains("@")) {
            ((EditText) findViewById(R.id.txtUserFrom)).setError(getResources().getString(R.string.Validacion_Correo_usuario));
            return false;
        }
        String passwordUserFrom = txtPasswordFrom.getText().toString();
        if(passwordUserFrom == null || passwordUserFrom.isEmpty()) {
            ((EditText) findViewById(R.id.txtPasswordFrom)).setError(getResources().getString(R.string.Validacion_Correo_password));
            return false;
        }
        String mailTo = txtMailTo.getText().toString();
        if(mailTo == null || mailTo.isEmpty() || !mailTo.contains("@")) {
            ((EditText) findViewById(R.id.txtMailTo)).setError(getResources().getString(R.string.Validacion_Correo_destino));
            return false;
        }

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
