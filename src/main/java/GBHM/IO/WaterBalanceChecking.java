/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.model.SpatialContext;
import net.casnw.home.model.TemporalContext;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDoubleArray;

/**
 *
 * @author longyinping
 */
public class WaterBalanceChecking extends AbsComponent {

    //IN
    public PoolDouble dt;
    public PoolDouble area;
    public PoolDouble prec;
    public PoolDoubleArray w;
    public PoolDouble GWst;
    public PoolDouble Cst;
    public PoolDouble snowdepth;
    public PoolDouble sst;
    public PoolDouble Eact;
    public PoolDouble q_hillslope;
    public PoolDouble qsub;
    public PoolDoubleArray D;
    private TemporalContext temporalContext;
    private SpatialContext spatialContext;
    double[][] w1, sst1, snow1, gwst1, cst1;
    double w2, sst2, snow2, gwst2, cst2;
    SimpleDateFormat format;
    int time;
    PrintWriter writer;
    PrintWriter writer1;

    @Override
    public void init() throws FileNotFoundException {
        temporalContext = (TemporalContext) this.getContext().getContext().getContext();
        spatialContext = (SpatialContext) this.getContext();
        format = new SimpleDateFormat("yyyyMMdd");
        int nrows = spatialContext.getRowsNum();
        int ncols = spatialContext.getColsNum();
        w1 = new double[nrows][ncols];
        sst1 = new double[nrows][ncols];
        snow1 = new double[nrows][ncols];
        gwst1 = new double[nrows][ncols];
        cst1 = new double[nrows][ncols];
        time = -1;
        writer = new PrintWriter("cell_balance.txt");
        writer1 = new PrintWriter("runoff.txt");
    }

    @Override
    public void run() {
        int row = spatialContext.getCurrentRowNum();
        int col = spatialContext.getCurrentColNum();


        time++;
        if (time > 23) {
            time = 0;
        }



        double waterdepth = 0;
        for (int i = 0; i < D.getValue().length; i++) {
            waterdepth = waterdepth + w.getValue(i) * D.getValue(i) * 1000;
        }

        if (temporalContext.getCurrentTime().getTime().compareTo(temporalContext.getStartTime().getTime()) == 0) {
            w1[row][col] = waterdepth;
            sst1[row][col] = sst.getValue();
            snow1[row][col] = snowdepth.getValue();
            gwst1[row][col] = GWst.getValue();
            cst1[row][col] = Cst.getValue();
        }
        if (temporalContext.getCurrentTime().getTime().after(temporalContext.getStartTime().getTime())) {
            w2 = waterdepth;
            sst2 = sst.getValue();
            snow2 = snowdepth.getValue();
            gwst2 = GWst.getValue();
            cst2 = Cst.getValue();

            double balance;
            balance = prec.getValue()
                    - Eact.getValue()
                    - (qsub.getValue() + q_hillslope.getValue()) * dt.getValue() / area.getValue() * 1000
                    - (w2 - w1[row][col])
                    - (sst2 - sst1[row][col])
                    - (snow2 - snow1[row][col])
                    - (gwst2 - gwst1[row][col]) * 1000
                    - (cst2 - cst1[row][col]);
            //           System.out.println(format.format(temporalContext.getCurrentTime().getTime()) + "  " + time + ": "
            //                   + "row = " + spatialContext.getCurrentRowNum() + "  col = " + spatialContext.getCurrentColNum());

            if (row == 44 && col == 61) {
                writer.println(format.format(temporalContext.getCurrentTime().getTime()) + "  " + time + "  "
                        + prec.getValue()
                        + " " + Eact.getValue()
                        + " " + (qsub.getValue() + q_hillslope.getValue()) * dt.getValue() / area.getValue() * 1000
                        + " " + (w2 - w1[row][col])
                        + " " + (sst2 - sst1[row][col])
                        + " " + (snow2 - snow1[row][col])
                        + " " + (gwst2 - gwst1[row][col]) * 1000
                        + " " + (cst2 - cst1[row][col]));
            }
            if (balance > 1.0e-5) {
                System.out.println(" not meeting water balance: " + row + "  " + col + "  " + balance);
            }

            w1[row][col] = w2;
            sst1[row][col] = sst2;
            snow1[row][col] = snow2;
            gwst1[row][col] = gwst2;
            cst1[row][col] = cst2;

            writer1.println(format.format(temporalContext.getCurrentTime().getTime()) + "  " + time + "  "
                    + row + "  " + col + "  " + qsub.getValue() + "  " + q_hillslope.getValue());
        }
    }
}
