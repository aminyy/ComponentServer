package GBHM.Modules0;

//@Component(author = "longyinping11@lzb.ac.cn", createTime = "2012.12.28", description = "calculation of surface routing: steady constant sheet flow,using Manning's equation", domain = "hydroloy", keyword = "Sheet Flow, Surface routing", name = "SheetFlow", source = "Yang Dawen", version = "GBHM 2006", references = "Yan Dawen, Li Chong, Ni Guangheng. Application of a Distributed Hydrologihcal Model to the Yellow River Basin[J].Acta Geographic Sinica, 2004, 59(1):143-154.")
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolString;

public class SheetFlow  extends AbsComponent{
//	@Variable(access = AccessType.IN, description = "time step (second)", unit = "second")

    public PoolDouble dt;
//	@Variable(access = AccessType.IN, description = "depth of river (m)", unit = "")
    public PoolDouble Dr;
//	@Variable(access = AccessType.IN, description = "depth of river water (m)", unit = "m")
    public PoolDouble Drw;
//	@Variable(access = AccessType.INOUT, description = "surface water storage(mm H2O)", unit = "mm")
    public PoolDouble sst;
//	@Variable(access = AccessType.OUT, description = "surface runoff(m3/s)", unit = "m3/s")
    public PoolDouble q_hillslope;
//	@Parameter(default_value = "1.0", description = "soil water conservation factor", range = "", unit = "")
    public PoolDouble soil_con_f;
//	@Parameter(default_value = "", description = "maximum surface water detension (mm)", range = "", unit = "mm")
    public PoolDouble Sstmax;
//	@Parameter(default_value = "", description = "Leaf Area Index", range = "", unit = "")
    public PoolDouble LAI;
//	@Parameter(description = "maximum LAI in a year, varied by landuse type", unit = "", default_value = "", range = "")
    public PoolDouble LAImax;
//	@Parameter(default_value = "", description = "surface roughness (Manning's coefficient)", range = "", unit = "")
    public PoolDouble surfn;
//	@Parameter(default_value = "", description = "average slope of the local grid", range = "", unit = "")
    public PoolDouble slp;
//	@Parameter(description = "landuse type ", unit = "", default_value = "", range = "")
    public PoolString landuse;
//	@Parameter(default_value = "", description = "average hillslope length (m)", range = "", unit = "m")
    public PoolDouble length;
//	@Parameter(default_value = "", description = "area of the local grid (m2)", range = "", unit = "m2")
    public PoolDouble area;

    @Override
    public void init() throws Exception {
    }

    @Override
    public void run() throws Exception {
//	@Variable(access = AccessType.IN, description = "time step (second)", unit = "second")
        double dt;
//	@Variable(access = AccessType.IN, description = "depth of river (m)", unit = "")
        double Dr;
//	@Variable(access = AccessType.IN, description = "depth of river water (m)", unit = "m")
        double Drw;
//	@Variable(access = AccessType.INOUT, description = "surface water storage(mm H2O)", unit = "mm")
        double sst;
//	@Variable(access = AccessType.OUT, description = "surface runoff(m3/s)", unit = "m3/s")
        double q_hillslope;
//	@Parameter(default_value = "1.0", description = "soil water conservation factor", range = "", unit = "")
        double soil_con_f;
//	@Parameter(default_value = "", description = "maximum surface water detension (mm)", range = "", unit = "mm")
        double Sstmax;
//	@Parameter(default_value = "", description = "Leaf Area Index", range = "", unit = "")
        double LAI;
//	@Parameter(description = "maximum LAI in a year, varied by landuse type", unit = "", default_value = "", range = "")
        double LAImax;
//	@Parameter(default_value = "", description = "surface roughness (Manning's coefficient)", range = "", unit = "")
        double surfn;
//	@Parameter(default_value = "", description = "average slope of the local grid", range = "", unit = "")
        double slp;
//	@Parameter(description = "landuse type ", unit = "", default_value = "", range = "")
        String landuse;
//	@Parameter(default_value = "", description = "average hillslope length (m)", range = "", unit = "m")
        double length;
//	@Parameter(default_value = "", description = "area of the local grid (m2)", range = "", unit = "m2")
        double area;

        dt = this.dt.getValue();
        Dr = this.Dr.getValue();
        Drw = this.Drw.getValue();
        sst = this.sst.getValue();
        soil_con_f = this.soil_con_f.getValue();
        Sstmax = this.Sstmax.getValue();
        LAI = this.LAI.getValue();
        LAImax = this.LAImax.getValue();
        surfn = this.surfn.getValue();
        slp = this.slp.getValue();
        landuse = this.landuse.getValue();
        length = this.length.getValue();
        area = this.area.getValue();


        double detension, water_depth, surface_n, waterhead;
        double power = 1.6667;        

        detension = Sstmax * soil_con_f;
        detension = Math.max(3.0, detension);
        water_depth = Math.max(0.0, sst - detension);
        if (water_depth <= 0.01 || Drw >= 1.0 * Dr) {
            q_hillslope = 0;
        } else {
            sst = sst - water_depth;
            water_depth = 0.001 * water_depth; // in meter, surface runoff
            surface_n = surfn;
            waterhead = slp;
            q_hillslope = dt * Math.sqrt(waterhead)
                    * Math.pow(water_depth, power) / surface_n; // m3/m, one
            // hillslope
            if (q_hillslope <= 0.1E-20) {
                q_hillslope = 0.0;
            }
            if (landuse.equals("irrigated-cropland")) {
                q_hillslope = q_hillslope * Math.max(0.3, 1.0 - LAI / LAImax);
            }
            q_hillslope = Math.min(q_hillslope, water_depth * length);
            water_depth = water_depth - q_hillslope / length;
            sst = sst + 1000.0 * water_depth;
            q_hillslope = q_hillslope / dt * area / length;// m3/m-->m3/s
        }

        this.sst.setValue(sst);
        this.q_hillslope.setValue(q_hillslope);
    }

    @Override
    public void clear() throws Exception {
    }
}
