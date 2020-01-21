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
 ("Subsurface flow." +
 "Adds inflow to subsurface reservoirs and computes" +
 "outflow to groundwater and to streamflow.")
 */

/*  Keywords
 ("Runoff, Subsurface")
 */
@ModuleMeta(moduleClass = "prms.process.Ssflow",
        name = "Ssflow",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "Runoff, Subsurface",
        description = "Subsurface flow"
        + "Adds inflow to subsurface reservoirs and computes"
        + "outflow to groundwater and to streamflow. ")
public class Ssflow extends AbsComponent {

    private static final Logger log = Logger.getLogger("HOME.model." + Ssflow.class.getSimpleName());
    private static double NEARZERO = 1.0e-10;
    // Input Params
    /* Role(PARAMETER)
     Description("Number of subsurface reservoirs.")
     */
    @VariableMeta(name = "nssr",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of subsurface reservoirs.")
    public PoolInteger nssr= new PoolInteger();
    /* Role(PARAMETER)
     Description("Number of HRUs.")
     */
    @VariableMeta(name = "nhru",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of HRUs.")
    public PoolInteger nhru= new PoolInteger();

    /* Role(PARAMETER)
     Description("Total basin area [basin]")
     Unit("acres")
     */
    @VariableMeta(name = "basin_area",
            dataType = DatatypeEnum.PoolDouble,
            description = "Total basin area")
    public PoolDouble basin_area= new PoolDouble();

    /* Role(PARAMETER)
     Description("Area of each HRU")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_area",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Area of each HRU")
    public PoolDoubleArray hru_area= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Index of subsurface reservoir receiving excess water  from HRU soil zone")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_ssres",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Index of subsurface reservoir receiving excess water from HRU soil zone")
    public PoolIntegerArray hru_ssres= new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("Initial storage in each subsurface reservoir;  estimated based on measured flow")
     Unit("inches")
     Bound ("nssr")
     */
    @VariableMeta(name = "ssstor_init",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Initial storage in each subsurface reservoir;  estimated based on measured flow")
    public PoolDoubleArray ssstor_init= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Coefficient to route subsurface storage to streamflow using the following equation:   ssres_flow = ssrcoef_lin * ssres_stor +  ssrcoef_sq * ssres_stor**2")
     Unit("1/day")
     Bound ("nssr")
     */
    @VariableMeta(name = "ssrcoef_lin",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Coefficient to route subsurface storage to streamflow using the following equation:   ssres_flow = ssrcoef_lin * ssres_stor +  ssrcoef_sq * ssres_stor**2")
    public PoolDoubleArray ssrcoef_lin= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Coefficient to route subsurface storage to streamflow using the following equation:   ssres_flow = ssrcoef_lin * ssres_stor +  ssrcoef_sq * ssres_stor**2")
     Bound ("nssr")
     */
    @VariableMeta(name = "ssrcoef_sq",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Coefficient to route subsurface storage to streamflow using the following equation:   ssres_flow = ssrcoef_lin * ssres_stor +  ssrcoef_sq * ssres_stor**2")
    public PoolDoubleArray ssrcoef_sq= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Coefficient to route water from subsurface to groundwater Coefficient in equation used to route water from the  subsurface reservoirs to the groundwater reservoirs:   ssr_to_gw = ssr2gw_rate *  ((ssres_stor / ssrmax_coef)**ssr2gw_exp)")
     Unit("1/day")
     Bound ("nssr")
     */
    @VariableMeta(name = "ssr2gw_rate",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Coefficient to route water from subsurface to groundwater Coefficient in equation used to route water from the  subsurface reservoirs to the groundwater reservoirs:   ssr_to_gw = ssr2gw_rate *  ((ssres_stor / ssrmax_coef)**ssr2gw_exp)")
    public PoolDoubleArray ssr2gw_rate= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Coefficient in equation used to route water from the subsurface reservoirs to the groundwater reservoirs:   ssr_to_gw = ssr2gw_rate *  ((ssres_stor / ssrmax_coef)**ssr2gw_exp);  recommended value is 1.0")
     Unit("inches")
     Bound ("nssr")
     */
    @VariableMeta(name = "ssrmax_coef",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Coefficient in equation used to route water from the subsurface reservoirs to the groundwater reservoirs:   ssr_to_gw = ssr2gw_rate *  ((ssres_stor / ssrmax_coef)**ssr2gw_exp);  recommended value is 1.0")
    public PoolDoubleArray ssrmax_coef= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Coefficient in equation used to route water from the subsurface reservoirs to the groundwater reservoirs:   ssr_to_gw = ssr2gw_rate *   ((ssres_stor / ssrmax_coef)**ssr2gw_exp);  recommended value is 1.0")
     Bound ("nssr")
     */
    @VariableMeta(name = "ssr2gw_exp",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Coefficient in equation used to route water from the subsurface reservoirs to the groundwater reservoirs:   ssr_to_gw = ssr2gw_rate *   ((ssres_stor / ssrmax_coef)**ssr2gw_exp);  recommended value is 1.0") 
    public PoolDoubleArray ssr2gw_exp= new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_type",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
    public PoolIntegerArray hru_type= new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("Flag for frozen ground (0=no; 1=yes)")
     Bound ("nhru")
     */
    @VariableMeta(name = "frozen",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Flag for frozen ground (0=no; 1=yes).")
    public PoolIntegerArray frozen= new PoolIntegerArray();
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
            description = "Length of the time step)")
    public PoolDouble deltim= new PoolDouble();

    /* Description("Subsurface reservoir area.")
     Unit("acres")
     Bound ("nssr")
     */
    @VariableMeta(name = "ssres_area",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Area of each subsurface reservoir; computed by summing areas of HRUs that contribute to it")
    public PoolDoubleArray ssres_area= new PoolDoubleArray();

    /* Description("The amount of water transferred from the soil zone to a subsurface reservoir for each HRU. [smbal]")
     Unit("inches")
     Bound ("nhru")
     */
    @VariableMeta(name = "soil_to_ssr",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "The amount of water transferred from the soil zone to a subsurface reservoir for each HRU. [smbal]")
    public PoolDoubleArray soil_to_ssr= new PoolDoubleArray();

    /* Description("HRU pervious area. [basin]")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_perv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Pervious area of each HRU")
    public PoolDoubleArray hru_perv= new PoolDoubleArray();

    /* Description("Routing order for HRUs")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_route_order",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Routing order for HRUs")
    public PoolIntegerArray hru_route_order= new PoolIntegerArray();

    /*Description("Number of active HRUs")*/
    @VariableMeta(name = "active_hrus",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of active HRUs")
    public PoolInteger active_hrus= new PoolInteger();
    // Output Var
    /* Description("Seepage from subsurface reservoir storage to  its associated groundwater reservoir each time step")
     Unit("inches")
     Bound ("nssr")
     */
    @VariableMeta(name = "ssr_to_gw",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Flow from each subsurface reservoir to its associated groundwater reservoir. [ssflow]")
    public PoolDoubleArray ssr_to_gw= new PoolDoubleArray();

    /* Description("Outflow from each subsurface reservoir")
     Unit("inches")
     Bound ("nssr")
     */
    @VariableMeta(name = "ssres_flow",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Outflow from each subsurface reservoir")
    public PoolDoubleArray ssres_flow= new PoolDoubleArray();

    /* Description("Sum of inflow to subsurface reservoir from all associated HRUs")
     Unit("inches")
     Bound ("nssr")
     */
    @VariableMeta(name = "ssres_in",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Sum of inflow to subsurface reservoir from all associated HRUs")
    public PoolDoubleArray ssres_in= new PoolDoubleArray();

    /* Description("Storage in each subsurface reservoir")
     Unit("inches")
     Bound ("nssr")
     */
    @VariableMeta(name = "ssres_stor",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Storage in each subsurface reservoir")
    public PoolDoubleArray ssres_stor= new PoolDoubleArray();

    /* Description("Basin average drainage from soil added to groundwater")
     Unit("inches")
     */
    @VariableMeta(name = "basin_ssr2gw",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin average drainage from soil added to groundwater")
    public PoolDouble basin_ssr2gw= new PoolDouble();

    /* Description("Basin weighted average for subsurface reservoir storage  volume")
     Unit("acre-inches")
     */
    @VariableMeta(name = "basin_ssvol",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin weighted average for subsurface reservoir storage  volume")
    public PoolDouble basin_ssvol= new PoolDouble();

    /* Description("Basin weighted average for subsurface reservoir storage")
     Unit("inches")
     */
    @VariableMeta(name = "basin_ssstor",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin weighted average for subsurface reservoir storage")
    public PoolDouble basin_ssstor= new PoolDouble();

    /* Description("Basin weighted average for subsurface reservoir outflow")
     Unit("inches")
     */
    @VariableMeta(name = "basin_ssflow",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area-weighted average for subsurface flow [ssflow]")
    public PoolDouble basin_ssflow = new PoolDouble();

    /* Description("Basin weighted average for inflow to subsurface reservoirs")
     Unit("inches")
     */
    public PoolDouble basin_ssin;
    //private define 
    //xml parameter io
    private double[] ssstor_init_pri;
    private double[] ssrcoef_lin_pri;
    private double[] ssrcoef_sq_pri;
    private double[] ssr2gw_rate_pri;
    private double[] ssrmax_coef_pri;
    private double[] ssr2gw_exp_pri;
    //input 
    private int nssr_pri;
    private int nhru_pri;
    private double basin_area_pri;
    private double[] hru_area_pri;
    private int[] hru_ssres_pri;
    private int[] hru_type_pri;
    private int[] frozen_pri;
    private double basin_area_inv_pri;
    private double deltim_pri;
    private double[] ssres_area_pri;
    private double soil_to_ssr_pri;
    private double hru_perv_pri;
    private int[] hru_route_order_pri;
    private int active_hrus_pri;
    //out
    private double[] ssr_to_gw_pri;
    private double[] ssres_flow_pri;
    private double[] ssres_in_pri;
    private double[] ssres_stor_pri;
    private double basin_ssr2gw_pri;
    private double basin_ssvol_pri;
    private double basin_ssstor_pri;
    private double basin_ssflow_pri;
    private double basin_ssin_pri;
    //private loop define
    private int k;
    private double tstep;
    private double ts;
    private double td;

    @Override
    public void init() throws Exception {

        //getvalue
        nssr_pri = this.nssr.getValue();
        nhru_pri = this.nhru.getValue();
        ssstor_init_pri = this.ssstor_init.getValue();
        ssrcoef_lin_pri = this.ssrcoef_lin.getValue();
        ssrcoef_sq_pri = this.ssrcoef_sq.getValue();
        ssr2gw_rate_pri = this.ssr2gw_rate.getValue();
        ssrmax_coef_pri = this.ssrmax_coef.getValue();
        ssr2gw_exp_pri = this.ssr2gw_exp.getValue();
        basin_area_pri = this.basin_area.getValue();
        hru_area_pri = this.hru_area.getValue();
        hru_type_pri = this.hru_type.getValue();
        basin_area_inv_pri = this.basin_area_inv.getValue();
        deltim_pri = this.deltim.getValue();
        hru_route_order_pri = this.hru_route_order.getValue();
        active_hrus_pri = this.active_hrus.getValue();
        frozen_pri = this.frozen.getValue();
        hru_ssres_pri = this.hru_ssres.getValue();
        ssres_area_pri = this.ssres_area.getValue();

        ssres_stor_pri = new double[nssr_pri];
        ssres_in_pri = new double[nssr_pri];
        ssres_flow_pri = new double[nssr_pri];
        ssr_to_gw_pri = new double[nssr_pri];

        if (nhru_pri != nssr_pri) {  //TODO unclear
//        if ( getparam('ssflow', 'hru_ssres', nhru, 'integer', hru_ssres)
//     +      .ne.0 ) return
        } else {
            for (int i = 0; i < nhru_pri; i++) {
                hru_ssres_pri[i] = i;
            }
        }

        for (int i = 0; i < nssr_pri; i++) {
            ssres_stor_pri[i] = ssstor_init_pri[i];
        }

        for (int kk = 0; kk < active_hrus_pri; kk++) {
            int i = hru_route_order_pri[kk];
            int j = hru_ssres_pri[i];
            if (hru_type_pri[i] == 2) {
                // assume if hru_type is 2, ssr has same area as hru
                if (ssres_stor_pri[j - 1] > 0.0) {
                    System.out.println("warning, ssres_stor>0 for lake hru: " + j + " "
                            + ssres_stor_pri[j - 1]);
                    ssres_stor_pri[j - 1] = 0.0;
                }
            }
        }

        basin_ssstor_pri = 0.0;
        for (int j = 0; j < nssr_pri; j++) {
            basin_ssstor_pri += ssres_stor_pri[j] * ssres_area_pri[j];
        }
        basin_ssstor_pri = basin_ssstor_pri * basin_area_inv_pri;

        k = -1;

        this.ssres_stor.setValue(ssres_stor_pri);

    }

    @Override
    public void run() throws Exception {

    	 for (int k = 0; k < active_hrus_pri; k++) {

        if (k == 0) {
            if (ssres_stor_pri == null) {
                init();
            }
            // ts=timesteps in a day, td=timestep in days

            tstep = deltim_pri;
            ts = 24.0 / tstep;
            td = tstep / 24.0;

            for (int j = 0; j < nssr_pri; j++) {
                ssres_in_pri[j] = 0.0;
            }
        }
        /**
         * *********************************loop**************************
         */
        //      for(int k=0;k<active_hrus_pri;k++){
        if (k < this.active_hrus.getValue()) {
            int i = hru_route_order_pri[k];

            soil_to_ssr_pri = this.soil_to_ssr.getValue(i);
            hru_perv_pri = this.hru_perv.getValue(i);

            int j = hru_ssres_pri[i];
            ssres_in_pri[j - 1] += (soil_to_ssr_pri * hru_perv_pri);
            //      }

           

        }
        /**
         * *********************************end********************************
         */
        if (k + 1 == 0) {
            basin_ssflow_pri = 0.0;
            basin_ssstor_pri = 0.0;
            basin_ssin_pri = 0.0;
            basin_ssr2gw_pri = 0.0;

            for (int j = 0; j < nssr_pri; j++) {
                ssres_flow_pri[j] = 0.0;
                ssr_to_gw_pri[j] = 0.0;
                double srarea = ssres_area_pri[j];

                //rsr, how do you know if frozen ssr, frozen is hru variable
                if (frozen_pri[j] == 1) {
                    if (ssres_in_pri[j] > 0.0) {
                        System.out.println("cfgi problem, ssres_in>0 " + ssres_in_pri[j]);
                    }
                    basin_ssstor_pri += ssres_stor_pri[j] * srarea;
                    continue;
                }

                ssres_in_pri[j] = ssres_in_pri[j] / srarea;

                if (ssres_stor_pri[j] > 0.0 || ssres_in_pri[j] > 0.0) {
                    inter_gw_flow(j, td, ts, ssrcoef_lin_pri[j],
                            ssrcoef_sq_pri[j], ssr2gw_rate_pri[j],
                            ssr2gw_exp_pri[j], ssrmax_coef_pri[j], ssres_in_pri[j]);

                    basin_ssstor_pri += ssres_stor_pri[j] * srarea;
                    basin_ssflow_pri += ssres_flow_pri[j] * srarea;
                    basin_ssin_pri += ssres_in_pri[j] * srarea;
                    basin_ssr2gw_pri += ssr_to_gw_pri[j] * srarea;
                }
            }
            basin_ssstor_pri *= basin_area_inv_pri;
            basin_ssflow_pri *= basin_area_inv_pri;
            basin_ssin_pri *= basin_area_inv_pri;
            basin_ssr2gw_pri *= basin_area_inv_pri;

            if (log.isLoggable(Level.INFO)) {
                log.info("SSflow  " + basin_ssstor_pri
                        + basin_ssflow_pri + " "
                        + basin_ssin_pri + " "
                        + basin_ssr2gw_pri);
            }

            this.ssr_to_gw.setValue(ssr_to_gw_pri);
            this.ssres_flow.setValue(ssres_flow_pri);
            this.ssres_in.setValue(ssres_in_pri);
            this.basin_ssvol.setValue(basin_ssvol_pri);
            this.basin_ssr2gw.setValue(basin_ssr2gw_pri);
            this.basin_ssstor.setValue(basin_ssstor_pri);
            this.basin_ssflow.setValue(basin_ssflow_pri);
         //   this.basin_ssin.setValue(basin_ssin_pri);

        }
    	 }

    }

    @Override
    public void clear() throws Exception {
    }

    //***********************************************************************
    //     compute interflow and flow to groundwater reservoir
    //***********************************************************************
    private void inter_gw_flow(int j, double td, double ts,
            double coef_lin, double coef_sq,
            double ssr2gw_rate, double ssr2gw_exp,
            double ssrmax_coef, double input) {

        double availh2o, sos;
        double c1, c2, c3;

        availh2o = ssres_stor_pri[j] + input;
        if (availh2o > 0.0) {
            //******compute interflow
            if (coef_lin < NEARZERO && input <= 0.0) {
                c1 = coef_sq * ssres_stor_pri[j];
                ssres_flow_pri[j] = ssres_stor_pri[j] * (c1 / (1.0 + c1));
            } else if (coef_sq < NEARZERO) {
                c2 = 1.0 - Math.exp(-coef_lin * td);
                ssres_flow_pri[j] = input * (1.0 - c2 / coef_lin * td) + ssres_stor_pri[j] * c2;
            } else {
                c3 = Math.sqrt(Math.pow(coef_lin, 2.0) + 4.0 * coef_sq * input * ts);
                sos = ssres_stor_pri[j] - ((c3 - coef_lin) / (2.0 * coef_sq));
                c1 = coef_sq * sos / c3;
                c2 = 1.0 - Math.exp(-c3 * td);
                ssres_flow_pri[j] = input + (sos * (1.0 + c1) * c2) / (1.0 + c1 * c2);
            }

            if (ssres_flow_pri[j] < 0.0) {
                ssres_flow_pri[j] = 0.0;
            } else if (ssres_flow_pri[j] > availh2o) {
                ssres_flow_pri[j] = availh2o;
            }

            ssres_stor_pri[j] = availh2o - ssres_flow_pri[j];
            if (ssres_stor_pri[j] < 0.0) {
                System.out.println("sanity check, ssres_stor<0.0 " + ssres_stor_pri[j]);
                ssres_stor_pri[j] = 0.0;
                // rsr, if very small storage, add it to interflow
            } else if (ssres_stor_pri[j] < NEARZERO) {
                ssres_flow_pri[j] = ssres_flow_pri[j] + ssres_stor_pri[j];
                ssres_stor_pri[j] = 0.0;
            }
        }

        //******compute flow to groundwater
        if (ssres_stor_pri[j] > 0.0 && ssr2gw_rate > NEARZERO) {
            ssr_to_gw_pri[j] = ssr2gw_rate * td * (Math.pow((ssres_stor_pri[j] / ssrmax_coef), ssr2gw_exp));
            if (ssr_to_gw_pri[j] > ssres_stor_pri[j]) {
                ssr_to_gw_pri[j] = ssres_stor_pri[j];
            }
            if (ssr_to_gw_pri[j] < 0.0) {
                ssr_to_gw_pri[j] = 0.0;
            }
            ssres_stor_pri[j] = ssres_stor_pri[j] - ssr_to_gw_pri[j];
        } else {
            ssr_to_gw_pri[j] = 0.0;
        }
    }
}
