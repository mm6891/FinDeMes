package globalsolutions.findemes.database.model;

/**
 * Created by manuel.molero on 06/02/2015.
 */
public class InformeItem {

    String periodoDesc;
    String ingresoValor;
    String gastoValor;

    public String getPeriodoDesc() {
        return periodoDesc;
    }

    public void setPeriodoDesc(String periodoDesc) {
        this.periodoDesc = periodoDesc;
    }

    public String getIngresoValor() {
        return ingresoValor;
    }

    public void setIngresoValor(String ingresoValor) {
        this.ingresoValor = ingresoValor;
    }

    public String getGastoValor() {
        return gastoValor;
    }

    public void setGastoValor(String gastoValor) {
        this.gastoValor = gastoValor;
    }

    public String getTotalValor() {
        return totalValor;
    }

    public void setTotalValor(String totalValor) {
        this.totalValor = totalValor;
    }

    String totalValor;

}
