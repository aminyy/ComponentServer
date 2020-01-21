/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.Utils;

import java.util.Date;

/**
 *
 * @author longyinping
 */
public class TemporalDownscale {

    public static double[] RainTemporalDownscale(Date date, double prec_day) {
        double tmp, rt1;
        int idh;
        int im = date.getMonth() + 1;
        double[] prec_hour = new double[24];
        //create the start time
        tmp = Math.random();
        //downscaling Prec
        rt1 = 4 + (int) ((21 - 4) * tmp + 0.45);
        if (rt1 > 21) {
            rt1 = 21;
        }
        for (idh = 0; idh < 24; idh++) {
            if (im <= 5) {
                prec_hour[idh] = 0;
                if (prec_day >= 0.0 && prec_day <= 8.0) {
                    if (idh == rt1) {
                        prec_hour[idh] = prec_day;
                    }
                } else if (prec_day > 8.0 && prec_day <= 15.0) {
                    if (idh == rt1 - 1) {
                        prec_hour[idh] = 0.1 * prec_day;
                    }
                    if (idh == rt1) {
                        prec_hour[idh] = 0.8 * prec_day;
                    }
                    if (idh == rt1 + 1) {
                        prec_hour[idh] = 0.1 * prec_day;
                    }
                } else if (prec_day > 15.0 && prec_day <= 25.0) {
                    if (idh == rt1 - 2) {
                        prec_hour[idh] = 0.1 * prec_day;
                    }
                    if (idh == rt1 - 1) {
                        prec_hour[idh] = 0.2 * prec_day;
                    }
                    if (idh == rt1) {
                        prec_hour[idh] = 0.4 * prec_day;
                    }
                    if (idh == rt1 + 1) {
                        prec_hour[idh] = 0.2 * prec_day;
                    }
                    if (idh == rt1 + 2) {
                        prec_hour[idh] = 0.1 * prec_day;
                    }
                } else if (prec_day > 25.0 && prec_day <= 50.0) {
                    if (idh == rt1 - 4) {
                        prec_hour[idh] = 0.05 * prec_day;
                    }
                    if (idh == rt1 - 3) {
                        prec_hour[idh] = 0.1 * prec_day;
                    }
                    if (idh == rt1 - 2) {
                        prec_hour[idh] = 0.1 * prec_day;
                    }
                    if (idh == rt1 - 1) {
                        prec_hour[idh] = 0.15 * prec_day;
                    }
                    if (idh == rt1) {
                        prec_hour[idh] = 0.2 * prec_day;
                    }
                    if (idh == rt1 + 1) {
                        prec_hour[idh] = 0.15 * prec_day;
                    }
                    if (idh == rt1 + 2) {
                        prec_hour[idh] = 0.1 * prec_day;
                    }
                    if (idh == rt1 + 3) {
                        prec_hour[idh] = 0.1 * prec_day;
                    }
                    if (idh == rt1 + 4) {
                        prec_hour[idh] = 0.05 * prec_day;
                    }
                } else if (prec_day > 50.0) {
                    prec_hour[idh] = prec_day / 24.0;
                }
            } else {
                prec_hour[idh] = 0.0;
                if (prec_day >= 0.0 && prec_day <= 5.0) {
                    if (idh == rt1) {
                        prec_hour[idh] = prec_day;
                    }
                } else if (prec_day > 5.0 && prec_day < 10.0) {
                    if (idh == rt1 - 1) {
                        prec_hour[idh] = 0.3 * prec_day;
                    }
                    if (idh == rt1) {
                        prec_hour[idh] = 0.4 * prec_day;
                    }
                    if (idh == rt1 + 1) {
                        prec_hour[idh] = 0.3 * prec_day;
                    }
                } else if (prec_day > 10.0 && prec_day <= 30.0) {
                    if (idh == rt1 - 3) {
                        prec_hour[idh] = 0.10 * prec_day;
                    }
                    if (idh == rt1 - 2) {
                        prec_hour[idh] = 0.15 * prec_day;
                    }
                    if (idh == rt1 - 1) {
                        prec_hour[idh] = 0.15 * prec_day;
                    }
                    if (idh == rt1) {
                        prec_hour[idh] = 0.20 * prec_day;
                    }
                    if (idh == rt1 + 1) {
                        prec_hour[idh] = 0.15 * prec_day;
                    }
                    if (idh == rt1 + 2) {
                        prec_hour[idh] = 0.15 * prec_day;
                    }
                    if (idh == rt1 + 3) {
                        prec_hour[idh] = 0.1 * prec_day;
                    }
                } else if (prec_day > 30.0) {
                    prec_hour[idh] = prec_day / 24.0;
                }
            }
        }
        return prec_hour;
    }

    public static double[] PotentialEvapTemporalDownscale(double ep_day) {
        int idh;
        double[] ep_hour = new double[24];
        for (idh = 0; idh < 24; idh++) {
            ep_hour[idh] = 0.0;
            if (idh > 6 && idh <= 18) {
                ep_hour[idh] = ep_day / 12.0;
            }
        }
        return ep_hour;
    }

    public static double[] TemperatureTemporalDownscale(double tmax_daily, double tmin_daily) {
        int idh, tmd;
        double[] T = new double[24];
        for (idh = 0; idh < 24; idh++) {
            if (idh >= 4 && idh < 14) {
                T[idh] = tmin_daily + (tmax_daily - tmin_daily)
                        * (1 - Math.cos((idh - 4) * 3.1415926 / 10.0)) / 2.0;
            } else {
                tmd = idh;
                if (idh < 4) {
                    tmd = 24 + idh;
                }
                T[idh] = tmin_daily + (tmax_daily - tmin_daily)
                        * (1 + Math.cos((tmd - 14) * 3.1415926 / 14.0)) / 2.0;
            }
        }
        return T;
    }
}
