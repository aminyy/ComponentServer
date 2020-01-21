package Thornthwaite.model;

import net.casnw.home.io.*;
import net.casnw.home.model.*;
import net.casnw.home.poolData.*;

public class Runoff extends AbsComponent {

    public PoolDouble runoffFactor;
    public PoolDouble remain;    
    public PoolDouble surfaceRunoff;  
    public PoolDouble snowMelt;   
    public PoolDouble runoff;

    @Override
    public void init() {
        
    }
    
    @Override
    public void run() {
        double runoffFactor = 0.1;
        double surfaceRunoff = this.surfaceRunoff.getValue();
        double snowMelt = this.snowMelt.getValue();
        double remain = 150;

        double ro1 = (surfaceRunoff + remain) * runoffFactor;
        remain = (surfaceRunoff + remain) * (1.0 - runoffFactor);

        this.runoff.setValue(ro1 + snowMelt);
        this.remain.setValue(remain);
    }
    
    @Override
    public void clear() {
        
    }
}
