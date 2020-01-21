package GBHM.Modules;

@Component(author = "longyinping11@lzb.ac.cn", 
		   createTime = "2012-11-12", 
		   description = "calculate snow depth on the ground(mm) and snow melt",
		   domain = "hydrology",
		   keyword = "snowmelt", 
		   name = "snowmelt", 
		   source = "Yang Dawen",
		   version = "GBHM 2006")
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;

public class Snow  extends AbsComponent{

	@Variable(access = AccessType.IN, description = "temperature(degree)", unit = "degree")
    public PoolDouble temper;
	@Variable(access = AccessType.INOUT, description = "snow depth(mm H2O)", unit = "mm")
    public PoolDouble snow;
	@Variable(access = AccessType.INOUT, description = "net precipitation(mm H2O)", unit = "mm")
    public PoolDouble Pnet;
	@Variable(access = AccessType.INOUT, description = "surface water storage(mm H2O)", unit = "mm")
    public PoolDouble sst;
	@Variable(access = AccessType.OUT, description = "snow melt(mm H2O)", unit = "mm")
    public PoolDouble snowmelt;
	@Parameter(description = "snowmelt factor", unit = "", default_value = "0.10", range = "")
    public PoolDouble SMF;
	@Parameter(description = "temperature above which snowmelt happens (degree)", unit = "degree", default_value = "1.5", range = "")
    public PoolDouble SMFTMP;

    @Override
    public void init() throws Exception {
    }

    @Override
    public void run() throws Exception {

        double temper;
        double snow;
        double Pnet;
        double sst;
        double snowmelt;
        double SMF;
	    double SMFTMP;

        temper = this.temper.getValue();
        snow = this.snow.getValue();
        Pnet = this.Pnet.getValue();
        sst = this.sst.getValue();
        snowmelt = this.snowmelt.getValue();
        SMF = this.SMF.getValue();
        SMFTMP = this.SMFTMP.getValue();

        if (temper < 1) {
            snow = snow + Pnet;
            Pnet = 0;
        }
        if (temper > SMFTMP && snow > 0) {
            snowmelt = (SMF + Pnet / 20.0) * (temper - SMFTMP);
            if (snowmelt > snow) {
                snowmelt = snow;
            }
            snow = snow - snowmelt;
            Pnet = Pnet + snowmelt;
        }
        sst = sst + Pnet;

        this.snow.setValue(snow);
        this.Pnet.setValue(Pnet);
        this.sst.setValue(sst);
        this.snowmelt.setValue(snowmelt);

    }

    @Override
    public void clear() throws Exception {
    }
}
