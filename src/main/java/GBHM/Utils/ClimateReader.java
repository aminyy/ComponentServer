/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author longyinping
 */
public class ClimateReader {

    //一个数据文件只存一个气象要素，但可以包含多个气象站点的值
    //格式为：
    //第一行：NAME 气象要素名称
    //第二行：UNIT 要素单位
    //第三行：NODATA 无值
    //第四行：NSTATION 站点数
    //第五行：STATION_NAME 站点名称
    //第六行：LONGITUDE
    //第七行：LATITUDE
    //第八行：X
    //第九行：Y
    //第十行：ELEV  
    //日期，站点1，站点2，…，日期暂不使用
    String NAME;
    String UNIT;
    double NODATA;
    int NSTATION;
    String[] STATION_NAME;
    double[] LON;
    double[] LAT;
    double[] X;
    double[] Y;
    double[] ELEV;
    FileReader fin;
    BufferedReader bin;
    boolean hasnext;
    String line;

    public ClimateReader(String fileName) throws IOException {
        fin = new FileReader(fileName);
        bin = new BufferedReader(fin);
        this.readHeader();       
    }

    /*    public double[] getNext() throws IOException {
     double[] record;
     String[] split;
     record = null;
     if (hasnext) {
     split = line.split(" +");
     record = new double[split.length - 1];
     for (int i = 0; i < record.length; i++) {
     record[i] = Double.parseDouble(split[i + 1]);
     }
     }
     return record;
     }*/
    public ClimateRecord getClimateRecord() throws IOException {
        ClimateRecord record = new ClimateRecord();
        Date date;
        double[] value;
        String[] split;
        if (hasnext) {            
            split = line.split(" +|, +");
            date = this.parseDATE(split[0]);
            value = new double[split.length - 1];
            for (int i = 0; i < value.length; i++) {
                value[i] = Double.parseDouble(split[i + 1]);
            }
            record.setDATE(date);
            record.setValue(value);
        }
        return record;
    }

    public void setNext() throws IOException {
        line = StringUtil.delSpace(bin.readLine());
        if (line != null) {
            hasnext = true;
        } else {
            hasnext = false;
            bin.close();
            fin.close();
        }
    }

    public String getNAME() {
        return this.NAME;
    }

    public String getUNIT() {
        return this.UNIT;
    }

    public double getNODATA() {
        return this.NODATA;
    }

    public int getNSTATION() {
        return this.NSTATION;
    }

    public String[] getSTATION_NAME() {
        return this.STATION_NAME;
    }

    public double[] getLONGITUDE() {
        return this.LON;
    }

    public double[] getLATITUDE() {
        return this.LAT;
    }

    public double[] getX() {
        return this.X;
    }

    public double[] getY() {
        return this.Y;
    }

    public double[] getELEV() {
        return this.ELEV;
    }

    private void readHeader() throws IOException {
        String[] splitName;
        String[] splitLON;
        String[] splitLAT;
        String[] splitX;
        String[] splitY;
        String[] splitELEV;
        this.NAME = StringUtil.delSpace(bin.readLine()).split(" +|, +")[1];
        this.UNIT = StringUtil.delSpace(bin.readLine()).split(" +|, +")[1];
        this.NODATA = Double.parseDouble(StringUtil.delSpace(bin.readLine()).split(" +|, +")[1]);
        this.NSTATION = Integer.parseInt(StringUtil.delSpace(bin.readLine()).split(" +|, +")[1]);

        STATION_NAME = new String[NSTATION];
        LON = new double[NSTATION];
        LAT = new double[NSTATION];
        X = new double[NSTATION];
        Y = new double[NSTATION];
        ELEV = new double[NSTATION];

        splitName = StringUtil.delSpace(bin.readLine()).split(" +|, +");
        splitLON = StringUtil.delSpace(bin.readLine()).split(" +|, +");
        splitLAT = StringUtil.delSpace(bin.readLine()).split(" +|, +");
        splitX = StringUtil.delSpace(bin.readLine()).split(" +|, +");
        splitY = StringUtil.delSpace(bin.readLine()).split(" +|, +");
        splitELEV = StringUtil.delSpace(bin.readLine()).split(" +|, +");
        for (int i = 0; i < NSTATION; i++) {
            STATION_NAME[i] = splitName[i + 1];
            LON[i] = Double.parseDouble(splitLON[i + 1]);
            LAT[i] = Double.parseDouble(splitLAT[i + 1]);
            X[i] = Double.parseDouble(splitX[i + 1]);
            Y[i] = Double.parseDouble(splitY[i + 1]);
            ELEV[i] = Double.parseDouble(splitELEV[i + 1]);
        }
    }

    private Date parseDATE(String str) {
        Date date = null;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(str);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return date;
    }
}
