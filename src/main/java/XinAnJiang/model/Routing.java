/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package XinAnJiang.model;

import net.casnw.home.io.DataWriter;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.model.TemporalContext;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author longyinping
 */
public class Routing extends AbsComponent {

//    public PoolInteger count;
    public PoolString dischargeFile;
    public PoolInteger num;
    public PoolDouble Kstor;
    public PoolDouble KKG;
    public PoolDouble RS;
    public PoolDouble RG;
    int count;
    double m_pRs[];		// 每一步长的基流径流深(毫米) 
    double m_pRg[];		// 每一步长的地表径流深(毫米) 
    double m_pQrg[];		// 流域出口地下径流量
    double m_pQrs[];		// 流域出口地表径流量
    double m_pQ[];		// 流域出口的总流量
    DataWriter flowWriter;

    @Override
    public void init() throws Exception {
        TemporalContext T;
        T = (TemporalContext) this.getContext();
        count = (int) T.iteratorNum.getValue();
        m_pRs = new double[count];
        m_pRg = new double[count];
        m_pQrg = new double[count];
        m_pQrs = new double[count];
        m_pQ = new double[count];
        flowWriter = new DataWriter(dischargeFile.getValue());
        flowWriter.writeLine("Q, Qrs, Qrg");

    }

    @Override
    public void run() throws Exception {

        int m_nSteps;// 模型要运行的步长
        double K;// 汇流参数
        double m_KKG;			// 地下径流消退系数
        double m_U;			// for 24h. U=A(km^2)/3.6/delta_t


        double UH[];	// 单位线,假定最长的汇流时间为100天
        double sum;
        int N = 0;		// 汇流天数 
        int i, j;

        if (num.getValue() < count) {
            m_pRs[num.getValue()] = RS.getValue();
            m_pRg[num.getValue()] = RG.getValue();
        }
        if (num.getValue() + 1 == count) {
            m_nSteps = count;
            K = Kstor.getValue();
            m_KKG = KKG.getValue();
            m_U = 1.0;

            //************************************************************************
            //routing algorithm
            //************************************************************************
            UH = new double[100];
            for (i = 0; i < 100; i++) {
                UH[i] = (1.0 / K) * Math.exp((-1.0 * i) / K);
            }
            UH[0] = (UH[1] + UH[2]) * 0.5;
            sum = 0.0;
            for (i = 0; i < 100; i++) {
                sum += UH[i];
                if (sum > 1.0) {
                    UH[i] = 1.0 - (sum - UH[i]);
                    N = i;
                    break;
                }
            }
            // 单位线汇流计算
            for (i = 0; i < m_nSteps; i++) {
                m_pQrs[i] = 0.0;
                for (j = 0; j <= N; j++) {
                    if ((i - j) < 0) {
                        continue;
                    }
                    m_pQrs[i] += m_pRs[i - j] * UH[j] * m_U;
                }
            }
            //地下水汇流计算
            m_pQrg[0] = 0.0;
            for (i = 1; i < m_nSteps; i++) {
                m_pQrg[i] = m_pQrg[i - 1] * m_KKG + m_pRg[i] * (1.0
                        - m_KKG) * m_U;
            }
            for (i = 0; i < m_nSteps; i++) {
                m_pQ[i] = m_pQrs[i] + m_pQrg[i];
            }
            //************************************************************************
            //end of routing algorithm
            //************************************************************************

            //write outputs
            for (i = 0; i < m_nSteps; i++) {
                flowWriter.writeLine(String.format("%1$16.3f  %2$16.3f  %3$16.3f",
                        m_pQ[i], m_pQrs[i], m_pQrg[i]));
            }
        }


    }

    @Override
    public void clear() throws Exception {
        flowWriter.close();
    }
}