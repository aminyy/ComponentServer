/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.Modules1;


/**
 *
 * @author longyinping
 */
public class PotentialEvaporation {

    public int Jd;//julian day number =day
    public double prec;//rainfall(mm)
    public double Tmin;
    public double Tmax;
    public double Tmean;//mean air temperature(degree)
    public double RH;//relative humidity,units 0.###
    public double sunshine;//sunshine time (hour)
    // public double cloud;//cloud cover [0.**]
    public double u;//u(z)wind speed (m/s)
    public double z;//height of location
    public String landuse;
    public double lat;//latitude
    public double Ep;// daily potential E (mm/day)

    public void init() {
    }

    public void run() {

        double R_n;//Rn  net radiation (mm/day)
        double alpha = 0;
        double sigma;//Stefan-Boltzmann constant
        double G;//heat flux into soil or snow layer
        double lamta;//latent heat of vaporization of water
        double phi;//latitude of site (positive for Nothern hemisphere) (radian)
        double pi = 3.1415926;
        double t_dew;//dew temperature(degree)
        double delta_s;//solar declination (radian)   
        double omega_s;//sunset hour angle (radian)
        double NN;//total day length(hr)
        double d_r;//dr  relative distance between the earth and the sun
        double s_0;//S0  extraterrestrial radiation (mm/day)
        double cloud_f;//n/N cloudiness_function (h/h)
        double e_d;//ed  vapor pressure at dew tempreture	
        double L_n;//long-wave radiation
        double delta;//slope of the curve of satuated vapor pressure at Ta (kPa/degC)
        double p0;//atmospheric pressure (kPa)
        double gamma;//psychrometric constant (kPa/degC)
        double gammas;//psychrometric constant (kPa/degC)
        double D;//D   vapor pressure deficit (kPa)
        double u_2;//wind speed at 2-m
        double Ah, e_smax, e_smin, e_s, as, bs, ac, bc, ae, be, S_t, S_n, St0, f, epocilon, p_s, e_sdew;
        switch (landuse) {
            case "waterbody":
                alpha = 0.08;
                break;
            case "urban-area":
                alpha = 0.23;
                break;
            case "baresoil":
                alpha = 0.20;
                break;
            case "forest":
                alpha = 0.13;
                break;
            case "irrigated-cropland":
                alpha = 0.23;
                break;
            case "non-irrigated cropland":
                alpha = 0.23;
                break;
            case "grassland":
                alpha = 0.23;
                break;
            case "shrub":
                alpha = 0.23;
                break;
            case "caodian":
                alpha = 0.23;
                break;
            case "Tundra-snowice":
                alpha = 0.6;
                break;
        }

        sigma = 4.903E-9;
        G = 0;
        Ah = 4.19 * Math.pow(10, -3) * prec * Tmean;
        lamta = 2.501 - 0.00236 * Tmean;
        if (Tmean < 0.0) {
            lamta = lamta + 0.334;
        }
        phi = lat * pi / 180.0;
        e_smax = 0.6108 * Math.exp(17.27 * Tmax / (237.3 + Tmax));
        if (Tmax < 0.0) {
            e_smax = 0.6108 * Math.exp(21.88 * Tmax / (265.5 + Tmax));
        }
        e_smin = 0.6108 * Math.exp(17.27 * Tmin / (237.3 + Tmin));
        if (Tmin < 0.0) {
            e_smin = 0.6108 * Math.exp(21.88 * Tmin / (265.5 + Tmin));
        }
        e_s = (e_smax + e_smin) / 2.0;

        if (RH > 0) {
            if (e_s > 0.6108) {	// esat(t=0 degree c)=6.11
                t_dew = 237.3 * Math.log10(RH * e_s / 0.6108) / (7.5 - Math.log10(RH * e_s / 0.6108));
                // Reverse form of Tetens    	// formula for water surface
            } else {
                t_dew = 265.3 * Math.log10(RH * e_s / 0.6108) / (9.5 - Math.log10(RH * e_s / 0.6108));
                // Reverse form of Tetens    	// formula for ice surface
            }
        } else {
            t_dew = Tmin - 2.0;  //only for arid zone
        }

        //calculate net radiation Rn
        delta_s = 0.4093 * Math.sin(2.0 * pi * Jd / 365.0 - 1.405);
        omega_s = Math.acos(-Math.tan(phi) * Math.tan(delta_s));
        NN = 24.0 * omega_s / pi;
        d_r = 1.0 + 0.033 * Math.cos(2.0 * pi * Jd / 365.0);
        s_0 = 15.392 * d_r * (omega_s * Math.sin(phi) * Math.sin(delta_s)
                + Math.cos(phi) * Math.cos(delta_s) * Math.sin(omega_s));      // mm/day
        if (sunshine > NN) {
            System.out.println(this.getClass().getName() + " wrong in calculating NN");
            sunshine = NN;
        }
        as = 0.25;
        bs = 0.5;
        ac = 1.35;
        bc = -0.35;
        ae = 0.34;
        be = -0.14;
        /*
         if (sunshine == -9999) {
         if (cloud < 0) {
         System.out.println(this.getClass().getName() + "wrong in data cloud and sunshine");
         cloud = 0.5;
         }
         cloud_f = 1 - cloud;
         } else {
         cloud_f = sunshine / NN;
         }*/
        cloud_f = sunshine / NN;
        S_t = (as + bs * cloud_f) * s_0;
        S_n = S_t * (1.0 - alpha);
        //estimating long-wave radiation Ln
        St0 = (as + bs) * s_0;
        f = ac * (S_t / St0) + bc;
        e_d = 0.6108 * Math.exp(17.27 * t_dew / (237.3 + t_dew));
        epocilon = ae + be * Math.sqrt(e_d);
        L_n = -f * epocilon * sigma * Math.pow(Tmean + 273.16, 4);
        L_n = L_n / lamta;//in mm/day
        //total net radiation
        R_n = S_n + L_n;
        if (R_n < 0.0) {
            R_n = 0.0;
        }
        //end of calculate net radiation Rn

        e_s = 0.6108 * Math.exp(17.27 * Tmean / (237.3 + Tmean));
        delta = 4098 * e_s / Math.pow(237.3 + Tmean, 2);
        if (Tmean < 0.0) {
            e_s = 0.6108 * Math.exp(21.88 * Tmean / (265.5 + Tmean));
            delta = 5809 * e_s / Math.pow(265.5 + Tmean, 2);
        }
        p0 = 101.325;
        p_s = p0 * Math.pow((293 - 0.0065 * z) / 293, 5.256);
        gamma = 0.0016286 * p_s / lamta;
        e_sdew = 0.6108 * Math.exp(17.27 * t_dew / (237.3 + t_dew));
        if ((e_smax + e_smin) / 2.0 < 0.6108) {
            e_sdew = 0.6108 * Math.exp(21.88 * t_dew / (265.5 + t_dew));
        }
        if (RH <= 0.0) {
            D = (e_smax + e_smin) / 2.0 - e_sdew;
        } else {
            D = (e_smax + e_smin) / 2.0 * (1.0 - RH);
        }
        if (u > 0) {
            u_2 = 0.749 * u;
        } else {
            u_2 = 0.0;
        }
        gammas = gamma * (1 + 0.33 * u_2);
        if (landuse.equals("waterbody")) {
            Ep = delta / (delta + gamma) * (R_n + Ah)
                    + gamma / (delta + gamma) * 6.43 * (1 + 0.536 * u_2) * D / lamta;
        } else {
            Ep = delta / (delta + gammas) * (R_n - G)
                    + gamma / (delta + gammas) * 900.0 * u_2 * D / (Tmean + 275);
        }
        if (Ep < 0.0) {
            System.out.println(this.getClass().getName() + "wrong in Ep, " + Ep);
        }


    }

    public void clear() {
    }
}
