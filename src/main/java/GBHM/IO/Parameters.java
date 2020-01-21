/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import java.util.List;
import net.casnw.home.io.SpatialParameterReader;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author longyinping
 */
public class Parameters extends AbsComponent {

    //@In
    public PoolString vegeParFile;
    public PoolString soilParFile;
    public PoolInteger soilcode;
    public PoolInteger landcode;
    public PoolDoubleArray D;
    public PoolDouble Ds;
    //@Out
    //vegetation parameters
    public PoolDouble Kcanopy;
    public PoolDouble LAImax;
    public PoolDouble kcrop;
    public PoolDouble root;
    public PoolDouble anik;
    public PoolDouble Sstmax;
    public PoolDouble surfn;
    public PoolString landuse;
    // soil parameters
    public PoolDouble wsat;
    public PoolDouble wfld;
    public PoolDouble wrsd;
    public PoolDouble alpha;
    public PoolDouble watern;
    public PoolDoubleArray k0;
    public PoolDouble kg;
    public PoolDouble GWcs;
    public PoolDouble SMF;
    public PoolDouble SMFTMP;
    public PoolDouble c1_factor;
    public PoolDouble c2_factor1;
    public PoolDouble c2_factor2;
    public PoolDouble c3_factor1;
    public PoolDouble c3_factor2;
    public PoolDouble c5;
    public PoolDouble para_r;
    public PoolDouble ss_f;
    public PoolDouble soil_con_f;
    private double fice;
    Double[][] soilPar;
    Double[][] vegePar;
    SpatialParameterReader SPsoilPar;
    SpatialParameterReader SPvegePar;
    
    @Override
    public void init() throws Exception {
        SPsoilPar = new SpatialParameterReader(soilParFile.getValue());
        SPvegePar = new SpatialParameterReader(vegeParFile.getValue());
        soilPar = SPsoilPar.getParameter();
        vegePar = SPvegePar.getParameter();
    }
    
    @Override
    public void run() throws Exception {
        fice = 0.5;
        List<Double> soil;
        List<Double> vege;
        soil = SPsoilPar.getParaFromID(soilcode.getValue(), soilPar);
        vege = SPvegePar.getParaFromID(landcode.getValue(), vegePar);

        //vegetation parameters
        Kcanopy.setValue(vege.get(1));
        LAImax.setValue(vege.get(2));
        kcrop.setValue(vege.get(3));
        root.setValue(vege.get(4));
        anik.setValue(vege.get(5));
        Sstmax.setValue(vege.get(6));
        surfn.setValue(vege.get(7));
        landuse.setValue(landuseQuery(landcode.getValue()));

        //soil parameters
        wsat.setValue(soil.get(1));
        wrsd.setValue(soil.get(2));
        alpha.setValue(soil.get(3));
        watern.setValue(soil.get(4));
        double ksat1 = soil.get(5);
        double ksat2 = soil.get(6);
        kg.setValue(fice * soil.get(7));
        GWcs.setValue(soil.get(8));
        k0.setValue(calSaturatedCon(ksat1, ksat2, D.getValue(), Ds.getValue()));
        for (int i = 0; i < k0.length; i++) {
            k0.setValue(i, fice * k0.getValue(i));
        }
        wfld.setValue(MoistureFromSuction_V(wsat.getValue(), wrsd.getValue(), watern.getValue(),
                alpha.getValue(), -1.02));
        soil_con_f.setValue(1);

        //snowmelt parameters
        SMF.setValue(0.1);
        SMFTMP.setValue(0.15);

        //evaporation parameters
        c1_factor.setValue(0.31);
        c2_factor1.setValue(0.05);
        c2_factor2.setValue(0.10);
        c3_factor1.setValue(0.4);
        c3_factor2.setValue(0.4);
        c5.setValue(0.1);
        para_r.setValue(0.1);

        //slope shape factor
        ss_f.setValue(0.1);
        
    }
    
    @Override
    public void clear() throws Exception {
    }
    
    private String landuseQuery(int landcode) {
        String vege = "";
        if (landcode == 1) {
            vege = "waterbody";
        } else if (landcode == 2) {
            vege = "urban-area";
        } else if (landcode == 3) {
            vege = "baresoil";
        } else if (landcode == 4) {
            vege = "forest";
        } else if (landcode == 5) {
            vege = "irrigated-cropland";
        } else if (landcode == 6) {
            vege = "non-irrigated cropland";
        } else if (landcode == 7) {
            vege = "grassland";
        } else if (landcode == 8) {
            vege = "shrub";
        } else if (landcode == 9) {
            vege = "caodian";
        } else if (landcode == 10) {
            vege = "Tundra-snowice";
        }
        return vege;
    }
    
    public static double MoistureFromSuction_V(double wsat, double wrsd, double n,
            double alpha, double ps) {
        double w;
        double se, tmpe, tmpps, m;
        tmpps = 100.0 * ps; // m->cm
        m = 1.0 - 1.0 / n;
        tmpe = 1.0 + Math.pow(alpha * Math.abs(tmpps), n);
        se = Math.pow(1.0 / tmpe, m);
        w = se * (wsat - wrsd) + wrsd;
        if (w > wsat) {
            w = wsat;
        }
        if (w < wrsd) {
            w = wrsd;
        }
        return w;
    }
    
    public double[] calSaturatedCon(double ksat1, double ksat2, double[] D, double Ds) {
        double tmp = 0;
        double[] ksat;
        double f;
        int i;
        ksat = new double[D.length];
        for (i = 0; i < D.length; i++) {
            tmp = tmp + D[i];
            if (landcode.getValue() == 5) {
                ksat1 = ksat1 / 10;
            }
            if (landcode.getValue() == 6) {
                ksat1 = ksat1 / 5;
            }
            f = -Math.log(ksat2 / ksat1) / Ds;
            ksat[i] = ksat1 * Math.exp(-f * tmp);
        }
        return ksat;
    }
}
