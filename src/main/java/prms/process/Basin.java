package prms.process;

import net.casnw.home.meta.DatatypeEnum;
import net.casnw.home.meta.ModuleMeta;
import net.casnw.home.meta.TimescaleEnum;
import net.casnw.home.meta.VariableMeta;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolIntegerArray;


/*
 ("Basin setup." +
 "Check for validity of basin parameters and compute reservoir areas.")
 */
@ModuleMeta(moduleClass = "prms.process.Basin",
        name = "Basin",
        author = "hzfwhy@lzb.ac.cn",
        keyword = "basin parameters",
        description = "Basin setup Check for validity of basin parameters and compute reservoir areas.",
        timeScale = TimescaleEnum.Hour)

public class Basin extends AbsComponent {

    // Input Params
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

    // Output vars
    /*Description("Number of active HRUs")
     */
    @VariableMeta(name = "active_hrus",
            dataType = DatatypeEnum.PoolInteger,
            description = "Number of active HRUs")
    public PoolInteger active_hrus = new PoolInteger();

    /* Description("Routing order for HRUs")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_route_order",
            dataType = DatatypeEnum.PoolIntegerArray,
            description = "Routing order for HRUs")
    public PoolIntegerArray hru_route_order = new PoolIntegerArray();


    /* Description("HRU depression storage area")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_dprst",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "HRU depression storage area")
    public PoolDoubleArray hru_dprst = new PoolDoubleArray();

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
            description = "Proportion of each HRU area that is pervious")
    public PoolDoubleArray hru_percent_perv = new PoolDoubleArray();

    /* Description("Pervious area of each HRU")
     Unit("acres")
     Bound ("nhru")
     */
    @VariableMeta(name = "hru_perv",
            dataType = DatatypeEnum.PoolDoubleArray,
            description = "Pervious area of each HRU")
    public PoolDoubleArray hru_perv = new PoolDoubleArray();
    public double[] hru_percent_imperv_pri;
    public int k;


    @Override
    public void init() throws Exception {

        k = -1;
        hru_percent_imperv_pri = this.hru_percent_imperv.getValue();
    }

    @Override
    public void run() throws Exception {

       // int spatial_flag = hru_flag.getValue();
        int[] hru_route_order_pri = this.hru_route_order.getValue();
        int[] hru_type_pri = this.hru_type.getValue();
        int dprst_flag_pri = this.dprst_flag.getValue();
        double[] hru_percent_dprst_pri = this.hru_percent_dprst.getValue();
        double[] hru_area_pri = this.hru_area.getValue();

        //out
        double[] hru_imperv_pri = this.hru_imperv.getValue();
        double[] hru_perv_pri = this.hru_perv.getValue();
        double[] hru_dprst_pri = this.hru_dprst.getValue();
        double[] hru_percent_perv_pri = this.hru_percent_perv.getValue();
        double[] hru_percent_impv_pri = this.hru_percent_impv.getValue();

        /**
         * ***********************************************************************
         */
        k++;
        

        for (int k = 0; k < this.active_hrus.getValue(); k++) {
        	int i = hru_route_order_pri[k];
     //   if (k < this.active_hrus.getValue()) {
            if (hru_type_pri[i] != 2) {
                check_imperv(i, hru_percent_imperv_pri[i], hru_percent_dprst_pri[i], dprst_flag_pri);
                hru_imperv_pri[i] = hru_percent_imperv_pri[i] * hru_area_pri[i];
                hru_perv_pri[i] = hru_area_pri[i] - hru_imperv_pri[i] - hru_dprst_pri[i];
                hru_percent_perv_pri[i] = 1.0 - hru_percent_imperv_pri[i];
                hru_percent_impv_pri[i] = hru_percent_imperv_pri[i];
            }
            //hru_percent_imperv_pri、hru_area_pri这个地方数组长度不对46？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
            //           if (k+1== this.active_hrus.getValue()){

            this.hru_imperv.setValue(i, hru_imperv_pri[i]);
            this.hru_perv.setValue(i, hru_perv_pri[i]);
            this.hru_dprst.setValue(i, hru_dprst_pri[i]);
            this.hru_percent_perv.setValue(i, hru_percent_perv_pri[i]);
            this.hru_percent_impv.setValue(i, hru_percent_impv_pri[i]);

            //          }
        }
       

    }

    @Override
    public void clear() throws Exception {

    }

    /**
     * ***********************************************************************
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
