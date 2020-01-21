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

/*  Description
 ("Groundwater Flow." +
 "Sums inflow to groundwater reservoirs and computes outflow to" +
 "streamflow and to a groundwater sink if specified.")
 */

/*Keywords
 ("Groundwater")
 */
@ModuleMeta(moduleClass = "prms.process.Gwflow",
        name = "Gwflow",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "Groundwater",
        description = "Groundwater Flow."
        + "Sums inflow to groundwater reservoirs and computes outflow to"
        + "streamflow and to a groundwater sink if specified.")
public class Gwflow extends AbsComponent {

    private static final Logger log = Logger.getLogger("HOME.model." + Gwflow.class.getSimpleName());
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

    /* Role(PARAMETER)
     Description("Total basin area.")
     Unit("acres")
     */
    @VariableMeta(name = "basin_area",
            dataType = DatatypeEnum.PoolDouble,
            description = "Total basin area.")
    public PoolDouble basin_area= new PoolDouble();

    /* Role(PARAMETER)
     Description("HRU area,  Area of each HRU")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_area",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "HRU area,  Area of each HRU")
    public PoolDoubleArray hru_area= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Initial storage in each gw reservoir Storage in each groundwater reservoir at the  beginning of a simulation")
     Unit("inches")
     Bound ("ngw")
     */
    @VariableMeta(name = "gwstor_init",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Initial storage in each gw reservoir Storage in each groundwater reservoir at the  beginning of a simulation")
    public PoolDoubleArray gwstor_init= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Index of groundwater reservoir assigned to HRU Index of groundwater reservoir receiving excess soil  water from each HRU")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_gwres",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Index of groundwater reservoir assigned to HRU Index of groundwater reservoir receiving excess soil  water from each HRU")
    public PoolIntegerArray hru_gwres= new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("Index of gw reservoir to receive flow from ss reservoir Index of the groundwater reservoir that will receive  flow from each subsurface or gravity reservoir")
     Bound ("nssr")
     */
    @VariableMeta(name = "ssr_gwres",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Index of gw reservoir to receive flow from ss reservoir Index of the groundwater reservoir that will receive  flow from each subsurface or gravity reservoir")
    public PoolIntegerArray ssr_gwres= new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("Groundwater routing coefficient Groundwater routing coefficient - is multiplied by the  storage in the groundwater reservoir to compute  groundwater flow contribution to down-slope flow")
     Unit("1/day")
     Bound ("ngw")
     */
    @VariableMeta(name = "gwflow_coef",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Groundwater routing coefficient Groundwater routing coefficient - is multiplied by the  storage in the groundwater reservoir to compute  groundwater flow contribution to down-slope flow")
    public PoolDoubleArray gwflow_coef= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Groundwater sink coefficient Groundwater sink coefficient - is multiplied by the  storage in the groundwater reservoir to compute the  seepage from each reservoir to the groundwater sink")
     Unit("1/day")
     Bound ("ngw")
     */
    @VariableMeta(name = "gwsink_coef",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Groundwater sink coefficient Groundwater sink coefficient - is multiplied by the  storage in the groundwater reservoir to compute the  seepage from each reservoir to the groundwater sink")
    public PoolDoubleArray gwsink_coef= new PoolDoubleArray();
    // Input Var

    /* Description("Inverse of total basin area as sum of HRU areas")
     Unit("1/acres")
     */
    @VariableMeta(name = "basin_area_inv",
            dataType = DatatypeEnum.PoolDouble,
            description = "Inverse of total basin area as sum of HRU areas")
    public PoolDouble basin_area_inv= new PoolDouble();

    /* Description("Length of the time step")
     Unit("hours")
     */
    @VariableMeta(name = "deltim",
            dataType = DatatypeEnum.PoolDouble,
            description = "Length of the time step")
    public PoolDouble deltim= new PoolDouble();

    /* Description("Groundwater reservoir area.")
     Unit("acres")
     Bound ("ngw")
     */
    @VariableMeta(name = "gwres_area",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Groundwater reservoir area.")
    public PoolDoubleArray gwres_area= new PoolDoubleArray();

    /* Description("The amount of water transferred from the soil zone to a groundwater reservoir for each HRU. [smbal]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "soil_to_gw",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "The amount of water transferred from the soil zone to a groundwater reservoir for each HRU. [smbal]")
    public PoolDoubleArray soil_to_gw= new PoolDoubleArray();

    /* Description("Flow from each subsurface reservoir to its associated groundwater reservoir. [ssflow]")
     Unit("inches")
     Bound ("nssr")
     */
    @VariableMeta(name = "ssr_to_gw",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Flow from each subsurface reservoir to its associated groundwater reservoir. [ssflow]")
    public PoolDoubleArray ssr_to_gw= new PoolDoubleArray();

    /* Description("HRU pervious area. [basin]")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_perv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "HRU pervious area. [basin]")
    public PoolDoubleArray hru_perv= new PoolDoubleArray();

    /* Description("Subsurface reservoir area. [ssflow]")
     Unit("acres")
     Bound ("nssr")
     */
    @VariableMeta(name = "ssres_area",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Subsurface reservoir area. [ssflow]")
    public PoolDoubleArray ssres_area= new PoolDoubleArray();

    /* Description("Number of active HRUs")*/
    @VariableMeta(name = "active_hrus",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of active HRUs")
    public PoolInteger active_hrus= new PoolInteger();

    /* Description("Number of active GWRs")*/
    @VariableMeta(name = "active_gwrs",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of active GWRs")
    public PoolInteger active_gwrs= new PoolInteger();

    /* Description("Routing order for HRUs")
     Bound ("nhru")
     */
    @VariableMeta(name = "Routing order for HRUs",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "hru_route_order")
    public PoolIntegerArray hru_route_order= new PoolIntegerArray();

    /* Description("Routing order for ground-water reservoirs")
     Bound ("ngw")
     */
    @VariableMeta(name = "gwr_route_order",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Routing order for ground-water reservoirs")
    public PoolIntegerArray gwr_route_order= new PoolIntegerArray();
    // Output Var
    /* Description("Basin area weighted average of groundwater flow")
     Unit("inches")
     */
    @VariableMeta(name = "basin_gwflow",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area weighted average of groundwater flow")
    public PoolDouble basin_gwflow= new PoolDouble();

    /* Description("Basin area weighted average of groundwater storage")
     Unit("inches")
     */
    @VariableMeta(name = "basin_gwstor",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area weighted average of groundwater storage")
    public PoolDouble basin_gwstor= new PoolDouble();

    /* Description("Basin area weighted average of groundwater  reservoir storage to the groundwater sink")
     Unit("inches")
     */
    @VariableMeta(name = "basin_gwsink",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area weighted average of groundwater  reservoir storage to the groundwater sink")
    public PoolDouble basin_gwsink= new PoolDouble();

    /* Description("Basin area weighted average of inflow to  groundwater reservoirs")
     Unit("inches")
     */
    @VariableMeta(name = "basin_gwin",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area weighted average of inflow to  groundwater reservoirs")
    public PoolDouble basin_gwin= new PoolDouble();

    /* Description("Storage in each groundwater reservoir")
     Unit("inches")
     Bound ("ngw")
     */
    @VariableMeta(name = "gwres_stor",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Storage in each groundwater reservoir")
    public PoolDoubleArray gwres_stor= new PoolDoubleArray();

    /* Description("Sum of inflows to each groundwater reservoir from the soil-water excess of associated HRUs")
     Unit("acre-inches")
     Bound ("ngw")
     */
    @VariableMeta(name = "gw_in_soil",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Sum of inflows to each groundwater reservoir from the soil-water excess of associated HRUs")
    public PoolDoubleArray gw_in_soil= new PoolDoubleArray();

    /* Description("Sum of inflows to each groundwater reservoir from  associated subsurface or gravity reservoirs")
     Unit("acre-inches")
     Bound ("ngw")
     */
    @VariableMeta(name = "gw_in_ssr",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Sum of inflows to each groundwater reservoir from  associated subsurface or gravity reservoirs")
    public PoolDoubleArray gw_in_ssr= new PoolDoubleArray();

    /* Description("Sum of inflows to each groundwater reservoir from all associated soil-zone reservoirs")
     Unit("acre-inches")
     Bound ("ngw")
     */
    @VariableMeta(name = "gwres_in",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Sum of inflows to each groundwater reservoir from all associated soil-zone reservoirs")
    public PoolDoubleArray gwres_in= new PoolDoubleArray();

    /* Description("Outflow from each groundwater reservoir to streams")
     Unit("inches")
     Bound ("ngw")
     */
    @VariableMeta(name = "gwres_flow",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Outflow from each groundwater reservoir to streams")
    public PoolDoubleArray gwres_flow= new PoolDoubleArray();

    /* Description("Amount of water transferred from groundwater reservoirs to the groundwater sink.  This water is  effectively routed out of the basin and will not  be included in streamflow")
     Unit("inches")
     Bound ("ngw")
     */
    @VariableMeta(name = "gwres_sink",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Amount of water transferred from groundwater reservoirs to the groundwater sink.  This water is  effectively routed out of the basin and will not  be included in streamflow")
    public PoolDoubleArray gwres_sink= new PoolDoubleArray();
    //private  define 
    //xml parameter io
    private double[] gwstor_init_pri;
    private int[] ssr_gwres_pri;
    private double[] gwflow_coef_pri;
    private double[] gwsink_coef_pri;
    private int active_gwrs_pri;//basin
    private int[] hru_gwres_pri;
    //input 
    private int nhru_pri;
    private int ngw_pri;
    private int nssr_pri;
    private double basin_area_pri;
    private double[] hru_area_pri;
    private double basin_area_inv_pri;
    private double deltim_pri;
    private double[] gwres_area_pri;
    private double soil_to_gw_pri;
    private double[] ssr_to_gw_pri;
    private double hru_perv_pri;
    private double[] ssres_area_pri;
    private int active_hrus_pri;
    private int[] hru_route_order_pri;
    private int[] gwr_route_order_pri;
    //out
    private double basin_gwflow_pri;
    private double basin_gwstor_pri;
    private double basin_gwsink_pri;
    private double basin_gwin_pri;
    private double[] gwres_stor_pri;
    private double[] gw_in_soil_pri;
    private double[] gw_in_ssr_pri;
    private double[] gwres_in_pri;
    private double[] gwres_flow_pri;
    private double[] gwres_sink_pri;
    // private define loop
    private int ii;
    private double td;

    @Override
    public void init() throws Exception {
        ngw_pri = this.ngw.getValue();
        gwres_stor_pri = new double[ngw_pri];
        gw_in_ssr_pri = new double[ngw_pri];
        gwres_in_pri = new double[ngw_pri];
        gwres_flow_pri = new double[ngw_pri];
        gwres_sink_pri = new double[ngw_pri];
        gw_in_soil_pri = new double[ngw_pri];

        //getvalue 
        gwstor_init_pri = this.gwstor_init.getValue();
        ssr_gwres_pri = this.ssr_gwres.getValue();
        gwflow_coef_pri = this.gwflow_coef.getValue();
        gwsink_coef_pri = this.gwsink_coef.getValue();
        active_gwrs_pri = this.active_gwrs.getValue();
        nhru_pri = this.nhru.getValue();

        nssr_pri = this.nssr.getValue();
        basin_area_pri = this.basin_area.getValue();
        hru_area_pri = this.hru_area.getValue();
        basin_area_inv_pri = this.basin_area_inv.getValue();
        deltim_pri = this.deltim.getValue();
        active_hrus_pri = this.active_hrus.getValue();
        hru_route_order_pri = this.hru_route_order.getValue();
        gwr_route_order_pri = this.gwr_route_order.getValue();
        hru_gwres_pri = this.hru_gwres.getValue();
        gwres_area_pri = this.gwres_area.getValue();

        for (int i = 0; i < ngw_pri; i++) {
            gwres_stor_pri[i] = gwstor_init_pri[i];
        }
        basin_gwstor_pri = 0.0;
        for (int jj = 0; jj < ngw_pri; jj++) {
            int j = gwr_route_order_pri[jj];
            basin_gwstor_pri += gwres_stor_pri[j] * gwres_area_pri[j];
        }
        basin_gwstor_pri = basin_gwstor_pri * basin_area_inv_pri;   //TODO unclear why

        ii = -1;

    }

    @Override
    public void run() throws Exception {

        ii++;

        if (ii == 0) {

            if (gwres_stor_pri == null) {
                init();
            }
            //*****ts= timesteps in a day, td = timestep in days
            //      ts = sngl(24.d0/deltim())
            double tstep = deltim_pri;
            //        double ts=24.0/tstep;
            td = tstep / 24.0;
            for (int ii = 0; ii < active_gwrs_pri; ii++) {
                int i = gwr_route_order_pri[ii];
                gwres_stor_pri[i] = gwres_stor_pri[i] * gwres_area_pri[i];
            }

            // Sum the inflows to each groundwater reservoir
            for (int i = 0; i < ngw_pri; i++) {
                gw_in_soil_pri[i] = 0.0;
                gw_in_ssr_pri[i] = 0.0;
            }
        }
        /**
         * **************loop*************************************
         */
               for(int ii=0;ii<this.active_hrus.getValue();ii++) {
     //   if (ii < this.active_hrus.getValue()) {
            int i = hru_route_order_pri[ii];
            soil_to_gw_pri = this.soil_to_gw.getValue(i);
            hru_perv_pri = this.hru_perv.getValue(i);

            int j = hru_gwres_pri[i];
            gw_in_soil_pri[j - 1] += (soil_to_gw_pri * hru_perv_pri);
            //       }        for(int ii=0;ii<active_gwrs_pri;ii++){

            //      gwres_stor_pri[i] = gwres_stor_pri[i] * gwres_area_pri[i];//移了位置
            //       }
            if (nhru_pri == nssr_pri) {
                //         for(int ii=0;ii<active_hrus;ii++){
                int ij = hru_route_order_pri[ii];
                ssr_to_gw_pri[ij] = this.ssr_to_gw.getValue(ij);
                ssres_area_pri[ij] = this.ssres_area.getValue(ij);
                int jj = hru_gwres_pri[i];
                gw_in_ssr_pri[jj - 1] += (ssr_to_gw_pri[ij] * ssres_area_pri[ij]);
                //      
            }


        }
        /**
         * **************loop
         * end********************************************************
         */
        if (ii + 1 == 0) {

            if (nhru_pri != nssr_pri) {
                for (int i = 0; i < nssr_pri; i++) {
                    ssr_to_gw_pri = this.ssr_to_gw.getValue();
                    ssres_area_pri = this.ssres_area.getValue();//取值的时候单个取值有问题
                    int j = ssr_gwres_pri[i];
                    gw_in_ssr_pri[j - 1] += (ssr_to_gw_pri[i] * ssres_area_pri[i]);
                    this.gw_in_ssr.setValue(j - 1, gw_in_ssr_pri[j - 1]);
                }
            }

            basin_gwflow_pri = 0.0;
            basin_gwstor_pri = 0.0;
            basin_gwsink_pri = 0.0;
            basin_gwin_pri = 0.0;

            for (int j = 0; j < ngw_pri; j++) {
                int i = gwr_route_order_pri[j];
                double gwarea = gwres_area_pri[i];
                double gwin = gw_in_soil_pri[i] + gw_in_ssr_pri[i];
                double gwstor = gwres_stor_pri[i] + gwin;
                double gwflow = (gwstor * gwflow_coef_pri[i]) * td;
                gwstor = gwstor - gwflow;
                double gwsink;
                if (gwsink_coef_pri[i] > 0.0) {
                    gwsink = (gwstor * gwsink_coef_pri[i]) * td;
                    gwstor = gwstor - gwsink;
                    if (gwstor < 0.0) {
                        gwstor = 0.0;
                    }
                    gwres_sink_pri[i] = gwsink / gwarea;
                } else {
                    gwsink = 0.;
                    gwres_sink_pri[i] = 0.0;
                }
                basin_gwflow_pri += gwflow;
                basin_gwstor_pri += gwstor;
                basin_gwsink_pri += gwsink;
                basin_gwin_pri += gwin;
                gwres_flow_pri[i] = gwflow / gwarea;
                gwres_stor_pri[i] = gwstor / gwarea;
                gwres_in_pri[i] = gwin / gwarea;
                gw_in_ssr_pri[i] = gw_in_ssr_pri[i] / gwarea;
                gw_in_soil_pri[i] = gw_in_soil_pri[i] / gwarea;
            }
            basin_gwflow_pri *= basin_area_inv_pri;
            basin_gwstor_pri *= basin_area_inv_pri;
            basin_gwsink_pri *= basin_area_inv_pri;
            basin_gwin_pri *= basin_area_inv_pri;

            if (log.isLoggable(Level.INFO)) {
                log.info(" Gwflow  " + basin_gwflow_pri);
            }
            this.basin_gwflow.setValue(basin_gwflow_pri);
            this.basin_gwstor.setValue(basin_gwstor_pri);
            this.basin_gwsink.setValue(basin_gwsink_pri);
            this.basin_gwin.setValue(basin_gwin_pri);
            this.gwres_stor.setValue(gwres_stor_pri);
            this.gw_in_soil.setValue(gw_in_soil_pri);
            this.gw_in_ssr.setValue(gw_in_ssr_pri);
            this.gwres_in.setValue(gwres_in_pri);
            this.gwres_flow.setValue(gwres_flow_pri);
            this.gwres_sink.setValue(gwres_sink_pri);

        }

    }

    @Override
    public void clear() throws Exception {
    }
}
