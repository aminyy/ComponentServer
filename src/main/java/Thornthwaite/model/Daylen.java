package Thornthwaite.model;
import net.casnw.home.io.*;
import net.casnw.home.model.*;
import net.casnw.home.poolData.*;

public class Daylen extends AbsComponent {

    //public int timex;
    //public double timed;
    public Double latitude;
    public PoolDouble daylength;
    public PoolInteger time;
    public PoolInteger year;
    public int timed;

    static final int[] DAYS = {
        15, 45, 74, 105, 135, 166, 196, 227, 258, 288, 319, 349
    };
    //public DataReader DR;
    //public DataWriter DW; 
    
    @Override
    public void init() {
        //DR = new DataReader(this.getModel().getRuntime().getInputPath());
    }
    
    @Override
    public void run() {
        
        //读取input.dat
        //if (DR.hasNext()){
        //    timed = DR.getNext().get(0);
        //    timex = (int) timed;
        //}
        timed = this.time.getValue();
        int month = timed - 1;
        int year = this.year.getValue();
        System.out.println("现在计算的是 " + year + " 年 " + timed + " 月");
        double latitude = 45.033;

        double dayl = (double) DAYS[month] - 80.;
        
        if (dayl < 0.0) {
            dayl = 285. + (double) DAYS[month];
        }
        //System.out.println(dayl);
        //System.out.println(month);
        
        double decr = 23.45 * Math.sin(dayl / 365. * 6.2832) * 0.017453;
        double alat = latitude * 0.017453;
        double csh = (-0.02908 - Math.sin(decr) * Math.sin(alat)) / (Math.cos(decr) * Math.cos(alat));
        double dl = 24.0 * (1.570796 - Math.atan(csh / Math.sqrt(1. - csh * csh))) / Math.PI;

        this.daylength.setValue(dl);
        //this.time.setValue(timex);
    }
    
    @Override
    public void clear() {
        
    }
}
