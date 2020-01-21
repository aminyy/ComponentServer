package Thornthwaite.model;

import java.util.ArrayList;
import java.util.List;
import net.casnw.home.io.*;
import net.casnw.home.model.*;
import net.casnw.home.poolData.*;


public class Climate extends AbsComponent {
    
    public Double timex;
    public Double tempx;
    public Double precipx;
    public int timed;
    public Double yearx;
    public int yeard;
    public PoolInteger year;
    public PoolInteger time;
    public PoolDouble temp;
    public PoolDouble precip;
    public DataReader DR;
    public DataWriter DW; 
    public PoolString inputName;
    
    @Override
    public void init() throws Exception {
        DR = new DataReader(inputName.getValue());
        //System.out.println("haha 000");
        //System.out.println("CLIMATE INIT");
        //DW = new DataWriter(this.getModel().getRuntime().getOutputPath());
        //写变量名(只写一次)
        //DW.writeLine("径流量 "+"下渗量 "+"水箱变化量");
    }

    @Override
    public void run() {

        //读取input.dat
        if (DR.hasNext()){
            List<Double> var  = new ArrayList<Double>();
            var = DR.getNext();
            yearx   = var.get(0);
            timex   = var.get(1);
            tempx   = var.get(2);
            precipx = var.get(3);
            //System.out.println(yearx + "haha");
        }
        timed = (int)timex.doubleValue();
        yeard = (int)yearx.doubleValue();
        //System.out.println(yeard + "test");
        this.year.setValue(yeard);
        this.time.setValue(timed);
        this.temp.setValue(tempx);
        this.precip.setValue(precipx);
        //System.out.println(time);
        //System.out.println(temp);
        //System.out.println(precip);
    }

    @Override
    public void clear() {
        
    }
}
