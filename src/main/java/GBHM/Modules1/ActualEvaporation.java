package GBHM.Modules1;

//@Component(author = "longyinping11@lzb.ac.cn", createTime = "2012.12.1", description = "actual evaporation", domain = "hydroloy", keyword = "evaporation", name = "ActualEvaporation", source = "Yang Dawen", version = "GBHM 2006", references = "Yan Dawen, Li Chong, Ni Guangheng. Application of a Distributed Hydrologihcal Model to the Yellow River Basin[J].Acta Geographic Sinica, 2004, 59(1):143-154.")
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolString;

public class ActualEvaporation extends AbsComponent {

//	@Variable(access = AccessType.IN, description = "potential evaporation (mm H2O)", unit = "mm")
    public PoolDouble Ep;
//	@Variable(access = AccessType.IN, description = "hourly rainfall (mm)", unit = "mm")
    public PoolDouble prec;
//	@Variable(access = AccessType.IN, description = "root depth, varied by landuse type(m)", unit = "m")
    public PoolDouble root;
//	@Variable(access = AccessType.IN, description = "number of UZ(Unsaturated Zone)layer", unit = "")
//    int layer;
//	@Variable(access = AccessType.IN, description = "depth of each UZ(Unsaturated Zone)layer(m)", unit = "m")
    public PoolDoubleArray D;
//	@Variable(access = AccessType.INOUT, description = "soil moisture for each UZ(Unsaturated Zone)layer,(mm H2O/ mm Soil)", unit = "mm/mm")
    public PoolDoubleArray w;
//	@Variable(access = AccessType.INOUT, description = "canopy storage(mm H2O)", unit = "mm")
    public PoolDouble Cst;
//	@Variable(access = AccessType.INOUT, description = "surface water storage(mm H2O)", unit = "mm")
    public PoolDouble sst;
//	@Variable(access = AccessType.OUT, description = "actual evaporation from canopy storage(mm H2O)", unit = "mm")
    public PoolDouble EfromCanopy;
//	@Variable(access = AccessType.OUT, description = "actual transpiration from crop(mm H2O)", unit = "mm")
    public PoolDouble EfromCrop;
//	@Variable(access = AccessType.OUT, description = "actual evaporation from soil surface(mm H2O)", unit = "mm")
    public PoolDouble EfromSurface;
//	@Variable(access = AccessType.OUT, description = "actual evaporation(mm H2O)", unit = "mm")
    public PoolDouble Eact;
//	@Parameter(description = "LAI", unit = "", default_value = "", range = "")
    public PoolDouble dLAI;
//	@Parameter(description = "maximum LAI in a year, varied by landuse type", unit = "", default_value = "", range = "")
    public PoolDouble LAImax;
//	@Parameter(description = "landuse type ", unit = "", default_value = "", range = "")
    public PoolString landuse;
    // @Parameter(default_value = "", description =
    // "evaporation coefficient of crop", range = "", unit = "")
    // public double kcrop;
//	@Parameter(description = "factor used to calculate c1, and c1 is used in reference evaporation calculation", unit = "", default_value = "0.31", range = "")
    public PoolDouble c1_factor;
//	@Parameter(description = "factor used to calculate c2, and c2 is used in reference evaporation calculation", unit = "", default_value = "0.05", range = "")
    public PoolDouble c2_factor1;
//	@Parameter(description = "factor used to calculate c2, and c2 is used in reference evaporation calculation", unit = "", default_value = "0.10", range = "")
    public PoolDouble c2_factor2;
//	@Parameter(description = "factor used to calculate c3, and c3 is used in reference evaporation calculation", unit = "", default_value = "0.4", range = "")
    public PoolDouble c3_factor1;
//	@Parameter(description = "factor used to calculate c3, and c3 is used in reference evaporation calculation", unit = "", default_value = "0.4", range = "")
    public PoolDouble c3_factor2;
//	@Parameter(description = "factor used in reference evaporation calculation", unit = "", default_value = "0.1", range = "")
    public PoolDouble c5;
//	@Parameter(default_value = "0.1", description = "root density ratio of deepest soil layer", range = "(0,1)", unit = "")
    public PoolDouble para_r;
//	@Parameter(default_value = "", description = "soil moisture at field capacity, varied by soil types", unit = "", range = "")
    public PoolDoubleArray wfld;
//	@Parameter(default_value = "", description = "residual soil moisture, varied by soil types", range = "(0,1)", unit = "")
    public PoolDoubleArray wrsd;

    @Override
    public void init() throws Exception {
        //       D = new double[layer];
        //       w = new double[layer];
    }

    @Override
    public void run() throws Exception {


//	@Variable(access = AccessType.IN, description = "potential evaporation (mm H2O)", unit = "mm")
        double Ep;
//	@Variable(access = AccessType.IN, description = "hourly rainfall (mm)", unit = "mm")
        double prec;
//	@Variable(access = AccessType.IN, description = "root depth, varied by landuse type(m)", unit = "m")
        double root;
//	@Variable(access = AccessType.IN, description = "number of UZ(Unsaturated Zone)layer", unit = "")
        int layer;
//	@Variable(access = AccessType.IN, description = "depth of each UZ(Unsaturated Zone)layer(m)", unit = "m")
        double D[];
//	@Variable(access = AccessType.IN, description = "Area fraction of each landuse type", unit = "")
        double land_ratio;
//	@Variable(access = AccessType.INOUT, description = "soil moisture for each UZ(Unsaturated Zone)layer,(mm H2O/ mm Soil)", unit = "mm/mm")
        double w[];
//	@Variable(access = AccessType.INOUT, description = "canopy storage(mm H2O)", unit = "mm")
        double Cst;
//	@Variable(access = AccessType.INOUT, description = "surface water storage(mm H2O)", unit = "mm")
        double sst;
//	@Variable(access = AccessType.OUT, description = "actual evaporation from canopy storage(mm H2O)", unit = "mm")
        double EfromCanopy;
//	@Variable(access = AccessType.OUT, description = "actual transpiration from crop(mm H2O)", unit = "mm")
        double EfromCrop;
//	@Variable(access = AccessType.OUT, description = "actual evaporation from soil surface(mm H2O)", unit = "mm")
        double EfromSurface;
//	@Variable(access = AccessType.OUT, description = "actual evaporation(mm H2O)", unit = "mm")
        double Eact;
//	@Parameter(description = "LAI", unit = "", default_value = "", range = "")
        double dLAI;
//	@Parameter(description = "maximum LAI in a year, varied by landuse type", unit = "", default_value = "", range = "")
        double LAImax;
//	@Parameter(description = "landuse type ", unit = "", default_value = "", range = "")
        String landuse;
        // @Parameter(default_value = "", description =
        // "evaporation coefficient of crop", range = "", unit = "")
        //  double kcrop;
//	@Parameter(description = "factor used to calculate c1, and c1 is used in reference evaporation calculation", unit = "", default_value = "0.31", range = "")
        double c1_factor;
//	@Parameter(description = "factor used to calculate c2, and c2 is used in reference evaporation calculation", unit = "", default_value = "0.05", range = "")
        double c2_factor1;
//	@Parameter(description = "factor used to calculate c2, and c2 is used in reference evaporation calculation", unit = "", default_value = "0.10", range = "")
        double c2_factor2;
//	@Parameter(description = "factor used to calculate c3, and c3 is used in reference evaporation calculation", unit = "", default_value = "0.4", range = "")
        double c3_factor1;
//	@Parameter(description = "factor used to calculate c3, and c3 is used in reference evaporation calculation", unit = "", default_value = "0.4", range = "")
        double c3_factor2;
//	@Parameter(description = "factor used in reference evaporation calculation", unit = "", default_value = "0.1", range = "")
        double c5;
//	@Parameter(default_value = "0.1", description = "root density ratio of deepest soil layer", range = "(0,1)", unit = "")
        double para_r;
//	@Parameter(default_value = "", description = "soil moisture at field capacity, varied by soil types", unit = "", range = "")
        double[] wfld;
//	@Parameter(default_value = "", description = "residual soil moisture, varied by soil types", range = "(0,1)", unit = "")
        double[] wrsd;


        double c1, c2, c3, c4; // crop coefficient
        double Etr;//used to calculate EfromCanopy
        double Es;//reference evaporation (mm)
        int i = 0; // layer of root
        int j, k;
        double tmp = 0, temp, tmpEp;
        double para_a, y, kmoist;

        this.EfromCanopy.setValue(0);
        this.EfromCrop.setValue(0);
        this.EfromSurface.setValue(0);
        if (this.D.getValue().length != this.w.getValue().length) {
            System.out.println(this.getClass().getName() + "D.length is not equal to w.length: " + "D.length=" + this.D.getValue().length + ", w.length=" + this.w.getValue().length);
        }

        Ep = this.Ep.getValue();
        prec = this.prec.getValue();
        root = this.root.getValue();
        D = this.D.getValue();
        layer = this.D.getValue().length;
        w = this.w.getValue();
        Cst = this.Cst.getValue();
        dLAI = this.dLAI.getValue();
        LAImax = this.LAImax.getValue();
        landuse = this.landuse.getValue();
        c1_factor = this.c1_factor.getValue();
        c2_factor1 = this.c2_factor2.getValue();
        c2_factor2 = this.c2_factor2.getValue();
        c3_factor1 = this.c3_factor1.getValue();
        c3_factor2 = this.c3_factor2.getValue();
        c5 = this.c5.getValue();
        para_r = this.para_r.getValue();
        wfld = this.wfld.getValue();
        wrsd = this.wrsd.getValue();
        //outs
        sst = this.sst.getValue();
        EfromCanopy = 0;
        EfromCrop = 0;
        EfromSurface = 0;

        // **************************************************************
        // Evaporation from canopy
        // **************************************************************
        c1 = c1_factor * dLAI;
        c2 = c2_factor1 + c2_factor2 * dLAI / LAImax;
        c3 = c3_factor1 + c3_factor2 * Math.pow(dLAI / LAImax, 0.5);

        c4 = 0.2;
        if (landuse.equals("urban-area")) {
            c4 = 0.06;
        } else if (landuse.equals("baresoil")) {
            c4 = 0.2;
        } else if (landuse.equals("forest")) {
            c4 = 0.1;
        } else if (landuse.equals("irrigated-cropland")) {
            c4 = 0.2;
        } else if (landuse.equals("non-irrigated cropland")) {
            c4 = 0.15;
        } else if (landuse.equals("grassland")) {
            c4 = 0.6;
        } else if (landuse.equals("shrub")) {
            c4 = 0.1;
        } else if (landuse.equals("caodian")) {
            c4 = 0.3;
        }
        /*    
         switch (landuse) {
         case "urban-area":
         c4 = 0.06;
         break;
         case "baresoil":
         c4 = 0.2;
         break;
         case "forest":
         c4 = 0.1;
         break;
         case "irrigated-cropland":
         c4 = 0.2;
         break;
         case "upland":
         c4 = 0.15;
         break;
         case "grassland":
         c4 = 0.6;
         break;
         case "shrub":
         c4 = 0.1;
         break;
         case "caodian":
         c4 = 0.3;
         break;
         default:
         c4 = 0.2;
         }
         */

        Etr = Ep * Math.min(c2 + c1, 1.0);
        Es = (Ep * (c2 + (1 - c2) * (1 - Math.min(c2 + c1, 1.0))) - Etr
                * (1 - Math.min(c2 + c1, 1.0)))
                * (1 - Math.exp(-c4 * dLAI))
                + Ep
                * Math.exp(-c4 * dLAI)
                * Math.pow(c5 + (1.0 - c5) * dLAI / LAImax, 1.0 + c3);
        if (landuse.equals("baresoil")) {

            Es = Ep * Math.exp(-c4 * dLAI);
        }
        if (landuse.equals("waterbody")) {

            Es = Ep;
        }
        Etr = Etr * (1 - Math.exp(-c4 * dLAI));

        // if (kcrop > 1 || (dLAI - LAImax) > 1.0e-6) {
        // System.out.println("kcrop out of range"+krop+dLAI+LAImax+landuse);
        // }
        if (prec <= 0.02) {
            if (Etr > 0.1e-9) {
                EfromCanopy = Math.min(Etr, Cst);
                Cst = Cst - EfromCanopy;

                // **************************************************************
                // Evaporation from vegetation
                // **************************************************************
                if (root > 0) {
                    for (j = 0; j < layer; j++) {
                        tmp = tmp + D[j];
                        i = i + 1;
                        if (tmp > root) {
                            break;
                        }
                    }
                    if (layer < i) {
                        i = layer;
                    }
                    para_a = 1.0 / ((double) i - 0.5 * (1.0 - para_r)
                            * (double) (i - 1));

                    for (j = 1; j <= i; j++) {
                        y = (1.0 - (1.0 - para_r) * (double) (j - 1)
                                / (double) i)
                                * para_a;
                        kmoist = this.ETsoil(w[j - 1], wfld[j - 1], wrsd[j - 1]);
                        tmp = y * Etr * kmoist;
                        temp = Math.max(0.0, 1000.0
                                * (w[j - 1] - wrsd[j - 1] - 1.0E-6) * D[j - 1]);
                        tmp = Math.min(tmp, temp);

                        w[j - 1] = w[j - 1] - tmp / (1000.0 * D[j - 1]);

                        if (w[j - 1] < wrsd[j - 1] + 0.1E-6) {
                            System.out.println("crop evap from w");
                        }
                        EfromCrop = EfromCrop + tmp;
                    }
                }
            }

            // **************************************************************
            // Evaporation from Surface
            // **************************************************************
            if (Es > 0.1E-9) {
                if (sst > 0) {
                    EfromSurface = Math.min(Es, sst);
                    sst = sst - EfromSurface;
                    if (EfromSurface < -0.0001) {
                        System.out.println(this.getClass().getName() + "wrong in EfromSurface");
                        System.out.println(EfromSurface);
                    }
                } else {
                    sst = 0;
                }
                tmpEp = Es - EfromSurface;
                if (tmpEp > 0.1E-9) {
                    kmoist = ETsoil(w[0], wfld[0], wrsd[0]);
                    tmp = tmpEp * kmoist;
                    if (tmp < 0.0) {
                        System.out.println(this.getClass().getName() + "wrong in EfromSoil");
                        System.out.print(tmp);
                    }
                    temp = Math
                            .max(0.0, 1000.0 * (w[0] - wrsd[0] - 1.0E-6) * D[0]);
                    tmp = Math.min(tmp, temp);
                    w[0] = w[0] - tmp / (1000.0 * D[0]);
                    if (w[0] < wrsd[0] + 0.1E-6) {
                        System.out.println(this.getClass().getName() + "wrong in calculate surface evap");
                    }
                    EfromSurface = EfromSurface + tmp;
                }
            }
        }
        Eact = EfromCanopy + EfromCrop + EfromSurface;


        this.sst.setValue(sst);
        this.EfromCanopy.setValue(EfromCanopy);
        this.EfromCrop.setValue(EfromCrop);
        this.EfromSurface.setValue(EfromSurface);
        this.Eact.setValue(Eact);
        this.w.setValue(w);
        this.Cst.setValue(Cst);

    }

    public double ETsoil(double w, double wfld, double wrsd) {
        double kmoist = 0;
        if (w > wfld) {
            kmoist = 1;
        }
        if (w < wfld && w > wrsd) {
            kmoist = (w - wrsd) / (wfld - wrsd);
        }
        if (kmoist < 0 || kmoist > 1) {
            System.out.println(this.getClass().getName() + "wrong in calculating Kmoist in ETsoil");
        }
        return kmoist;
    }

    @Override
    public void clear() throws Exception {
    }
}
