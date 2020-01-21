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


/*  Description
 ("TranspTindex" +
 "Determines whether current time period is one of active" +
 "transpiration based on temperature index method")
 */
/*  Keywords
 ("Evapotranspiration")
 */
@ModuleMeta(moduleClass = "prms.process.TranspTindex",
        name = "TranspTindex",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "Evapotranspiration",
        description = "TranspTindex "
        + "Determines whether current time period is one of active "
        + "transpiration based on temperature index method ")
public class TranspTindex extends AbsComponent {

    private static final Logger log = Logger.getLogger("HOME." + TranspTindex.class.getSimpleName());

    // private fields
    double[] tmax_sum;

    // "Indicator for whether within period to check for beginning of transpiration, 0=no, 1=yes.
    int[] transp_check;
    int[] transp_end_12;
    double freeze_temp;

    // Input params

    /* Role(PARAMETER)
     Description("Number of HRUs.")
     */
    @VariableMeta(name = "nhru",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of HRUs.")
    public PoolInteger nhru= new PoolInteger();

    /*  Role(PARAMETER)
     Description("Units for measured temperature (0=Fahrenheit; 1=Celsius)")*/
    @VariableMeta(name = "temp_units",
            dataType = DatatypeEnum.PoolInteger,
            description = "Units for measured temperature (0=Fahrenheit; 1=Celsius)")
    public PoolInteger temp_units= new PoolInteger();

    /* Role(PARAMETER)
     Description("Month to begin summing tmaxf for each HRU; when sum is  >= to transp_tmax, transpiration begins")
     Unit("month")
     Bound ("nhru")
     */
    @VariableMeta(name = "transp_beg",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Month to begin summing tmaxf for each HRU; when sum is  >= to transp_tmax, transpiration begins")

    public PoolIntegerArray transp_beg= new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("Month to stop transpiration " +
     "computations;  transpiration is computed thru end of previous month")
     Unit("month")
     Bound ("nhru")
     */
    @VariableMeta(name = "transp_end",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Month to stop transpiration computations;  transpiration is computed thru end of previous month")

    public PoolIntegerArray transp_end= new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("Temperature index to determine the specific date of the  start of the transpiration " +
     "period.  Subroutine sums tmax  for each HRU starting with the first " +
     "day of month  transp_beg.  When the sum exceeds this index, transpiration begins")
     Unit("degrees")
     Bound ("nhru")
     */
    @VariableMeta(name = "transp_tmax",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Temperature index to determine the specific date of the  start of the transpiration)")

    public PoolDoubleArray transp_tmax= new PoolDoubleArray();

    // Input vars

    /* Description("Maximum HRU temperature. [temp]")
     Unit("deg C")
     Bound ("nhru")
     */
    @VariableMeta(name = "tmaxc",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum HRU temperature. [temp]")
    public PoolDoubleArray tmaxc= new PoolDoubleArray();

    /* Description("Maximum HRU temperature. [temp]")
     Unit("deg F")
     Bound ("nhru")
     */
    @VariableMeta(name = "tmaxf",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Maximum HRU temperature. [temp]")
    public PoolDoubleArray tmaxf= new PoolDoubleArray();

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

    /* Description("Date of the current time step")
     Unit("yyyy mm dd hh mm ss")
     */
    /**
     * *****************时间格式**********
     */
//      public Calendar date;
    @VariableMeta(name = "date_mo",
            dataType = DatatypeEnum.PoolInteger,
            description = "Calendar.MONTH)")
    public PoolInteger date_mo= new PoolInteger();
    @VariableMeta(name = "date_day",
            dataType = DatatypeEnum.PoolInteger,
            description = " Calendar.DAY_OF_MONTH)")
    public PoolInteger date_day= new PoolInteger();
    //开始时间定义
    @VariableMeta(name = "starttime",
            dataType = DatatypeEnum.PoolCalendar,
            description = "starttime")
    public PoolCalendar starttime= new PoolCalendar();
    //    public PoolInteger startDate_day;


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
    /* Description("Switch indicating whether transpiration is occurring  " +
     "anywhere in the basin (0=no; 1=yes)")
     */
    @VariableMeta(name = "basin_transp_on",
            dataType = DatatypeEnum.PoolInteger,
            description = "Switch indicating whether transpiration is occurring anywhere in the basin (0=no; 1=yes)")
    public PoolInteger basin_transp_on= new PoolInteger();

    /* Description("Switch indicating whether transpiration is occurring (0=no; 1=yes)")
     Bound ("nhru")
     */
    @VariableMeta(name = "transp_on",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Indicator for whether transpiration is occurring, 0=no, 1=yes. [potet]")
    public PoolIntegerArray transp_on = new PoolIntegerArray();

    //private define 
    //xml parameter+time startDate_mo,startDate_day
    private int[] transp_beg_pri;
    private int[] transp_end_pri;
    private double[] transp_tmax_pri;
      // 

    //input
    private int nhru_pri;
    private int temp_units_pri;
    private double tmaxc_pri;
    private double tmaxf_pri;
    private int newday_pri;
    private int active_hrus_pri;
    private int[] hru_route_order_pri;
    private double deltim_pri;
    private int route_on_pri;

    //out
    private int basin_transp_on_pri;
    private int[] transp_on_pri;

    //define loop
    private int j;
    private int i;
    int motmp;
    int mo_now;

    int day;
    int mo;

    @Override
    public void init() throws Exception {

        transp_beg_pri = this.transp_beg.getValue();
        transp_end_pri = this.transp_end.getValue();
        transp_tmax_pri = this.transp_tmax.getValue();
        nhru_pri = this.nhru.getValue();
        temp_units_pri = this.temp_units.getValue();
        newday_pri = this.newday.getValue();//读模块
        active_hrus_pri = this.active_hrus.getValue();
        hru_route_order_pri = this.hru_route_order.getValue();
        deltim_pri = this.deltim.getValue();
        route_on_pri = this.route_on.getValue();

        tmax_sum = new double[nhru_pri];
        transp_check = new int[nhru_pri];
        transp_end_12 = new int[nhru_pri];
        transp_on_pri = new int[nhru_pri];

        if (temp_units_pri == 0) {
            freeze_temp = 32.0;
        } else {
            freeze_temp = 0.0;
        }
        /**
         * ************时间格式**************************
         *
         * 这个地方从什么地方获取值？这个地方是从OBS的初始化中获得的开始时间(解决)
         */

        int mo_pri = this.starttime.get(Calendar.MONTH) + 1;
        int day_pri = this.starttime.get(Calendar.DAY_OF_MONTH);
        /*
         int mo = date.get(Calendar.MONTH) + 1;
         int day = date.get(Calendar.DAY_OF_MONTH);
         */

        basin_transp_on_pri = 0;
        int motmp_ini = mo_pri + 12;

        for (int ii = 0; ii < nhru_pri; ii++) {
            tmax_sum[ii] = 0.0;
            transp_on_pri[ii] = 0;
            transp_check[ii] = 0;
            if (mo_pri == transp_beg_pri[ii]) {
                if (day_pri > 10) {
                    transp_on_pri[ii] = 1;
                } else {
                    transp_check[ii] = 1;
                }
            } else if ((transp_end_pri[ii] - transp_beg_pri[ii]) > 0) {
                if (mo_pri > transp_beg_pri[ii] && mo_pri < transp_end_pri[ii]) {
                    transp_on_pri[ii] = 1;
                }
            } else {
                transp_end_12[ii] = transp_end_pri[ii] + 12;
                if ((mo_pri > transp_beg_pri[ii] && motmp_ini < transp_end_12[ii])) {
                    transp_on_pri[ii] = 1;
                }
            }
            if (transp_on_pri[ii] == 1) {
                basin_transp_on_pri = 1;
            }
        }

        transp_on.setValue(transp_on_pri);
        j = -1;

    }

    @Override
    public void run() throws Exception {

    	 for (int j = 0; j < active_hrus_pri; j++) {
        i = hru_route_order_pri[j];

        double dt;

        //getvalue 
        tmaxc_pri = this.tmaxc.getValue(i);
        tmaxf_pri = this.tmaxf.getValue(i);

        if (j == 0) {

            if (transp_on == null) {
                init();
            }
            mo = 0;
            day = 0;
            mo_now = 0;
            motmp = 0;
            dt = deltim_pri;
            /*
             mo = date.get(Calendar.MONTH);
             day = date.get(Calendar.DAY_OF_MONTH);
             */
            mo = this.date_mo.getValue();
            day = this.date_day.getValue();
            mo_now = mo + 1;

            // Set switch for active transpiration period
            //if (newday == 1) {
            basin_transp_on_pri = 0;
            motmp = mo_now + 12;

        }
        /**
         * ****************************loop*********************************
         */
        //           for (int j = 0; j < active_hrus; j++) {
//                
        if (j < this.active_hrus.getValue()) {

            i = hru_route_order_pri[j];
            // If in checking period, then for each day
            // sum max temp until greater than temperature index parameter,
            // at which currentTime, turn transpiration switch on, check switch off
            if (transp_check[i] == 1) {
                if (temp_units_pri == 0) {
                    if (tmaxf_pri > freeze_temp) {
                        tmax_sum[i] += tmaxf_pri;
                    }
                } else {
                    if (tmaxc_pri > freeze_temp) {
                        tmax_sum[i] += tmaxc_pri;
                    }
                }
                if (tmax_sum[i] > transp_tmax_pri[i]) {
                    transp_on_pri[i] = 1;
                    transp_check[i] = 0;
                    tmax_sum[i] = 0.;
                }
                // Otherwise, check for month to turn check switch on or
                // transpiration switch off
            } else if (day == 1) {
                if (mo_now == transp_beg_pri[i]) {
                    transp_check[i] = 1;
                    if (temp_units_pri == 0) {
                        if (tmaxf_pri > freeze_temp) {
                            tmax_sum[i] += tmaxf_pri;
                        }
                    } else if (tmaxc_pri > freeze_temp) {
                        tmax_sum[i] += tmaxc_pri;
                    }

                    // If transpiration switch on, check for end of period
                } else if (transp_on_pri[i] == 1) {
                    if (transp_end_pri[i] - transp_beg_pri[i] > 0) {
                        if (mo_now == transp_end_pri[i]) {
                            transp_on_pri[i] = 0;
                        }
                    } else {
                        if (motmp == transp_end_12[i]) {
                            transp_on_pri[i] = 0;
                        }
                    }
                }
                if (transp_on_pri[i] == 1) {
                    basin_transp_on_pri = 1;
                }
            }
        }

        this.transp_on.setValue(i, transp_on_pri[i]);
        this.basin_transp_on.setValue(basin_transp_on_pri);

       
    	 }

        /**
         * ***********************end*********************************
         */
    }

    @Override
    public void clear() throws Exception {

    }

}
