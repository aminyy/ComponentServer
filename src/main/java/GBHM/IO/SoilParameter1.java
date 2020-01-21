/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import java.io.IOException;
import java.util.List;
import net.casnw.home.io.SpatialParameterReader;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author longyinping
 */
public class SoilParameter1 extends AbsComponent{
        //@In
    public PoolString wsatFile;
    public PoolString wrsdFile;
    public PoolString alphaFile;
    public PoolString waternFile;
    public PoolString ksatFile;
    public PoolString soilParFile;
    public PoolInteger soilcode;
    public PoolDouble elev;
    public PoolDouble2DArray data;
    //@Out   
    public PoolDouble Ds;
    public PoolDouble Dg;
    public PoolDoubleArray D;
    public PoolDoubleArray wsat;
    public PoolDoubleArray wfld;
    public PoolDoubleArray wrsd;
    public PoolDoubleArray alpha;
    public PoolDoubleArray watern;
    public PoolDoubleArray k0;
    public PoolDouble kg;
    public PoolDouble GWcs;
    public PoolDouble sol_BD;
    private SpatialParameterReader SPsoilPar;
    private Double[][] soilPar;
    @Override
    public void init() throws IOException{
        SPsoilPar = new SpatialParameterReader(soilParFile.getValue());
        soilPar = SPsoilPar.getParameter();
        
    }
    @Override
    public void run(){
        List<Double> soil;
        soil = SPsoilPar.getParaFromID(soilcode.getValue(), soilPar);
        
    }
    
    
    
}
