/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package XinAnJiang.model;

import java.util.List;
import net.casnw.home.io.DataReader;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolString;

/**
 *
 * @author longyinping
 */
public class Parameter extends AbsComponent {

    public PoolString paraFile;
    public PoolDouble K;
    public PoolDouble IMP;
    public PoolDouble B;
    public PoolDouble WMM;
    public PoolDouble WM;
    public PoolDouble Wum;
    public PoolDouble Wlm;
    public PoolDouble Wdm;
    public PoolDouble C;
    public PoolDouble FC;
    public PoolDouble KKG;
    public PoolDouble Kstor;
    DataReader para;

    @Override
    public void init() throws Exception {
        para = new DataReader(paraFile.getValue());
        List<Double> paras;
        if (para.hasNext()) {
            paras = para.getNext();

            K.setValue(paras.get(0));
            IMP.setValue(paras.get(1));
            B.setValue(paras.get(2));
            Wum.setValue(paras.get(3));
            Wlm.setValue(paras.get(4));
            Wdm.setValue(paras.get(5));
            C.setValue(paras.get(6));
            FC.setValue(paras.get(7));
            KKG.setValue(paras.get(8));
            Kstor.setValue(paras.get(9));

            WM.setValue(Wum.getValue() + Wlm.getValue() + Wdm.getValue());
            WMM.setValue(WM.getValue() * (1.0 + B.getValue()) / (1.0 - IMP.getValue()));

        }

    }
}
