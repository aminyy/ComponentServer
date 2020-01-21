/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prms.io;

import java.util.*;
import net.casnw.home.io.DataReader;
import net.casnw.home.meta.DatatypeEnum;
import net.casnw.home.meta.ModuleMeta;
import net.casnw.home.meta.TimescaleEnum;
import net.casnw.home.meta.VariableMeta;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolCalendar;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolIntegerArray;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author Administrator
 */
@ModuleMeta(moduleClass = "prms.io.ObsIO",
        name = "ObsIO",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "obs io",
        description = "obs io")
public class ObsIO extends AbsComponent {

    @VariableMeta(name = "date_mo",
            dataType = DatatypeEnum.PoolInteger,
            description = "Calendar.MONTH)")
    public PoolInteger date_mo = new PoolInteger();

    @VariableMeta(name = "date_jday",
            dataType = DatatypeEnum.PoolInteger,
            description = "Calendar date)")
    public PoolInteger date_jday =new PoolInteger();;

    @VariableMeta(name = "date_day",
            dataType = DatatypeEnum.PoolInteger,
            description = " Calendar.DAY_OF_MONTH)")
    public PoolInteger date_day =new PoolInteger();;

    @VariableMeta(name = "date_year",
            dataType = DatatypeEnum.PoolInteger,
            description = " Calendar date)")
    public PoolInteger date_year=new PoolInteger();

    @VariableMeta(name = "rain_day",
            dataType = DatatypeEnum.PoolInteger,
            description = "rain of day)")
    public PoolInteger rain_day=new PoolInteger();;

    @VariableMeta(name = "newday",
            dataType = DatatypeEnum.PoolInteger,
            description = " Switch signifying the start of a new day (0=no; 1=yes))")
    public PoolInteger newday =new PoolInteger();;

    @VariableMeta(name = "deltim",
            dataType = DatatypeEnum.PoolDouble,
            description = "Length of the time step)")
    public PoolDouble deltim = new PoolDouble();

    @VariableMeta(name = "rain_code",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "(if rain_code=4)")
    public PoolIntegerArray rain_code= new PoolIntegerArray() ;

    @VariableMeta(name = "route_on",
            dataType = DatatypeEnum.PoolInteger,
            description = "Potential evapotranspiration on an HRU)")
    public PoolInteger route_on = new PoolInteger();;

    @VariableMeta(name = "runoff",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "(runoff)")
    public PoolDoubleArray runoff =new PoolDoubleArray();;

    @VariableMeta(name = "precip",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "(precip)")
    public PoolDoubleArray precip=new PoolDoubleArray();

    @VariableMeta(name = "tmin",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "(Minimum temperature)")
    public PoolDoubleArray tmin=new PoolDoubleArray();

    @VariableMeta(name = "tmax",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "(Maximum temperature )")
    public PoolDoubleArray tmax=new PoolDoubleArray();
    //xml parameter

    @VariableMeta(name = "nobs",
            dataType = DatatypeEnum.PoolInteger,
            description = "the number of observation")
    public PoolInteger nobs=new PoolInteger();;

    @VariableMeta(name = "nrain",
            dataType = DatatypeEnum.PoolInteger,
            description = "the number of rain observation")
    public PoolInteger nrain=new PoolInteger();;

    @VariableMeta(name = "ntemp",
            dataType = DatatypeEnum.PoolInteger,
            description = "the number of temperature observation")
    public PoolInteger ntemp=new PoolInteger();;

    @VariableMeta(name = "starttime",
            dataType = DatatypeEnum.PoolCalendar,
            description = "starttime")
    public PoolCalendar starttime=new PoolCalendar();;
    //private define
    private double[] runoff_pri;
    private double[] precip_pri;
    public double[] tmin_pri;
    public double[] tmax_pri;
    private int rain_day_pri;
    private double deltim_pri;
    private int newday_pri;
    private int[] rain_code_pri;//xml中写入
    private int last_day_pri = 0;
    private int route_on_pri;
    /*  int mo = date.get(Calendar.MONTH);
     int jday = date.get(Calendar.DAY_OF_YEAR);
     int jwday = Times.getDayOfYear(date, Times.WATER_YEAR);
     int day = date.get(Calendar.DAY_OF_MONTH);
     int year = date.get(Calendar.YEAR);
     */
    private int mo;
    private int jday;
    private int year;
    private int day;

    DataReader Obs;
    public PoolString obsInputPath;

    @Override
    public void init() throws Exception {

        Obs = new DataReader(obsInputPath.getValue());

        deltim_pri = 24.0;
        route_on_pri = 0;
        newday_pri = 1;
        last_day_pri = 0;

        deltim.setValue(deltim_pri);
        route_on.setValue(route_on_pri);
        rain_code_pri = this.rain_code.getValue();

    }

    @Override
    public void run() throws Exception {

        List<Double> inputObs;
        if (Obs.hasNext() == true) {

            inputObs = Obs.getNext();

            year = inputObs.get(0).intValue();
            mo = inputObs.get(1).intValue();///这个地方 
            day = inputObs.get(2).intValue();
            datecomputer(year, mo, day);

            date_year.setValue(year);
            date_mo.setValue(mo);
            date_day.setValue(day);
            date_jday.setValue(jday);
            rain_day.setValue(rain_day_pri);
            newday.setValue(newday_pri);

            for (int i = 0; i < nobs.getValue(); i++) {

                runoff.setValue(i, inputObs.get(6));

            }

            for (int i = 0; i < nrain.getValue(); i++) {

                precip.setValue(i, inputObs.get(7 + i));
                //    precip.setValue(i+1, inputObs.get(8));
                //    precip.setValue(i+2, inputObs.get(9));
                //    precip.setValue(i+3, inputObs.get(10));
                //    precip.setValue(i+4, inputObs.get(11));

            }

            for (int i = 0; i < ntemp.getValue(); i++) {

                tmin.setValue(i, inputObs.get(12 + i));
                // tmin.setValue(i+1, inputObs.get(13));

            }
            for (int i = 0; i < ntemp.getValue(); i++) {

                tmax.setValue(i, inputObs.get(14 + i));
                //  tmax.setValue(i+1, inputObs.get(15));

            }

        }

    }

    @Override
    public void clear() throws Exception {

    }

    private void datecomputer(int yyear, int mmonth, int dday) {

        GregorianCalendar gc = new GregorianCalendar();
        gc.set(yyear, mmonth - 1, dday);//设置日历字段 YEAR、MONTH 和 DAY_OF_MONTH 的值。
        jday = gc.get(GregorianCalendar.DAY_OF_YEAR);//获得当前年中的天数。
        day = gc.get(GregorianCalendar.DAY_OF_MONTH);
        mo = gc.get(GregorianCalendar.MONTH);
        year = gc.get(GregorianCalendar.YEAR);

        if (last_day_pri != jday) {
            newday_pri = 1;
            last_day_pri = jday;
        }
        if (rain_code_pri[mo] != 4) {
            rain_day_pri = 1;
        }

    }

}
