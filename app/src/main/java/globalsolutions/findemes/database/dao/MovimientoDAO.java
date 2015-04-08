package globalsolutions.findemes.database.dao;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import globalsolutions.findemes.R;
import globalsolutions.findemes.database.model.Gasto;
import globalsolutions.findemes.database.model.Ingreso;
import globalsolutions.findemes.database.model.MovimientoItem;
import globalsolutions.findemes.database.model.Registro;
import globalsolutions.findemes.database.util.Constantes;

/**
 * Created by manuel.molero on 13/02/2015.
 */
public class MovimientoDAO {

    public ArrayList<MovimientoItem> cargaMovimientos(Context context){
        MovimientoItem[] movsArray;
        Gasto[] gastos = new GastoDAO(context).selectGastos();
        Ingreso[] ingresos = new IngresoDAO(context).selectIngresos();
        Registro[] registros = new RegistroDAO(context).selectRegistros();

        movsArray = new MovimientoItem[gastos.length + ingresos.length];
        for(int i = 0 ; i < gastos.length ; i++){
            MovimientoItem m = new MovimientoItem();
            m.setValor(gastos[i].getValor());
            m.setDescripcion(gastos[i].getDescripcion());
            m.setFecha(gastos[i].getFecha());
            m.setCategoria(gastos[i].getGrupoGasto().getGrupo());
            m.setTipoMovimiento((String) Constantes.TIPO_MOVIMIENTO_GASTO);
            movsArray[i] = m;
        }
        for(int j = 0 ; j < ingresos.length ; j++){
            MovimientoItem m = new MovimientoItem();
            m.setValor(ingresos[j].getValor());
            m.setDescripcion(ingresos[j].getDescripcion());
            m.setFecha(ingresos[j].getFecha());
            m.setCategoria(ingresos[j].getGrupoIngreso().getGrupo());
            m.setTipoMovimiento((String) Constantes.TIPO_MOVIMIENTO_INGRESO);
            movsArray[gastos.length + j] = m;
        }

        ArrayList<MovimientoItem> movs = new ArrayList(Arrays.asList(movsArray));
        ArrayList<MovimientoItem> registrosTratados = tratarRegistros(registros,context);
        movs.addAll(registrosTratados);

        //ordenamos los movimientos por fecha descendente
        Collections.sort(movs, new Comparator<MovimientoItem>() {
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public int compare(MovimientoItem o1, MovimientoItem o2) {
                try {
                    return f.parse(o1.getFecha()).compareTo(f.parse(o2.getFecha()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        return movs;
    }

    private ArrayList<MovimientoItem> tratarRegistros(Registro[] registros, Context context){
        ArrayList<MovimientoItem> ret = new ArrayList<MovimientoItem>();

        for(int i = 0 ; i < registros.length ; i++) {
            if (registros[i].getActivo().equals(Constantes.REGISTRO_ACTIVO)) {
                if (registros[i].getPeriodicidad().equals(context.getResources().getString(R.string.PERIODICIDAD_REGISTRO_MENSUAL))){
                    //hay que desglosar el registro en 12 movimientos mensuales
                    for(int j = 1 ; j < 13 ; j++) {
                        MovimientoItem m = new MovimientoItem();
                        m.setValor(registros[i].getValor());
                        m.setDescripcion(registros[i].getDescripcion());
                        //tratamos el caso especial de la fecha
                        m.setFecha("01/" + String.format("%010d", new Integer(j)) + "/" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
                        m.setCategoria(registros[i].getGrupo());
                        m.setTipoMovimiento(registros[i].getTipo());
                        ret.add(m);
                    }
                }
                else if (registros[i].getPeriodicidad().equals(context.getResources().getString(R.string.PERIODICIDAD_REGISTRO_ANUAL))){
                    //anyadimos solo un movimiento anual
                    MovimientoItem m = new MovimientoItem();
                    m.setValor(registros[i].getValor());
                    m.setDescripcion(registros[i].getDescripcion());
                    //tratamos el caso especial de la fecha
                    m.setFecha("01/01" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
                    m.setCategoria(registros[i].getGrupo());
                    m.setTipoMovimiento(registros[i].getTipo());
                    ret.add(m);
                }
            }
        }

        return ret;
    }
}
