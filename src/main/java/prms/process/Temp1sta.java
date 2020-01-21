package prms.process;

import java.util.Calendar;
import java.util.logging.*;
import net.casnw.home.meta.DatatypeEnum;
import net.casnw.home.meta.ModuleMeta;
import net.casnw.home.meta.VariableMeta;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolCalendar;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolIntegerArray;

/* Description
 ("Temperature distribution." +
 "Distributes temperatures to HRU's using temperature data measured at a station" +
 "and a monthly parameter based on the lapse rate with elevation.")
 * /

 /* Keywords
 ("Temperature")
 */
@ModuleMeta(moduleClass = "prms.process.Temp1sta",
        name = "Temp1sta",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "Temperature",
        description = "Temperature distribution. "
        + "Distributes temperatures to HRU's using temperature data measured at a station"
        + "and a monthly parameter based on the lapse rate with elevation. ")
public class Temp1sta extends AbsComponent {

    private static final Logger log = Logger.getLogger("HOME.model." + Temp1sta.class.getSimpleName());
    // private fields
    private double tcrn[];
    private double tcrx[];
    private double tcr[];
    private double elfac[];
    private double[] obs_temp;   // ???????
    // Input param
    /* Role(PARAMETER)
     Description("Number of HRUs.")
     */
    @VariableMeta(name = "nhru",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of HRUs.")
    public PoolInteger nhru= new PoolInteger();

    /* Role(PARAMETER)
     Description("Number of temperature stations.")
     */
    @VariableMeta(name = "ntemp",
            dataType = DatatypeEnum.PoolInteger,
            description = "the number of temperature observation")
    public PoolInteger ntemp= new PoolInteger();

    /* Role(PARAMETER)
     Description("Index of main temperature station Index of temperature station used to compute basin  temperature values")
     */
    @VariableMeta(name = "basin_tsta",
            dataType = DatatypeEnum.PoolInteger,
            description = "Index of main temperature station Index of temperature station used to compute basin  temperature values")
    public PoolInteger basin_tsta= new PoolInteger();

    /* Role(PARAMETER)
     Description("Area of each HRU")
     Unit("acres")
     Bound ("nhru_pri")
     */
    @VariableMeta(name = "hru_area",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Area of each HRU")
    public PoolDoubleArray hru_area= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Mean elevation for each HRU")
     Unit("elev_units")
     Bound ("nhru_pri")
     */
    @VariableMeta(name = "hru_elev",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "  Mean elevation for each HRU  ")
    public PoolDoubleArray hru_elev= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Index of the base temperature station used for lapse  rate calculations")
     Bound ("nhru_pri")
     */
    @VariableMeta(name = "hru_tsta",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Index of the base temperature station used for lapse  rate calculations")
    public PoolIntegerArray hru_tsta= new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("Units for measured temperature (0=Fahrenheit; 1=Celsius)")
     */
    @VariableMeta(name = "temp_units",
            dataType = DatatypeEnum.PoolInteger,
            description = "Units for measured temperature (0=Fahrenheit; 1=Celsius)")
    public PoolInteger temp_units= new PoolInteger();

    /*Role(PARAMETER)
     Description("Adjustment to maximum temperature for each HRU, estimated  based on slope and aspect")
     Unit("degrees")
     Bound ("nhru_pri")
     */
    @VariableMeta(name = "tmax_adj",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Adjustment to maximum temperature for each HRU, estimated  based on slope and aspect")

    public PoolDoubleArray tmax_adj= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Adjustment to minimum temperature for each HRU, estimated  based on slope and aspect")
     Unit("degrees")
     Bound ("nhru_pri")
     */
    @VariableMeta(name = "tmin_adj",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Adjustment to minimum temperature for each HRU, estimated  based on slope and aspect")

    public PoolDoubleArray tmin_adj= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Array of twelve values representing the change in maximum temperature per 1000 elev_units of elevation change for each month, January to December")
     Unit("degrees")
     Bound ("nmonths")
     */
    @VariableMeta(name = "tmax_lapse",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "rray of twelve values representing the change in maximum temperature per 1000 elev_units of elevation change for each month, January to December")

    public PoolDoubleArray tmax_lapse= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Array of twelve values representing the change in minimum temperture per 1000 elev_units of  elevation change for each month, January to December")
     Unit("degrees")
     Bound ("nmonths")
     */
    @VariableMeta(name = "tmin_lapse",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Array of twelve values representing the change in minimum temperture per 1000 elev_units of  elevation change for each month, January to December")

    public PoolDoubleArray tmin_lapse= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Elevation of each temperature measurement station")
     Unit("elev_units")
     Bound ("ntemp_pri")
     */
    @VariableMeta(name = "tsta_elev",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Elevation of each temperature measurement station")
    public PoolDoubleArray tsta_elev= new PoolDoubleArray();
    // Input var

    /* Description("Routing order for HRUs")
     Bound ("nhru_pri")
     */
    @VariableMeta(name = "hru_route_order",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Routing order for HRUs")
    public PoolIntegerArray hru_route_order= new PoolIntegerArray();

    /* Description("Inverse of total basin area as sum of HRU areas")
     Unit("1/acres")
     */
    @VariableMeta(name = "basin_area_inv",
            dataType = DatatypeEnum.PoolDouble,
            description = "Inverse of total basin area as sum of HRU areas")
    public PoolDouble basin_area_inv= new PoolDouble();

    /* Description("Number of active HRUs")*/
    @VariableMeta(name = "active_hrus",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of active HRUs")
    public PoolInteger active_hrus= new PoolInteger();

    /* Description("Measured maximum temperature at each temperature measurement station, F or C depending on units of data. [obs]")
     Bound ("nhru_pri")
     */
    @VariableMeta(name = "tmax",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "(Maximum temperature )")
    public PoolDoubleArray tmax= new PoolDoubleArray();
    /* Description("Measured minimum temperature at each temperature measurement station, F or C depending on units of data. [obs]")
     Bound ("nhru_pri")
     */
    @VariableMeta(name = "tmin",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "(Minimum temperature)")
    public PoolDoubleArray tmin= new PoolDoubleArray();

    /*Description("Kinematic routing switch - 0= non storm period, 1=storm period [obs]")*/
    @VariableMeta(name = "route_on",
            dataType = DatatypeEnum.PoolInteger,
            description = "Kinematic routing switch (0=daily; 1=storm period)")
    public PoolInteger route_on= new PoolInteger();
    /**
     * *******************Need
     * rewrite********************************************
     */
    /* Description("Date of the current time step")
     Unit("yyyy mm dd hh mm ss")
     */
    /*    public Calendar date; */
    @VariableMeta(name = "date_mo",
            dataType = DatatypeEnum.PoolInteger,
            description = "Calendar.MONTH)")
    public PoolInteger date_mo= new PoolInteger();

    @VariableMeta(name = "date_day",
            dataType = DatatypeEnum.PoolInteger,
            description = " Calendar.DAY_OF_MONTH)")
    public PoolInteger date_day= new PoolInteger();
    /**
     * *******************Need
     * rewrite********************************************
     */
    // Output var
    /* Description("Basin area-weighted temperature for timestep < 24")
     Unit("degrees")
     */
    @VariableMeta(name = "basin_temp",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area-weighted temperature for timestep < 24")
    public PoolDouble basin_temp= new PoolDouble();

    /* Description("Basin area-weighted daily maximum temperature")
     Unit("degrees")
     */
    @VariableMeta(name = "basin_tmax",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area-weighted daily maximum temperature")
    public PoolDouble basin_tmax= new PoolDouble();

    /* Description("Basin area-weighted daily minimum temperature")
     Unit("degrees")
     */
    @VariableMeta(name = "basin_tmin",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area-weighted daily minimum temperature")
    public PoolDouble basin_tmin= new PoolDouble();

    /* Description("HRU adjusted daily average temperature")
     Unit("degrees Celsius")
     Bound ("nhru_pri")
     */
    @VariableMeta(name = "tavgc",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Average HRU temperature. [temp]")
    public PoolDoubleArray tavgc= new PoolDoubleArray();

    /* Description("HRU adjusted daily average temperature")
     Unit("degrees F")
     Bound ("nhru_pri")
     */
    @VariableMeta(name = "tavgf",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Average HRU temperature. [temp]")
    public PoolDoubleArray tavgf= new PoolDoubleArray();

    /* Description("HRU adjusted daily maximum temperature")
     Unit("degrees Celsius")
     Bound ("nhru_pri")
     */
    @VariableMeta(name = "tmaxc",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum HRU temperature. [temp]")
    public PoolDoubleArray tmaxc= new PoolDoubleArray();

    /* Description("HRU adjusted daily maximum temperature")
     Unit("degrees F")
     Bound ("nhru_pri")
     */
    @VariableMeta(name = "tmaxf",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum HRU temperature. [temp]")
    public PoolDoubleArray tmaxf= new PoolDoubleArray();

    /* Description("HRU adjusted daily minimum temperature")
     Unit("degrees Celsius")
     Bound ("nhru_pri")
     */
    @VariableMeta(name = "tminc",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Minimum HRU temperature. [temp]")
    public PoolDoubleArray tminc= new PoolDoubleArray();

    /* Description("HRU adjusted daily minimum temperature")
     Unit("degrees F")
     Bound ("nhru_pri")
     */
    @VariableMeta(name = "tminf",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Minimum HRU temperature. [temp]")
    public PoolDoubleArray tminf= new PoolDoubleArray();

    /* Description("HRU adjusted temperature for timestep < 24")
     Unit("degrees Celsius")
     Bound ("nhru_pri")
     */
    @VariableMeta(name = "tempc",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "HRU adjusted temperature for timestep < 24")
    public PoolDoubleArray tempc= new PoolDoubleArray();

    /* Description("HRU adjusted temperature for timestep < 24")
     Unit("degrees F")
     Bound ("nhru_pri")
     */
    @VariableMeta(name = "tempf",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "HRU adjusted temperature for timestep < 24")
    public PoolDoubleArray tempf= new PoolDoubleArray();
    /*Description("Basin daily maximum temperature for use with solrad radiation")
     Unit("degrees")
     */
    @VariableMeta(name = "solrad_tmax",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin daily maximum temperature adjusted to elevation of solar radiation station")
    public PoolDouble solrad_tmax= new PoolDouble();
    /* Description("Basin daily minimum temperature for use with solrad radiation")
     Unit("degrees")
     */
    @VariableMeta(name = "starttime",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin daily minimum temperature for use with solrad radiation")
    public PoolDouble solrad_tmin= new PoolDouble();

    @VariableMeta(name = "starttime",
            dataType = DatatypeEnum.PoolCalendar,
            description = "starttime")
    public PoolCalendar starttime= new PoolCalendar();
    /**
     * **********************************Private***********************************************
     */
    //xml parameter IO
    private int ntemp_pri;
    private int basin_tsta_pri;
    private double[] hru_elev_pri;
    private int[] hru_tsta_pri;
    private int temp_units_pri;
    private double[] tmax_adj_pri;
    private double[] tmin_adj_pri;
    private double[] tmax_lapse_pri;
    private double[] tmin_lapse_pri;
    private double[] tsta_elev_pri;
    // Time series IO  在读模块中实现转换 此处在读模块中要实现数组的转换
    private double[] tmax_pri;
    private double[] tmin_pri;
    // Input
    private int nhru_pri;
    private double[] hru_area_pri;
    private int[] hru_route_order_pri;
    private double basin_area_inv_pri;
    private int active_hrus_pri;
    private int route_on_pri;// 在读模块中获取
    //out
    private double basin_temp_pri;
    private double basin_tmax_pri;
    private double basin_tmin_pri;
    private double[] tavgc_pri;
    private double[] tavgf_pri;
    private double[] tmaxc_pri;
    private double[] tmaxf_pri;
    private double[] tminc_pri;
    private double[] tminf_pri;
    private double[] tempc_pri;
    private double[] tempf_pri;
    private double solrad_tmax_pri;
    private double solrad_tmin_pri;
    public int jj;
    private double tmaxlaps;
    private double tminlaps;
    private double ts_temp;
    private int mo;    //时间模块中读日期
    private int day;   //时间模块中读日期

    @Override
    public void init() throws Exception {

        ntemp_pri = this.ntemp.getValue();
        basin_tsta_pri = this.basin_tsta.getValue();
        hru_elev_pri = this.hru_elev.getValue();
        hru_tsta_pri = this.hru_tsta.getValue();
        temp_units_pri = this.temp_units.getValue();
        tmax_adj_pri = this.tmax_adj.getValue();
        tmin_adj_pri = this.tmin_adj.getValue();
        tmax_lapse_pri = this.tmax_lapse.getValue();
        tmin_lapse_pri = this.tmin_lapse.getValue();
        tsta_elev_pri = this.tsta_elev.getValue();

        // Input
        nhru_pri = this.nhru.getValue();
        hru_area_pri = this.hru_area.getValue();
        hru_route_order_pri = this.hru_route_order.getValue();
        basin_area_inv_pri = this.basin_area_inv.getValue();
        active_hrus_pri = this.active_hrus.getValue();
        route_on_pri = this.route_on.getValue();// 在读模块中获取
        jj = -1;
        tmaxlaps = 0;
        tminlaps = 0;
        ts_temp = 0.;
        day = 0;
        obs_temp = new double[ntemp_pri];  //TODO is obs_temp needed?
        tavgc_pri = new double[nhru_pri];
        tavgf_pri = new double[nhru_pri];
        tempc_pri = new double[nhru_pri];
        tempf_pri = new double[nhru_pri];
        tmaxc_pri = new double[nhru_pri];
        tmaxf_pri = new double[nhru_pri];
        tminc_pri = new double[nhru_pri];
        tminf_pri = new double[nhru_pri];
        tcrn = new double[nhru_pri];
        tcrx = new double[nhru_pri];
        tcr = new double[nhru_pri];
        elfac = new double[nhru_pri];
        /**
         * ****** int mo = date.get(Calendar.MONTH);**********************
         */
        int mo_s = starttime.get(Calendar.MONTH);
        tmaxlaps = tmax_lapse_pri[mo_s];
        tminlaps = tmin_lapse_pri[mo_s];
        for (int j = 0;
                j < nhru_pri;
                j++) {
            if (hru_tsta_pri[j] < 1) {
                hru_tsta_pri[j] = 1;
            }
            if (hru_tsta_pri[j] > ntemp_pri) {
                throw new RuntimeException("***error, hru_tsts>ntemp, HRU: " + j);
            }
            int k = hru_tsta_pri[j] - 1;
            elfac[j] = (hru_elev_pri[j] - tsta_elev_pri[k]) / 1000.;
            tcrx[j] = tmaxlaps * elfac[j] - tmax_adj_pri[j];
            tcrn[j] = tminlaps * elfac[j] - tmin_adj_pri[j];
            tcr[j] = (tcrx[j] + tcrn[j]) * 0.5;
        }
    }

    @Override
    public void run() throws Exception {

    	 for (int jj = 0; jj < active_hrus_pri; jj++) {

        if (jj == 0) {

            if (elfac == null) {
                init();
            }
            // Time series IO  在读模块中实现转换 此处在读模块中要实现数组的转换
            tmax_pri = this.tmax.getValue();
            tmin_pri = this.tmin.getValue();
            ts_temp = 0.;
            mo = this.date_mo.getValue();//时间模块中读日期
            day = this.date_day.getValue();//时间模块中读日期
 /*       int mo = date.get(Calendar.MONTH);*/

            /*       int day = date.get(Calendar.DAY_OF_MONTH);*/
            basin_tmax_pri = 0.0;
            basin_tmin_pri = 0.0;
            basin_temp_pri = 0.0;

            tmaxlaps = tmax_lapse_pri[mo];
            tminlaps = tmin_lapse_pri[mo];
        }
        /*   *****************      loop      ************************/
        int j = hru_route_order_pri[jj];
//        jj++;
        int k = hru_tsta_pri[j] - 1;

        if (day == 1) {
            tcrx[j] = tmaxlaps * elfac[j] - tmax_adj_pri[j];
            tcrn[j] = tminlaps * elfac[j] - tmin_adj_pri[j];
            if (route_on_pri == 1) {
                tcr[j] = (tcrx[j] + tcrn[j]) * 0.5;
            }
        }

        double tmn = tmin_pri[k] - tcrn[j];
        double tmx = tmax_pri[k] - tcrx[j];

        if (route_on_pri == 1) {
            ts_temp = obs_temp[k] - tcr[j];     /// obs_temp ????未赋初值是否出错？
            basin_temp_pri = basin_temp_pri + ts_temp * hru_area_pri[j];
        }
        if (temp_units_pri == 0) {
            // Degrees F
            tmaxf_pri[j] = tmx;
            tminf_pri[j] = tmn;
            tavgf_pri[j] = (tmx + tmn) * 0.5;
            tmaxc_pri[j] = f_to_c(tmx);
            tminc_pri[j] = f_to_c(tmn);
            tavgc_pri[j] = f_to_c(tavgf_pri[j]);
            if (route_on_pri == 1) {
                tempf_pri[j] = ts_temp;
                tempc_pri[j] = f_to_c(ts_temp);
            }
        } else {
            // Degrees C
            tmaxc_pri[j] = tmx;
            tminc_pri[j] = tmn;
            tavgc_pri[j] = (tmx + tmn) * 0.5;
            tmaxf_pri[j] = c_to_f(tmx);
            tminf_pri[j] = c_to_f(tmn);
            tavgf_pri[j] = c_to_f(tavgc_pri[j]);
            if (route_on_pri == 1) {
                tempc_pri[j] = ts_temp;
                tempf_pri[j] = c_to_f(ts_temp);
            }
        }
        basin_tmax_pri += tmx * hru_area_pri[j];
        basin_tmin_pri += tmn * hru_area_pri[j];
        solrad_tmax_pri = tmax_pri[basin_tsta_pri - 1];
        solrad_tmin_pri = tmin_pri[basin_tsta_pri - 1];
        this.solrad_tmax.setValue(solrad_tmax_pri);
        this.solrad_tmin.setValue(solrad_tmin_pri);

        /**
         * *******************************************************
         */
        if (jj + 1 == this.active_hrus.getValue()) {
            basin_tmax_pri *= basin_area_inv_pri;
            basin_tmin_pri *= basin_area_inv_pri;
            if (route_on_pri == 1) {
                basin_temp_pri *= basin_area_inv_pri;
            }
//            solrad_tmax_pri = tmax_pri[basin_tsta_pri - 1];
//            solrad_tmin_pri = tmin_pri[basin_tsta_pri - 1];

            if (log.isLoggable(Level.INFO)) {
            //    log.info("Temp " + basin_tmax_pri + " " + basin_tmin_pri);
            }

            this.basin_tmax.setValue(basin_tmax_pri);
            this.basin_tmin.setValue(basin_tmin_pri);
            this.basin_temp.setValue(basin_temp_pri);
//            this.solrad_tmax.setValue(solrad_tmax_pri);//可能存在的问题未赋初值
//            this.solrad_tmin.setValue(solrad_tmin_pri);//可能存在的问题未赋初值

        }

        this.tavgc.setValue(j, tavgc_pri[j]);
        this.tavgf.setValue(j, tavgf_pri[j]);
        this.tempc.setValue(j, tempc_pri[j]);
        this.tempf.setValue(j, tempf_pri[j]);
        this.tmaxc.setValue(j, tmaxc_pri[j]);
        this.tmaxf.setValue(j, tmaxf_pri[j]);
        this.tminc.setValue(j, tminc_pri[j]);
        this.tminf.setValue(j, tminf_pri[j]);

       
    	 }

    }

    @Override
    public void clear() throws Exception {
    }
    static final double FIVENITH = 5.0 / 9.0;

    //***********************************************************************
    // convert fahrenheit to celsius
    //***********************************************************************
    static final double f_to_c(double temp) {
        return (temp - 32.0) * FIVENITH;
    }

    //***********************************************************************
    // convert celsius to fahrenheit
    //***********************************************************************
    static final double c_to_f(double temp) {
        return temp * FIVENITH + 32.0;
    }
}
