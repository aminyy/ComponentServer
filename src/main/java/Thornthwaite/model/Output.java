package Thornthwaite.model;

import java.io.IOException;
import java.text.DecimalFormat;
import net.casnw.home.io.*;
import net.casnw.home.model.*;
import net.casnw.home.poolData.*;

public class Output extends AbsComponent {
    
    public PoolDouble daylength;
    public PoolInteger time;
    public PoolInteger year;
    //public PoolDouble timed;
    public PoolDouble potET;
    public PoolDouble snowMelt;
    public PoolDouble runoff;
    public PoolDouble soilMoistStor;
    public PoolString fileName;
    public DataWriter DW;
    public PoolString outputName;
    
    @Override
    public void init() throws Exception {

        DW = new DataWriter(outputName.getValue());
        DW.writeLine("Year " + "Month "+"Daylength "+"PotET " + "SnowMelt " + "Runoff " + "SoilMoistStor");
        
    }

    @Override
    public void run() throws IOException{

        //try {
            //DecimalFormat formatter = new DecimalFormat(".00");   
            //this.daylength.setValue(Double.valueOf(formatter.format(daylength)));
            //this.potET.setValue(Double.valueOf(formatter.format(potET)));
            //this.snowMelt.setValue(Double.valueOf(formatter.format(snowMelt)));
            //this.runoff.setValue(Double.valueOf(formatter.format(runoff)));
            //this.soilMoistStor.setValue(Double.valueOf(formatter.format(soilMoistStor)));
            //DW.writeLine(time + " " + daylengthx + " " + potETx + " " + snowMeltx + " " + runoffx + " " +soilMoistStorx);
            //this.timed.setValue((int)this.time.getValue());
            DW.writeLine(year + " "+ time + " " + daylength + " " + potET + " " + snowMelt + " " + runoff + " " +soilMoistStor);
        //} 
        //catch (IOException ex) {
          //  System.out.println (ex.toString()); 
        //}

    }

    @Override
    public void clear() {
        
    }
}
