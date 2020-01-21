/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author longyinping
 */
public class RiverReader {

    private int nflow;//total number of flow-intervals in a sub-basin
    private int[] ngrid;//number of grids in this flow-interval
    private double[] dx;//flow interval length (m)
    private double[] s0;
    private double[] b;
    private double[] roughness;
    private double[] Dr;
    private int[][] row;
    private int[][] col;
//    int gridnum;

    public RiverReader(String RiverFile) throws IOException {
        readRiver(RiverFile);
    }

    public int get_nflow() {
        return nflow;
    }

    public int get_ngrid(int iflow) {
        return ngrid[iflow];
    }

    public int[] get_ngrid() {
        return ngrid;
    }

    public int[] getFlowRows(int iflow) {
        return row[iflow];
    }

    public int[][] getFlowRows() {
        return row;
    }

    public int[] getFlowCols(int iflow) {
        return col[iflow];
    }

    public int[][] getFlowCols() {
        return col;
    }

    /*   public int[] getNextRowCol() {
     int iflow;
     int igrid;
     int i;
     int[] rowcol;
     rowcol = new int[2];
     iflow = flownum;
     igrid = gridnum;
     for (i = flownum; i > 0; i--) {
     igrid = igrid - ngrid[i - 1];
     }
     rowcol[0] = row[iflow][igrid];
     rowcol[1] = col[iflow][igrid];

     gridnum++;
     if (igrid + 1 == ngrid[flownum]) {
     flownum++;
     }

     return rowcol;
     }
     * */
    /*

     public boolean hasNextGrid() {
     boolean next;
     int i;
     int sum = 0;
     for (i = 0; i < nflow; i++) {
     sum = sum + ngrid[i];
     }
     next = true;
     if (gridnum == sum) {
     next = false;
     }
     return next;
     }

     public int getNGrid(int iflow) {

     return ngrid[iflow];
     }
     * */
    public double getFlowLength(int iflow) {
        return dx[iflow];
    }

    public double[] getFlowLength() {
        return dx;
    }

    public double getFlowbedSlope(int iflow) {
        return s0[iflow];
    }

    public double[] getFlowbedSlope() {
        return s0;
    }

    public double getFlowWidth(int iflow) {
        return b[iflow];
    }

    public double[] getFlowWidth() {
        return b;
    }

    public double getFlowDepth(int iflow) {
        return Dr[iflow];
    }

    public double[] getFlowDepth() {
        return Dr;
    }

    public double getFlowRoughness(int iflow) {
        return roughness[iflow];
    }

    public double[] getFlowRoughness() {
        return roughness;
    }

    private void readRiver(String RiverFile) throws IOException {
        int iflow, igrid;
        String line;
        String[] split;

        FileReader fin = new FileReader(RiverFile);
        BufferedReader bin = new BufferedReader(fin);

        line = StringUtil.delSpace(bin.readLine());
        nflow = Integer.parseInt(line);
        ngrid = new int[nflow];
        dx = new double[nflow];
        s0 = new double[nflow];
        b = new double[nflow];
        roughness = new double[nflow];
        Dr = new double[nflow];
        row = new int[nflow][300];
        col = new int[nflow][300];

        for (iflow = 0; iflow < nflow; iflow++) {
            line = StringUtil.delSpace(bin.readLine());
            split = line.split(" +");
            ngrid[iflow] = Integer.parseInt(split[0]);
            dx[iflow] = Double.parseDouble(split[1]);
            s0[iflow] = Double.parseDouble(split[2]);
            b[iflow] = Double.parseDouble(split[3]);
            roughness[iflow] = Double.parseDouble(split[4]);
            Dr[iflow] = Double.parseDouble(split[5]);

            if (dx[iflow] < 0.1) {
                System.out.println("wrong in dx, " + RiverFile + "  " + iflow
                        + "  " + dx[iflow]);
                dx[iflow] = 5000;
            }
            if (s0[iflow] == 0) {
                System.out.println("wrong in s0, " + RiverFile + "  " + iflow
                        + "  " + s0[iflow]);
                s0[iflow] = 0.00001;
            }
            if (s0[iflow] == -9999) {
                System.out.println("wrong in s0, " + RiverFile + "  " + iflow
                        + "  " + s0[iflow]);
                s0[iflow] = 0.00001;
            }
            if (s0[iflow] <= 0.1e-5) {
                s0[iflow] = 0.1e-5;
            }

            line = "";
            do {
                line = line + StringUtil.delSpace(bin.readLine()) + " ";
                split = line.split(" +");
            } while (split.length < ngrid[iflow] * 2);
            for (igrid = 0; igrid < 2 * ngrid[iflow]; igrid = igrid + 2) {
                row[iflow][igrid / 2] = Integer
                        .parseInt(split[igrid]) - 1;
                col[iflow][igrid / 2] = Integer
                        .parseInt(split[igrid + 1]) - 1;
            }
        }
        bin.close();
        fin.close();
    }
}
