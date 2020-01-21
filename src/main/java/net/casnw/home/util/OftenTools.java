/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.casnw.home.util;

import net.casnw.home.model.*;
import net.casnw.home.poolData.Datable;
import net.casnw.home.poolData.PoolBoolean;
import net.casnw.home.poolData.PoolBoolean2DArray;
import net.casnw.home.poolData.PoolBooleanArray;
import net.casnw.home.poolData.PoolCalendar;
import net.casnw.home.poolData.PoolDate;
import net.casnw.home.poolData.PoolDouble;
import net.casnw.home.poolData.PoolDouble2DArray;
import net.casnw.home.poolData.PoolDouble3DArray;
import net.casnw.home.poolData.PoolDoubleArray;
import net.casnw.home.poolData.PoolFloat;
import net.casnw.home.poolData.PoolFloat2DArray;
import net.casnw.home.poolData.PoolFloatArray;
import net.casnw.home.poolData.PoolInteger;
import net.casnw.home.poolData.PoolInteger2DArray;
import net.casnw.home.poolData.PoolInteger3DArray;
import net.casnw.home.poolData.PoolIntegerArray;
import net.casnw.home.poolData.PoolLong;
import net.casnw.home.poolData.PoolLong2DArray;
import net.casnw.home.poolData.PoolLongArray;
import net.casnw.home.poolData.PoolObject;
import net.casnw.home.poolData.PoolObjectArray;
import net.casnw.home.poolData.PoolString;
import net.casnw.home.poolData.PoolString2DArray;
import net.casnw.home.poolData.PoolStringArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 提供一些公共类
 *
 * @author myf@lzb.ac.cn
 * @since 2013-05-23
 * @version 1.0
 */
public class OftenTools {

    /**
     * 将一定格式的字符串转换为Date类型的数据
     *
     * @param date
     * @param format
     * @return Date
     */
    public static Date toDate(String date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        try {
            dateFormat.applyPattern(format);
            java.util.Date vDate = dateFormat.parse(date);
            return new Date(vDate.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将一定格式的字符串转换为Calendar类型的数据
     *
     * @param date
     * @param format
     * @return Calendar
     */
    public static Calendar toCanlendar(String date, String format) {
        Date adate = toDate(date,format);
        Calendar cl = Calendar.getInstance();
        cl.setTime(adate);
        return cl;
    }
    
    public static Datable getDataObject(String type,  String size) {
        Datable dataObject = null;
       
            switch (type) {
                case "PoolInteger":
                    dataObject = new PoolInteger();
                    break;
                case "PoolDouble":
                    dataObject = new PoolDouble();
                    break;
                case "PoolFloat":
                    dataObject = new PoolFloat();
                    break;
                case "PoolLong":
                    dataObject = new PoolLong();
                    break;
                case "PoolString":
                    dataObject = new PoolString();
                    if (size != null && !"".equalsIgnoreCase(size)) {
                        ((PoolString) dataObject).length = Integer.parseInt(size);
                    }
                    break;
                case "PoolBoolean":
                    dataObject = new PoolBoolean();
                    break;
                case "PoolObject":
                    dataObject = new PoolObject();
                    break;
                case "PoolIntegerArray":
                    dataObject = new PoolIntegerArray();
                    if (size != null && !"".equalsIgnoreCase(size)) {
                        ((PoolIntegerArray) dataObject).length = Integer.parseInt(size);
                    }
                    break;
                case "PoolDoubleArray":
                    dataObject = new PoolDoubleArray();
                    if (size != null && !"".equalsIgnoreCase(size)) {
                        ((PoolDoubleArray) dataObject).length = Integer.parseInt(size);
                    }
                    break;
                case "PoolFloatArray":
                    dataObject = new PoolFloatArray();
                    if (size != null && !"".equalsIgnoreCase(size)) {
                        ((PoolFloatArray) dataObject).length = Integer.parseInt(size);
                    }
                    break;
                case "PoolLongArray":
                    dataObject = new PoolLongArray();
                    if (size != null && !"".equalsIgnoreCase(size)) {
                        ((PoolLongArray) dataObject).length = Integer.parseInt(size);
                    }
                    break;
                case "PoolBooleanArray":
                    dataObject = new PoolBooleanArray();
                    if (size != null && !"".equalsIgnoreCase(size)) {
                        ((PoolBooleanArray) dataObject).length = Integer.parseInt(size);
                    }
                    break;
                case "PoolStringArray":
                    dataObject = new PoolStringArray();
                    if (size != null && !"".equalsIgnoreCase(size)) {
                        ((PoolStringArray) dataObject).length = Integer.parseInt(size);
                    }
                    break;
                case "PoolObjectArray":
                    dataObject = new PoolObjectArray();
                    if (size != null && !"".equalsIgnoreCase(size)) {
                        ((PoolObjectArray) dataObject).length = Integer.parseInt(size);
                    }
                    break;
                case "PoolInteger2DArray":
                    dataObject = new PoolInteger2DArray();

                    if (size != null && size.split(",").length == 2) {
                        ((PoolInteger2DArray) dataObject).rowsNum = Integer.parseInt(size.split(",")[0]);
                        ((PoolInteger2DArray) dataObject).colsNum = Integer.parseInt(size.split(",")[1]);
                    }

                    break;
                case "PoolDouble2DArray":
                    dataObject = new PoolDouble2DArray();
                    if (size != null && size.split(",").length == 2) {
                        ((PoolDouble2DArray) dataObject).rowsNum = Integer.parseInt(size.split(",")[0]);
                        ((PoolDouble2DArray) dataObject).colsNum = Integer.parseInt(size.split(",")[1]);
                    }
                    break;
                case "PoolFloat2DArray":
                    dataObject = new PoolFloat2DArray();
                    if (size != null && size.split(",").length == 2) {
                        ((PoolFloat2DArray) dataObject).rowsNum = Integer.parseInt(size.split(",")[0]);
                        ((PoolFloat2DArray) dataObject).colsNum = Integer.parseInt(size.split(",")[1]);
                    }
                    break;
                case "PoolLong2DArray":
                    dataObject = new PoolLong2DArray();
                    if (size != null && size.split(",").length == 2) {
                        ((PoolLong2DArray) dataObject).rowsNum = Integer.parseInt(size.split(",")[0]);
                        ((PoolLong2DArray) dataObject).colsNum = Integer.parseInt(size.split(",")[1]);
                    }
                    break;
                case "PoolBoolean2DArray":
                    dataObject = new PoolBoolean2DArray();
                    if (size != null && size.split(",").length == 2) {
                        ((PoolBoolean2DArray) dataObject).rowsNum = Integer.parseInt(size.split(",")[0]);
                        ((PoolBoolean2DArray) dataObject).colsNum = Integer.parseInt(size.split(",")[1]);
                    }
                    break;
                case "PoolString2DArray":
                    dataObject = new PoolString2DArray();
                    if (size != null && size.split(",").length == 2) {
                        ((PoolString2DArray) dataObject).rowsNum = Integer.parseInt(size.split(",")[0]);
                        ((PoolString2DArray) dataObject).colsNum = Integer.parseInt(size.split(",")[1]);
                    }
                    break;
                case "PoolDouble3DArray":
                    dataObject = new PoolDouble3DArray();
                    break;
                case "PoolInteger3DArray":
                    dataObject = new PoolInteger3DArray();
                    break;
                case "PoolCalendar":
                    dataObject = new PoolCalendar();
                    break;
                case "PoolDate":
                    dataObject = new PoolDate();
                    break;
            }

        return dataObject;
    }
}
