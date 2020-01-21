/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolIntegerArray;
import net.casnw.home.poolData.PoolString;
import net.casnw.home.poolData.PoolStringArray;
import GBHM.Utils.StringUtil;

/**
 *
 * @author longyinping
 */
public class RiverInitialInput extends AbsComponent {

    //In
    public PoolString dischargeInputPath;
    public PoolDouble2DArray b;//nsub,nflow //width of river (m)
    public PoolDouble2DArray roughness;//nsub,nflow //Manning's roughness
    public PoolDouble2DArray s0;//nsub,nflow //slope of river bed (ND)
    public PoolStringArray subName;  //nsub
    public PoolIntegerArray nflow;//nsub    
    //end of In
    //Out
    public PoolDouble2DArray qlin1_river;//nsub,nflow //lateral inflow of last time step (m^3/s)
    public PoolDouble2DArray q1_river;//nsub,nflow    //discharge of last time step (m^3/s)  
    public PoolDouble2DArray Drw_river;//nsub,nflow    

    //end of Out
    @Override
    public void init() throws IOException {

        double[][] qlin1;
        double[][] q1;
        double[][] Drw;
        int nsub = subName.getValue().length;

        qlin1 = new double[nsub][100];
        q1 = new double[nsub][100];


        //qlin1 initialization

        for (int i = 0; i < nsub; i++) {
            for (int j = 0; j < nflow.getValue(i); j++) {
                qlin1[i][j] = 0;
            }
        }
        //end of qlin1 initialization

        //q1 initialization
        if (dischargeInputPath.getValue() == null || dischargeInputPath.getValue().equals("")) {//computed 
            for (int i = 0; i < nsub; i++) {
                q1[i][0] = 0.5;
                for (int j = 1; j < nflow.getValue(i); j++) {
                    q1[i][j] = q1[i][j - 1] + 0.4;
                }
            }
        } else {//read from file   
            for (int i = 0; i < nsub; i++) {
                q1[i] = this.readDischarge(dischargeInputPath.getValue() + subName.getValue(i) + "I_flow.dat");
            }
        }
        //end of q1 initialization

        //initial river water depth calculation        
        Drw = new double[nsub][100];
        for (int i = 0; i < nsub; i++) {
            Drw[i] = this.calRiverWaterDepth(i, nflow.getValue(), q1, b.getValue(), roughness.getValue(), s0.getValue());
        }
        //end of initial river water depth calculation

        qlin1_river.setValue(qlin1);
        q1_river.setValue(q1);
        Drw_river.setValue(Drw);
    }

    private double[] readDischarge(String fileName) throws IOException {
        double[] array;
        ArrayList<Double> q = new ArrayList();
        String line;
        String[] split;
        FileReader fin = new FileReader(fileName);
        BufferedReader bin = new BufferedReader(fin);
        line = StringUtil.delSpace(bin.readLine());
        while (line != null) {
            split = line.split(" +|, +|\t");
            q.add(Double.parseDouble(split[1]));
            line = StringUtil.delSpace(bin.readLine());
        }

        array = new double[q.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = q.get(i);
        }
        return array;
    }

    private double[] calRiverWaterDepth(int isub, int[] nflow, double[][] q1, double[][] b, double[][] roughness, double[][] s0) {
        double[] depth = new double[nflow[isub]];
        double criterion = 0.01;
        double h1, tmp, f, df;
        double h2 = 0;
        for (int i = 0; i < nflow[isub]; i++) {
            h1 = q1[isub][i] / b[isub][i];           
            for (int j = 1; j < 10; j++) {
                tmp = roughness[isub][i] * q1[isub][i] / Math.sqrt(s0[isub][i]);
                f = b[isub][i] * h1 - Math.pow(tmp, 0.6) * Math.pow(b[isub][i] + 2.0 * h1, 0.4);
                if (j > 1 && Math.abs(f) < criterion) {
                    break;
                }
                df = b[isub][i] - 0.8 * Math.pow(tmp / (b[isub][i] + 2.0 * h1), 0.6);
                h2 = h1 - f / df;
                h1 = h2;
            }
            depth[i] = h2;
        }
        return depth;
    }
}
