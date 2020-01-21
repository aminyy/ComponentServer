/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import java.io.IOException;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolInteger2DArray;
import net.casnw.home.poolData.PoolInteger3DArray;
import net.casnw.home.poolData.PoolIntegerArray;
import net.casnw.home.poolData.PoolString;
import net.casnw.home.poolData.PoolStringArray;
import GBHM.Utils.RiverReader;
import GBHM.Utils.SubBasinReader;

/**
 *
 * @author longyinping
 */
public class RiverParameter extends AbsComponent {

    //In
    public PoolString kfsFile;
    public PoolString riverFilePath;
    //end of In
    public PoolInteger nsub;
    public PoolIntegerArray upstreamID1;//nsub
    public PoolIntegerArray upstreamID2;//nsub
    public PoolDouble2DArray dx;//nsub,nflow//flow interval length (m)
    public PoolDouble2DArray s0;//nsub,nflow//slope of river bed (ND)
    public PoolDouble2DArray b;//nsub,nflow//width of river (m)
    public PoolDouble2DArray roughness;//nsub,nflow//Manning's roughness
    public PoolDouble2DArray Dr_river;//nsub,nflow//river depth in the flow interval(m)
    public PoolInteger3DArray row;//nsub,nflow,ngrid
    public PoolInteger3DArray col;//nsub,nflow,ngrid
    public PoolInteger2DArray ngrid;//nsub,nflow
    public PoolIntegerArray nflow;//nsub
    public PoolStringArray subName;
    private SubBasinReader subReader;
    private RiverReader riverReader;

    @Override
    public void init() throws IOException {

        int nsub1;
        double[][] dx1;
        double[][] s01;
        double[][] b1;
        double[][] roughness1;
        double[][] Dr1;
        int[][][] row1;
        int[][][] col1;
        int[][] ngrid1;
        int[] nflow1;
        String[] subName1;

        subReader = new SubBasinReader(kfsFile.getValue());

        nsub1 = subReader.getSubNum();

        dx1 = new double[nsub1][];
        s01 = new double[nsub1][];
        b1 = new double[nsub1][];
        roughness1 = new double[nsub1][];
        Dr1 = new double[nsub1][];
        row1 = new int[nsub1][][];
        col1 = new int[nsub1][][];
        ngrid1 = new int[nsub1][];
        nflow1 = new int[nsub1];

        subName1 = subReader.getSubName();
        for (int i = 0; i < nsub1; i++) {
            riverReader = new RiverReader(riverFilePath.getValue() + subName1[i] + "_river");
            dx1[i] = riverReader.getFlowLength();
            s01[i] = riverReader.getFlowbedSlope();
            b1[i] = riverReader.getFlowWidth();
            roughness1[i] = riverReader.getFlowRoughness();
            Dr1[i] = riverReader.getFlowDepth();
            row1[i] = riverReader.getFlowRows();
            col1[i] = riverReader.getFlowCols();
            ngrid1[i] = riverReader.get_ngrid();
            nflow1[i] = riverReader.get_nflow();
        }

        nsub.setValue(nsub1);
        upstreamID1.setValue(subReader.getUpstreamID1());
        upstreamID2.setValue(subReader.getUpstreamID2());
        dx.setValue(dx1);
        s0.setValue(s01);
        b.setValue(b1);
        roughness.setValue(roughness1);
        Dr_river.setValue(Dr1);
        row.setValue(row1);
        col.setValue(col1);
        ngrid.setValue(ngrid1);
        nflow.setValue(nflow1);
        subName.setValue(subName1);

    }
}
