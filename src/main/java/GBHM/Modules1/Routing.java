/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.Modules1;

import net.casnw.home.model.AbsComponent;
import net.casnw.home.model.TemporalContext;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolInteger2DArray;
import net.casnw.home.poolData.PoolInteger3DArray;
import net.casnw.home.poolData.PoolIntegerArray;

/**
 *
 * @author longyinping
 */
public class Routing extends AbsComponent {

    //IN
    public PoolDouble dt;
    public PoolDouble2DArray qin;//ncols,nrows,from gridIteration
    public PoolInteger nsub;
    public PoolIntegerArray upstreamID1;//nsub
    public PoolIntegerArray upstreamID2;//nsub
    public PoolDouble2DArray dx;//nsub,nflow//flow interval length (m)
    public PoolDouble2DArray s0;//nsub,nflow//slope of river bed (ND)
    public PoolDouble2DArray b;//nsub,nflow//width of river (m)
    public PoolDouble2DArray roughness;//nsub,nflow//Manning's roughness    
    public PoolInteger3DArray row;//nsub,nflow,ngrid
    public PoolInteger3DArray col;//nsub,nflow,ngrid
    public PoolInteger2DArray ngrid;//nsub,nflow
    public PoolIntegerArray nflow;//nsub
    public PoolDouble2DArray Dr;//nsub,nflow
    //end of IN
    //OUT
    public PoolDouble2DArray q1;//nsub,nflow
    public PoolDouble2DArray q2;//nsub,nflow
    public PoolDouble2DArray Drw;
    public PoolDouble2DArray qlin1;//nsub,nflow
    //end of OUT
    //private double q1_upper;
    private double q2_upper;
    private KinematicWaveModel nkws = new KinematicWaveModel();
    private TemporalContext hourlyContext = new TemporalContext();
    private TemporalContext temporalContext = new TemporalContext();

    @Override
    public void init() {
        q2.setValue(new double[nsub.getValue()][100]);
        hourlyContext = (TemporalContext) this.getContext().getContext();
        temporalContext = (TemporalContext) this.getContext().getContext().getContext();
    }

    @Override
    public void run() {
        int[][] ngrid1 = ngrid.getValue();
        double qlin2;
        //       System.out.println(qin.getCellValue(13, 29));
        for (int i = 0; i < nsub.getValue(); i++) {
            for (int j = 0; j < nflow.getValue(i); j++) {
                q2_upper = defineRiverNetwork(q2.getValue(), i, j, nflow.getValue(), upstreamID1.getValue(), upstreamID2.getValue());
                nkws.dt = dt.getValue();
                nkws.qlin1 = qlin1.getCellValue(i, j);
                qlin2 = calLateralInflow(qin.getValue(), row.getValue(i, j), col.getValue(i, j), ngrid1[i][j]);
                nkws.qlin2 = qlin2;
                if (this.temporalContext.getCurrentTime().compareTo(this.temporalContext.getStartTime()) == 0
                        && this.hourlyContext.getCurrentIteratorNum() == 1) {
                    nkws.qlin1 = qlin2;
                }

                nkws.q1 = q1.getCellValue(i, j);
                nkws.q2_upper = q2_upper;
                nkws.dx = dx.getCellValue(i, j);
                nkws.s0 = s0.getCellValue(i, j);
                if (Drw.getCellValue(i, j) > 0.5 * Dr.getCellValue(i, j) && Drw.getCellValue(i, j) <= 1.0 * Dr.getCellValue(i, j)) {
                    nkws.b = 1.1 * b.getCellValue(i, j);
                    nkws.roughness = 1.5 * roughness.getCellValue(i, j);
                } else if (Drw.getCellValue(i, j) > 1.0 * Dr.getCellValue(i, j)) {
                    nkws.b = 1.5 * b.getCellValue(i, j);
                    nkws.roughness = 3.0 * roughness.getCellValue(i, j);
                } else {
                    nkws.b = b.getCellValue(i, j);
                    nkws.roughness = roughness.getCellValue(i, j);
                }
                nkws.run();

                //out
                Drw.setCellValue(i, j, nkws.Drw);
                q2.setCellValue(i, j, nkws.q2);

                q1.setCellValue(i, j, nkws.q2);
                qlin1.setCellValue(i, j, qlin2);
            }
        }
    }

    //calculate lateral inflow for a flow interval
    private double calLateralInflow(double[][] qin, int[] rows, int[] cols, int ngrid) {
        double qin1;
        int r, c;
        qin1 = 0;
        for (int i = 0; i < ngrid; i++) {
            r = rows[i];
            c = cols[i];
            qin1 = qin1 + qin[r][c];
        }
        return qin1;
    }

    private double defineRiverNetwork(double[][] q2, int isub, int iflow, int[] nflow, int[] upStreamID1, int[] upStreamID2) {
        int up1, up2;
        double q_upper;
        if (iflow == 0) {
            if (upStreamID1[isub] == -1) {
                //         q1_upper = 0;
                q_upper = 0;
            } else {
                up1 = upStreamID1[isub];
                up2 = upStreamID2[isub];
                //         q1_upper = q1[up1][nflow[up1] - 1] + q1[up2][nflow[up2] - 1];
                q_upper = q2[up1][nflow[up1] - 1] + q2[up2][nflow[up2] - 1];
            }
        } else {
            //     q1_upper = q1[isub][iflow-1];
            q_upper = q2[isub][iflow - 1];
        }
        return q_upper;
    }
}
