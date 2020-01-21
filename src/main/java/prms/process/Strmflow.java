package prms.process;

import java.util.logging.*;
import net.casnw.home.meta.DatatypeEnum;
import net.casnw.home.meta.ModuleMeta;
import net.casnw.home.meta.VariableMeta;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolIntegerArray;


/*   Description
 ("Calculates daily streamflow, individual storm flows, and daily reservoir routing." +
 "Procedures to compute (1) daily streamflow as the sum of surface, subsurface," +
 "and ground-water flow contributions, (2) storm runoff totals for storm periods," +
 "and (3) daily reservoir routing.")
 */
/* Keywords
 ("Runoff")
 */
@ModuleMeta(moduleClass = "prms.process.Strmflow",
        name = "Strmflow",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "Runoff",
        description = "Calculates daily streamflow, individual storm flows, and daily reservoir routing."
        + "Procedures to compute (1) daily streamflow as the sum of surface, subsurface,"
        + "and ground-water flow contributions, (2) storm runoff totals for storm periods,"
        + "and (3) daily reservoir routing.")
public class Strmflow extends AbsComponent {

    private static final Logger log = Logger.getLogger("HOME.model." + Strmflow.class.getSimpleName());

    // private fields
    private static final double CFS2CMS = 0.028316844;

    //("Flow from channel reach [kinroute_chan]")
    //("cfs")
    double[] q_chan = new double[0];  //TODO for now.
    //("Channel segment number of outlet Channel segment number of outlet")

    int outlet_chan = 0;

    // Input Params
    /* Role(PARAMETER)
     Description("Number of HRUs.")
     */
    @VariableMeta(name = "nhru",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of HRUs.")
    public PoolInteger nhru= new PoolInteger();

    /* Role(PARAMETER)
     Description("Number of Ground water reservoirs.")
     */
    @VariableMeta(name = "ngw",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of Ground water reservoirs.")
    public PoolInteger ngw= new PoolInteger();

    /* Role(PARAMETER)
     Description("Number of subsurface reservoirs.")
     */
    @VariableMeta(name = "nssr",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of subsurface reservoirs.")
    public PoolInteger nssr= new PoolInteger();

//    @Role(PARAMETER)
//    @Description("Number of surface reservoirs.")
//    @In public int nsfres;

    /* Role(PARAMETER)
     Description("Total basin area")
     Unit("acres")
     */
    @VariableMeta(name = "basin_area",
            dataType = DatatypeEnum.PoolDouble,
            description = "Total basin area")
    public PoolDouble basin_area= new PoolDouble();

    /* Role(PARAMETER)
     Description("HRU area")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_area",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Area of each HRU")
    public PoolDoubleArray hru_area= new PoolDoubleArray();

//    @Role(PARAMETER)
//    @Description("initial lake surace elevation")
//    @Unit("ft")
//    @Bound ("nsfres")
//    @In public double[] elevsurf_init;
    // Input Var
    /* Description("Basin area-weighted average of surface runoff [srunoff]")
     Unit("inches")
     */
    @VariableMeta(name = "basin_sroff",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area-weighted average of surface runoff")
    public PoolDouble basin_sroff= new PoolDouble();

//    @Description("Total basin surface runoff for a storm timestep")
//    @Unit("inches")
//    @In public double dt_sroff;

    /* Description("Basin area-weighted average for ground-water flow [gwflow]")
     Unit("inches")
     */
    @VariableMeta(name = "basin_gwflow",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area weighted average of groundwater flow")
    public PoolDouble basin_gwflow= new PoolDouble();

    /* Description("Basin area-weighted average for subsurface flow [ssflow]")
     Unit("inches")
     */
    @VariableMeta(name = "basin_ssflow",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area-weighted average for subsurface flow [ssflow]")
    public PoolDouble basin_ssflow= new PoolDouble();

    /* Description("Routing order for HRUs")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_route_order",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Routing order for HRUs")
    public PoolIntegerArray hru_route_order= new PoolIntegerArray();

    /* Description("Inverse of total basin area as sum of HRU areas")
     Unit("1/acres")
     */
    @VariableMeta(name = "basin_area_inv",
            dataType = DatatypeEnum.PoolDouble,
            description = "Inverse of total basin area as sum of HRU areas")
    public PoolDouble basin_area_inv= new PoolDouble();

    /* Description("Number of active HRUs")*/
    @VariableMeta(name = "active_hrus",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of active HRUs")
    public PoolInteger active_hrus= new PoolInteger();

    /* Description("Kinematic routing switch - 0= non storm period, 1=storm period [obs]")*/
    @VariableMeta(name = "route_on",
            dataType = DatatypeEnum.PoolInteger,
            description = "Kinematic routing switch (0=daily; 1=storm period)")
    public PoolInteger route_on= new PoolInteger();

    /* Description("Length of the time step")
     Unit("hours")
     */
    @VariableMeta(name = "deltim",
            dataType = DatatypeEnum.PoolDouble,
            description = "Length of the time step)")
    public PoolDouble deltim= new PoolDouble();

    // Output Var
    /* Description("Sum of basin_sroff, basin_ssflow and basin_gwflow for  timestep")
     Unit("inches")
     */
    @VariableMeta(name = "basin_stflow",
            dataType = DatatypeEnum.PoolDouble,
            description = "Sum of basin_sroff, basin_ssflow and basin_gwflow for  timestep")
    public PoolDouble basin_stflow= new PoolDouble();

    /* Description("Streamflow from basin")
     Unit("cms")
     */
    @VariableMeta(name = " basin_cms",
            dataType = DatatypeEnum.PoolDouble,
            description = "Sum of basin_sroff, basin_ssflow and basin_gwflow for  timestep")
    public PoolDouble basin_cms= new PoolDouble();

    /* Description("Basin surface runoff for timestep ")
     Unit("cfs")
     */
    @VariableMeta(name = "basin_sroff_cfs",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin surface runoff for timestep")
    public PoolDouble basin_sroff_cfs= new PoolDouble();

    /* Description("Basin subsurface flow for timestep")
     Unit("cfs")
     */
    @VariableMeta(name = "basin_ssflow_cfs",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin subsurface flow for timestep")
    public PoolDouble basin_ssflow_cfs= new PoolDouble();

    /* Description("Basin ground-water flow for timestep")
     Unit("cfs")
     */
    @VariableMeta(name = "basin_gwflow_cfs",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin ground-water flow for timestep")
    public PoolDouble basin_gwflow_cfs= new PoolDouble();

    /* Description("Streamflow from basin")
     Unit("cfs")
     */
    @VariableMeta(name = "basin_cfs",
            dataType = DatatypeEnum.PoolDouble,
            description = "Streamflow from basin")
    public PoolDouble basin_cfs= new PoolDouble();

    // In Out variables
//    @Description("elevation of the lake surface")
//    @Unit("feet")
//    @Bound ("nsfres")
//    @In @Out public double[] elevsurf;
//private define 
    //input 
    private int nhru_pri;
    private int ngw_pri;
    private int nssr_pri;
    private double basin_area_pri;
    private double[] hru_area_pri;
    private double basin_sroff_pri;
    private double basin_ssflow_pri;
    private int[] hru_route_order_pri;
    private double basin_area_inv_pri;
    private int active_hrus_pri;
    private int route_on_pri;
    private double deltim_pri;
    private double basin_gwflow_pri;

    //out
    private double basin_stflow_pri;
    private double basin_cms_pri;
    private double basin_sroff_cfs_pri;
    private double basin_ssflow_cfs_pri;
    private double basin_gwflow_cfs_pri;
    private double basin_cfs_pri;

    //define loop
    private int k;

    @Override
    public void init() throws Exception {

//   if(elevsurf == null) {
//            elevsurf = new double[nsfres];
//            for(int i=0; i < nsfres; i++) {
//                elevsurf[i] =  elevsurf_init[i];
//            }
//        }
        //getvalue
        nhru_pri = this.nhru.getValue();
        ngw_pri = this.ngw.getValue();
        nssr_pri = this.nssr.getValue();
        basin_area_pri = this.basin_area.getValue();
        hru_area_pri = this.hru_area.getValue();
        hru_route_order_pri = this.hru_route_order.getValue();
        basin_area_inv_pri = this.basin_area_inv.getValue();
        active_hrus_pri = this.active_hrus.getValue();
        route_on_pri = this.route_on.getValue();
        deltim_pri = this.deltim.getValue();

        k = -1;

    }

    @Override
    public void run() throws Exception {

//        if(elevsurf == null) {
//            init();
//        }
    	 for (int k = 0; k < active_hrus_pri; k++) {

        if (k == 0) {
            //getvalue    
            basin_sroff_pri = this.basin_sroff.getValue();
            basin_ssflow_pri = this.basin_ssflow.getValue();
            basin_gwflow_pri = this.basin_gwflow.getValue();

            double dts = deltim_pri * 3600.0;
            double cfs_conv = 43560.0 / 12.0 / dts;
            double area_fac = cfs_conv / basin_area_inv_pri;

            //   check to see if in a storm period or daily time step
            if (route_on_pri == 0) {
                //   daily time step.
                //   compute daily flow.
                basin_stflow_pri = basin_sroff_pri + basin_gwflow_pri + basin_ssflow_pri;
                //rsr, original code used .04208754 instead of cfs_conv
                //       should have been .04201389
                basin_cfs_pri = basin_stflow_pri * area_fac;

                //   storm in progress. compute streamflow for this time step.
                //   q_chan and dt_sroff are computed in routing routines
                //   reservoir routing is computed in stream routing module.
            } else {
//            basin_sroff = dt_sroff;
                basin_stflow_pri = basin_sroff_pri + basin_gwflow_pri + basin_ssflow_pri;
                basin_cfs_pri = q_chan[outlet_chan];
            }
            basin_cms_pri = basin_cfs_pri * CFS2CMS;
            basin_sroff_cfs_pri = basin_sroff_pri * area_fac;
            basin_ssflow_cfs_pri = basin_ssflow_pri * area_fac;
            basin_gwflow_cfs_pri = basin_gwflow_pri * area_fac;
            if (log.isLoggable(Level.INFO)) {
              //  log.info("streamflow " + basin_cms_pri);
            }
        }

        this.basin_stflow.setValue(basin_stflow_pri);
        this.basin_cms.setValue(basin_cms_pri);
        this.basin_sroff_cfs.setValue(basin_sroff_cfs_pri);
        this.basin_ssflow_cfs.setValue(basin_ssflow_cfs_pri);
        this.basin_gwflow_cfs.setValue(basin_gwflow_cfs_pri);
        this.basin_cfs.setValue(basin_cfs_pri);

        
    	 }

    }

    @Override
    public void clear() throws Exception {

    }

}
