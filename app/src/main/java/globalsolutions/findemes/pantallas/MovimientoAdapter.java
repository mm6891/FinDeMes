package globalsolutions.findemes.pantallas;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.GastoDAO;
import globalsolutions.findemes.database.dao.MovimientoDAO;
import globalsolutions.findemes.database.model.Gasto;
import globalsolutions.findemes.database.model.MovimientoItem;

/**
 * Created by manuel.molero on 04/02/2015.
 */

public class MovimientoAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<MovimientoItem> items;
    private ArrayList<MovimientoItem> itemsFiltrado;
    private ItemFilter mFilter = new ItemFilter();

    //tipos de filtro
    public final String TIPO_FILTRO_GASTO = "GASTO";
    public final String TIPO_FILTRO_INGRESO = "INGRESO";
    public final String TIPO_FILTRO_RESETEO = "TODO";

    private String mesSeleccionado = "";
    public String getMesSeleccionado(){
        return mesSeleccionado;
    }
    public void setMesSeleccionado(String mesSeleccionado){
        this.mesSeleccionado = mesSeleccionado;
    }

    public MovimientoAdapter(Context context, ArrayList<MovimientoItem> items) {
        this.context = context;
        this.items = items;
        this.itemsFiltrado = items;
    }

    @Override
    public int getCount() {
        return this.itemsFiltrado.size();
    }

    @Override
    public Object getItem(int position) {
        return this.itemsFiltrado.get(position);
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
            rowView = inflater.inflate(R.layout.movimiento_item, parent, false);
        }

        // Set data into the view.

        TextView tvDescr = (TextView) rowView.findViewById(R.id.tvDescMov);
        TextView tvvalor = (TextView) rowView.findViewById(R.id.tvValorMov);
        TextView tvgrupo = (TextView) rowView.findViewById(R.id.tvGrupoMov);
        TextView tvfecha = (TextView) rowView.findViewById(R.id.tvFechaMov);
        ImageView ivIconMov = (ImageView) rowView.findViewById(R.id.ivIcon);

        MovimientoItem item = this.itemsFiltrado.get(position);
        tvDescr.setText(item.getDescripcion());
        tvvalor.setText(item.getValor());
        tvgrupo.setText(item.getCategoria());
        tvfecha.setText(item.getFecha());
        if(item.getTipoMovimiento().trim().equals("GASTO"))
            ivIconMov.setImageResource(R.drawable.minus);
        if(item.getTipoMovimiento().trim().equals("INGRESO"))
            ivIconMov.setImageResource(R.drawable.plus);

        return rowView;
    }

    public void updateReceiptsList(ArrayList<MovimientoItem> newlist) {
        itemsFiltrado.clear();
        itemsFiltrado.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }



    private class ItemFilter extends Filter {

            @Override
            protected Filter.FilterResults performFiltering(CharSequence constraint) {

                items = new MovimientoDAO().cargaMovimientos(context);
                FilterResults results = new FilterResults();

                if(constraint.toString().equals(TIPO_FILTRO_RESETEO)){

                    results.values = items;
                    results.count = items.size();

                }
                else {
                    String filterString = constraint.toString().toLowerCase();

                    final ArrayList<MovimientoItem> list = items;

                    int count = list.size();
                    final ArrayList<MovimientoItem> nlist = new ArrayList<MovimientoItem>(count);

                    String filterableString;

                    for (int i = 0; i < count; i++) {
                        filterableString = list.get(i).getTipoMovimiento();
                        if (filterableString.toLowerCase().trim().equals(filterString.trim())) {
                            nlist.add(list.get(i));
                        }
                    }

                    results.values = nlist;
                    results.count = nlist.size();

                }
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                updateReceiptsList((ArrayList<MovimientoItem>) results.values);
            }

        }
}
