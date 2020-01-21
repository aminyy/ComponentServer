/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.Utils;

import java.util.Date;

/**
 *
 * @author longyinping
 */
public class ClimateRecord {

    private Date DATE;
    private double[] VALUE;

    public void ClimateRecord() {
    }

    public Date getDATE() {
        return this.DATE;
    }

    public void setDATE(Date date) {
        this.DATE = date;
    }

    public double[] getVALUE() {
        return this.VALUE;
    }

    public void setValue(double[] value) {
        this.VALUE = value;
    }
}
