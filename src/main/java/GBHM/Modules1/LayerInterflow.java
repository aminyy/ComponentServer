package GBHM.Modules1;

import net.casnw.home.model.AbsComponent;
import net.casnw.home.model.SpatialContext;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDoubleArray;

public class LayerInterflow extends AbsComponent {
//	@Variable(access = AccessType.IN, description = "number of UZ(Unsaturated Zone)layer", unit = "")

//    @In
//    public int layer;
//	@Variable(access = AccessType.IN, description = "time step (second)", unit = "second")
    public PoolDouble dt;
//	@Variable(access = AccessType.IN, description = "depth of each UZ(Unsaturated Zone)layer(m)", unit = "m")
    public PoolDoubleArray D;
//	@Variable(access = AccessType.IN, description = "infiltration(mm H2O)", unit = "mm")
    public PoolDouble inf;
//	@Variable(access = AccessType.IN, description = "recharge rate from unsaturated zone to groundwater(m H2O)", unit = "m")
    public PoolDouble rech;
//	@Variable(access = AccessType.IN, description = "average depth of topsoil (m)", unit = "m")
    public PoolDouble Ds;
//	@Variable(access = AccessType.INOUT, description = "depth to groundwater level (m)", unit = "m")
    public PoolDouble dgl;
//	@Variable(access = AccessType.INOUT, description = "soil moisture for each UZ(Unsaturated Zone)layer,(mm H2O/ mm Soil)", unit = "mm/mm")
    public PoolDoubleArray w;
//	@Variable(access = AccessType.INOUT, description = "surface water storage(mm H2O)", unit = "mm")
    public PoolDouble sst;
//	@Parameter(default_value = "", description = "saturated soil moisture, varied by soil types", range = "(0,1)", unit = "")
    public PoolDoubleArray wsat;
//	@Parameter(default_value = "", description = "residual soil moisture, varied by soil types", range = "(0,1)", unit = "")
    public PoolDoubleArray wrsd;
//	@Parameter(default_value = "", description = "saturated hydraulic conductivity (mm H2O/hour) for each UZ(Unsaturated Zone)layer", range = "", unit = "mm H2O/hour")
    public PoolDoubleArray k0;
//	@Parameter(default_value = "", description = "soil water parameter", range = "", unit = "")
    public PoolDoubleArray alpha;
//	@Parameter(default_value = "", description = "soil water parameter", range = "", unit = "")
    public PoolDoubleArray watern;

    @Override
    public void init() throws Exception {
    }

    @Override
    public void run() throws Exception {

        //	@Variable(access = AccessType.IN, description = "time step (second)", unit = "second")
        double dt;
//	@Variable(access = AccessType.IN, description = "depth of each UZ(Unsaturated Zone)layer(m)", unit = "m")
        double D[];
//	@Variable(access = AccessType.IN, description = "infiltration(mm H2O)", unit = "mm")
        double inf;
//	@Variable(access = AccessType.IN, description = "recharge rate from unsaturated zone to groundwater(m H2O)", unit = "m")
        double rech;
//	@Variable(access = AccessType.IN, description = "average depth of topsoil (m)", unit = "m")
        double Ds;
        double dgl;
//	@Variable(access = AccessType.INOUT, description = "soil moisture for each UZ(Unsaturated Zone)layer,(mm H2O/ mm Soil)", unit = "mm/mm")
        double w[];
//	@Variable(access = AccessType.INOUT, description = "surface water storage(mm H2O)", unit = "mm")
        double sst;
//	@Parameter(default_value = "", description = "saturated soil moisture, varied by soil types", range = "(0,1)", unit = "")
        double[] wsat;
//	@Parameter(default_value = "", description = "residual soil moisture, varied by soil types", range = "(0,1)", unit = "")
        double[] wrsd;
//	@Parameter(default_value = "", description = "saturated hydraulic conductivity (mm H2O/hour) for each UZ(Unsaturated Zone)layer", range = "", unit = "mm H2O/hour")
        double k0[];
//	@Parameter(default_value = "", description = "soil water parameter", range = "", unit = "")
        double[] alpha;
//	@Parameter(default_value = "", description = "soil water parameter", range = "", unit = "")
        double[] watern;

        dt = this.dt.getValue();
        D = this.D.getValue();
        inf = this.inf.getValue();
        rech = this.rech.getValue();
        Ds = this.Ds.getValue();
        dgl = this.dgl.getValue();
        w = this.w.getValue();
        sst = this.sst.getValue();
        wsat = this.wsat.getValue();
        wrsd = this.wrsd.getValue();
        k0 = this.k0.getValue();
        alpha = this.alpha.getValue();
        watern = this.watern.getValue();


        int i, GWkey;
        int layer;
        double tmp, tmp2, qmax, qmin, excess, deficit;
        double ksoil[];// hydraulic conductivity of each layer (m/s)
        double suct[];// soil suction
        double avz[];// average depth
        double avk[];// average hydraulic conductivity
        double dpdw[];
        double temA[], temB[], temC[], temD[], temq[], temq1[];


        layer = D.length;
        if (layer > 2) {

            ksoil = new double[layer];
            suct = new double[layer];
            avz = new double[layer];
            avk = new double[layer];
            dpdw = new double[layer];

            temA = new double[layer];
            temB = new double[layer];
            temC = new double[layer];
            temD = new double[layer];
            temq = new double[layer];
            // Unit Conversion
            sst = sst / 1000; // mm---->m
            inf = inf / 1000; // mm---->m
            for (i = 0; i < layer; i++) // mm/hr--->m/s
            {
                k0[i] = k0[i] * 0.001 / 3600.0;
            }
            // End of Unit Conversion

            for (i = 0; i < layer; i++) {
                ksoil[i] = Functions.conductivity_V(k0[i], wsat[i], wrsd[i], watern[i], w[i]);
                suct[i] = Functions.SuctionFromMoisture_V(w[i], wsat[i], wrsd[i], watern[i], alpha[i]);
            }
            for (i = 0; i < layer - 1; i++) {
                avz[i] = 0.5 * (D[i] + D[i + 1]);
                tmp = (w[i] * w[i + 1] * (D[i] + D[i + 1]))
                        / (w[i] * D[i] + w[i + 1] * D[i + 1]);
                tmp2 = (k0[i] * D[i] + k0[i + 1] * D[i + 1]) / (D[i] + D[i + 1]);
                avk[i] = Functions.conductivity_V(tmp2, wsat[i], wrsd[i], watern[i], tmp);
                if (Math.abs(w[i + 1] - w[i]) < 0.1E-5) {
                    dpdw[i] = 0.0;
                } else {
                    dpdw[i] = (suct[i + 1] - suct[i]) / (w[i + 1] - w[i]);
                }
            }

            // ***************************************************************************
            // backward implicit calculation of flows between soil layers.
            // ***************************************************************************
            temA[0] = 0;
            temB[0] = 1.0 + avk[0] * dpdw[0] / avz[0] * (dt / D[0] + dt / D[1]);
            temC[0] = -avk[0] * dpdw[0] / avz[0] * dt / D[1];
            temD[0] = avk[0] * dpdw[0] / avz[0] * (w[0] - w[1] + inf * dt / D[0])
                    + avk[0];
            for (i = 1; i <= layer - 3; i++) {
                temA[i] = -avk[i] * dpdw[i] / avz[i] * dt / D[i];
                temB[i] = 1.0 + avk[i] * dpdw[i] / avz[i]
                        * (dt / D[i] + dt / D[i + 1]);
                temC[i] = -avk[i] * dpdw[i] / avz[i] * dt / D[i + 1];
                temD[i] = avk[i] * dpdw[i] / avz[i] * (w[i] - w[i + 1]) + avk[i];
            }
            temA[layer - 2] = -avk[layer - 2] * dpdw[layer - 2] / avz[layer - 2]
                    * dt / D[layer - 2];
            temB[layer - 2] = 1.0 + avk[layer - 2] * dpdw[layer - 2]
                    / avz[layer - 2] * (dt / D[layer - 2] + dt / D[layer - 1]);
            temC[layer - 2] = 0.0;
            temD[layer - 2] = avk[layer - 2] * dpdw[layer - 2] / avz[layer - 2]
                    * (w[layer - 2] - w[layer - 1] + rech * dt / D[layer - 1])
                    + avk[layer - 2];
            if (layer == 3) {
                temq[0] = (temD[1] - temD[0] * temB[1] / temC[0])
                        / (temA[1] - temB[0] * temB[1] / temC[0]);
                temq[1] = (temD[1] - temD[0] * temA[1] / temB[0])
                        / (temB[1] - temA[1] * temC[0] / temB[0]);

            } else {
                temq1 = Functions.TRDIG(layer - 1, temA, temB, temC, temD);
                for (i = 0; i < layer - 1; i++) {
                    temq[i] = temq1[i];
                }
            }

            // **********************************************************************
            // update moisture of each soil moisture layer due to layer interflow
            // **********************************************************************
            for (i = 0; i < layer - 1; i++) {
                qmax = Math.max((w[i] - wrsd[i] - 1.0E-6) * D[i] / dt, 0.0);
                qmin = Math.min(-(w[i + 1] - wrsd[i] - 1.0E-6) * D[i + 1] / dt, 0.0);
                if (temq[i] < 0.0 && temq[i + 1] > 0.0) {
                    qmin = 0.4 * qmin;
                }
                if (i >= 1 && temq[i - 1] < 0.0 && temq[i] > 0.0) {
                    qmax = 0.5 * qmax;
                }
                temq[i] = Math.min(temq[i], qmax);
                temq[i] = Math.max(temq[i], qmin);
                w[i] = w[i] - temq[i] * dt / D[i];
                w[i + 1] = w[i + 1] + temq[i] * dt / D[i + 1];
            }
            w[layer - 1] = w[layer - 1] - rech * dt / D[layer - 1];
            // saturation excess
            GWkey = 0;
            excess = 0.0;
            excess = (w[layer - 1] - wsat[layer - 1]) * D[layer - 1];
            excess = Math.max(excess, 0.0);
            w[layer - 1] = w[layer - 1] - excess / D[layer - 1];
            if (Math.abs(dgl - Ds) < 0.1E-5
                    && Math.abs(w[layer - 1] - wsat[layer - 1]) < 0.1E-5) {
                dgl = Ds - D[layer - 1];
                GWkey = 1;
            } else {
                GWkey = 0;
            }
            for (i = layer - 2; i >= 0; i--) {
                w[i] = w[i] + excess / D[i];
                excess = (w[i] - wsat[i]) * D[i];
                excess = Math.max(excess, 0.0);
                w[i] = w[i] - excess / D[i];
                if (GWkey == 1 && Math.abs(w[i] - wsat[i]) < 0.1E-5) {
                    dgl = dgl - D[i];
                } else {
                    GWkey = 0;
                }
            }
            sst = sst - inf * dt + excess;
            for (i = 0; i < layer; i++) {
                if (w[i] < wrsd[i] - 0.0001 || w[i] > wsat[i] + 1.0e-8) {
                    System.out.println("LayerInterflow: " + "i=" + i + ";w[" + i + "]=" + w[i]
                            + ";wrsd=" + wrsd[i] + ";wsat=" + wsat[i] + ";temq[" + i
                            + "]=" + temq[i]);
                }
            }
            // *********************************************************************
            // prevent negative values of www(i)
            // *********************************************************************
            for (i = 0; i < layer - 1; i++) {
                deficit = Math.max(0.0, 1.0e-10 - w[i]);
                w[i] = w[i] + deficit;
                w[i + 1] = w[i + 1] - deficit * D[i] / D[i + 1];
            }
            w[layer - 1] = Math.max(w[layer - 1], 1.0e-10);

            // Unit Conversion        
            sst = sst * 1000;// m--->mm
            // End of Unit Conversion

            this.dgl.setValue(dgl);
            this.w.setValue(w);
            this.sst.setValue(sst);
        }   
    }

    @Override
    public void clear() throws Exception {
    }
}
