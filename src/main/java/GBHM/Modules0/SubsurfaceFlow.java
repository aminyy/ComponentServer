package GBHM.Modules0;

//@Component(author = "longyinping11@lzb.ac.cn", createTime = "2012.12.21", description = "calculation of exchange rate between groundwater and river: qsub(ground water flow), and subsurface flow from top saturated zone above the groundwater level(lateral flow)", domain = "hydroloy", keyword = "groundWater flow, lateral flow", name = "Subsurface flow", source = "Yang Dawen", version = "GBHM 2006", references = "Yang, D., Herath, S. and Musiake, K. (2000), Comparison of different distributed hydrological models for characterization of catchment spatial variability. Hydrological Processes, 14:403�C416. Yan Dawen, Li Chong, Ni Guangheng. ;Yan Dawen, Li Chong, Ni Guangheng. Application of a Distributed Hydrologihcal Model to the Yellow River Basin[J].Acta Geographic Sinica, 2004, 59(1):143-154.")
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDoubleArray;

public class SubsurfaceFlow extends AbsComponent {
//	@Variable(access = AccessType.IN, description = "number of Unsaturated Zone layers", unit = "")

//    @In
//    public int layer;
//	@Variable(access = AccessType.IN, description = "time step (second)", unit = "second")
    public PoolDouble dt;
//	@Variable(access = AccessType.IN, description = "average depth of topsoil (m)", unit = "m")
    public PoolDouble Ds;
//	@Variable(access = AccessType.IN, description = "average depth of the unconfined aquifer (m),for default, it is three times of average depth of topsoil(Ds)", unit = "")
    public PoolDouble Dg;
//	@Variable(access = AccessType.IN, description = "depth of river (m)", unit = "")
    public PoolDouble Dr;
//	@Variable(access = AccessType.IN, description = "depth of each UZ(Unsaturated Zone)layer(m)", unit = "m")
    public PoolDoubleArray D;
//	@Variable(access = AccessType.INOUT, description = "depth to groundwater level (m)", unit = "m")
    public PoolDouble dgl;
//	@Variable(access = AccessType.IN, description = "depth of river water (m)", unit = "m")
    public PoolDouble Drw;
//	@Variable(access = AccessType.INOUT, description = "grondwater storage(m H2O)", unit = "m")
    public PoolDouble GWst;
//	@Variable(access = AccessType.INOUT, description = "soil moisture for each UZ(Unsaturated Zone)layer,(mm H2O/ mm Soil)", unit = "mm/mm")
    public PoolDoubleArray w;
//	@Variable(access = AccessType.OUT, description = "runoff from subsurface (m3/s)", unit = "m3/s")
    public PoolDouble qsub;
//	@Parameter(default_value = "", description = "saturated hydraulic conductivity (mm H2O/hour) for each UZ(Unsaturated Zone)layer", range = "", unit = "mm H2O/hour")
    public PoolDoubleArray k0;
//	@Parameter(default_value = "", description = "hydraulic conductivity of groundwater (mm/hour)", range = "", unit = "mm/hour")
    public PoolDouble kg;
//	@Parameter(default_value = "", description = "groundwater storage coefficient,varied by soil types", range = "", unit = "")
    public PoolDouble GWcs;
//	@Parameter(default_value = "", description = "average hillslope length (m)", range = "", unit = "m")
    public PoolDouble length;
//	@Parameter(default_value = "", description = "area of the local grid (m2)", range = "", unit = "m2")
    public PoolDouble area;
//	@Parameter(default_value = "", description = "slope gradient of hillslope", range = "", unit = "")
    public PoolDouble slope;
//	@Parameter(default_value = "", description = "soil anisotropy ratio (>=1.0),varied by landuse type", range = ">=1.0", unit = "")
    public PoolDouble anik;
//	@Parameter(default_value = "", description = "saturated soil moisture, varied by soil types", range = "(0,1)", unit = "")
    public PoolDouble wsat;
//	@Parameter(default_value = "", description = "soil moisture at field capacity, varied by soil types", range = "", unit = "")
    public PoolDouble wfld;
//	@Parameter(default_value = "", description = "residual soil moisture, varied by soil types", range = "(0,1)", unit = "")
    public PoolDouble wrsd;
//	@Parameter(default_value = "", description = "soil water parameter", range = "", unit = "")
    public PoolDouble watern;
//	@Parameter(default_value = "0.03", description = "slope shape (concave or convex) factor,0.03 is for linear or convex slope", range = "", unit = "")
    public PoolDouble ss_f;

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
//	@Variable(access = AccessType.IN, description = "depth of river (m)", unit = "")
        double Dr;
//	@Variable(access = AccessType.IN, description = "depth of each UZ(Unsaturated Zone)layer(m)", unit = "m")
        double D[];
//	@Variable(access = AccessType.INOUT, description = "depth to groundwater level (m)", unit = "m")
        double dgl;
//	@Variable(access = AccessType.INOUT, description = "depth of river water (m)", unit = "m")
        double Drw;
//	@Variable(access = AccessType.INOUT, description = "grondwater storage(m H2O)", unit = "m")
        double GWst;
//	@Variable(access = AccessType.INOUT, description = "soil moisture for each UZ(Unsaturated Zone)layer,(mm H2O/ mm Soil)", unit = "mm/mm")
        double w[];
//	@Variable(access = AccessType.OUT, description = "runoff from subsurface (m3/s)", unit = "m3/s")
        double qsub;
//	@Parameter(default_value = "", description = "saturated hydraulic conductivity (mm H2O/hour) for each UZ(Unsaturated Zone)layer", range = "", unit = "mm H2O/hour")
        double k0[];
//	@Parameter(default_value = "", description = "hydraulic conductivity of groundwater (mm/hour)", range = "", unit = "mm/hour")
        double kg;
//	@Parameter(default_value = "", description = "groundwater storage coefficient,varied by soil types", range = "", unit = "")
        double GWcs;
//	@Parameter(default_value = "", description = "average hillslope length (m)", range = "", unit = "m")
        double length;
//	@Parameter(default_value = "", description = "area of the local grid (m2)", range = "", unit = "m2")
        double area;
//	@Parameter(default_value = "", description = "slope gradient of hillslope", range = "", unit = "")
        double slope;
//	@Parameter(default_value = "", description = "soil anisotropy ratio (>=1.0),varied by landuse type", range = ">=1.0", unit = "")
        double anik;
//	@Parameter(default_value = "", description = "saturated soil moisture, varied by soil types", range = "(0,1)", unit = "")
        double wsat;
//	@Parameter(default_value = "", description = "soil moisture at field capacity, varied by soil types", range = "", unit = "")
        double wfld;
//	@Parameter(default_value = "", description = "residual soil moisture, varied by soil types", range = "(0,1)", unit = "")
        double wrsd;
//	@Parameter(default_value = "", description = "soil water parameter", range = "", unit = "")
        double watern;
//	@Parameter(default_value = "0.03", description = "slope shape (concave or convex) factor,0.03 is for linear or convex slope", range = "", unit = "")
        double ss_f;

        dt = this.dt.getValue();
        Ds = this.Ds.getValue();
        Dg = this.Dg.getValue();
        Dr = this.Dr.getValue();
        D = this.D.getValue();
        dgl = this.dgl.getValue();
        Drw = this.Drw.getValue();
        GWst = this.GWst.getValue();
        w = this.w.getValue();
        k0 = this.k0.getValue();
        kg = this.kg.getValue();
        GWcs = this.GWcs.getValue();
        length = this.length.getValue();
        area = this.area.getValue();
        slope = this.slope.getValue();
        anik = this.anik.getValue();
        wsat = this.wsat.getValue();
        wfld = this.wfld.getValue();
        wrsd = this.wrsd.getValue();
        watern = this.watern.getValue();
        ss_f = this.ss_f.getValue();

        int layer;
        double tmpqsub, tmp_D, tmp, excess, f;
        double avkg; // average GW hydraulic conductivity (m/s)
        double ksoil[];
        int isat, i, j, GWkey;

        layer = D.length;

        // Unit conversion
        kg = kg * 0.001 / 3600.0; // mm/hr--->m/s
        for (i = 0; i < layer; i++) // mm/hr--->m/s
        {
            k0[i] = k0[i] * 0.001 / 3600.0;
        }
        // end of Unit conversion

        // *************************************************************************************
        // exchange rate between groundwater and river: qsub(ground water flow)
        // *************************************************************************************
        isat = layer;
        if (dgl - Ds < 0 && Drw >= Dr && dgl <= 0.0) {
            avkg = kg;
            qsub = gwriv(dgl, length, slope, Ds, Dg, Dr, Drw, avkg);
        } else {
            if ((dgl - Ds) >= 0.0) { // ground water table is below the top
                // layer
                avkg = kg;
                tmpqsub = gwriv(dgl, length, slope, Ds, Dg, Dr, Drw, avkg); // m3/s/m
                qsub = tmpqsub * dt / length; // m
                if (Math.abs(qsub) < 0.1E-20) {
                    qsub = 0.0;
                }
            } else {
                tmp_D = 0.0;
                avkg = 0.0;
                for (i = layer - 1; i >= 0; i--) {
                    tmp_D = tmp_D + D[i];
                    if ((Ds - tmp_D) <= 0.5) {
                        avkg = avkg + anik * k0[i] * D[i];
                    } else {
                        avkg = avkg + k0[i] * D[i];
                    }

                    if (tmp_D >= (Ds - dgl)) {
                        break;
                    }
                }
                if (Dr > Ds) {
                    tmp_D = tmp_D + Dr - Ds;
                    avkg = avkg + kg * (Dr - Ds);
                }
                avkg = avkg / tmp_D;
                tmpqsub = gwriv(dgl, length, slope, Ds, Dg, Dr, Drw, avkg);// m3/s/m
                qsub = tmpqsub * dt / length;// m
                if (Math.abs(qsub) < 0.1E-20) {
                    qsub = 0.0;
                }
            }

            // Renewing the groundwater table and top layer soil moisture
            if (qsub < 0.0) {
                // River infiltrates into aquifer
                GWst = GWst - qsub;
                if (GWst <= Dg * GWcs) {
                    dgl = dgl + qsub / GWcs;
                    if (dgl < 0.0) {
                        System.out
                                .println(this.getClass().getName() + "wrong in renewing groundwater table ... 1"
                                + dgl);
                    }
                } else {
                    tmp = GWst - Dg * GWcs;
                    GWst = Dg * GWcs;
                    dgl = Ds;
                    w[layer - 1] = w[layer - 1] + tmp / D[layer - 1];
                    excess = (w[layer - 1] - wsat) * D[layer - 1];
                    excess = Math.max(excess, 0.0);
                    w[layer - 1] = w[layer - 1] - excess / D[layer - 1];
                    if (Math.abs(w[layer - 1] - wsat) < 0.1E-5) {
                        dgl = Ds - D[layer - 1];
                        GWkey = 1;
                    } else {
                        GWkey = 0;
                    }
                    for (i = layer - 2; i >= 0; i--) {
                        w[i] = w[i] + excess / D[i];
                        excess = (w[i] - wsat) * D[i];
                        excess = Math.max(excess, 0.0);
                        w[i] = w[i] - excess / D[i];
                        if (GWkey == 1 && Math.abs(w[i] - wsat) < 0.1E-5) {
                            dgl = dgl - D[i];
                        } else {
                            GWkey = 0;
                        }
                    }
                }
            } else if (qsub > 0) {// Aquifer flows out
                if (dgl >= Ds - 0.01) {
                    GWst = GWst - qsub;
                    dgl = dgl + qsub / GWcs;
                    if (dgl > (Ds + Dg) || GWst < 0.0) {
                        qsub = qsub + GWst;
                        dgl = Ds + Dg;
                        GWst = 0.0;
                    }
                } else {
                    tmp_D = 0.0;
                    isat = layer;
                    for (i = layer - 1; i >= 0; i--) {
                        tmp_D = tmp_D + D[i];
                        if (Math.abs(tmp_D - (Ds - dgl)) <= 0.1E-3) {
                            break;
                        }
                    }
                    if (i == -1) {
                        if (Math.abs(w[i + 1] - wsat) <= 0.1E-3) {
                            isat = i + 1;
                        }
                    } else {
                        if (Math.abs(w[i] - wsat) <= 0.1E-3) {
                            isat = i;
                        }
                    }
                    tmp = 0;
                    for (i = isat; i <= layer - 1; i++) {
                        if (w[i] - wsat > 0.1E-3) {
                            System.out
                                    .println(this.getClass().getName() + "wrong in GW flow:" + "isat="
                                    + isat + ";layer=" + i + ";dgl="
                                    + dgl + ";w[" + i + "]=" + w[i]
                                    + ";wsat=" + wsat);
                        }
                        tmp = tmp + GWcs * D[i];
                        w[i] = w[i] - GWcs;
                        dgl = dgl + D[i];
                        if (tmp >= qsub) {
                            break;
                        }
                    }
                    if (i <= layer - 1) {
                        w[i] = w[i] + (tmp - qsub) / D[i];
                    }
                    if (i > layer - 1) {
                        GWst = GWst - (qsub - tmp);
                        dgl = dgl + (qsub - tmp) / GWcs;
                    }
                    if (i >= 1 && w[i - 1] - wsat > 0.1E-3) {
                        System.out.println(this.getClass().getName() + "w(i)>wsat" + "i-1=" + (i - 1)
                                + ";w[" + (i - 1) + "]=" + w[i - 1] + ";wsat="
                                + wsat + ";isat=" + isat + ";layer=" + layer);
                    }
                    if (dgl > Ds && Math.abs(GWst - Dg * GWcs) < 0.1E-3) {
                        dgl = Ds;
                    }
                }
            }
            // *********************************************************************************

            // *********************************************************************************
            // Subsurface flow from top saturated zone above the groundwater
            // level
            // *********************************************************************************
            j = Math.min(isat, layer - 1);
            ksoil = new double[layer];            
            tmp_D=0;
            for (i = 0; i <= j; i++) {
                tmp_D = tmp_D + D[i];
                if ((w[i] - wfld) > 0.1E-3) {
                    ksoil[i] = conductivity_V(k0[i], wsat, wrsd, watern, w[i]);
                    tmpqsub = ksoil[i] * slope * dt;
                    if (tmp_D <= 0.5) {
                        tmpqsub = anik * tmpqsub;
                    }
                    f = 100.0;
                    if (tmp_D <= 1.5) {
                        f = -Math.log(0.1 / 1.0);
                    }
                    tmpqsub = tmpqsub
                            * (D[i] + Math.exp(-f * tmp_D) * ss_f * length)
                            / length;
                    tmp = (w[i] - wfld) * D[i];
                    tmpqsub = Math.min(tmpqsub, tmp);
                    tmpqsub = Math.max(tmpqsub, 0.0);
                    w[i] = w[i] - tmpqsub / D[i];
                    qsub = qsub + tmpqsub;
                }
            }
            qsub = qsub * length / dt;// m --> m3/sec/m            
        }        
        qsub = qsub * area / length; //m3/m/s-->m3/s

        this.dgl.setValue(dgl);
        this.GWst.setValue(GWst);
        this.w.setValue(w);
        this.qsub.setValue(qsub);
    }

    @Override
    public void clear() throws Exception {
    }

    public double gwriv(double Dtg, double length, double slope, double Ds,
            double Dg, double Dr, double Drw, double kg) {
        double Q;
        // The datum is sitted at the bottom of the unconfined aquifer
        // Varibles:
        // Dtg: depth to groundwater (m)
        // length: length of hillslope (m)
        // slope: slope of hillslope (m)
        // Ds: depth of top soil (m)
        // Dg: depth of unconfined groundwater acquifer below topsoil (m)
        // Dr: depth of river (m)
        // Drw: depth of river water (m)
        // kg: hydraulic conductivity (m/sec)
        // conlen: contact length between the river and aquifer (m)
        // grad: gradient of water head
        // Q: discharge exchanged between aquifer and river (m^3/sec)
        double H1, H2, hs1, hs2, grad, conlen, Hrd;
        Drw = Math.max(Drw, 0.0);
        Drw = Math.min(Dr, Drw);
        Hrd = Ds + Dg - Dr; // distance from datum to riverbed (m)

        // waterhead of groundwater (m)
        if (Dtg < Dr - Drw) {
            H1 = 0.5 * length * slope + Ds + Dg - Dtg;
        } else {
            H1 = Math.sqrt(0.5 * length * slope) + Ds + Dg - Dtg;
        }
        hs1 = H1 - Hrd;// saturated acquifer depth (m)
        if (Hrd >= 0.0) {
            H2 = Hrd + Drw; // waterhead of river (m)
            hs2 = Drw; // water depth in river (m)
        } else {
            hs2 = Math.max(0.0, Drw);
            H2 = Math.max(Hrd + Drw, 0.0);
        }

        // gradient of waterhead
        grad = (H1 - H2) / (0.5 * length);
        // contact length between the river and aquifer (m)
        conlen = 0.5 * (Math.abs(hs1) + hs2);
        // discharge per unit width of a hillslope (m3/sec/m)
        Q = kg * grad * conlen;
        return Q;
    }

    public double conductivity_V(double k0, double wsat, double wrsd, double n,
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
            System.out.println(this.getClass().getName() + "wrong in calculating conductivity" + "w=" + w
                    + "conductivity=" + conductivity);
        }
        return conductivity;
    }
}
