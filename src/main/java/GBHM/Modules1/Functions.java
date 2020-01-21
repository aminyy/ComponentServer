/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.Modules1;

/**
 *
 * @author longyinping
 */
public class Functions {

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
    // ***************************************************************************
    // calculate suction from soil moisture by Van Genuchten's equation
    // ***************************************************************************

    public static double SuctionFromMoisture_V(double w, double wsat, double wrsd,
            double n, double alpha) {
        double ps, m, se, tmpe, tmpps, tmpw;
        m = 1.0 - 1.0 / n;
        tmpw = w;
        if (tmpw >= wsat) {
            ps = 0.0;
        } else {
            if (tmpw < wrsd + 0.001) {
                tmpw = wrsd + 0.001;
            }
            se = (tmpw - wrsd) / (wsat - wrsd);
            tmpe = Math.pow(se, 1.0 / m);
            tmpps = -Math.pow(1.0 / tmpe - 1.0, 1.0 / n) / alpha;
            ps = tmpps / 100.0; // cm->m
        }
        return ps;
    }

    // ***************************************************************************
    // calculate soil hydraulic conductivity by Van Genuchten's equation
    // ***************************************************************************
    public static double conductivity_V(double k0, double wsat, double wrsd, double n,
            double w) {
        double conductivity;
        double m, tmpw, se;
        m = 1.0 - 1.0 / n;
        tmpw = w;
        if (tmpw > wsat) {
            tmpw = wsat;
        }
        if (tmpw <= wrsd) {
            tmpw = wrsd + 1.0E-10;
        } else if (tmpw < wrsd + 0.0001) {
            tmpw = wrsd + 0.0001;
        }
        se = (tmpw - wrsd) / (wsat - wrsd);
        conductivity = k0 * Math.sqrt(se)
                * Math.pow((1.0 - Math.pow(1.0 - Math.pow(se, 1.0 / m), m)), 2);
        if (conductivity < 0.0 || conductivity > k0) {
            System.out.println("wrong in calculating conductivity" + "w=" + w
                    + "conductivity=" + conductivity);
        }
        return conductivity;
    }
    
    
    // C*****************************************************************
    // C *
    // C This routine solves a linear system of equations *
    // C A * X = RS *
    // C for a tridiagonal, strongly nonsingular matrix A. *
    // C The matrix is given by the three vectors DL, DM and DU *
    // C which designate the lower co-diagonal, the diagonal and *
    // C the upper co-diagonal elements of A, respectively. *
    // C The system of equations has the form : *
    // C *
    // C DM(1) * X(1) + DU(1) * X(2) = RS(1) *
    // C *
    // C DL(I) * X(I-1) + DM(I) * X(I) + DU(I) * X(I+1) = RS(I) *
    // C for I = 2, ..., N-1, and *
    // C *
    // C DL(N) * X(N-1) + DM(N) * X(N) = RS(N) *
    // C *
    // C *
    // C INPUT PARAMETERS: *
    // C ================= *
    // C N : number of equations, N > 2 *
    // C DL : N-vector DL(1:N); lower co-diagonal of A *
    // C DL(2), DL(3), ... ,DL(N) *
    // C DM : N-vector DM(1:N); the diagonal of A *
    // C DM(1), DM(2), ... , DM(N) *
    // C DU : N-vector DU(1:N); upper co-diagonal of A *
    // C DU(1), DU(2), ... , DU(N-1) *
    // C RS : N-vector RS(1:N); the right hand side *
    // C *
    // C *
    // C OUTPUT PARAMETERS: *
    // C ================== *
    // C DL :) *
    // C DM :) *
    // C DU :) these are overwritten with auxiliary vectors *
    // C RS :) *
    // C X : N-vector X(1:N), containing the solution of the *
    // C system of equations *
    // C MARK : error parameter *
    // C MARK= 1 : everything is o.k. *
    // C MARK= 0 : the matrix A is not strongly nonsingular *
    // C MARK=-1 : error on N: N <= 2 *
    // C *
    // C NOTE: if MARK = 1, the determinant of A can be calculated *
    // C after this subroutine has run as follows: *
    // C DET A = DM(1) * DM(2) * ... * DM(N) *
    // C *
    // C----------------------------------------------------------------*

    public static double[] TRDIG(int N, double[] DL, double[] DM, double[] DU,
            double[] RS) {
        int i, MARK = -1;
        double X[];
        double ROW;
        double M;
        // DL = new double[N];
        // DM = new double[N];
        // DU = new double[N];
        // RS = new double[N];
        X = new double[N];

        if (N >= 3) {
            // checking for strong nonsingularity with N=1
            MARK = 0;
            ROW = Math.abs(DM[0]) + Math.abs(DU[0]);
            if (ROW != 0) {
                M = 1.0E0 / ROW;
                if (Math.abs(DM[0]) * M > 1.0E-20) {

                    // factoring A while checking for strong nonsingularity
                    DL[0] = 0.0E0;
                    DU[N - 1] = 0.0E0;
                    DU[0] = DU[0] / DM[0];
                    MARK = 1;
                    for (i = 1; i < N; i++) {
                        ROW = Math.abs(DL[i]) + Math.abs(DM[i])
                                + Math.abs(DU[i]);
                        if (ROW == 0) {
                            MARK = 0;
                            break;
                        }
                        M = 1.0E0 / ROW;
                        DM[i] = DM[i] - DL[i] * DU[i - 1];
                        if (Math.abs(DM[i]) * M <= 1.0E-20) {
                            MARK = 0;
                            break;
                        }
                        if (i < N) {
                            DU[i] = DU[i] / DM[i];
                        }
                    }
                }
            }
        }
        // If MARK = 1, update the right hand side and solve via
        // backsubstitution
        if (MARK == 1) {
            RS[0] = RS[0] / DM[0];
            for (i = 1; i < N; i++) {
                RS[i] = (RS[i] - DL[i] * RS[i - 1]) / DM[i];
            }
            X[N - 1] = RS[N - 1];
            for (i = N - 2; i >= 0; i--) {
                X[i] = RS[i] - DU[i] * X[i + 1];
            }
        } else {
            System.out.println("LayerInterflow: " + "wrong in solving  linear equations...");
        }
        return X;

    }
}
