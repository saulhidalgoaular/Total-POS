package totalpos;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Saúl Hidalgo
 */
public class Price implements Serializable {
    private Date date;
    private Double quant;

    public Price(Date date, Double quant) {
        this.date = date;
        this.quant = Shared.round(quant,2);
    }

    public Price(Price p){
        this.date = p.getDate();
        this.quant = Shared.round(p.getQuant(),2);
    }

    public Date getDate() {
        return date;
    }

    public Double getQuant() {
        return quant;
    }

    public Price newest(Price o){
        if ( getDate().before(o.getDate()) ){
            return o;
        }
        return this;
    }

    @Override
    public String toString() {
        return Shared.df.format(quant) + "";
    }

    public Price plusIva(){
        return new Price(
                getDate(),
                Shared.round(getQuant()*(Double.valueOf(Shared.getConfig().get("iva"))+1.0),2));
    }

    public Price getIva(){
        return new Price(
                getDate(),
                Shared.round(getQuant()*(Double.valueOf(Shared.getConfig().get("iva"))),2));
    }

    public Price withDiscount(Double p){
        // TODO Parse discount like expressions;
        return new Price(
                getDate(),
                (getQuant()*(100.0-p)/100.0));
    }

}
