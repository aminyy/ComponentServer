/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package XinAnJiang.model;

import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;

/**
 *
 * @author longyinping
 */
public class Runoff extends AbsComponent {

    public PoolDouble WMM;// 流域内最大蓄水容量
    public PoolDouble WM;// 流域平均蓄水容量（毫米）(WM=WUM+WLM+WDM)
    public PoolDouble Wum;// 流域内上层土壤蓄水容量，植被良好的流域，约为20mm,差的流域,2~10mm
    public PoolDouble Wlm;// 流域内下层土壤蓄水容量，可取60~90mm
    public PoolDouble B;	// 蓄水容量曲线的方次，小流域（几平方公里）B为0.1左右，中等面积（300平方公里以内）0.2~0.3，较大面积0.3~0.4 
    public PoolDouble FC;// 稳定入渗率，毫米/小时
    public PoolDouble IMP;// 不透水面积占全流域面积之比
    public PoolDouble PE;  // 大于零时为净雨量，小于零时为蒸发不足量，单位（毫米）
    public PoolDouble W;   // 流域内土壤湿度
    public PoolDouble WU;	// 流域内上层土壤湿度
    public PoolDouble WL;	// 流域内下层土壤适度
    public PoolDouble WD;	// 流域内深层土壤湿度
    public PoolDouble R;	// 产流深度，包括地表径流深度和地下径流深度两部分（毫米）
    public PoolDouble RG;	// 地下径流深度，单位（毫米）
    public PoolDouble RS;	// 地表径流深度，单位（毫米）

    @Override
    public void run() throws Exception {

        double m_WMM;// 流域内最大蓄水容量
        double m_WM;// 流域平均蓄水容量（毫米）(WM=WUM+WLM+WDM)
        double m_Wum;// 流域内上层土壤蓄水容量，植被良好的流域，约为20mm,差的流域,2~10mm
        double m_Wlm;// 流域内下层土壤蓄水容量，可取60~90mm
        double m_B;	// 蓄水容量曲线的方次，小流域（几平方公里）B为0.1左右，中等面积（300平方公里以内）0.2~0.3，较大面积0.3~0.4 
        double m_FC;// 稳定入渗率，毫米/小时
        double m_IMP;// 不透水面积占全流域面积之比
        double m_PE;  // 大于零时为净雨量，小于零时为蒸发不足量，单位（毫米）
        double m_W;   // 流域内土壤湿度
        double m_WU;	// 流域内上层土壤湿度
        double m_WL;	// 流域内下层土壤适度
        double m_WD;	// 流域内深层土壤湿度
        double m_R;	// 产流深度，包括地表径流深度和地下径流深度两部分（毫米）
        double m_RG;	// 地下径流深度，单位（毫米）
        double m_RS;	// 地表径流深度，单位（毫米）


        double A; // 当流域内的土壤湿度为W是,土壤含水量折算成的径流深度,单位（毫米）

        //parameters
        m_WMM = WMM.getValue();
        m_WM = WM.getValue();
        m_Wum = Wum.getValue();
        m_Wlm = Wlm.getValue();
        m_B = B.getValue();
        m_FC = FC.getValue();
        m_IMP = IMP.getValue();

        //variables
        m_PE = PE.getValue();
        m_W = W.getValue();
        m_WU = WU.getValue();
        m_WL = WL.getValue();
        m_WD = WD.getValue();



        if (m_PE < 0) {
            m_R = 0.0;		// 产流总量为零
            m_RG = 0.0;		// 地下径流量为零
            m_RS = 0.0;		// 地表径流量为零
        } else {
            // 计算流域当天土壤含水量折算成的径流深度Ａ
            // m_WM:流域平均蓄水容量(一个参数),
            // m_W:流域内土壤湿度(一个状态变量)
            // m_B:蓄水容量曲线的方次(一个参数)                     
            A = m_WMM * (1 - Math.pow((1.0 - m_W / m_WM), 1.0 / (1 + m_B)));
            // 土壤湿度折算净雨量加上降水后蒸发剩余雨量小于流域内最大含水容量
            if ((A + m_PE) < m_WMM) {
                // 流域内的产流深度计算
                m_R = m_PE /* 降水蒸发后的剩余量(PE=P-E:状态变量) */
                        + m_W /* 流域内土壤湿度 (W:状态变量)         */
                        + m_WM * Math.pow((1 - (m_PE + A) / m_WMM), (1 + m_B)) - m_WM;	/* 减去流域平均蓄水容量（m_WM:参数）   */
            } // 土壤湿度折算净雨量加上降水后蒸发剩余雨量大于流域内最大含水容量
            else {
                // 流域内的产流深度计算
                m_R = m_PE /* 降水蒸发后的剩余量(PE=P-E:状态变量) */
                        + m_W /* 流域内土壤湿度 (W:状态变量)         */
                        - m_WM;		/* 减去流域平均蓄水容量（m_WM:参数）   */
            }
            // 如果降水经过蒸散发后的剩余量大于等于土壤稳定入渗率//
            if (m_PE > m_FC) {
                // 计算地下径流的产流深度
                m_RG = (m_R - m_IMP * m_PE) * (m_FC / m_PE);
                // 计算地表径流的产流深度 
                m_RS = m_R - m_RG;
            } // 如果降水蒸发后的剩余量小于土壤的稳定入渗率(m_FC:参数)
            //除了不透水面积上的地表产流外，全部下渗，形成地下径流
            else {
                // 计算地下径流的产流深度
                m_RG = m_R - m_IMP * m_PE;
                // 计算地表径流的产流深度 
                m_RS = m_R - m_RG;
            }
            /**
             * ************* 以下代码负责土壤含水量的更新计算 *************
             */
            // 如果上层土壤含水量与降水蒸散发剩余量之和减去产流量之后
            // 大于上层土壤的蓄水能力
            if ((m_WU + m_PE - m_R) >= m_Wum) {
                // 上层含水量+下层含水量+降水剩余量-产流量-上层土壤蓄水需求
                // 后的水量大于下层土壤蓄水需求，多余水量分配到深层土壤
                if ((m_WU + m_WL + m_PE - m_R - m_Wum) > m_Wlm) {
                    m_WU = m_Wum;	/* 上层土壤含水量=上层土壤蓄水容量 */
                    m_WL = m_Wlm;	/* 下层土壤含水量=下层土壤蓄水容量 */
                    m_WD = m_W + m_PE - m_R - m_WU - m_WL;	/* 绝对降水剩余量补充到深层土壤中  */
                } // 上层含水量+下层含水量+降水剩余量-产流量-上层土壤蓄水需求
                // 后的水量小于下层土壤蓄水需求，剩余水量补充到下层土壤中
                else {
                    m_WL = m_WU + m_WL + m_PE - m_R - m_Wum;	/* 下层土壤含水量           */
                    m_WU = m_Wum;	/* 上层土壤蓄水容量得到满足 */
                }
            } // 如果上层土壤含水量与降水蒸散发剩余量之和减去产流量之后
            // 小于上层土壤的蓄水能力
            else {
                m_WU = m_WU + m_PE - m_R;
                // WU 可能小于零，应该加一些控制代码..........
            }
            /**
             * ************* 土壤含水量的更新计算结束 *************
             */
        }


        //SET VALUE
        WU.setValue(m_WU);
        WL.setValue(m_WL);
        WD.setValue(m_WD);
        W.setValue(m_WU + m_WL + m_WD);
        R.setValue(m_R);
        RS.setValue(m_RS);
        RG.setValue(m_RG);

    }

    @Override
    public void clear() throws Exception {
    }
}
