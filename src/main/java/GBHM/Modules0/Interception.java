package GBHM.Modules0;

//@Component(author = "longyinping11@lzb.ac.cn", createTime = "2012.11.25", description = "interception by canopy", domain = "hydrology", keyword = "interception", name = "interception", source = "Yang Dawen", version = "GBHM 2006", references = "Yan Dawen, Li Chong, Ni Guangheng. Application of a Distributed Hydrologihcal Model to the Yellow River Basin[J].Acta Geographic Sinica, 2004, 59(1):143-154.")
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolString;

public class Interception  extends AbsComponent{

//	@Variable(access = AccessType.INOUT, description = "canopy storage(mm H2O)", unit = "mm")
    public PoolDouble Cst;
    //	@Variable(access = AccessType.IN, description = "hourly rainfall (mm)", unit = "mm")
    public PoolDouble prec;
//	@Variable(access = AccessType.OUT, description = "net precipitation(mm H2O", unit = "mm")
    public PoolDouble Pnet;
//	@Parameter(description = "LAI", unit = "", default_value = "", range = "")
    public PoolDouble dLAI;
//	@Parameter(description = "maximal canopy storage calculation factor, multiplied by LAI, 0.15 for forest or shrub, 0.10 for other landuse types", unit = "", default_value = "0.10", range = "")
//    public double Cstmax_factor;
    //	@Parameter(description = "landuse type ", unit = "", default_value = "", range = "")
    public PoolString landuse;

    @Override
    public void init() throws Exception {
    }

    @Override
    public void run() throws Exception {

        //	@Variable(access = AccessType.INOUT, description = "canopy storage(mm H2O)", unit = "mm")

        double Cst;
//	@Variable(access = AccessType.INOUT, description = "net precipitation(mm H2O", unit = "mm")
        double Pnet;
//	@Parameter(description = "LAI", unit = "", default_value = "", range = "")
        double dLAI;
//	@Parameter(description = "maximal canopy storage calculation factor, multiplied by LAI, 0.15 for forest or shrub, 0.10 for other landuse types", unit = "", default_value = "0.10", range = "")
//    public double Cstmax_fator;
        //	@Parameter(description = "landuse type ", unit = "", default_value = "", range = "")
        String landuse;

        Cst = this.Cst.getValue();
        Pnet = this.prec.getValue();
        dLAI = this.dLAI.getValue();
        landuse = this.landuse.getValue();

        double Cstmax; // maximal canopy storage
        double DeficitCst;
        double Cstmax_factor;
        Cstmax_factor = 0.10;

        if (landuse.equals(
                "forest") || landuse.equals("shrub")) {
            Cstmax_factor = 0.15;
        }
        Cstmax = Cstmax_factor * dLAI;
        DeficitCst = Cstmax - Cst;
        DeficitCst = Math.max(DeficitCst, 0);
        if (Pnet > DeficitCst) {
            Cst = Cst + DeficitCst;
            Pnet = Pnet - DeficitCst;
        } else {
            Cst = Cst + Pnet;
            Pnet = 0;
        }


        this.Cst.setValue(Cst);
        this.Pnet.setValue(Pnet);

    }

    @Override
    public void clear() throws Exception {
    }
}
