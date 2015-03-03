package globalsolutions.findemes.pantallas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.dao.MovimientoDAO;
import globalsolutions.findemes.database.model.InformeItem;
import globalsolutions.findemes.database.model.MovimientoItem;
import globalsolutions.findemes.database.util.Constantes;

/**
 * Created by manuel.molero on 04/02/2015.
 */

public class InformeAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<InformeItem> items;
    private ArrayList<InformeItem> itemsFiltrado;
    private ItemFilter mFilter = new ItemFilter();

    public InformeAdapter(Context context, ArrayList<InformeItem> items) {
        this.context = context;
        this.items = items;
        this.itemsFiltrado = items;
    }

    private int anyoSeleccionado;
    public int getAnyoSeleccionado() {
        return anyoSeleccionado;
    }
    public void setAnyoSeleccionado(int anyoSeleccionado) {
        this.anyoSeleccionado = anyoSeleccionado;
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
            rowView = inflater.inflate(R.layout.informe_item, parent, false);
        }

        // Set data into the view.
        TextView tvPeriodo = (TextView) rowView.findViewById(R.id.tvPeriodoInforme);
        TextView tvIngresoValor = (TextView) rowView.findViewById(R.id.tvIngresosInformeValor);
        TextView tvGastoValor = (TextView) rowView.findViewById(R.id.tvGastosInformeValor);
        TextView tvTotalValor = (TextView) rowView.findViewById(R.id.tvTotalInformeValor);

        InformeItem item = this.itemsFiltrado.get(position);
        tvPeriodo.setText(item.getPeriodoDesc());
        tvIngresoValor.setText(item.getIngresoValor());
        tvGastoValor.setText(item.getGastoValor());
        tvTotalValor.setText(item.getTotalValor());

        return rowView;
    }

    public void updateReceiptsList(ArrayList<InformeItem> newlist) {
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
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                String filterString = constraint.toString().toLowerCase();

                if(constraint.toString().equals(Constantes.TIPO_FILTRO_RESETEO)){
                    
                }

                //load resumen
                //recuperamos movimientos
                final ArrayList<MovimientoItem> movs = new MovimientoDAO().cargaMovimientos(context);

                /*int mesActual = Calendar.getInstance().get(Calendar.MONTH);
                int anyoActal = Calendar.getInstance().get(Calendar.YEAR);*/
                /*Double ingresos = new Double(0.00);
                Double gastos = new Double(0.00);
                Double saldo = new Double(0.00);

                for(MovimientoItem mov : movs){
                    String fecha = mov.getFecha();
                    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar cal  = Calendar.getInstance();
                    try {
                        cal.setTime(formato.parse(fecha));
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                    //int mesMovimiento = cal.get(Calendar.MONTH);
                    int anyoMovimiento = cal.get(Calendar.YEAR);
                    if (mov.getTipoMovimiento().equals(Constantes.TIPO_MOVIMIENTO_GASTO)
                            && mesMovimiento == getMesSeleccionado() && getAnyoSeleccionado() == anyoMovimiento)
                        gastos += Double.valueOf(mov.getValor());
                    else if (mov.getTipoMovimiento().equals(Constantes.TIPO_MOVIMIENTO_INGRESO)
                            && mesMovimiento == getMesSeleccionado() && getAnyoSeleccionado() == anyoMovimiento)
                        ingresos += Double.valueOf(mov.getValor());
                }*/

               /* tvIngresosValor.setText(String.valueOf(ingresos));
                tvGastosValor.setText(String.valueOf(gastos));
                saldo = ingresos - gastos;
                tvSaldo.setText(String.valueOf(saldo));
                tvMes.setText(new DateFormatSymbols().getMonths()[mesActual-1]);*/

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                updateReceiptsList((ArrayList<InformeItem>) results.values);
            }

        }
}
