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

import java.lang.reflect.Array;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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

    //mes, array de movimientos
    private HashMap<Integer,ArrayList<MovimientoItem>> informes = new HashMap<Integer,ArrayList<MovimientoItem>>();

    public InformeAdapter(Context context, ArrayList<InformeItem> items) {
        this.context = context;
        this.items = items;
        this.itemsFiltrado = items;
    }

    public int getPeriodoFiltroSeleccionado() {
        return periodoFiltroSeleccionado;
    }
    public void setPeriodoFiltroSeleccionado(int periodoFiltroSeleccionado) {
        this.periodoFiltroSeleccionado = periodoFiltroSeleccionado;
    }
    private int periodoFiltroSeleccionado;


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
            String[] filtersString = constraint.toString().split(";");
            String tipoMovimiento = filtersString[0];
            String periodo = filtersString[1];
            String periodoFiltro = filtersString[2];

            //recuperamos movimientos
            final ArrayList<MovimientoItem> movs = new MovimientoDAO().cargaMovimientos(context);
            informes.clear();

            if(tipoMovimiento.equals(Constantes.TIPO_FILTRO_RESETEO)){
                //filtrotodo y mensual
                if(periodo.equals(Constantes.TIPO_FILTRO_INFORME_MENSUAL)){
                    int mesActual = Integer.MIN_VALUE;
                    int anyoActual = new Integer(periodoFiltro).intValue();

                    for(int i = 0 ; i < movs.size() ; i++) {
                        String fecha = movs.get(i).getFecha();
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Calendar cal = Calendar.getInstance();
                        try {
                            cal.setTime(formato.parse(fecha));
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
                        //int mesMovimiento = cal.get(Calendar.MONTH);
                        int anyoMovimiento = cal.get(Calendar.YEAR);

                        if (anyoMovimiento == anyoActual) {
                            boolean existeMes = existeMesInforme(mesActual);
                            if(!existeMes){
                                nuevoMesInforme(mesActual, movs.get(i));
                            }
                            else{
                                actualizaMesInforme(mesActual, movs.get(i));
                            }
                        }
                    }

                    ArrayList<InformeItem> resultado = calculaInformes();
                    results.values = resultado;
                    results.count = resultado.size();
                }
            }

            return results;
        }

        private ArrayList<InformeItem> calculaInformes(){
            ArrayList<InformeItem> result = new ArrayList<InformeItem>(informes.size());
            for(Integer integer : informes.keySet()){


            }
            return result;
        }

        public boolean existeMesInforme(int key){
            return informes.containsKey(new Integer(key));
        }

        public void actualizaMesInforme(int key, MovimientoItem mov){
            informes.get(key).add(mov);
        }

        public void nuevoMesInforme(int key,MovimientoItem nuevo){
            ArrayList<MovimientoItem> nuevoArray = new ArrayList<MovimientoItem>();
            nuevoArray.add(nuevo);
            informes.put(key, nuevoArray);
        }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                updateReceiptsList((ArrayList<InformeItem>) results.values);
            }

        }
}
