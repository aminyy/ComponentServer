package prms.process;

import java.util.Calendar;
import java.util.logging.*;
import net.casnw.home.meta.DatatypeEnum;
import net.casnw.home.meta.ModuleMeta;
import net.casnw.home.meta.TypeEnum;
import net.casnw.home.meta.VariableMeta;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolCalendar;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolIntegerArray;

@ModuleMeta(moduleClass = "prms.process.PotetJh",
        name = "PotetJh",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "Evapotranspiration",
        description = "Potential ET - Jensen Haise."
        + "Determines whether current time period is one of active"
        + "transpiration and computes the potential evapotranspiration"
        + "for each HRU using the Jensen-Haise formulation.")

public class PotetJh extends AbsComponent {

    private static final Logger log = Logger.getLogger("HOME.model." + PotetJh.class.getSimpleName());
    // Input params

    /* Role(PARAMETER)
     @Description("Number of HRUs.")
     */
    @VariableMeta(name = "nhru",
            dataType = DatatypeEnum.PoolInteger,
            type = TypeEnum.In,
            description = "Number of HRUs.")
    public PoolInteger nhru= new PoolInteger();

    /* Role(PARAMETER)
     Description("Number of solar radiation stations.")
     */
    @VariableMeta(name = "nsol",
            dataType = DatatypeEnum.PoolInteger,
            type = TypeEnum.In,
            description = "Number of solar radiation stations.")
    public PoolInteger nsol= new PoolInteger();

    /* Role(PARAMETER)
     Description("HRU area ,  Area of each HRU")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_area",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Area of each HRU")
    public PoolDoubleArray hru_area= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Monthly air temperature coefficient used in Jensen -Haise potential evapotranspiration " +
     "computations, see PRMS manual for calculation method")
     Unit("per degrees")
     Bound ("nmonths")
     */
    @VariableMeta(name = "jh_coef",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Monthly air temperature coefficient used in Jensen -Haise potential evapotranspiration "
            + "computations, see PRMS manual for calculation method")
    public PoolDoubleArray jh_coef= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Jensen-Haise Air temperature coefficient used in Jensen-Haise potential  evapotranspiration " +
     "computations for each HRU.  See PRMS  manual for calculation method")
     Unit("per degrees")
     Bound ("nhru")
     */
    @VariableMeta(name = "jh_coef_hru",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = " Jensen-Haise Air temperature coefficient used in Jensen-Haise potential  evapotranspiration \" +\n"
            + "     \"computations for each HRU.  See PRMS  manual for calculation method")
    public PoolDoubleArray jh_coef_hru= new PoolDoubleArray();
    // Input vars
    /* Description("The computed solar radiation for each HRU. [solrad]")
     Unit("calories/cm2")
     Bound ("nhru")
     */
    @VariableMeta(name = "swrad",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "The computed solar radiation for each HRU [solrad]")
    public PoolDoubleArray swrad= new PoolDoubleArray();

    /* Description("Average HRU temperature. [temp]")
     Unit("deg C")
     Bound ("nhru")
     */
    @VariableMeta(name = "tavgc",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Average HRU temperature. [temp]")
    public PoolDoubleArray tavgc= new PoolDoubleArray();

    /* Description("Average HRU temperature. [temp]")
     Unit("deg F")
     Bound ("nhru")
     */
    @VariableMeta(name = "tavgf",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Average HRU temperature. [temp]")
    public PoolDoubleArray tavgf= new PoolDoubleArray();

    /* Description("Switch signifying the start of a new day (0=no; 1=yes)")*/
    @VariableMeta(name = "newday",
            dataType = DatatypeEnum.PoolInteger,
            description = " Switch signifying the start of a new day (0=no; 1=yes))")
    public PoolInteger newday= new PoolInteger();

    /*Description("Number of active HRUs")*/
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
    public PoolIntegerArray hru_route_order= new PoolIntegerArray();

    /* Description("Inverse of total basin area as sum of HRU areas")
     Unit("1/acres")
     */
    @VariableMeta(name = "basin_area_inv",
            dataType = DatatypeEnum.PoolDouble,
            description = "Inverse of total basin area as sum of HRU areas")
    public PoolDouble basin_area_inv= new PoolDouble();

    /* Description("Date of the current time step")
     Unit("yyyy mm dd hh mm ss")
     */
    /**
     * *******************************************************
     */
    /*时间定义   public Calendar date;*/
    @VariableMeta(name = "date_mo",
            dataType = DatatypeEnum.PoolInteger,
            description = "Calendar.MONTH)")
    public PoolInteger date_mo= new PoolInteger();
    @VariableMeta(name = "date_day",
            dataType = DatatypeEnum.PoolInteger,
            description = " Calendar.DAY_OF_MONTH)")
    public PoolInteger date_day= new PoolInteger();
    @VariableMeta(name = "starttime",
            dataType = DatatypeEnum.PoolCalendar,
            description = "starttime")
    public PoolCalendar starttime= new PoolCalendar();
    /* Description("Length of the time step")
     Unit("hours")
     */
    @VariableMeta(name = "deltim",
            dataType = DatatypeEnum.PoolDouble,
            description = "Length of the time step)")
    public PoolDouble deltim= new PoolDouble();

    /*Description("Kinematic routing switch (0=daily; 1=storm period)")*/
    @VariableMeta(name = "route_on",
            dataType = DatatypeEnum.PoolInteger,
            description = "Kinematic routing switch (0=daily; 1=storm period)")
    public PoolInteger route_on= new PoolInteger();
    // Output vars

    /* Description("Potential evapotranspiration on an HRU")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "potet",
            dataType = DatatypeEnum.PoolDoubleArray,
            type = TypeEnum.InOut,
            description = "Potential evapotranspiration for each HRU. [potet]")
    public PoolDoubleArray potet= new PoolDoubleArray();

    /* Description("Basin area-weighted average of potential et")
     Unit("inches")
     */
    @VariableMeta(name = "basin_potet",
            dataType = DatatypeEnum.PoolDouble,
            type = TypeEnum.InOut,
            description = "Basin area-weighted average of potential et")
    public PoolDouble basin_potet= new PoolDouble();

    /* Description("Basin area-weighted average of potential et")
     Unit("inches")
     */
    @VariableMeta(name = "basin_potet_jh",
            dataType = DatatypeEnum.PoolDouble,
            type = TypeEnum.InOut,
            description = "Basin area-weighted average of potential et")
    public PoolDouble basin_potet_jh = new PoolDouble();

    //out
    private double[] potet_pri;
    // input
    private int nhru_pri;
    public int j;
    //private
    private double[] jh_coef_pri;
    private int nsol_pri;
    private double[] jh_coef_hru_pri;
    private double[] hru_area_pri;
    private int active_hrus_pri;
    private int[] hru_route_order_pri;
    private double basin_area_inv_pri;
    private int newday_pri;
    private double deltim_pri;
    private int route_on_pri;
    private int date_day_pri;
    private int mo;
    private int day;
    private double factor;
    private int date_mo_pri;
    //out *+ potet
    private double basin_potet_pri;
    private double basin_potet_jh_pri;

    @Override
    public void init() throws Exception {
        nhru_pri = this.nhru.getValue();
        potet_pri = new double[nhru_pri];
        j = -1;
        /**
         * *时间格式处理*******************
         */
        /* int mo = date.get(Calendar.MONTH) + 1;
         int day = date.get(Calendar.DAY_OF_MONTH);
         */
        int mo_ss = this.starttime.get(Calendar.MONTH) + 1;
        int day_ss = this.starttime.get(Calendar.DAY_OF_MONTH);

        //private define
        //xml parameter IO
        nsol_pri = this.nsol.getValue();
        jh_coef_pri = this.jh_coef.getValue();
        jh_coef_hru_pri = this.jh_coef_hru.getValue();
        //input
        hru_area_pri = this.hru_area.getValue();
        active_hrus_pri = this.active_hrus.getValue();
        hru_route_order_pri = this.hru_route_order.getValue();
        basin_area_inv_pri = this.basin_area_inv.getValue();

    }

    @Override
    public void run() throws Exception {
    	 for (int j = 0; j < active_hrus_pri; j++) {
        int ii = hru_route_order_pri[j];
        //input
        double swrad_pri = 1;// this.swrad.getValue(ii);
        double tavgc_pri = this.tavgc.getValue(ii);
        double tavgf_pri = this.tavgf.getValue(ii);

        if (j == 0) {

            //input
            newday_pri = this.newday.getValue();//从读模块中获取
            deltim_pri = this.deltim.getValue();//从读模块中获取
            route_on_pri = this.route_on.getValue();//从读模块中获取

            // time series IO
            date_day_pri = this.date_day.getValue();
            date_mo_pri = this.date_mo.getValue();

            if (potet == null) {
                init();
            }

            double dt = deltim_pri;
            /*      int mo = date.get(Calendar.MONTH);*/
            mo = date_mo_pri;

            basin_potet_jh_pri = 0.0;
            basin_potet_pri = 0.0;

            factor = 1.;
            if (route_on_pri == 1 && nsol_pri == 0) {
                factor = dt / 24.;
            }
        }
        /**
         * ****************loop**************************************
         */
        //        
        if (j < active_hrus_pri) {
            int i = hru_route_order_pri[j];
            double elh = (597.3 - (.5653 * tavgc_pri)) * 2.54;
            potet_pri[i] = factor * jh_coef_pri[mo] * (tavgf_pri - jh_coef_hru_pri[i]) * swrad_pri / elh;//???????????????????
            if (potet_pri[i] < 0.) {
                potet_pri[i] = 0.0;
            }
            basin_potet_pri = basin_potet_pri + potet_pri[i] * hru_area_pri[i];
            this.potet.setValue(i, potet_pri[i]);
        }
        //       }

        /**
         * *********************************************************
         */
        if (j + 1 == this.active_hrus.getValue()) {
            basin_potet_pri = basin_potet_pri * basin_area_inv_pri;
            basin_potet_jh_pri = basin_potet_pri;
            if (log.isLoggable(Level.INFO)) {
             //   log.info("JH " + basin_potet_pri + " " + basin_area_inv_pri);
            }
           
            this.basin_potet.setValue(basin_potet_pri);
            this.basin_potet_jh.setValue(basin_potet_jh_pri);

        }
    	 }

    }

    public void clear() {
    }
}
