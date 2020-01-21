/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prms.io;

import javax.annotation.Resource;

/**
 *
 * @author Administrator
 */
import net.casnw.home.meta.DatatypeEnum;
import net.casnw.home.meta.ModuleMeta;
import net.casnw.home.meta.TimescaleEnum;
import net.casnw.home.meta.VariableMeta;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.*;
import prms.api.RedisUtil;

@ModuleMeta(moduleClass = "prms.io.InitBasin",
        name = "InitBasin",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "initialize",
        description = "Basin setup initialize,Check for validity of basin parameters and compute reservoir areas.",
        timeScale = TimescaleEnum.Hour)

/*
 ("Basin setup initialize." +
 "Check for validity of basin parameters and compute reservoir areas.")
 */
/* 
 * keywords
 * initialize
 */
public class InitBasin extends AbsComponent {

    // private fields
    private static double NEARZERO = 1.0e-06;
    @Resource
	RedisUtil redisUtil;
    // Input Params

    @VariableMeta(name = "nhru",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of HRUs.")
    public PoolInteger nhru = new PoolInteger();

    @VariableMeta(name = "ngw",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of Ground water reservoirs.")
    public PoolInteger ngw = new PoolInteger();

    @VariableMeta(name = "nssr",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of subsurface reservoirs.")
    public PoolInteger nssr = new PoolInteger();

    /* Role(PARAMETER)
     Description("Proportion of each HRU area that is impervious")
     Unit("decimal fraction")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_percent_imperv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Proportion of each HRU area that is impervious")
    public PoolDoubleArray hru_percent_imperv = new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Area of each HRU")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_area",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Area of each HRU")
    public PoolDoubleArray hru_area = new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Total basin area")
     Unit("acres")
     */
    @VariableMeta(name = "basin_area",
            dataType = DatatypeEnum.PoolDouble,
            description = "Total basin area")
    public PoolDouble basin_area = new PoolDouble();
    /* Role(PARAMETER)
     Description("Index of subsurface reservoir receiving excess water from HRU soil zone")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_ssres",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Index of subsurface reservoir receiving excess water from HRU soil zone")
    public PoolIntegerArray hru_ssres = new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("Index of groundwater reservoir receiving excess soil water from each HRU")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_gwres",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Index of groundwater reservoir receiving excess soil water from each HRU")
    public PoolIntegerArray hru_gwres = new PoolIntegerArray();

    /* Role(PARAMETER)
     Description("Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_type",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Type of each HRU (0=inactive; 1=land; 2=lake; 3=swale)")
    public PoolIntegerArray hru_type = new PoolIntegerArray();

    /*Role(PARAMETER)
     Description("Selection flag for depression storage computation. 0=No; 1=Yes")
     */
    @VariableMeta(name = "dprst_flag",
            dataType = DatatypeEnum.PoolInteger,
            description = "Selection flag for depression storage computation. 0=No; 1=Yes")
    public PoolInteger dprst_flag = new PoolInteger();

    /* Role(PARAMETER)
     Description("HRU depression storage area as a decimal percent of the total HRU area")
     Unit("decimal fraction")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_percent_dprst",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "HRU depression storage area as a decimal percent of the total HRU area")
    public PoolDoubleArray hru_percent_dprst = new PoolDoubleArray();

    /* Role(PARAMETER)
     Description("Decimal fraction of depression storage area that can flow to a stream channel. Amount of flow is a  function of storage.")
     Unit("decimal fraction")
     Bound ("nhru")
     */
    @VariableMeta(name = "dprst_pct_open",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Decimal fraction of depression storage area that can flow to a stream channel. Amount of flow is a  function of storage.")
    public PoolDoubleArray dprst_pct_open = new PoolDoubleArray();
    // Output vars
    /* Description("Basin area composed of land.")
     Unit("acres")
     */
    @VariableMeta(name = "land_area",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area composed of land.")
    public PoolDouble land_area = new PoolDouble();

    /*Description("Basin area composed of water bodies")
     Unit("acres")
     */
    @VariableMeta(name = "water_area",
            dataType = DatatypeEnum.PoolDouble,
            description = "Basin area composed of water bodies")

    public PoolDouble water_area = new PoolDouble();

    /* Description("Inverse of total basin area as sum of HRU areas")
     Unit("1/acres")
     */
    @VariableMeta(name = "basin_area_inv",
            dataType = DatatypeEnum.PoolDouble,
            description = "Inverse of total basin area as sum of HRU areas")
    public PoolDouble basin_area_inv = new PoolDouble();

    /*Description("Number of active HRUs")
     */
    @VariableMeta(name = "active_hrus",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of active HRUs")
    public PoolInteger active_hrus = new PoolInteger();

    /* Description("Number of active GWRs")
     */
    @VariableMeta(name = "active_gwrs",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of active  GWRs")
    public PoolInteger active_gwrs = new PoolInteger();

    /* Description("Routing order for HRUs")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_route_order",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Routing order for HRUs")
    public PoolIntegerArray hru_route_order = new PoolIntegerArray();

    /* Description("Routing order for ground-water reservoirs")
     Bound ("ngw")
     */
    @VariableMeta(name = "gwr_route_order",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Routing order for ground-water reservoirs")
    public PoolIntegerArray gwr_route_order = new PoolIntegerArray();

    /*Descrription("Area of each subsurface reservoir; computed by summing areas of HRUs that contribute to it")
     Unit("acres")
     Bound ("nssr")
     */
    @VariableMeta(name = "ssres_area",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Area of each subsurface reservoir; computed by summing areas of HRUs that contribute to it")
    public PoolDoubleArray ssres_area = new PoolDoubleArray();

    /*Description("Area of each groundwater reservoir. Computed by summing areas of HRUs that contribute to it")
     Unit("acres")
     Bound ("ngw")
     */
    @VariableMeta(name = "gwres_area",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Area of each groundwater reservoir. Computed by summing areas of HRUs that contribute to it")
    public PoolDoubleArray gwres_area = new PoolDoubleArray();

    /* Description("HRU depression storage area")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_dprst",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "HRU depression storage area")
    public PoolDoubleArray hru_dprst = new PoolDoubleArray();

    /* Description("HRU depression storage area defined by DEM")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "dem_dprst",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "HRU depression storage area defined by DEM")
    public PoolDoubleArray dem_dprst = new PoolDoubleArray();

    /* Description("HRU depression storage area that can flow to a stream")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "dprst_open",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "HRU depression storage area that can flow to a stream")
    public PoolDoubleArray dprst_open = new PoolDoubleArray();

    /* Description("HRU depression storage area that is closed and can  only spill")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "dprst_clos",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "HRU depression storage area that is closed and can  only spill")
    public PoolDoubleArray dprst_clos = new PoolDoubleArray();

    /* Description("Proportion of each HRU area that is impervious")
     Unit("decimal fraction")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_percent_impv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Proportion of each HRU area that is impervious")
    public PoolDoubleArray hru_percent_impv = new PoolDoubleArray();

    /* Description("Impervious area of each HRU")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_imperv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Impervious area of each HRU")
    public PoolDoubleArray hru_imperv = new PoolDoubleArray();

    /* Description("Proportion of each HRU area that is pervious")
     Unit("decimal fraction")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_percent_perv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Proportion of each HRU area that is perviou")
    public PoolDoubleArray hru_percent_perv =new PoolDoubleArray();

    /* Description("Pervious area of each HRU")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_perv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Pervious area of each HRU")
    public PoolDoubleArray hru_perv = new PoolDoubleArray();
    public double[] hru_percent_imperv_pri ;

    @Override
    public void init() throws Exception {
        double totarea = 0.0;
        this.land_area.setValue(0.0);
        double land_area_pri = this.land_area.getValue();
        this.water_area.setValue(0.0);
        double water_area_pri = this.water_area.getValue();

        //xml parameter
        int nhru_pri = this.nhru.getValue();
        int ngw_pri = this.ngw.getValue();
        int nssr_pri = this.nssr.getValue();
        double[] hru_percent_dprst_pri = this.hru_percent_dprst.getValue();
        double[] hru_area_pri = this.hru_area.getValue();
        int[] hru_type_pri = this.hru_type.getValue();
        double[] dprst_pct_open_pri = this.dprst_pct_open.getValue();
        int dprst_flag_pri = this.dprst_flag.getValue();
        double basin_area_pri = this.basin_area.getValue();
        int[] hru_ssres_pri = this.hru_ssres.getValue();
        int[] hru_gwres_pri = this.hru_gwres.getValue();

        /* need write hru_percent_imperv to xml??????????????????????????????????????*/
        //out
        double[] hru_imperv_pri = new double[nhru_pri];
        double[] hru_perv_pri = new double[nhru_pri];
        double[] hru_percent_perv_pri = new double[nhru_pri];
        double[] hru_percent_impv_pri = new double[nhru_pri];
        int[] hru_route_order_pri = new int[nhru_pri];
        double[] hru_dprst_pri = new double[nhru_pri];
        double[] dem_dprst_pri = new double[nhru_pri];
        double[] dprst_open_pri = new double[nhru_pri];
        double[] dprst_clos_pri = new double[nhru_pri];

        int[] gwr_route_order_pri = new int[ngw_pri];
        double[] gwres_area_pri = new double[ngw_pri];
        double[] ssres_area_pri = new double[nssr_pri];

        hru_percent_imperv_pri = this.hru_percent_imperv.getValue();

        int active_hrus_pri;
        int active_gwrs_pri;
        double basin_area_inv_pri;

        /**
         * ******************************************************
         */
        double numlakes = 0.;
        int j = -1;
        for (int i = 0; i < nhru_pri; i++) {
            if (dprst_flag_pri > 0) {
                dem_dprst_pri[i] = 0.0;
                dprst_open_pri[i] = 0.0;
                dprst_clos_pri[i] = 0.0;
            } else {
                hru_percent_dprst_pri[i] = 0.0;
            }
            double harea = hru_area_pri[i];
            if (hru_type_pri[i] != 0) {
                j = j + 1;
                hru_route_order_pri[j] = i;
                if (hru_type_pri[i] == 2) {
                    water_area_pri = water_area_pri + harea;
                    numlakes = numlakes + 1;
                } else {
                    check_imperv(i, hru_percent_imperv_pri[i], hru_percent_dprst_pri[i], dprst_flag_pri);
                    hru_imperv_pri[i] = hru_percent_imperv_pri[i] * harea;
                    hru_perv_pri[i] = harea - hru_imperv_pri[i];
                    hru_percent_perv_pri[i] = 1.0 - hru_percent_imperv_pri[i];
                    hru_percent_impv_pri[i] = hru_percent_imperv_pri[i];
                    land_area_pri = land_area_pri + harea;
                    // added for depression storage calulations:
                    if (dprst_flag_pri > 0) {
                        hru_dprst_pri[i] = hru_percent_dprst_pri[i] * harea;
                        dem_dprst_pri[i] = hru_dprst_pri[i];
                        dprst_open_pri[i] = hru_dprst_pri[i] * dprst_pct_open_pri[i];
                        dprst_clos_pri[i] = hru_dprst_pri[i] * (1.0 - dprst_pct_open_pri[i]);
                        if (dprst_open_pri[i] < NEARZERO) {
                            dprst_open_pri[i] = 0.0;
                        }
                        if (dprst_clos_pri[i] < NEARZERO) {
                            dprst_clos_pri[i] = 0.0;
                        }
                        hru_perv_pri[i] = hru_perv_pri[i] - hru_dprst_pri[i];
                        hru_percent_perv_pri[i] = hru_perv_pri[i] / harea;
                    }
                }
            }
            totarea = totarea + harea;
            if (nssr_pri == nhru_pri) {
                ssres_area_pri[i] = harea;
            }
            if (ngw_pri == nhru_pri) {
                gwres_area_pri[i] = harea;
            }
        }

        double diff = (totarea - basin_area_pri) / basin_area_pri;
        if (basin_area_pri > 0.0 && Math.abs(diff) > 0.01) {
            System.out.println("warning, basin_area > 1% different than sum of hru areas  "
                    + "basin_area: " + basin_area_pri + " sum of hru areas: "
                    + totarea + " percent diff: " + diff * 100.);
        }

        active_hrus_pri = j + 1;
        double active_area = land_area_pri + water_area_pri;

        if (nssr_pri != nhru_pri) {
            for (int i = 0; i < nssr_pri; i++) {
                ssres_area_pri[i] = 0.0;
            }
            for (int k = 0; k < active_hrus_pri; k++) {
                int i = hru_route_order_pri[k];
                j = hru_ssres_pri[i] - 1;
                // assume if hru_type is 2, ssr has zero area
                if (hru_type_pri[i] != 2) {
                    ssres_area_pri[j] += hru_area_pri[i];
                }
            }
        }
        if (ngw_pri == nhru_pri) {
            active_gwrs_pri = active_hrus_pri;
        } else {
            for (int i = 0; i < ngw_pri; i++) {
                gwr_route_order_pri[i] = i;
                gwres_area_pri[i] = 0.0;
            }
            active_gwrs_pri = ngw_pri;
            for (int k = 0; k < active_hrus_pri; k++) {
                int i = hru_route_order_pri[k];
                j = hru_gwres_pri[i] - 1;
                gwres_area_pri[j] += hru_area_pri[i];
            }
        }
        basin_area_inv_pri = 1.0 / active_area;
        /**
         * ******************************************************
         */
        //out
        this.hru_imperv.setValue(hru_imperv_pri);       
        this.hru_perv.setValue(hru_perv_pri);
        this.hru_percent_perv.setValue(hru_percent_perv_pri);
        this.hru_percent_impv.setValue(hru_percent_impv_pri);
        this.hru_route_order.setValue(hru_route_order_pri);
        this.hru_dprst.setValue(hru_dprst_pri);
        this.dem_dprst.setValue(dem_dprst_pri);
        this.dprst_open.setValue(dprst_open_pri);
        this.dprst_clos.setValue(dprst_clos_pri);
        this.gwr_route_order.setValue(gwr_route_order_pri);
        this.gwres_area.setValue(gwres_area_pri);
        this.ssres_area.setValue(ssres_area_pri);
        this.active_hrus.setValue(active_hrus_pri);
        this.active_gwrs.setValue(active_gwrs_pri);
        this.basin_area_inv.setValue(basin_area_inv_pri);
        this.water_area.setValue(water_area_pri);
        this.land_area.setValue(land_area_pri);

        // parameter output
        this.nhru.setValue(nhru_pri);
        this.ngw.setValue(ngw_pri);
        this.nssr.setValue(nssr_pri);
        this.hru_percent_dprst.setValue(hru_percent_dprst_pri);
        this.hru_area.setValue(hru_area_pri);
        this.hru_type.setValue(hru_type_pri);
        this.dprst_pct_open.setValue(dprst_pct_open_pri);
        this.dprst_flag.setValue(dprst_flag_pri);
        this.basin_area.setValue(basin_area_pri);
        this.hru_ssres.setValue(hru_ssres_pri);
        this.hru_gwres.setValue(hru_gwres_pri);
        this.hru_percent_imperv.setValue(hru_percent_imperv_pri);
    }

    @Override
    public void run() throws Exception {

    }

    @Override
    public void clear() throws Exception {

    }

    /**
     * ******************************************************
     */
    private void check_imperv(int ihru, double hru_pct_imperv,
            double hru_percent_dprst, int dprst_flg) {
        if (hru_pct_imperv > 0.99) {
            System.out.println("warning, hru_percent_imperv > .99 for hru: " + ihru
                    + " reset to .99, was: " + hru_pct_imperv);
            hru_percent_imperv_pri[ihru] = 0.99;
        }
        if (dprst_flg == 1) {
            if (hru_pct_imperv + hru_percent_dprst > .99) {
                System.out.println("warning, hru_percent_imperv+hru_percent_dprst>.99 "
                        + " hru_percent_imperv has been reduced to meet this "
                        + " condition. imperv: " + hru_pct_imperv + " dprst: "
                        + hru_percent_dprst + " hru: " + ihru);
                hru_percent_imperv_pri[ihru] = .99 - hru_percent_dprst;
            }
        }
        return;
    }
}
