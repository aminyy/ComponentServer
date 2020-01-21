/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.IO;

import GBHM.Modules1.PotentialEvaporation;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import net.casnw.home.model.AbsComponent;
import net.casnw.home.model.Contextable;
import net.casnw.home.model.TemporalContext;
import net.casnw.home.poolData.PoolDate;
import net.casnw.home.poolData.PoolObjectArray;
import net.casnw.home.poolData.PoolString;
import GBHM.Utils.ClimateReader;
import GBHM.Utils.ClimateRecord;
import GBHM.Utils.ObjectUtil;
import GBHM.Utils.Point;

/**
 *
 * @author longyinping
 */
public class ClimateInput extends AbsComponent {

    //In   
    public PoolString rainFile;
    public PoolString meanTempFile;
    public PoolString maxTempFile;
    public PoolString minTempFile;
    public PoolString humidityFile;
    public PoolString sunshineFile;
    public PoolString windFile;
    //end of In
    //Out
    public PoolObjectArray precPoints;  
    public PoolObjectArray tmeanPoints;
    public PoolObjectArray tmaxPoints;
    public PoolObjectArray tminPoints;
    public PoolObjectArray EpPoints;
    public PoolDate currentDate;
    //end of Out
    private ClimateReader precReader;
    private ClimateReader tminReader;
    private ClimateReader tmaxReader;
    private ClimateReader tmeanReader;
    private ClimateReader rhReader;
    private ClimateReader sunReader;
    private ClimateReader windReader;
    private TemporalContext temporalContext;

    @Override
    public void init() throws IOException {
        precReader = new ClimateReader(rainFile.getValue());
        tminReader = new ClimateReader(minTempFile.getValue());
        tmaxReader = new ClimateReader(maxTempFile.getValue());
        tmeanReader = new ClimateReader(meanTempFile.getValue());
        rhReader = new ClimateReader(humidityFile.getValue());
        sunReader = new ClimateReader(sunshineFile.getValue());
        windReader = new ClimateReader(windFile.getValue());
        Contextable con = this.getContext();
        temporalContext = (TemporalContext) (con.getContext());       
    }

    @Override
    public void run() throws IOException {
        Date currentTime;
        Date Date;

        Point[] precPts;
        Point[] tmeanPts;
        Point[] EpPts;


        EpPts = null;
        Point[] tminPts;
        Point[] tmaxPts;
        Point[] rhPts;
        Point[] sunPts;
        Point[] windPts;

        ClimateRecord precRecord;
        ClimateRecord tmeanRecord;
        ClimateRecord tmaxRecord;
        ClimateRecord tminRecord;
        ClimateRecord rhRecord;
        ClimateRecord sunRecord;
        ClimateRecord windRecord;


        //要求各气象要素文件中，记录的起止时间相等

        currentTime = temporalContext.getCurrentTime().getTime();
        //currentTime.setDate(currentTime.getDate() - 1);

        do {
            precReader.setNext();
            precRecord = precReader.getClimateRecord();
            Date = precRecord.getDATE();
        } while (Date.before(currentTime));
        precPts = this.getPoints(precReader, precRecord);

        do {
            tmeanReader.setNext();
            tmeanRecord = tmeanReader.getClimateRecord();
            Date = tmeanRecord.getDATE();
        } while (Date.compareTo(currentTime) < 0);
        tmeanPts = this.getPoints(tmeanReader, tmeanRecord);

        do {
            tmaxReader.setNext();
            tmaxRecord = tmaxReader.getClimateRecord();
            Date = tmaxRecord.getDATE();
        } while (Date.compareTo(currentTime) < 0);
        tmaxPts = this.getPoints(tmaxReader, tmaxRecord);

        do {
            tminReader.setNext();
            tminRecord = tminReader.getClimateRecord();
            Date = tminRecord.getDATE();
        } while (Date.compareTo(currentTime) < 0);
        tminPts = this.getPoints(tminReader, tminRecord);

        do {
            rhReader.setNext();
            rhRecord = rhReader.getClimateRecord();
            Date = rhRecord.getDATE();
        } while (Date.compareTo(currentTime) < 0);
        rhPts = this.getPoints(rhReader, rhRecord);

        do {
            sunReader.setNext();
            sunRecord = sunReader.getClimateRecord();
            Date = sunRecord.getDATE();
        } while (Date.compareTo(currentTime) < 0);
        sunPts = this.getPoints(sunReader, sunRecord);

        do {
            windReader.setNext();
            windRecord = windReader.getClimateRecord();
            Date = windRecord.getDATE();
        } while (Date.compareTo(currentTime) < 0);
        windPts = this.getPoints(windReader, windRecord);


        if (tmeanPts != null && tmaxPts != null && tminPts != null && rhPts != null && sunPts != null && windPts != null) {
            EpPts = calEp(Date, tmeanPts, tmaxPts, tminPts, rhPts, sunPts, windPts);
        }

        this.precPoints.setValue(ObjectUtil.toObject(precPts)); 
        this.tmeanPoints.setValue(ObjectUtil.toObject(tmeanPts));
        this.tmaxPoints.setValue(ObjectUtil.toObject(tmaxPts));
        this.tminPoints.setValue(ObjectUtil.toObject(tminPts));
        this.EpPoints.setValue(ObjectUtil.toObject(EpPts));
        this.currentDate.setValue(currentTime);
        

    }

    @Override
    public void clear() {
    }

    private Point[] calEp(Date date, Point[] tmeanPts, Point[] tmaxPts, Point[] tminPts, Point[] rhPts, Point[] sunPts, Point[] windPts) {
        int day;
        double lat;
        double elev;
        double tmean, tmax, tmin, rh, sun, wind;
        Point[] EpPts1;
        ArrayList<Point> pts = new ArrayList();
        Point tmeanPt, tmaxPt, tminPt, rhPt, sunPt, windPt;

        SimpleDateFormat format = new SimpleDateFormat("DDD");
        day = Integer.parseInt(format.format(date));

        for (int i = 0; i < tmeanPts.length; i++) {
            tmeanPt = tmeanPts[i];
            lat = tmeanPt.LAT;
            elev = tmeanPt.ELVE;
            tmaxPt = this.queryPoint(tmeanPt, tmaxPts);
            tminPt = this.queryPoint(tmeanPt, tminPts);
            rhPt = this.queryPoint(tmeanPt, rhPts);
            sunPt = this.queryPoint(tmeanPt, sunPts);
            windPt = this.queryPoint(tmeanPt, windPts);
            if (tmaxPt != null && tminPt != null && rhPt != null && sunPt != null && windPt != null) {
                tmean = tmeanPt.value;
                tmax = tmaxPt.value;
                tmin = tminPt.value;
                rh = rhPt.value;
                sun = sunPt.value;
                wind = windPt.value;
                PotentialEvaporation evap = new PotentialEvaporation();
                evap.Jd = day;
                evap.Tmean = tmean;
                evap.Tmax = tmax;
                evap.Tmin = tmin;
                evap.RH = rh;
                evap.sunshine = sun;
                evap.u = wind;
                evap.lat = lat;
                evap.z = elev;
                evap.prec = 0;
                evap.landuse = "grassland";
                evap.init();
                evap.run();
                Point pt = new Point();
                pt.X = tmeanPt.X;
                pt.Y = tmeanPt.Y;
                pt.ELVE = tmeanPt.ELVE;
                pt.LAT = tmeanPt.LAT;
                pt.LON = tmeanPt.LON;
                pt.value = evap.Ep;

                pts.add(pt);
            }
        }

        EpPts1 = new Point[pts.size()];
        for (int i = 0; i < pts.size(); i++) {
            EpPts1[i] = pts.get(i);
        }
        return EpPts1;
    }

    private Point queryPoint(Point pt, Point[] pts) {
        double lon, lat;
        Point pt1 = null;
        lon = pt.LON;
        lat = pt.LAT;
        for (int i = 0; i < pts.length; i++) {
            if (pts[i].LON == lon && pts[i].LAT == lat) {
                pt1 = pts[i];
                break;
            }
        }
        return pt1;
    }

    private Point[] getPoints(ClimateReader reader, ClimateRecord record) {
        ArrayList<Point> pts;
        Point pt;
        Point[] pts1;
        int nstation = reader.getNSTATION();
        double nodata = reader.getNODATA();
        double[] x = reader.getX();
        double[] y = reader.getY();
        double[] elev = reader.getELEV();
        double[] lon = reader.getLONGITUDE();
        double[] lat = reader.getLATITUDE();
        double[] value = record.getVALUE();

        pts = new ArrayList();
        for (int i = 0; i < nstation; i++) {
            if (value[i] != nodata) {
                pt = new Point();
                pt.X = x[i];
                pt.Y = y[i];
                pt.ELVE = elev[i];
                pt.LON = lon[i];
                pt.LAT = lat[i];
                pt.value = value[i];
                pts.add(pt);
            }
        }

        pts1 = new Point[pts.size()];
        for (int i = 0; i < pts1.length; i++) {
            pts1[i] = pts.get(i);
        }

        return pts1;
    }
}
