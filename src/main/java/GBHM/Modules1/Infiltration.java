package GBHM.Modules1;

//@Component(author = "longyinping11@lzb.ac.cn", createTime = "2012.12.12", description = "infiltration from surface store to unsaturated zone", domain = "hydroloy", keyword = "infiltration", name = "Infiltration", source = "Yang Dawen", version = "GBHM 2006", references = "Yan Dawen, Li Chong, Ni Guangheng. Application of a Distributed Hydrologihcal Model to the Yellow River Basin[J].Acta Geographic Sinica, 2004, 59(1):143-154.")
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDoubleArray;

public class Infiltration extends AbsComponent {

//	@Variable(access = AccessType.IN, description = "time step (second)", unit = "second")
    public PoolDouble dt;
//	@Variable(access = AccessType.IN, description = "number of Unsaturated Zone layers", unit = "")
//    @In
//    public int layer;
//	@Variable(access = AccessType.IN, description = "depth of each UZ(Unsaturated Zone)layer(m)", unit = "m")
    public PoolDoubleArray D;
//	@Variable(access = AccessType.INOUT, description = "surface water storage(mm H2O)", unit = "mm")
    public PoolDouble sst;
//	@Variable(access = AccessType.INOUT, description = "soil moisture for each UZ(Unsaturated Zone)layer,(mm H2O/ mm Soil)", unit = "mm/mm")
    public PoolDoubleArray w;
//	@Variable(access = AccessType.OUT, description = "infiltration(mm H2O)", unit = "mm")
    public PoolDouble inf;
//	@Parameter(default_value = "", description = "saturated hydraulic conductivity (mm H2O/hour) for each UZ(Unsaturated Zone)layer", range = "", unit = "mm H2O/hour")
    public PoolDoubleArray k0;
//	@Parameter(default_value = "", description = "saturated soil moisture, varied by soil types", range = "(0,1)", unit = "")
    public PoolDoubleArray wsat;

    @Override
    public void init() throws Exception {
        //       D = new double[layer];
        //       k0 = new double[layer];
        //       w = new double[layer];
    }

    @Override
    public void run() throws Exception {

        //	@Variable(access = AccessType.IN, description = "time step (second)", unit = "second")
        double dt;
//	@Variable(access = AccessType.IN, description = "number of Unsaturated Zone layers", unit = "")
        int layer;
//	@Variable(access = AccessType.IN, description = "depth of each UZ(Unsaturated Zone)layer(m)", unit = "m")
        double D[];
//	@Variable(access = AccessType.INOUT, description = "surface water storage(mm H2O)", unit = "mm")
        double sst;
//	@Variable(access = AccessType.INOUT, description = "soil moisture for each UZ(Unsaturated Zone)layer,(mm H2O/ mm Soil)", unit = "mm/mm")
        double w[];
//	@Variable(access = AccessType.OUT, description = "infiltration(mm H2O)", unit = "mm")
        double inf;
//	@Parameter(default_value = "", description = "saturated hydraulic conductivity (mm H2O/hour) for each UZ(Unsaturated Zone)layer", range = "", unit = "mm H2O/hour")
        double k0[];
//	@Parameter(default_value = "", description = "saturated soil moisture, varied by soil types", range = "(0,1)", unit = "")
        double[] wsat;

        dt = this.dt.getValue();
        layer = this.D.getValue().length;
        D = this.D.getValue();
        sst = this.sst.getValue();
        w = this.w.getValue();
        k0 = this.k0.getValue();
        wsat = this.wsat.getValue();

        double tmp;

        layer = D.length;
        //inf = 0;

        if (sst > 0.0) {
            inf = Math.max(k0[0] * dt / 3600, 0.0);
            inf = Math.min(inf, sst);
            sst = sst - inf;
            tmp = w[0] + inf / (1000 * D[0]);
            w[0] = Math.min(wsat[0], tmp);
            sst = sst + (tmp - w[0]) * D[0] * 1000;
            if (sst < 0.1e-9) {
                sst = 0.0;
            }
        }
        inf = 0;


        this.sst.setValue(sst);
        this.w.setValue(w);
        this.inf.setValue(inf);
    }

    @Override
    public void clear() throws Exception {
    }
}
