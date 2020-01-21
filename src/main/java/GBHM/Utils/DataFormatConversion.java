/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author longyinping
 */
public class DataFormatConversion {

    PrintWriter precW;
    PrintWriter tmeanW;
    PrintWriter tmaxW;
    PrintWriter tminW;
    PrintWriter rhW;
    PrintWriter sunW;
    PrintWriter windW;
    int nstation;
    //station.txt
    ArrayList<Integer> stationID;
    ArrayList<String> stationName;
    ArrayList<Double> lon;
    ArrayList<Double> lat;
    ArrayList<Double> elev;
    ArrayList<Double> prjX;
    ArrayList<Double> prjY;
    //气象站下载数据
    ArrayList<Integer> ID;
    ArrayList<Date> date;
    ArrayList<Double> prec;
    ArrayList<Double> tmean;
    ArrayList<Double> tmax;
    ArrayList<Double> tmin;
    ArrayList<Double> rh;
    ArrayList<Double> sun;
    ArrayList<Double> wind;
    private double nodata = -9999;

    public void climateConversion(String stationFileName, String weatherDataPath) throws IOException {

        ID = new ArrayList();
        date = new ArrayList();
        prec = new ArrayList();
        tmean = new ArrayList();
        tmax = new ArrayList();
        tmin = new ArrayList();
        rh = new ArrayList();
        sun = new ArrayList();
        wind = new ArrayList();

        precW = new PrintWriter("data/climate/prec.txt");
        tmeanW = new PrintWriter("data/climate/tmean.txt");
        tmaxW = new PrintWriter("data/climate/tmax.txt");
        tminW = new PrintWriter("data/climate/tmin.txt");
        rhW = new PrintWriter("data/climate/humidity.txt");
        sunW = new PrintWriter("data/climate/sunshine.txt");
        windW = new PrintWriter("data/climate/wind.txt");

        this.readStation(stationFileName);
        nstation = stationID.size();
        for (int i = 0; i < nstation; i++) {
            this.readWeatherData(weatherDataPath + Integer.toString(stationID.get(i)) + ".txt");
        }

        this.printHeader(precW, "Daily Precipitation", "mm");
        this.printHeader(tmeanW, "Daily Mean Temperature", "℃");
        this.printHeader(tmaxW, "Daily Maximum Temperature", "℃");
        this.printHeader(tminW, "Daily Minimum Temperature", "℃");
        this.printHeader(rhW, "Daily Mean Relative Humidity", "1");
        this.printHeader(sunW, "Daily Sunshine Duration", "hour");
        this.printHeader(windW, "Daily Wind Speed", "m/s");

        this.printValues("prec", precW, prec);
        this.printValues("tmean", tmeanW, tmean);
        this.printValues("tmax", tmaxW, tmax);
        this.printValues("tmin", tminW, tmin);
        this.printValues("rh", rhW, rh);
        this.printValues("sun", sunW, sun);
        this.printValues("wind", windW, wind);

    }

    private void readStation(String fileName) throws FileNotFoundException, IOException {
        //读取station.txt
        //区站号,名称,经度,纬度,高程,PrjX,PrjY
        stationID = new ArrayList();
        stationName = new ArrayList();
        lon = new ArrayList();
        lat = new ArrayList();
        elev = new ArrayList();
        prjX = new ArrayList();
        prjY = new ArrayList();
        String line;
        String[] split;

        FileReader fin = new FileReader(fileName);
        BufferedReader bin = new BufferedReader(fin);
        bin.readLine();
        line = bin.readLine();
        while (line != null) {
            line = line.trim();
            split = line.split(" +|\t");

            stationID.add(Integer.parseInt(split[0]));
            stationName.add(split[1]);
            lon.add(Double.parseDouble(split[2]));
            lat.add(Double.parseDouble(split[3]));
            elev.add(Double.parseDouble(split[4]));
            prjX.add(Double.parseDouble(split[5]));
            prjY.add(Double.parseDouble(split[6]));

            line = bin.readLine();
        }
        bin.close();
        fin.close();
        //读取station.txt结束

    }

    private void readWeatherData(String fileName) throws FileNotFoundException, IOException {
        //读取气象数据

        int precNum = 0, tmeanNum = 0, tmaxNum = 0, tminNum = 0, rhNum = 0, sunNum = 0, windNum = 0;
        String line;
        String[] split;
        FileReader fin2 = new FileReader(fileName);
        BufferedReader bin2 = new BufferedReader(fin2);
        line = bin2.readLine();
        if (line != null) {
            line = line.trim();
            split = line.split(" +|\t");
            for (int i = 4; i < split.length; i++) {
                if (split[i].equals("20-20时降水量")) {
                    precNum = i;
                } else if (split[i].equals("平均气温")) {
                    tmeanNum = i;
                } else if (split[i].equals("日最高气温")) {
                    tmaxNum = i;
                } else if (split[i].equals("日最低气温")) {
                    tminNum = i;
                } else if (split[i].equals("平均相对湿度")) {
                    rhNum = i;
                } else if (split[i].equals("日照时数")) {
                    sunNum = i;
                } else if (split[i].equals("平均风速")) {
                    windNum = i;
                }
            }
        }

        line = bin2.readLine();
        while (line != null) {
            line = line.trim();
            split = line.split(" +|\t");

            ID.add(Integer.parseInt(split[0]));

            Date day = new Date();
            day.setYear(Integer.parseInt(split[1]) - 1900);
            day.setMonth(Integer.parseInt(split[2]) - 1);
            day.setDate(Integer.parseInt(split[3]));
            date.add(day);

            prec.add(Double.parseDouble(split[precNum]));
            tmean.add(Double.parseDouble(split[tmeanNum]));
            tmax.add(Double.parseDouble(split[tmaxNum]));
            tmin.add(Double.parseDouble(split[tminNum]));
            rh.add(Double.parseDouble(split[rhNum]));
            sun.add(Double.parseDouble(split[sunNum]));
            wind.add(Double.parseDouble(split[windNum]));

            line = bin2.readLine();
        }
        bin2.close();
        fin2.close();
    }

    private void printHeader(PrintWriter w, String name, String unit) {
        //输出头文件
        int length = ID.size() / nstation;
        w.println("NAME      " + name);
        w.println("UNIT      " + unit);
        w.println("NODATA    " + String.format("%1$12.3f", nodata));
        w.println("NSTATION" + String.format("%1$8d", nstation));

        w.print("STATION_NAME    ");
        for (int i = 0; i < nstation; i++) {
            w.print(String.format("%1$8d", stationID.get(i)));
        }
        w.println();

        w.print("LON    ");
        for (int i = 0; i < nstation; i++) {
            w.print(String.format("%1$8.2f", lon.get(i)));
        }
        w.println();

        w.print("LAT    ");
        for (int i = 0; i < nstation; i++) {
            w.print(String.format("%1$8.2f", lat.get(i)));
        }
        w.println();

        w.print("X    ");
        for (int i = 0; i < nstation; i++) {
            w.print(String.format("%1$12.3f", prjX.get(i)));
        }
        w.println();

        w.print("Y    ");
        for (int i = 0; i < nstation; i++) {
            w.print(String.format("%1$12.3f", prjY.get(i)));
        }
        w.println();

        w.print("ELEV(m)    ");
        for (int i = 0; i < nstation; i++) {
            w.print(String.format("%1$12.3f", elev.get(i)));
        }
        w.println();
        //输出头文件结束
    }

    private void printValues(String name, PrintWriter w, ArrayList<Double> list) {
        double value;
        int length = list.size() / nstation;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < length; i++) {
            w.print(format.format(date.get(i)));
            for (int j = 0; j < nstation; j++) {
                value = list.get(i + j * length);

                if (value == 32766) {//缺测
                    value = nodata;
                } else {
                    switch (name) {
                        case "prec":
                            if (value == 32700) {
                                value = 0;
                            } else if (value > 30000 && value < 32700) {//雨雪雾露霜
                                value = (value - 30000) * 0.1;
                            } else {
                                value = value * 0.1;
                            }
                            break;
                        case "rh":
                            if (value > 300000) {
                                value = value - 300000;
                            }
                            value = value * 0.01;
                            break;
                        case "wind":
                            if (value > 1000000) {
                                value = value - 1000000;
                            }
                            value = value * 0.1;
                            break;
                        default:
                            value = value * 0.1;
                            break;
                    }
                }

                w.print(String.format("%1$16.3f", value));
            }
            w.println();
        }
        w.flush();
    }
}
