package GBHM.Modules1;

//@Component(author = "longyinping11@lzb.ac.cn", createTime = "2012.12.11", description = "calculation of recharge rate from unsaturated zone to groundwater using Darcy's law", domain = "Hydrology", keyword = "vertical recharge rate", name = "RechargeToGroundWater", references = "Yan Dawen, Li Chong, Ni Guangheng. Application of a Distributed Hydrologihcal Model to the Yellow River Basin[J].Acta Geographic Sinica, 2004, 59(1):143-154.", source = "Yang Dawen", version = "GBHM 2006")
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDoubleArray;

public class RechargeToGroundWater extends AbsComponent {
//	@Variable(access = AccessType.IN, description = "number of Unsaturated Zone layers", unit = "")

//    @In
//    public int layer;
//	@Variable(access = AccessType.IN, description = "time step (second)", unit = "second")
    public PoolDouble dt;
//	@Variable(access = AccessType.IN, description = "average depth of topsoil (m)", unit = "m")
    public PoolDouble Ds;
//	@Variable(access = AccessType.IN, description = "average depth of the unconfined aquifer (m),for default, it is three times of average depth of topsoil(Ds)", unit = "")
    public PoolDouble Dg;
//	@Variable(access = AccessType.IN, description = "depth of each UZ(Unsaturated Zone)layer(m)", unit = "m")
    public PoolDoubleArray D;
//	@Variable(access = AccessType.INOUT, description = "soil moisture for each UZ(Unsaturated Zone)layer,(mm H2O/ mm Soil)", unit = "mm/mm")
    public PoolDoubleArray w;
//	@Variable(access = AccessType.INOUT, description = "grondwater storage(m H2O)", unit = "m")
    public PoolDouble GWst;
//	@Variable(access = AccessType.INOUT, description = "depth to groundwater level (m)", unit = "m")
    public PoolDouble dgl;
//	@Variable(access = AccessType.OUT, description = "recharge rate from unsaturated zone to groundwater(m H2O)", unit = "m")
    public PoolDouble rech;
//	@Parameter(default_value = "", description = "saturated soil moisture, varied by soil types", range = "(0,1)", unit = "")
    public PoolDoubleArray wsat;
//	@Parameter(default_value = "", description = "soil moisture at field capacity, varied by soil types", range = "", unit = "")
    public PoolDoubleArray wfld;
//	@Parameter(default_value = "", description = "residual soil moisture, varied by soil types", range = "(0,1)", unit = "")
    public PoolDoubleArray wrsd;
//	@Parameter(default_value = "", description = "saturated hydraulic conductivity (mm H2O/hour) for each UZ(Unsaturated Zone)layer", range = "", unit = "mm H2O/hour")
    public PoolDoubleArray k0;
//	@Parameter(default_value = "", description = "soil water parameter", range = "", unit = "")
    public PoolDoubleArray watern;
//	@Parameter(default_value = "", description = "soil water parameter", range = "", unit = "")
    public PoolDoubleArray alpha;
//	@Parameter(default_value = "", description = "groundwater storage coefficient,varied by soil types", range = "", unit = "")
    public PoolDouble GWcs;

    @Override
    public void init() throws Exception {
    }

    @Override
    public void run() throws Exception {
        //	@Variable(access = AccessType.IN, description = "time step (second)", unit = "second")
        double dt;
//	@Variable(access = AccessType.IN, description = "average depth of topsoil (m)", unit = "m")
        double Ds;
//	@Variable(access = AccessType.IN, description = "average depth of the unconfined aquifer (m),for default, it is three times of average depth of topsoil(Ds)", unit = "")
        double Dg;
//	@Variable(access = AccessType.IN, description = "depth of each UZ(Unsaturated Zone)layer(m)", unit = "m")
        double D[];
//	@Variable(access = AccessType.INOUT, description = "soil moisture for each UZ(Unsaturated Zone)layer,(mm H2O/ mm Soil)", unit = "mm/mm")
        double w[];
//	@Variable(access = AccessType.INOUT, description = "grondwater storage(m H2O)", unit = "m")
        double GWst;
//	@Variable(access = AccessType.INOUT, description = "depth to groundwater level (m)", unit = "m")
        double dgl;
//	@Variable(access = AccessType.OUT, description = "recharge rate from unsaturated zone to groundwater(m H2O)", unit = "m")
        double rech;
//	@Parameter(default_value = "", description = "saturated soil moisture, varied by soil types", range = "(0,1)", unit = "")
        double[] wsat;
//	@Parameter(default_value = "", description = "soil moisture at field capacity, varied by soil types", range = "", unit = "")
        double[] wfld;
//	@Parameter(default_value = "", description = "residual soil moisture, varied by soil types", range = "(0,1)", unit = "")
        double[] wrsd;
//	@Parameter(default_value = "", description = "saturated hydraulic conductivity (mm H2O/hour) for each UZ(Unsaturated Zone)layer", range = "", unit = "mm H2O/hour")
        double k0[];
//	@Parameter(default_value = "", description = "soil water parameter", range = "", unit = "")
        double[] watern;
//	@Parameter(default_value = "", description = "soil water parameter", range = "", unit = "")
        double[] alpha;
//	@Parameter(default_value = "", description = "groundwater storage coefficient,varied by soil types", range = "", unit = "")
        double GWcs;

        dt = this.dt.getValue();
        Ds = this.Ds.getValue();
        Dg = this.Dg.getValue();
        D = this.D.getValue();
        w = this.w.getValue();
        GWst = this.GWst.getValue();
        dgl = this.dgl.getValue();
        wsat = this.wsat.getValue();
        wfld = this.wfld.getValue();
        wrsd = this.wrsd.getValue();
        k0 = this.k0.getValue();
        watern = this.watern.getValue();
        alpha = this.alpha.getValue();
        GWcs = this.GWcs.getValue();

        int i;
        int layer;
        double temq[];
        double avz[];// average depth
        double avk[];// average hydraulic conductivity
        double tmp = 0, tmp2;
        double ps1, ps2;

        layer = D.length;
        rech = 0.0;

        temq = new double[layer];
        avz = new double[layer];
        avk = new double[layer];



        // Unit Conversion
        for (i = 0; i < layer; i++) // mm/hr--->m/s
        {
            k0[i] = k0[i] * 0.001 / 3600.0;
        }
        // End of Unit Conversion

        for (i = 0; i < layer; i++) {
            temq[i] = 0.0;
        }

        if (dgl >= Ds) {
            ps1 = Functions.SuctionFromMoisture_V(w[layer - 1], wsat[layer - 1], wrsd[layer - 1], watern[layer - 1], alpha[layer - 1]);
            tmp = wfld[layer - 1] * (1.0 - (dgl - Ds) / Dg);
            tmp = Math.max(tmp, wrsd[layer - 1]);
            ps2 = Functions.SuctionFromMoisture_V(tmp, wsat[layer - 1], wrsd[layer - 1], watern[layer - 1], alpha[layer - 1]);
            avz[layer - 1] = 0.5 * (D[layer - 1] + (dgl - Ds));
            avk[layer - 1] = Functions.conductivity_V(k0[layer - 1], wsat[layer - 1], wrsd[layer - 1], watern[layer - 1],
                    w[layer - 1]);
            tmp = 0.5 * (D[layer - 1] + (dgl - Ds));
            // Darcy's law
            rech = -avk[layer - 1] * (ps2 - ps1) / tmp + avk[layer - 1]; // m/s

            rech = rech * dt;
            if (rech >= 0.0) {
                tmp = (w[layer - 1] - wfld[layer - 1]) * D[layer - 1];
                tmp = Math.max(tmp, 0.0);
                tmp2 = Dg * GWcs - GWst;
                rech = Math.min(Math.min(rech, tmp), tmp2);
                if (rech < -0.1E-6) {
                    System.out.println(this.getClass().getName() + "rech<0" + ",rech=" + rech + ",tmp="
                            + tmp + ",tmp2=" + tmp2 + ",GWst=" + GWst
                            + ",GWcs=" + GWcs);
                }
            } else {
                rech = Math.max(rech, -1.0 * GWst);
                tmp = Math.max(0.0, (wsat[layer - 1] - w[layer - 1]) * D[layer - 1]);
                rech = Math.max(rech, -1.0 * tmp);
                if (rech > 1.0E-10) {
                    System.out.println(this.getClass().getName() + "rech>0" + ",rech=" + rech + ",tmp="
                            + tmp + ",GWst=" + GWst + ",GWcs=" + GWcs);
                }
            }
            if (Math.abs(rech) < 0.1E-20) {
                rech = 0.0;
            } else {
                w[layer - 1] = w[layer - 1] - rech / D[layer - 1];
                GWst = GWst + rech;
                dgl = dgl - rech / GWcs;
                if (dgl > (Ds + Dg + 1.0E-8)) {
                    System.out.println(this.getClass().getName() + "dgl>Ds+Dg" + ",dgl=" + dgl + ",Ds="
                            + Ds + ",Dg=" + Dg + ",rech=" + rech + ",ps2="
                            + ps2 + ",ps1=" + ps1 + ",avk[" + (layer - 1)
                            + "]=" + avk[layer - 1]);
                }
                if (GWst < 0) {
                    System.out.println(this.getClass().getName() + "Gwst<0" + ",Gwst=" + GWst);
                }
                if (dgl > (Ds + Dg + 1.0E-8) || GWst < 0) {
                    w[layer - 1] = w[layer - 1] + rech / D[layer - 1];
                    GWst = GWst - rech;
                    dgl = dgl + rech / GWcs;
                }
                if ((GWst - Dg * GWcs) > 0.1E-5) {
                    System.out.println(this.getClass().getName() + "wrong in recharge:" + ",rech=" + rech
                            + ",GWst=" + GWst + ",Dg=" + Dg + ",dgl=" + dgl);
                }
            }
            rech = 0.0;
        }


        this.w.setValue(w);
        this.GWst.setValue(GWst);
        this.dgl.setValue(dgl);
        this.rech.setValue(rech); 
    }

    @Override
    public void clear() throws Exception {
    }



}
