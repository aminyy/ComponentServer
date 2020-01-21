package Thornthwaite.model;

import java.util.Calendar;
import net.casnw.home.io.*;
import net.casnw.home.model.*;
import java.util.Calendar;
import net.casnw.home.poolData.*;

public class HamonET extends AbsComponent {
   
    public PoolInteger time;
    public PoolDouble temp;
    public PoolDouble daylength;
    public PoolDouble potET;

    /*
    public void init() {
    System.out.println(potET.getUnit());
    System.out.println(daylength.getUnit());
    System.out.println(potET.getUnit().isCompatible(daylength.getUnit()));
    Converter conv = daylength.getUnit().getConverterTo(potET.getUnit());
    System.out.println(conv.convert(1));
    }
     */
    
    @Override
    public void init() {
        
    }
    
    @Override
    public void run() {

        double temp = this.temp.getValue();
        double daylength = this.daylength.getValue();
        int time = this.time.getValue();
        
        Calendar cd = Calendar.getInstance();
        int maxdays = cd.getActualMaximum(time);
                
        double Wt = 4.95 * Math.exp(0.062 * temp) / 100.;
        double D2 = (daylength / 12.0) * (daylength / 12.0);
        double potET = 0.55 * maxdays * D2 * Wt;

        if (potET <= 0.0) {
            potET = 0.0;
        }
        if (temp <= -1.0) {
            potET = 0.0;
        }

        potET *= 25.4;

        this.potET.setValue(potET);
    }
    
    @Override
    public void clear() {
        
    }
    
}
