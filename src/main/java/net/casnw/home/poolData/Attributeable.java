//@DECLARE@
package net.casnw.home.poolData;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * 数据对象接口
 *
 * @author myf@lzb.ac.cn
 * @since 2013-04-09
 * @version 1.0
 *
 */
public interface Attributeable {

	public interface Integerable extends Datable {

		int getValue();

		void setValue(int value);

		String toString();
	}

	public interface Doubleable extends Datable {

		double getValue();

		void setValue(double value);

		String toString();
	}

	public interface Longable extends Datable {

		long getValue();

		void setValue(long value);

		String toString();
	}

	public interface Floatable extends Datable {

		float getValue();

		void setValue(float value);

		String toString();
	}

	public interface Stringable extends Datable {

		String getValue();

		void setValue(String value);

		String toString();
	}

	public interface Booleanable extends Datable {

		boolean getValue();

		void setValue(boolean value);

		String toString();
	}

	public interface Objectable extends Datable {

		public java.lang.Object getValue();

		public void setValue(java.lang.Object value);

		String toString();
	}

	public interface IntegerArrayable extends Datable {

		int[] getValue();

		int getValue(int index);

		String toString();

		void setValue(int[] value);

		void setValue(int index, int value);
	}

	public interface LongArrayable extends Datable {

		long[] getValue();

		long getValue(int index);

		void setValue(long[] value);

		void setValue(int index, long value);

		String toString();
	}

	public interface FloatArrayable extends Datable {

		float[] getValue();

		float getValue(int index);

		void setValue(float[] value);

		void setValue(int index, float value);

		String toString();
	}

	public interface DoubleArrayable extends Datable {

		double[] getValue();

		double getValue(int index);

		void setValue(double[] value);

		void setValue(int index, double value);

		String toString();
	}

	public interface StringArrayable extends Datable {

		String[] getValue();

		String getValue(int index);

		void setValue(String[] value);

		void setValue(int index, String value);

		String toString();
	}

	public interface BooleanArrayable extends Datable {

		boolean[] getValue();

		boolean getValue(int index);

		void setValue(boolean[] value);

		void setValue(int index, boolean value);

		String toString();
	}

	public interface ObjectArrayable extends Datable {

		Object[] getValue();

		Object getValue(int index);

		void setValue(Object[] value);

		void setValue(int index, Object value);

		String toString();
	}

	public interface Dateable extends Datable {

		Date getValue();

		void setValue(Date value);

		void setValue(long value);

		void setTime(long value);

		boolean after(Date value);

		boolean before(Date value);

		int compareTo(Date value);

		String toString();
	}

	public interface Calendarable extends Datable {

		Attributeable.Calendarable getValue();

		void setValue(long value);

		void setValue(Date value);

		void setValue(String value, String format) throws ParseException;

		void setValue(Calendar cal);

		int compareTo(PoolCalendar cal);

		boolean after(PoolCalendar when);

		boolean before(PoolCalendar when);

		public long getTimeInMillis();

		String toString();
	}

	public interface Integer2DArrayable extends Datable {

		int[][] getValue();

		void setValue(int[][] value);

		int[] getRowValue(int index);

		int[] getColValue(int index);

		int getCellValue(int row, int col);

		void setCellValue(int row, int col, int cellValue);

		int getRowsNum();

		int getColsNum();

		String toString();
	}

	public interface Double2DArrayable extends Datable {

		double[][] getValue();

		void setValue(double[][] value);

		double[] getRowValue(int index);

		double[] getColValue(int index);

		double getCellValue(int row, int col);

		void setCellValue(int row, int col, double cellValue);

		int getRowsNum();

		int getColsNum();

		String toString();
	}

	public interface Float2DArrayable extends Datable {

		float[][] getValue();

		void setValue(float[][] value);

		float[] getRowValue(int index);

		float[] getColValue(int index);

		float getCellValue(int row, int col);

		void setCellValue(int row, int col, float cellValue);

		int getRowsNum();

		int getColsNum();

		String toString();
	}

	public interface Long2DArrayable extends Datable {

		long[][] getValue();

		void setValue(long[][] value);

		long[] getRowValue(int index);

		long[] getColValue(int index);

		long getCellValue(int row, int col);

		void setCellValue(int row, int col, long cellValue);

		int getRowsNum();

		String toString();

		int getColsNum();
	}

	public interface Boolean2DArrayable extends Datable {

		boolean[][] getValue();

		void setValue(boolean[][] value);

		boolean[] getRowValue(int index);

		boolean[] getColValue(int index);

		boolean getCellValue(int row, int col);

		void setCellValue(int row, int col, boolean cellValue);

		int getRowsNum();

		int getColsNum();

		String toString();
	}

	public interface String2DArrayable extends Datable {

		String[][] getValue();

		void setValue(String[][] value);

		String[] getRowValue(int index);

		String[] getColValue(int index);

		String getCellValue(int row, int col);

		void setCellValue(int row, int col, String cellValue);

		int getRowsNum();

		int getColsNum();

		String toString();
	}

	public interface Double3DArrayable extends Datable {

		double[][][] getValue();

		void setValue(double[][][] value);

		double getCellValue(int i, int j, int k);

		void setCellValue(int i, int j, int k, double cellValue);

		void setValue(int i, int j, double[] value);

		double[] getValue(int i, int j);

		String toString();
	}

	public interface Integer3DArrayable extends Datable {

		int[][][] getValue();

		void setValue(int[][][] value);

		int getCellValue(int i, int j, int k);

		void setCellValue(int i, int j, int k, int cellValue);

		void setValue(int i, int j, int[] value);

		int[] getValue(int i, int j);

		String toString();
	}

	public interface Double4DArrayable extends Datable {

		double[][][][] getValue();

		void setValue(double[][][][] value);

		double getCellValue(int i, int j, int k, int l);

		void setCellValue(int i, int j, int k, int l, double cellValue);

		void setValue(int i, int j, int k, double[] value);

		double[] getValue(int i, int j, int k);

		String toString();
	}

	public interface DoubleNDArrayable extends Datable {

		double[] getValue();

		void setValue(double[] value);

		String getCellValue(int[] indexArray);

		void setCellValue(int[] indexArray, String cellValue);

		void setDimension();

		int getDimension();

		String toString();
	}
}
