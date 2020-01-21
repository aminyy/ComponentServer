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
public class Evaporation extends AbsComponent {

    public PoolDouble PE;  // 大于零时为净雨量，小于零时为蒸发不足量，单位（毫米）
    public PoolDouble pP;// 降水数据
    public PoolDouble pEm;// 水面蒸发数据
    public PoolDouble Wum;// 流域内上层土壤蓄水容量，植被良好的流域，约为20mm,差的流域,2~10mm
    public PoolDouble Wlm;// 流域内下层土壤蓄水容量，可取60~90mm
    public PoolDouble Wdm;// 流域内深层土壤蓄水容量，WDM=WM-WUM-WLM 
    public PoolDouble K; // 流域蒸散发能力与实测蒸散发值的比
    public PoolDouble C;	// 流域内深层土壤蒸发系数，江南湿润地区：0.15-0.2，华北半湿润地区：0.09-0.12
    public PoolDouble E;   //蒸散发 
    public PoolDouble EU;  // 上层土壤蒸散发量（毫米）
    public PoolDouble EL;  // 下层土壤蒸散发量（毫米）
    public PoolDouble ED;  // 深层土壤蒸散发量（毫米）
    public PoolDouble W;   // 流域内土壤湿度
    public PoolDouble WU;	// 流域内上层土壤湿度
    public PoolDouble WL;	// 流域内下层土壤适度
    public PoolDouble WD;	// 流域内深层土壤湿度

    @Override
    public void init() {

        WU.setValue(Wum.getValue());
        WL.setValue(Wlm.getValue());
        WD.setValue(Wdm.getValue());
        W.setValue(WU.getValue() + WL.getValue() + WD.getValue());

        EU.setValue(0.0);
        EL.setValue(0.0);
        ED.setValue(0.0);


    }

    @Override
    public void run() throws Exception {
        double m_PE;  // 大于零时为净雨量，小于零时为蒸发不足量，单位（毫米）
        double m_pP;// 降水数据
        double m_pEm;// 水面蒸发数据
        double m_Wlm;// 流域内下层土壤蓄水容量，可取60~90mm
        double m_K; // 流域蒸散发能力与实测蒸散发值的比
        double m_C;	// 流域内深层土壤蒸发系数，江南湿润地区：0.15-0.2，华北半湿润地区：0.09-0.12
        double m_EU;  // 上层土壤蒸散发量（毫米）
        double m_EL;  // 下层土壤蒸散发量（毫米）
        double m_ED;  // 深层土壤蒸散发量（毫米）
        double m_W;   // 流域内土壤湿度
        double m_WU;	// 流域内上层土壤湿度
        double m_WL;	// 流域内下层土壤适度
        double m_WD;	// 流域内深层土壤湿度


        //climate
        m_pP = pP.getValue();
        m_pEm = pEm.getValue();
        //parameters
        m_Wlm = Wlm.getValue();
        m_K = K.getValue();
        m_C = C.getValue();
        //variables
        m_WU = WU.getValue();
        m_WL = WL.getValue();
        m_WD = WD.getValue();


        m_PE = m_pP - m_K * m_pEm;
        if (m_PE < 0) {
            if ((m_WU + m_PE) > 0.0) {
                // 上层土壤为流域蒸散发提供水量
                m_EU = m_K * m_pEm;
                // 没有降水量用于增加土壤湿度
                m_EL = 0.0;		/* 降水用来增加土壤湿度的部分 */
                // 
                m_ED = 0.0;
                // 更新上层土壤含水量
                m_WU = m_WU + m_PE;
            } else {
                m_EU = m_WU + m_pP;	// 上层土壤蒸发,降水全部用于蒸发
                m_WU = 0.0;		// 上层含水量为0，全部水分被蒸发
                // 如果下层含水量大于下层土壤的蒸散发潜力
                if (m_WL > (m_C * m_Wlm)) {
                    m_EL = (m_K * m_pEm - m_EU) * (m_WL / m_Wlm);
                    m_WL = m_WL - m_EL;
                    m_ED = 0;
                } // 如果下层土壤含水量小于下层土壤的蒸散发潜力
                else {
                    // 如果下层土壤的含水量蒸发之后还有剩余
                    if (m_WL > m_C * (m_K * m_pEm - m_EU)) {
                        m_EL = m_C * (m_K * m_pEm - m_EU);
                        m_WL = m_WL - m_EL;
                        m_ED = 0.0;
                    } // 如果下层土壤含水量全部蒸发之后尚不能满足蒸发需求
                    else {
                        m_EL = m_WL;	/* 下层土壤含水量全部用于蒸散发 */
                        m_WL = 0.0;	/* 下层土剩余壤含水量为0        */
                        m_ED = m_C * (m_K * m_pEm - m_EU) - m_EL;	/* 深层土壤含水量参与蒸发 */
                        m_WD = m_WD - m_ED;	/* 深层土壤含水量更新 */
                    }
                }
            }
        } else {
            m_EU = m_K * m_pEm;
            m_ED = 0.0;
            m_EL = 0.0;
        }

        //SET VALUE
        PE.setValue(m_PE);
        EU.setValue(m_EU);
        EL.setValue(m_EL);
        ED.setValue(m_ED);
        E.setValue(EU.getValue() + EL.getValue() + ED.getValue());
        WU.setValue(m_WU);
        WL.setValue(m_WL);
        WD.setValue(m_WD);
        W.setValue(WU.getValue() + WL.getValue() + WD.getValue());
    }

    @Override
    public void clear() throws Exception {
    }
}
