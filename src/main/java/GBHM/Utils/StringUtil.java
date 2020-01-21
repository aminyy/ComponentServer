/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.Utils;

/**
 *
 * @author longyinping
 */
public class StringUtil {

    public static String delSpace(String str) {

        if (str == null) {
            return null;
        }
        String regStartSpace = "^[　 ]*";
        String regEndSpace = "[　 ]*$";

        String strDelSpace = str.replaceAll(regStartSpace, "").replaceAll(
                regEndSpace, "");
        return strDelSpace;
    }
}
