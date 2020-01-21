package prms.process;

import java.util.logging.*;
import net.casnw.home.meta.DatatypeEnum;
import net.casnw.home.meta.ModuleMeta;
import net.casnw.home.meta.VariableMeta;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolIntegerArray;

@ModuleMeta(moduleClass = "prms.process.Ddsolrad",
        name = "Ddsolrad",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "Radiation",
        description = "Solar radiation distribution algorithm and estimation procedure for missing radiation data."
        + "Procedures for distributing solar radiation to each HRU "
        + "and for estimating missing solar radiation data using a "
        + "maximum temperature / degree-day relationship. ")
/*   Description
 ("Solar radiation distribution algorithm and estimation procedure for missing radiation data." +
 "Procedures for distributing solar radiation to each HRU " +
 "and for estimating missing solar radiation data using a " +
 "maximum temperature / degree-day relationship. ")
 */

/*Keywords
 ("Radiation")
 */
public class Ddsolrad extends AbsComponent {

    private static final Logger log = Logger.getLogger("HOME.model." + Ddsolrad.class.getSimpleName());

    // private fields
    private static double SOLF[] = {
        .20, .35, .45, .51, .56, .59, .62, .64, .655, .67,
        .682, .69, .70, .71, .715, .72, .722, .724, .726, .728,
        .73, .734, .738, .742, .746, .75
    };
    private static double NEARZERO = 1.0e-10;

    private double plrad[];

    // Input params
    /* Role(PARAMETER)
     Description("Number of HRUs.")
     */
    @VariableMeta(name = "nhru",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of HRUs.")
    public PoolInteger nhru= new PoolInteger();

    /* Role(PARAMETER)
     Description("Number of radiation planes.")
     */
    @VariableMeta(name = "nradpl",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of radiation planes.")
    public PoolInteger nradpl= new PoolInteger();

    /* Role(PARAMETER)
     Description("Number of solar radiation stations.")
     */
    @VariableMeta(name = "nsol",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of solar radiation stations.")
    public PoolInteger nsol= new PoolInteger();

    /* Role(PARAMETER)
     Description("Intercept in temperature / degree-day relationship. Intercept in relationship: dd-coef =  dday_intcp + dday_slope*(tmax)+1.")
     Unit("dday")
     Bound ("nmonths")
     */
    @VariableMeta(name = "dday_intcp",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Intercept in temperature / degree-day relationship. Intercept in relationship: dd-coef =  dday_intcp + dday_slope*(tmax)+1.")
    public PoolDoubleArray dday_intcp= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Slope in temperature / degree-day relationship. Coefficient in relationship: dd-coef =  dday_intcp + dday_slope*(tmax)+1.")
     Unit("dday/degree")
     Bound ("nmonths")
     */
    @VariableMeta(name = "dday_slope",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Slope in temperature / degree-day relationship. Coefficient in relationship: dd-coef =  dday_intcp + dday_slope*(tmax)+1.")
    public PoolDoubleArray dday_slope= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Index of the radiation plane used to compute solar radiation for a given HRU")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_radpl",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Index of the radiation plane used to compute solar radiation for a given HRU")
    public PoolIntegerArray hru_radpl= new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("If basin precipitation exceeds this value, solar radiation is mutiplied by the summer or winter precip adjustment factor, depending on the season. ")
     Unit("inches")
     Bound ("nmonths")
     */
    @VariableMeta(name = "ppt_rad_adj",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "If basin precipitation exceeds this value, solar radiation is mutiplied by the summer or winter precip adjustment factor, depending on the season. ")
    public PoolDoubleArray ppt_rad_adj= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Intercept in the temperature-range adjustment equation for solar radiation. Intercept in equation:  adj = radadj_intcp + radadj_slope*(tmax-tmax_index)")
     Unit("dday")
     */
    @VariableMeta(name = "radadj_intcp",
            dataType = DatatypeEnum.PoolDouble,
            description = "Intercept in the temperature-range adjustment equation for solar radiation. Intercept in equation:  adj = radadj_intcp + radadj_slope*(tmax-tmax_index)")
    public PoolDouble radadj_intcp= new PoolDouble();

    /* Role(PARAMETER)
     Description("Slope in the temperature-range adjustment equation for solar radiation. Slope in equation: adj = radadj_intcp + radadj_slope *  (tmax - tmax_index)")
     Unit("dday/degree")
     */
    @VariableMeta(name = "radadj_slope",
            dataType = DatatypeEnum.PoolDouble,
            description = "Slope in the temperature-range adjustment equation for solar radiation. Slope in equation: adj = radadj_intcp + radadj_slope *  (tmax - tmax_index)")
    public PoolDouble radadj_slope= new PoolDouble();

    /* Role(PARAMETER)
     Description("Adjustment factor for computed solar radiation for summer day with greater than ppt_rad_adj inches precip")
     Unit("decimal fraction")
     */
    @VariableMeta(name = "radj_sppt",
            dataType = DatatypeEnum.PoolDouble,
            description = "Adjustment factor for computed solar radiation for summer day with greater than ppt_rad_adj inches precip")
    public PoolDouble radj_sppt= new PoolDouble();

    /* Role(PARAMETER)
     Description("Adjustment factor for computed solar radiation for winter day with greater than ppt_rad_adj inches precip")
     Unit("decimal fraction")
     */
    @VariableMeta(name = "radj_wppt",
            dataType = DatatypeEnum.PoolDouble,
            description = "Adjustment factor for computed solar radiation for winter day with greater than ppt_rad_adj inches precip")
    public PoolDouble radj_wppt= new PoolDouble();

    /* Role(PARAMETER)
     Description("Index of solar radiation station associated with each HRU")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_solsta",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Index of solar radiation station associated with each HRU")
    public PoolIntegerArray hru_solsta= new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("The maximum portion of the potential solar radiation that may reach the ground due to haze, dust, smog, etc.")
     Unit("decimal fraction")
     */
    @VariableMeta(name = "radmax",
            dataType = DatatypeEnum.PoolDouble,
            description = "The maximum portion of the potential solar radiation that may reach the ground due to haze, dust, smog, etc.")
    public PoolDouble radmax= new PoolDouble();

    /* Role(PARAMETER)
     Description("If maximum temperature of an HRU is greater than or equal to this value (for each month, January to December),  precipitation is assumed to be rain")
     Unit("degrees")
     Bound ("nmonths")
     */
    @VariableMeta(name = "tmax_allrain",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "If maximum temperature of an HRU is greater than or equal to this value (for each month, January to December),  precipitation is assumed to be rain")
    public PoolDoubleArray tmax_allrain= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Index temperature, by month, used to determine precipitation adjustments to solar radiation, in deg F or C depending  on units of data")
     Unit("degrees")
     Bound ("nmonths")
     */
    @VariableMeta(name = "tmax_index",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Index temperature, by month, used to determine precipitation adjustments to solar radiation, in deg F or C depending  on units of data")
    public PoolDoubleArray tmax_index= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Index of the solar radiation station used to compute basin radiation values")
     */
    @VariableMeta(name = "basin_solsta",
            dataType = DatatypeEnum.PoolInteger,
            description = "Index of the solar radiation station used to compute basin radiation values")
    public PoolInteger basin_solsta= new PoolInteger();

    /* Role(PARAMETER)
     Description("Conversion factor to convert measured radiation to langleys")
     */
    @VariableMeta(name = "rad_conv",
            dataType = DatatypeEnum.PoolDouble,
            description = "Conversion factor to convert measured radiation to langleys")
    public PoolDouble rad_conv= new PoolDouble();

    /* Role(PARAMETER)
     Description("Area of each HRU")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_area",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Area of each HRU")
    public PoolDoubleArray hru_area= new PoolDoubleArray();

    // Input vars 
    /* Description("Cosine of the radiation plane slope [soltab]")
     Bound("nradpl")
     */
    @VariableMeta(name = "radpl_cossl",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Cosine of the radiation plane slope [soltab]")
    public PoolDoubleArray radpl_cossl= new PoolDoubleArray();

    /* Description("Potential shortwave radiation for each radiation plane for each timestep [soltab]")
     Unit("langleys")
     Bound("366,nradpl")
     */
    @VariableMeta(name = "radpl_soltab",
            dataType = DatatypeEnum.PoolDouble2DArray,
            description = "Potential shortwave radiation for each radiation plane for each timestep [soltab]")
    public PoolDouble2DArray radpl_soltab= new PoolDouble2DArray();//二维数组

    /* Description("Area-weighted measured average precipitation for basin. [precip]")
     Unit("inches")
     */
    @VariableMeta(name = "basin_obs_ppt",
            dataType = DatatypeEnum.PoolDouble,
            description = "Area-weighted measured average precipitation for basin. [precip]")
    public PoolDouble basin_obs_ppt= new PoolDouble();

    /* Description("Observed solar radiation [obs]")
     Unit("langleys")
     Bound("nsol")
     */
    //    public PoolDoubleArray solrad;

    /* Description("Basin daily maximum temperature adjusted to elevation of solar radiation station")
     Unit("degrees F")
     */
    @VariableMeta(name = "solrad_tmax",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin daily maximum temperature adjusted to elevation of solar radiation station")
    public PoolDouble solrad_tmax= new PoolDouble();

    /* Description("Switch signifying the start of a new day (0=no; 1=yes)") */
    @VariableMeta(name = "newday",
            dataType = DatatypeEnum.PoolInteger,
            description = "Switch signifying the start of a new day (0=no; 1=yes)")
    public PoolInteger newday= new PoolInteger();

    /* Description("Number of active HRUs")*/
    @VariableMeta(name = "active_hrus",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of active HRUs")
    public PoolInteger active_hrus= new PoolInteger();

    /* Description("Routing order for HRUs")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_route_order",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Routing order for HRUs")
    public PoolIntegerArray hru_route_order = new PoolIntegerArray();

    /* Description("Inverse of total basin area, expressed as the sum of HRU areas")
     Unit("1/acres")
     */
    @VariableMeta(name = "basin_area_inv",
            dataType = DatatypeEnum.PoolDouble,
            description = "Inverse of total basin area, expressed as the sum of HRU areas")
    public PoolDouble basin_area_inv = new PoolDouble();

    /* Description("Flag to indicate in which hemisphere the basin resides  (0=Northern; 1=Southern)") */
    @VariableMeta(name = "hemisphere",
            dataType = DatatypeEnum.PoolInteger,
            description = "Flag to indicate in which hemisphere the basin resides  (0=Northern; 1=Southern)")
    public PoolInteger hemisphere = new PoolInteger();

    /* Description("Date of the current time step")
     Unit("yyyy mm dd hh mm ss")
     */
    /**
     * *********日期格式******************************
     */
    /*@In public Calendar date;*/
    @VariableMeta(name = "date_mo",
            dataType = DatatypeEnum.PoolInteger,
            description = "public Calendar date")  
    public PoolInteger date_mo= new PoolInteger();
    
    
    @VariableMeta(name = "date_jday",
            dataType = DatatypeEnum.PoolInteger,
            description = "")
    public PoolInteger date_jday= new PoolInteger();

    // Output vars 
    /* Description("Computed shortwave radiation for each HRU")
     Unit("langleys")
     Bound("nhru")
     */
    @VariableMeta(name = "swrad",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Computed shortwave radiation for each HRU")
    public PoolDoubleArray swrad= new PoolDoubleArray();

    /* Description("Area-weighted average of potential shortwave radiation for the basin")
     Unit("langleys")
     */
    @VariableMeta(name = "basin_potsw",
            dataType = DatatypeEnum.PoolDouble,
            description = "Area-weighted average of potential shortwave radiation for the basin")
    public PoolDouble basin_potsw= new PoolDouble();

    /* Description("Measured or computed solar radiation on a horizontal surface")
     Unit("langleys")
     */
    @VariableMeta(name = "orad",
            dataType = DatatypeEnum.PoolDouble,
            description = "Measured or computed solar radiation on a horizontal surface")
    public PoolDouble orad= new PoolDouble();

    /* Description("Potential shortwave radiation for the basin centroid")
     Unit("langleys")
     */
    @VariableMeta(name = "basin_horad",
            dataType = DatatypeEnum.PoolDouble,
            description = "Potential shortwave radiation for the basin centroid")
    public PoolDouble basin_horad= new PoolDouble();

    // private define
    /**
     * ********************************************************
     */
    //xml parameter 
    private int nradpl_pri;
    private int nsol_pri;
    private double[] dday_intcp_pri;
    private double[] dday_slope_pri;
    private int[] hru_radpl_pri;
    private double[] ppt_rad_adj_pri;
    private double radadj_intcp_pri;
    private double radadj_slope_pri;
    private double radj_sppt_pri;
    private double radj_wppt_pri;
    private int[] hru_solsta_pri;
    private double radmax_pri;
    private double[] tmax_allrain_pri;
    private double[] tmax_index_pri;
    private int basin_solsta_pri;
    private double rad_conv_pri;

    //input
    private int nhru_pri;
    private double[] hru_area_pri;
    private double[] radpl_cossl_pri;
    private double[][] radpl_soltab_pri;
    private double basin_obs_ppt_pri;
    private double[] solrad_pri;//obs 得到一维数组，站点的
    private double solrad_tmax_pri;
    private int newday_pri;
    private int active_hrus_pri;
    private int[] hru_route_order_pri;
    private double basin_area_inv_pri;
    private int hemisphere_pri;

    //time series IO
    private int mo;
    private int jday;
    private int ijday;
    //out

    private double[] swrad_pri;
    private double basin_potsw_pri;
    private double orad_pri;
    private double basin_horad_pri;
    //jj
    private int jj;

    public void init() throws Exception {
        nhru_pri = this.nhru.getValue();
        nradpl_pri = this.nradpl.getValue();
        dday_intcp_pri = this.dday_intcp.getValue();
        dday_slope_pri = this.dday_slope.getValue();
        hru_radpl_pri = this.hru_radpl.getValue();
        ppt_rad_adj_pri = this.ppt_rad_adj.getValue();
        radadj_intcp_pri = this.radadj_intcp.getValue();
        radadj_slope_pri = this.radadj_slope.getValue();
        radj_sppt_pri = this.radj_sppt.getValue();
        radj_wppt_pri = this.radj_wppt.getValue();
        hru_solsta_pri = this.hru_solsta.getValue();
        radmax_pri = this.radmax.getValue();
        tmax_allrain_pri = this.tmax_allrain.getValue();
        tmax_index_pri = this.tmax_index.getValue();
        basin_solsta_pri = this.basin_solsta.getValue();
        rad_conv_pri = this.rad_conv.getValue();
        radpl_soltab_pri = this.radpl_soltab.getValue();
        active_hrus_pri = this.active_hrus.getValue();
        hru_route_order_pri = this.hru_route_order.getValue();
        basin_area_inv_pri = this.basin_area_inv.getValue();
        hemisphere_pri = this.hemisphere.getValue();
        hru_area_pri = this.hru_area.getValue();
        nsol_pri = this.nsol.getValue();
        radpl_cossl_pri = this.radpl_cossl.getValue();

        swrad_pri = new double[nhru_pri];
        plrad = new double[nradpl_pri];

        jj = -1;
//        ijday=0;
    }

    @Override
    public void run() throws Exception {

        jj++;
//     ijday++;
        int ii;

        if (jj == 0) {
            //input*input
            //time loop input
            basin_obs_ppt_pri = this.basin_obs_ppt.getValue();
            solrad_tmax_pri = this.solrad_tmax.getValue();
            newday_pri = this.newday.getValue();//从obs中得到看是否变动
            /**
             * ************处理时间格式*****************
             */
            /* int mo = date.get(java.util.Calendar.MONTH); //读模块
             int jday = date.get(Calendar.DAY_OF_YEAR);  //读模块
             */
            mo = this.date_mo.getValue();
            jday = this.date_jday.getValue();
//        computerJday(ijday);
            this.date_jday.setValue(jday);
            //       solrad_pri=this.solrad.getValue();//站点一维数组

            if (swrad == null) {
                init();
            }

            if (newday_pri == 0) {
                return;
            }

            double pptadj;
            double radadj = 0.0;
            basin_horad_pri = radpl_soltab_pri[jday - 1][0];
            orad_pri = -999.0;

            if (nsol_pri > 0) {
                if (basin_solsta_pri > 0) {
                    orad_pri = solrad_pri[basin_solsta_pri - 1] * rad_conv_pri;
                }
            }

            if (orad_pri < NEARZERO || orad_pri > 10000.) {
                double dday = (dday_slope_pri[mo] * solrad_tmax_pri) + dday_intcp_pri[mo] + 1.0;

                if (dday < 1.0) {
                    dday = 1.0;
                }
                if (basin_obs_ppt_pri <= ppt_rad_adj_pri[mo]) {
                    pptadj = 1.0;
                } else if (solrad_tmax_pri >= tmax_index_pri[mo]) {
                    double tdif = solrad_tmax_pri - tmax_index_pri[mo];
                    pptadj = radadj_intcp_pri + radadj_slope_pri * tdif;
                    if (pptadj > 1.) {
                        pptadj = 1.;
                    }
                } else {
                    pptadj = radj_wppt_pri;
                    if (solrad_tmax_pri >= tmax_allrain_pri[mo]) {
                        if (hemisphere_pri == 0) { // northern hemisphere
                            if (jday < 79 || jday > 265) { // equinox
                                pptadj = radj_wppt_pri;
                            } else {
                                pptadj = radj_sppt_pri;
                            }
                        } else {  // southern hemisphere
                            if (jday > 79 || jday < 265) {  // equinox
                                pptadj = radj_wppt_pri;
                            } else {
                                pptadj = radj_sppt_pri;
                            }
                        }
                    }
                }

                if (dday < 26.) {
                    int kp = (int) dday;
                    double ddayi = kp;
                    int kp1 = kp + 1;
                    radadj = SOLF[kp - 1] + ((SOLF[kp1 - 1] - SOLF[kp - 1]) * (dday - ddayi));
                } else {
                    radadj = radmax_pri;
                }
                // System.out.println(jday + " radadj " + radadj + " pptadj " + pptadj +
//                        " soltmx " + solrad_tmax + " obsppt " + basin_obs_ppt);
                radadj = radadj * pptadj;
                if (radadj < .2) {
                    radadj = .2;
                }
                orad_pri = radadj * basin_horad_pri;
            }
            // System.out.println("lday  " + lday + " orad  " + orad + " radadj " + radadj
            //           + " horad " + basin_horad);

            for (int j = 0; j < nradpl_pri; j++) {
                plrad[j] = (radpl_soltab_pri[jday - 1][j] / basin_horad_pri) * (orad_pri / radpl_cossl_pri[j]);
                // System.out.println("lday" + lday +  "rsoltab " + radpl_soltab.getValue(jday-1,j) + " plrad " +
                //           plrad[j] + " rcosl " + radpl_cossl[j]);
            }
            // System.out.println("hrus " + active_hrus);

            basin_potsw_pri = 0.0;
            this.orad.setValue(orad_pri);
            this.basin_horad.setValue(basin_horad_pri);

        }

        /**
         * **********************loop****************************************
         */
     //   if (jj < this.active_hrus.getValue()) {
            if (nsol_pri == 0) {
                  for (int jj = 0; jj < this.active_hrus.getValue(); jj++) {
                ii = hru_route_order_pri[jj];
                int ir = hru_radpl_pri[jj] - 1;
                swrad_pri[ii] = plrad[ir];
                basin_potsw_pri += swrad_pri[ii] * hru_area_pri[ii];
                   }
            } else {
                 for (int jj = 0; jj < this.active_hrus.getValue(); jj++) {
                ii = hru_route_order_pri[jj];
                int k = hru_solsta_pri[ii] - 1;
                if (k == 0 || k > nsol_pri - 1) {
                    int ir = hru_radpl_pri[ii] - 1;
                    swrad_pri[ii] = plrad[ir];
                } else {
                    swrad_pri[ii] = solrad_pri[k] * rad_conv_pri;
                    this.swrad.setValue(ii, swrad_pri[ii]);
                }
                basin_potsw_pri += swrad_pri[ii] * hru_area_pri[ii];
                    }

            }

            

        

        /**
         * **************************************************************
         */
     //   if (jj == this.active_hrus.getValue()) {

            basin_potsw_pri *= basin_area_inv_pri;
            if (log.isLoggable(Level.INFO)) {
          //      log.info("Solrad " + basin_potsw_pri);
            }

            this.basin_potsw.setValue(basin_potsw_pri);

      //      jj = -1;
     //   }
    }

    @Override
    public void clear() throws Exception {

    }
    //计算jday
// private void computerJday(int i){
//     
//    if (i<365){
//        i++;
//        jday= i;
//     }else{
//      
//     i=1;
//    
//    }
//      
//   
// }

}
