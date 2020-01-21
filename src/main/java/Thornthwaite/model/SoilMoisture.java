package Thornthwaite.model;

import net.casnw.home.io.*;
import net.casnw.home.model.*;
import net.casnw.home.poolData.*;

public class SoilMoisture extends AbsComponent {

    
    public PoolDouble soilMoistStorCap;   
    public PoolDouble potET;   
    public PoolDouble temp;    
    public PoolDouble precip;   
    public PoolDouble prestor;   
    public PoolDouble soilMoistStor;   
    public PoolDouble surfaceRunoff;  
    public PoolDouble pmpe;   
    public PoolDouble actET;  
    public PoolDouble dff;

    @Override
    public void init() {
        
    }
    
    @Override
    public void run() {

        // get the parameter values
        double soilMoistStorCap = 200;
        double prestor = 150;

        // get the input values
        double temp = this.temp.getValue();
        double precip = this.precip.getValue();
        double potET = this.potET.getValue();

        double pmpe = precip - potET;

        double surfaceRunoff = 0.0;
        double soilMoistStor = 0.0;
        double actET = 0.0;

        if (temp < 0.0 && pmpe > 0.0) {
            surfaceRunoff = 0.0;
            soilMoistStor = prestor;
            actET = 0.0;
        } else if (pmpe > 0.0 || pmpe == 0.0) {

            actET = potET;
            //  SOIL MOISTURE RECHARGE
            if (prestor < soilMoistStorCap) {
                soilMoistStor = prestor + pmpe;
            }

            // SOIL MOISTURE STORAGE AT CAPACITY
            if (prestor == soilMoistStorCap) {
                soilMoistStor = soilMoistStorCap;
            }
            if (soilMoistStor > soilMoistStorCap) {
                soilMoistStor = soilMoistStorCap;
            }
            // CALCULATE SURPLUS
            surfaceRunoff = (prestor + pmpe) - soilMoistStorCap;
            if (surfaceRunoff < 0.0) {
                surfaceRunoff = 0.0;
            }
            //  CALCULATE MONTHLY CHANGE IN SOIL MOISTURE
            prestor = soilMoistStor;
        } else {
            soilMoistStor = prestor - Math.abs(pmpe * (prestor / soilMoistStorCap));
            if (soilMoistStor < 0.0) {
                soilMoistStor = 0.0;
            }
            double delstor = soilMoistStor - prestor;
            prestor = soilMoistStor;
            actET = precip + (delstor * (-1.0));
            surfaceRunoff = 0.0;
        }

        //SETTING THE OUTPUT VARIABLES
        this.dff.setValue(potET - actET);
        this.pmpe.setValue(pmpe);
        this.surfaceRunoff.setValue(surfaceRunoff);
        this.soilMoistStor.setValue(soilMoistStor);
        this.actET.setValue(actET);
        this.prestor.setValue(prestor);
    }
    
    @Override
    public void clear() {
        
    }
}
