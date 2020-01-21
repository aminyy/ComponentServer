package GBHM.Modules0;


//@Component(author = "longyinping11@lzb.ac.cn", createTime = "2012.1.6", description = "River routing using Kinematic Wave Method(Nonlinear Scheme using Newton's method)", domain = "hydroloy", keyword = "River Routing", name = "RiverRouting", source = "Yang Dawen", version = "GBHM 2006", references = "Yan Dawen, Li Chong, Ni Guangheng. Application of a Distributed Hydrologihcal Model to the Yellow River Basin[J].Acta Geographic Sinica, 2004, 59(1):143-154.")
public class KinematicWaveModel {

//	@Variable(access = AccessType.IN, description = "time step (second)", unit = "second")
    public double dt;
//	@Variable(access = AccessType.IN, description = "lateral inflow of last time step (m^3/s)", unit = "m3/s")
    public double qlin1;
//	@Variable(access = AccessType.IN, description = "lateral inflow of current time step (m^3/s)", unit = "m3/s")
    public double qlin2;
    // @Variable(access = AccessType.IN, description = "", unit = "")
    // public double q1_upper;
//	@Variable(access = AccessType.IN, description = "discharge of last time step (m^3/s)", unit = "m3/s")
    public double q1;
//	@Variable(access = AccessType.IN, description = "discharge of current time step (m^3/s) from upper rivers", unit = "m3/s")
    public double q2_upper;
//	@Variable(access = AccessType.OUT, description = "depth of river water (m)", unit = "m")
    public double Drw;
//	@Variable(access = AccessType.OUT, description = "discharge of current time step (m^3/s)", unit = "m3/s")
    public double q2;
//	@Parameter(default_value = "", description = "flow interval length (m)", range = "", unit = "m")
    public double dx;
//	@Parameter(default_value = "", description = "width of river (m)", range = "", unit = "m")
    public double b;
//	@Parameter(default_value = "", description = "slope of river bed (ND)", range = "", unit = "")
    public double s0;
//	@Parameter(default_value = "", description = "Manning's roughness", range = "", unit = "")
    public double roughness;

    public void run() {
        
        double beta = 0.6;
        double criterion = 0.0001;
        double h1, tmp, f, df, h2 = 0, p, alfa, aa, bb, cc, qq1, qq2 = 0, ctmp;
        int k;
        // m3/s-->m3/s/m
        qlin1 = qlin1 / dx;
        qlin2 = qlin2 / dx;

        h1 = q1 / b;
        for (k = 1; k < 30; k++) {
            tmp = roughness * q1 / Math.sqrt(s0);
            f = b * h1 - Math.pow(tmp, 0.6) * Math.pow(b + 2.0 * h1, 0.4);
            if (k > 1 && Math.abs(f) < criterion) {
                break;
            }
            df = b - 0.8 * Math.pow(tmp / (b + 2.0 * h1), 0.6);
            h2 = h1 - f / df;
            h1 = h2;
        }
        Drw = h2;
        p = b + 2.0 * Drw; // for rectangular channel

        // the initial discharge estimated using linear scheme
        alfa = Math
                .pow(roughness * Math.pow(p, 2.0 / 3.0) / Math.sqrt(s0), 0.6);
        if (q1 + q2_upper <= 0.0) {
            cc = 0;
        } else {
            cc = Math.pow(0.5 * (q1 + q2_upper), beta - 1.0);
        }
        aa = dt * q2_upper / dx + alfa * beta * q1 * cc + 0.5 * (qlin1 + qlin2)
                * dt;
        bb = dt / dx + alfa * beta * cc;
        qq1 = aa / bb;
        if (qq1 <= 0.1e-5) {
            qq1 = 0.1e-5;
        }
        // Using Newton's method to calculate discharge
        ctmp = dt * q2_upper / dx + alfa * Math.pow(q1, beta) + 0.5 * dt
                * (qlin2 + qlin1);
        for (k = 1; k < 30; k++) {
            f = dt * qq1 / dx + alfa * Math.pow(qq1, beta) - ctmp;
            if ((k > 1) && (Math.abs(f) < criterion)) {
                break;
            }
            df = dt / dx + alfa * beta * Math.pow(qq1, beta - 1.0);
            qq2 = qq1 - f / df;
            if (qq2 <= 0.1e-5) {
                qq2 = 0.0;
                break;
            }
            qq1 = qq2;
        }
        q2 = qq2;

        h1 = q2 / b;
        for (k = 1; k < 30; k++) {
            tmp = roughness * q2 / Math.sqrt(s0);
            f = b * h1 - Math.pow(tmp, 0.6) * Math.pow(b + 2.0 * h1, 0.4);
            if (k > 1 && Math.abs(f) < criterion) {
                break;
            }
            df = b - 0.8 * Math.pow(tmp / (b + 2.0 * h1), 0.6);
            h2 = h1 - f / df;
            h1 = h2;
        }
        Drw = h2;
       
    }
}
