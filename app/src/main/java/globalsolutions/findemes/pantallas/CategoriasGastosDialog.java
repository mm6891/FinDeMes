package globalsolutions.findemes.pantallas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.GrupoGastoDAO;
import globalsolutions.findemes.database.util.Constantes;

/**
 * Created by manuel.molero on 16/02/2015.
 */
public class CategoriasGastosDialog extends DialogFragment {

    private ListView listViewCategoriasGastos;
    private ImageButton btnPlusCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.categoria_gasto_dialog, container, false);

        List<String> list = new ArrayList<String>();
        GrupoGastoDAO grupoGastoDAO = new GrupoGastoDAO(view.getContext());
        String[] categoriasGastos = grupoGastoDAO.selectGrupos();
        list = Arrays.asList(categoriasGastos);

        listViewCategoriasGastos = (ListView) view.findViewById(R.id.listViewCatGas);
        listViewCategoriasGastos.setAdapter(new CategoriaAdapter(view.getContext(),new ArrayList<String>(list)));

        listViewCategoriasGastos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position,
                                    long id) {
                final String categoriaGasto = (String) listViewCategoriasGastos.getItemAtPosition(position);
                final CharSequence[] items = {Constantes.ACCION_MODIFICAR, Constantes.ACCION_ELIMINAR};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("OPCIONES");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                //Eliminar Movimiento
                                String accion = (String) items[item];
                                boolean realizado;

                                if (accion.equals(Constantes.ACCION_ELIMINAR)) {

                                    GrupoGastoDAO grupoGastoDAO = new GrupoGastoDAO(getActivity());
                                    realizado = grupoGastoDAO.deleteRecords(categoriaGasto);
                                    if (realizado) {
                                        showToast(view.getContext(),"¡Grupo eliminado!");
                                       String[] newList = grupoGastoDAO.selectGrupos();
                                        ((CategoriaAdapter) listViewCategoriasGastos.getAdapter()).updateReceiptsList(
                                                new ArrayList<String>(Arrays.asList(newList)));
                                    } else
                                        showToast(view.getContext(),"No se ha podido eliminar el grupo");
                                }
                                if (accion.equals(Constantes.ACCION_MODIFICAR)) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("nameCategory", categoriaGasto);
                                    // Create an instance of the dialog fragment and show it*//*
                                    showEditGastoDialog(view, bundle);
                                }
                            }
                        }
                ).show();
            }
        });

        btnPlusCategory = (ImageButton) view.findViewById(R.id.btnPlusGastoCategory);
        btnPlusCategory.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewGastoDialog();
            }
        });

        // Inflate the layout to use as dialog or embedded fragment
        return view;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    public void showToast(Context c, String message){
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    public void showEditGastoDialog(View v, Bundle bundle) {
        DialogFragment newFragment = new EditCategoriaGastoDialog();
        newFragment.setArguments(bundle);
        newFragment.setTargetFragment(this,1);
        newFragment.show(getFragmentManager(),"EDITGASTO");
    }

    public void showNewGastoDialog() {
        DialogFragment newFragment = new NewCategoriaGastoDialog();
        newFragment.setTargetFragment(this,1);
        newFragment.show(getFragmentManager(),"NEWGASTO");
    }

    public void OnCategoriaGastoDialogSubmit(String result) {
        if(result.equals(String.valueOf(Activity.RESULT_OK))){
            GrupoGastoDAO grupoGastoDAO = new GrupoGastoDAO(getActivity());
            String[] newList = grupoGastoDAO.selectGrupos();
            ((CategoriaAdapter) listViewCategoriasGastos.getAdapter()).updateReceiptsList(
                    new ArrayList<String>(Arrays.asList(newList)));
        }
    }
}
