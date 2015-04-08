package globalsolutions.findemes.pantallas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import globalsolutions.findemes.R;
import globalsolutions.findemes.database.model.RegistroItem;
import globalsolutions.findemes.database.util.Constantes;

/**
 * Created by manuel.molero on 04/02/2015.
 */

public class RegistroAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<RegistroItem> items;

    public RegistroAdapter(Context context, ArrayList<RegistroItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if (convertView == null) {
            // Create a new view into the list.
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.registro_item, parent, false);
        }

        // Set data into the view.
        TextView tvRegistroItem = (TextView) rowView.findViewById(R.id.tvRegistroItem);
        TextView tvPeriodicidadItem = (TextView) rowView.findViewById(R.id.tvPeriodicidadItem);
        TextView tvTipoItem = (TextView) rowView.findViewById(R.id.tvTipoItem);
        TextView tvCategoriaItem = (TextView) rowView.findViewById(R.id.tvCategoriaItem);
        TextView tvValorItem = (TextView) rowView.findViewById(R.id.tvValorItem);
        TextView tvActivoItem = (TextView) rowView.findViewById(R.id.tvActivoItem);

        RegistroItem item = this.items.get(position);
        tvRegistroItem.setText(item.getDescripcion());
        tvPeriodicidadItem.setText(item.getPeriodicidad());
        tvTipoItem.setText(item.getTipo());
        tvCategoriaItem.setText(item.getGrupo());
        tvValorItem.setText(item.getValor());
        tvActivoItem.setText(item.getActivo().equals(Integer.valueOf(Constantes.REGISTRO_ACTIVO.toString()))
                ? context.getResources().getString(R.string.AFIRMACION) : context.getResources().getString(R.string.NEGACION));

        return rowView;
    }

    public void updateReceiptsList(ArrayList<RegistroItem> newlist) {
        items.clear();
        items.addAll(newlist);
        this.notifyDataSetChanged();
    }
}
