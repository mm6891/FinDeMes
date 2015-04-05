package globalsolutions.findemes.pantallas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

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

        // Create a new view into the list.
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //establer el item segun el tipo de movimiento
        InformeItem item = this.itemsFiltrado.get(position);
        if(item.getTipoInforme().equals(Constantes.TIPO_FILTRO_RESETEO)){
            rowView = inflater.inflate(R.layout.informe_item, parent, false);
            // Set data into the view.
            TextView tvPeriodo = (TextView) rowView.findViewById(R.id.tvPeriodoInforme);
            TextView tvIngresoValor = (TextView) rowView.findViewById(R.id.tvIngresosInformeValor);
            TextView tvGastoValor = (TextView) rowView.findViewById(R.id.tvGastosInformeValor);
            TextView tvTotalValor = (TextView) rowView.findViewById(R.id.tvTotalInformeValor);

            tvPeriodo.setText(item.getPeriodoDesc());
            tvIngresoValor.setText(item.getIngresoValor());
            tvGastoValor.setText(item.getGastoValor());
            tvTotalValor.setText(item.getTotalValor());
        }
        else if(item.getTipoInforme().equals(Constantes.TIPO_MOVIMIENTO_GASTO)){
            rowView = inflater.inflate(R.layout.informe_item_gasto, parent, false);
            // Set data into the view.
            TextView tvPeriodo = (TextView) rowView.findViewById(R.id.tvPeriodoInforme);
            TextView tvGastoValor = (TextView) rowView.findViewById(R.id.tvGastosInformeValor);

            tvPeriodo.setText(item.getPeriodoDesc());
            tvGastoValor.setText(item.getGastoValor());
        }
        if(item.getTipoInforme().equals(Constantes.TIPO_MOVIMIENTO_INGRESO)){
            rowView = inflater.inflate(R.layout.informe_item_ingreso, parent, false);
            // Set data into the view.
            TextView tvPeriodo = (TextView) rowView.findViewById(R.id.tvPeriodoInforme);
            TextView tvIngresoValor = (TextView) rowView.findViewById(R.id.tvIngresosInformeValor);

            tvPeriodo.setText(item.getPeriodoDesc());
            tvIngresoValor.setText(item.getIngresoValor());
        }

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
                int anyoMovimiento = cal.get(Calendar.YEAR);

                if (anyoMovimiento == anyoActual) {
                    int periodoMovimiento = Integer.MIN_VALUE;
                    if(periodo.equals(Constantes.TIPO_FILTRO_INFORME_MENSUAL)) {
                        periodoMovimiento = cal.get(Calendar.MONTH);
                    }
                    //trimestral
                    if(periodo.equals(Constantes.TIPO_FILTRO_INFORME_TRIMESTRAL)){
                        periodoMovimiento = (cal.get(Calendar.MONTH) / 3) + 1;
                    }
                    //quincenal
                    else if(periodo.equals(Constantes.TIPO_FILTRO_INFORME_QUINCENAL)){
                        periodoMovimiento = (cal.get(Calendar.DAY_OF_YEAR) / 14) + 1;
                    }

                    boolean existePeriodoInforme = existePeriodoInforme(periodoMovimiento);
                    if(!existePeriodoInforme){
                        nuevoMesInforme(periodoMovimiento, movs.get(i));
                    }
                    else{
                        actualizaMesInforme(periodoMovimiento, movs.get(i));
                    }
                }
            }

            ArrayList<InformeItem> resultado = calculaInformes(tipoMovimiento,periodo);
            results.values = resultado;
            results.count = resultado.size();

            return results;
        }

        //tipoPeriodo:  MENSUAL, TRIMESTRAL, QUINCENAL
        private ArrayList<InformeItem> calculaInformes(String tipoMovimiento, String tipoPeriodo){
            ArrayList<InformeItem> result = new ArrayList<InformeItem>(informes.size());
            for(Integer integer : informes.keySet()){
                ArrayList<MovimientoItem> movsMes = informes.get(integer);

                Double ingresos = new Double(0.00);
                Double gastos = new Double(0.00);
                Double saldo = new Double(0.00);

                for(MovimientoItem mov : movsMes){
                    if (mov.getTipoMovimiento().equals(Constantes.TIPO_MOVIMIENTO_GASTO))
                        gastos += Double.valueOf(mov.getValor());
                    else if (mov.getTipoMovimiento().equals(Constantes.TIPO_MOVIMIENTO_INGRESO))
                        ingresos += Double.valueOf(mov.getValor());
                }

                //si es un gasto o ingreso sin valor, no se incluye en la lista
                if(tipoMovimiento.equals(Constantes.TIPO_MOVIMIENTO_GASTO) &&
                        gastos == (new Double(0.00)))
                    continue;
                if(tipoMovimiento.equals(Constantes.TIPO_MOVIMIENTO_INGRESO) &&
                        ingresos == (new Double(0.00)))
                    continue;

                InformeItem nuevoInforme = new InformeItem();
                nuevoInforme.setTipoInforme(tipoMovimiento);

                nuevoInforme.setGastoValor(String.valueOf(gastos));
                nuevoInforme.setIngresoValor(String.valueOf(ingresos));
                saldo = ingresos - gastos;
                nuevoInforme.setTotalValor(String.valueOf(saldo));

                if(tipoPeriodo.equals(Constantes.TIPO_FILTRO_INFORME_MENSUAL)) {
                    nuevoInforme.setPeriodoDesc(new DateFormatSymbols().getMonths()[integer.intValue()].toUpperCase());
                }
                else if(tipoPeriodo.equals(Constantes.TIPO_FILTRO_INFORME_TRIMESTRAL)) {
                    //trimestres
                    switch (integer.intValue()){
                        case 1:
                            nuevoInforme.setPeriodoDesc("1 Ene - 31 Mar");
                            break;
                        case 2:
                            nuevoInforme.setPeriodoDesc("1 Abr - 30 Jun");
                            break;
                        case 3:
                            nuevoInforme.setPeriodoDesc("1 Jul - 30 Sep");
                            break;
                        case 4:
                            nuevoInforme.setPeriodoDesc("1 Oct - 31 Dic");
                            break;
                    }
                }

                else if(tipoPeriodo.equals(Constantes.TIPO_FILTRO_INFORME_QUINCENAL)) {
                    //trimestres
                    switch (integer.intValue()){
                        case 1:
                            nuevoInforme.setPeriodoDesc("1 Ene - 15 Ene");
                            break;
                        case 2:
                            nuevoInforme.setPeriodoDesc("16 Ene - 31 Ene");
                            break;
                        case 3:
                            nuevoInforme.setPeriodoDesc("1 Feb - 15 Feb");
                            break;
                        case 4:
                            nuevoInforme.setPeriodoDesc("16 Feb - 28 Feb");
                            break;
                        case 5:
                            nuevoInforme.setPeriodoDesc("01 Mar - 15 Mar");
                            break;
                        case 6:
                            nuevoInforme.setPeriodoDesc("16 Mar - 31 Mar");
                            break;
                        case 7:
                            nuevoInforme.setPeriodoDesc("01 Abr - 15 Abr");
                            break;
                        case 8:
                            nuevoInforme.setPeriodoDesc("16 Abr - 30 Abr");
                            break;
                        case 9:
                            nuevoInforme.setPeriodoDesc("01 May - 15 May");
                            break;
                        case 10:
                            nuevoInforme.setPeriodoDesc("16 May - 31 May");
                            break;
                        case 11:
                            nuevoInforme.setPeriodoDesc("01 Jun - 15 Jun");
                            break;
                        case 12:
                            nuevoInforme.setPeriodoDesc("16 Jun - 30 Jun");
                            break;
                        case 13:
                            nuevoInforme.setPeriodoDesc("01 Jul - 15 Jul");
                            break;
                        case 14:
                            nuevoInforme.setPeriodoDesc("16 Jul - 31 Jul");
                            break;
                        case 15:
                            nuevoInforme.setPeriodoDesc("01 Ago - 15 Ago");
                            break;
                        case 16:
                            nuevoInforme.setPeriodoDesc("16 Ago - 31 Ago");
                            break;
                        case 17:
                            nuevoInforme.setPeriodoDesc("01 Sep - 15 Sep");
                            break;
                        case 18:
                            nuevoInforme.setPeriodoDesc("16 Sep - 30 Sep");
                            break;
                        case 19:
                            nuevoInforme.setPeriodoDesc("01 Oct - 15 Oct");
                            break;
                        case 20:
                            nuevoInforme.setPeriodoDesc("16 Oct - 31 Oct");
                            break;
                        case 21:
                            nuevoInforme.setPeriodoDesc("01 Nov - 15 Nov");
                            break;
                        case 22:
                            nuevoInforme.setPeriodoDesc("16 Nov - 30 Nov");
                            break;
                        case 23:
                            nuevoInforme.setPeriodoDesc("01 Dic - 15 Dic");
                            break;
                        case 24:
                            nuevoInforme.setPeriodoDesc("16 Dic - 31 Dic");
                            break;
                    }
                }

                result.add(nuevoInforme);
            }
            return result;
        }

        public boolean existePeriodoInforme(int key){
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
