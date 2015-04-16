package globalsolutions.findemes.pantallas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.RegistroDAO;
import globalsolutions.findemes.database.model.RegistroItem;
import globalsolutions.findemes.database.util.Constantes;

public class RegistrosActivity extends FragmentActivity implements NuevoRegistroDialog.ONuevoRegistroDialogListener, EditRegistroDialog.OnEditRegistroDialogListener {


    private ListView listViewReg;
    private Button btnNuevoRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros);

        //boton retroceder
        ImageButton btnReturn = (ImageButton) findViewById(R.id.btnBackButton);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                backActivity();
            }
        });

        //boton nuevo registro
        btnNuevoRegistro = (Button) findViewById(R.id.btnNuevoRegistro);
        btnNuevoRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNuevoRegistroDialog();
            }
        });

        //recuperamos registros
        ArrayList<RegistroItem> regs = new ArrayList(new RegistroDAO(getApplicationContext()).selectRegistrosItems());
        listViewReg = (ListView) findViewById(R.id.listViewReg);
        listViewReg.setAdapter(new RegistroAdapter(getApplicationContext(), regs));

        listViewReg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position,
                                    long id) {
                final RegistroItem registroItem = (RegistroItem) listViewReg.getItemAtPosition(position);
                final CharSequence[] items = {Constantes.ACCION_MODIFICAR, Constantes.ACCION_ELIMINAR};

                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrosActivity.this);
                builder.setTitle("OPCIONES");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                //Eliminar Registro
                                String accion = (String) items[item];
                                boolean realizado;

                                if (accion.equals(Constantes.ACCION_ELIMINAR)) {
                                    RegistroDAO registroDAO = new RegistroDAO(RegistrosActivity.this);
                                    realizado = registroDAO.deleteRegistro(registroItem.get_id());
                                    if (realizado) {
                                        showToast("¡Registro eliminado!");
                                        ((RegistroAdapter)listViewReg.getAdapter()).updateReceiptsList(new RegistroDAO(getApplicationContext()).selectRegistrosItems());
                                    } else
                                        showToast("No se ha podido eliminar el registro");
                                }
                                if (accion.equals(Constantes.ACCION_MODIFICAR)) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("_id", String.valueOf(registroItem.get_id()));
                                    bundle.putString("nombre", registroItem.getDescripcion());
                                    bundle.putString("periodicidad", registroItem.getPeriodicidad());
                                    bundle.putString("valor", registroItem.getValor());
                                    bundle.putString("fecha", registroItem.getFecha());
                                    bundle.putString("categoria", registroItem.getGrupo());
                                    bundle.putString("tipo", registroItem.getTipo());
                                    bundle.putString("activo", String.valueOf(registroItem.getActivo()));

                                    // Create an instance of the dialog fragment and show it*//*
                                    showEditRegistroDialog(bundle);
                                }
                            }
                        }
                ).show();
            }
        });
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    private void backActivity(){
        Intent in = new Intent(RegistrosActivity.this, MainActivity.class);
        startActivity(in);
        setResult(RESULT_OK);
        finish();
    }

    //muestra el modal de nuevo registro frecuente
    public void showNuevoRegistroDialog() {
        DialogFragment newFragment = new NuevoRegistroDialog();
        newFragment.show(getFragmentManager(),"NUEVO REGISTRO");
    }

    //muestra el modal de edicion de registro frecuente
    public void showEditRegistroDialog(Bundle bundle) {
        DialogFragment newFragment = new EditRegistroDialog();
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(),"EDITAR REGISTRO");
    }

    @Override
    public void ONuevoRegistroDialogSubmit(String result) {
        if(result.equals(String.valueOf(Activity.RESULT_OK)))
            ((RegistroAdapter)listViewReg.getAdapter()).updateReceiptsList(new RegistroDAO(getApplicationContext()).selectRegistrosItems());
    }

    @Override
    public void OnEditRegistroDialogSubmit(String result) {
        if(result.equals(String.valueOf(Activity.RESULT_OK)))
            ((RegistroAdapter)listViewReg.getAdapter()).updateReceiptsList(new RegistroDAO(getApplicationContext()).selectRegistrosItems());
    }
}