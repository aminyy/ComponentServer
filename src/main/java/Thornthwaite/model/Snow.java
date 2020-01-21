package Thornthwaite.model;

import java.util.Calendar;
import net.casnw.home.io.*;
import net.casnw.home.model.*;
import net.casnw.home.runtime.*;
import net.casnw.home.poolData.*;

public class Snow extends AbsComponent {
    
    public PoolDouble snowStorage;
    public PoolDouble potET;
    public PoolDouble temp; 
    public PoolDouble precip;
    public PoolDouble snowMelt;

    @Override
    public void init() {
        
    }
    
    @Override
    public void run() {

        double snowStorage = 0;

        double temp = this.temp.getValue();

        double potET = this.potET.getValue();
        double precip = this.precip.getValue();
        double pmpe = precip - potET;

        double snowMelt = 0.0;

        if (temp < 0.0 && pmpe > 0.0) {
            snowStorage = precip + snowStorage;
        }

        if (snowStorage > 0.0 && temp >= 0.0) {
            snowMelt = snowStorage * 0.5;
            snowStorage = snowStorage * 0.5;
        } else if (snowStorage == 0.0) {
            snowMelt = 0.0;
        }

        this.snowStorage.setValue(snowStorage);
        this.snowMelt.setValue(snowMelt);
    }
    
    @Override
    public void clear() {
        
    }
}
