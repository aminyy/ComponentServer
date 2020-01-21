/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.Modules1;

import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolInteger;

/**
 *
 * @author longyinping
 */
public class SoilTemperature extends AbsComponent {

    //IN
    public PoolDouble albday;//albedo of ground for day
    public PoolDouble radiation;//solar radiation for the day,MJ/m^2
    public PoolDouble snow;//amount of water in snow,mm H2O
    public PoolDouble sol_avbd;//average bulk density of soil profile,Mg/m^3
    public PoolDouble sol_cov;//amount of residue on soil surface,kg/ha
    public PoolInteger sol_nly;//number of soil layers in profile
    public PoolDouble sol_sw;//amount of water stored in soil profile,mm H2O
    public PoolDoubleArray sol_z;//depth of each soil layer,mm
    public PoolDouble tmn;//minimum temperature for the day,deg C
    public PoolDouble tmp_an;//average annual air temperature,deg C
    public PoolDouble tmpav;//average temperature for the day,deg C
    public PoolDouble tmx;//maximum temperature for the day,deg C
    //INOUT
    public PoolDoubleArray sol_tmp;//average temperature of soil layer,deg C

    @Override
    public void run() {
        double f, dp, ww, b, wc, dd, xx, st0, Ds;
        double tlag, df, zd, bcv, tbare, tcov, tmp_srf;
        /*
         * !!    ~ ~ ~ LOCAL DEFINITIONS ~ ~ ~
         !!    name        |units         |definition
         !!    ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
         !!    b           |none          |variable to hold intermediate calculation
         !!    bcv         |none          |lagging factor for cover
         !!    dd          |mm            |damping depth for day
         !!    df          |none          |depth factor
         !!    dp          |mm            |maximum damping depth
         !!    f           |none          |variable to hold intermediate calculation 
         !!                               |result
         !!    j           |none          |HRU number
         !!    k           |none          |counter
         !!    st0         |MJ/m^2        |radiation hitting soil surface on day
         !!    tbare       |deg C         |temperature of bare soil surface
         !!    tcov        |deg C         |temperature of soil surface corrected for
         !!                               |cover
         !!    tlag        |none          |lag coefficient for soil temperature
         !!    tmp_srf     |deg C         |temperature of soil surface
         !!    wc          |none          |scaling factor for soil water impact on daily
         !!                               |damping depth
         !!    ww          |none          |variable to hold intermediate calculation
         !!    xx          |none          |variable to hold intermediate calculation
         !!    zd          |none          |ratio of depth at center of layer to
         !!                               |damping depth
         !!    ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
         * 
         */
        tlag = 0.8;
        //calculate damping depth
        //   calculate maximum damping depth
        //   SWAT Theory2005 equation 1:1.3.6
        f = 0.0;
        dp = 0.0;
        f = sol_avbd.getValue() / (sol_avbd.getValue() + 686.0 * Math.exp(-5.63 * sol_avbd.getValue()));
        dp = 1000. + 2500. * f;

        //calculate scaling factor for soil water
        //   SWAT manual equation 1:1.3.7   
        Ds = 0.0;
        for (int i = 0; i < sol_z.length; i++) {
            Ds = Ds + sol_z.getValue(i);
        }
        ww = 0.0;
        wc = 0.;
        ww = .356 - .144 * sol_avbd.getValue();
        wc = sol_sw.getValue() / (ww * Ds);
        // calculate daily value for damping depth
        // SWAT manual equation 1:1.3.8
        b = 0.;
        f = 0.;
        dd = 0.;
        b = Math.log(500. / dp);
        f = Math.exp(b * Math.pow((1. - wc) / (1. + wc), 2));
        dd = f * dp;

        // calculate lagging factor for soil cover impact on soil surface temp
        // SWAT manual equation 1:1.3.11
        bcv = 0.;
        bcv = sol_cov.getValue() / (sol_cov.getValue() + Math.exp(7.563 - 1.297e-4 * sol_cov.getValue()));
        if (snow.getValue() != 0.) {
            if (snow.getValue() <= 120.) {
                xx = snow.getValue() / (snow.getValue() + Math.exp(6.055 - .3002 * snow.getValue()));
            } else {
                xx = 1.;
            }
            bcv = Math.max(xx, bcv);
        }

        // calculate temperature at soil surface
        st0 = 0.;
        tbare = 0.;
        tcov = 0.;
        tmp_srf = 0.;
        // SWAT manual equation 1:1.3.10
        st0 = (radiation.getValue() * (1. - albday.getValue()) - 14.) / 20.;
        // SWAT manual equation 1:1.3.9
        tbare = tmpav.getValue() + 0.5 * (tmx.getValue() - tmn.getValue()) * st0;
        // SWAT manual equation 1:1.3.12
        tcov = bcv * sol_tmp.getValue(0) + (1. - bcv) * tbare;

        //taking average of bare soil and covered soil as in APEX
        //previously using minumum causing soil temp to decrease
        //in summer due to high biomass
        tmp_srf = 0.5 * (tbare + tcov); //following Jimmy's code

        //calculate temperature for each layer on current day
        xx = 0.;
        Ds = 0.;
        for (int k = 0; k < sol_nly.getValue(); k++) {
            zd = 0.;
            df = 0.;
            Ds = Ds + sol_z.getValue(k);
            zd = (xx + Ds) / 2.;  // calculate depth at center of layer
            zd = zd / dd;                 // SWAT manual equation 1:1.3.5
            // SWAT manual equation 1:1.3.4
            df = zd / (zd + Math.exp(-.8669 - 2.0775 * zd));
            // SWAT manual equation 1:1.3.3
            sol_tmp.setValue(k, tlag * sol_tmp.getValue(k) + (1. - tlag) * (df * (tmp_an.getValue() - tmp_srf) + tmp_srf));
            xx = Ds;
        }
    }
}
