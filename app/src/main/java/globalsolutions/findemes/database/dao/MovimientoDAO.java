package globalsolutions.findemes.database.dao;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;

import globalsolutions.findemes.database.model.Gasto;
import globalsolutions.findemes.database.model.Ingreso;
import globalsolutions.findemes.database.model.MovimientoItem;
import globalsolutions.findemes.database.util.Constantes;

/**
 * Created by manuel.molero on 13/02/2015.
 */
public class MovimientoDAO {

    public ArrayList<MovimientoItem> cargaMovimientos(Context context){
        MovimientoItem[] movsArray;
        Gasto[] gastos = new GastoDAO(context).selectGastos();
        Ingreso[] ingresos = new IngresoDAO(context).selectIngresos();
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

        return new ArrayList(Arrays.asList(movsArray));
    }
}
