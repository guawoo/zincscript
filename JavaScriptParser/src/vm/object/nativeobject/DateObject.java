package vm.object.nativeobject;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import vm.VMStack;
import vm.object.VMObject;

/**
 * @author Jarod Yv
 * @see ECMA-262 117页~129页 15.9.DateObjects
 */
public class DateObject extends VMObject {
	private static final int ID_TO_DATE_STRING = 903;
	private static final int ID_TO_TIME_STRING = 904;
	private static final int ID_TO_LOCALE_DATE_STRING = 906;
	private static final int ID_TO_LOCALE_TIME_STRING = 907;
	private static final int ID_GET_TIME = 909;
	private static final int ID_GET_FULL_YEAR = 910;
	private static final int ID_GET_UTC_FULL_YEAR = 911;
	private static final int ID_GET_MONTH = 912;
	private static final int ID_GET_UTC_MONTH = 913;
	private static final int ID_GET_DATE = 914;
	private static final int ID_GET_UTC_DATE = 915;
	private static final int ID_GET_DAY = 916;
	private static final int ID_GET_UTC_DAY = 917;
	private static final int ID_GET_HOURS = 918;
	private static final int ID_GET_UTC_HOURS = 919;
	private static final int ID_GET_MINUTES = 920;
	private static final int ID_GET_UTC_MINUTES = 921;
	private static final int ID_GET_SECONDS = 922;
	private static final int ID_GET_UTC_SECONDS = 923;
	private static final int ID_GET_MILLISECONDS = 924;
	private static final int ID_GET_UTC_MILLISECONDS = 925;
	private static final int ID_GET_TIMEZONE_OFFSET = 926;
	private static final int ID_SET_TIME = 927;
	private static final int ID_SET_MILLISECONDS = 928;
	private static final int ID_SET_UTC_MILLISECONDS = 929;
	private static final int ID_SET_SECONDS = 930;
	private static final int ID_SET_UTC_SECONDS = 931;
	private static final int ID_SET_MINUTES = 932;
	private static final int ID_SET_UTC_MINUTES = 933;
	private static final int ID_SET_HOURS = 934;
	private static final int ID_SET_UTC_HOURS = 935;
	private static final int ID_SET_DATE = 936;
	private static final int ID_SET_UTC_DATE = 937;
	private static final int ID_SET_MONTH = 938;
	private static final int ID_SET_UTC_MONTH = 939;
	private static final int ID_SET_FULL_YEAR = 940;
	private static final int ID_SET_UTC_FULL_YEAR = 941;
	private static final int ID_TO_UTC_STRING = 942;

	public static final DateObject DATE_PROTOTYPE = new DateObject();
	static {
		DATE_PROTOTYPE.addProperty("toDateString", new FunctionObject(
				ID_TO_TIME_STRING, 0));
		DATE_PROTOTYPE.addProperty("toTimeSring", new FunctionObject(
				ID_TO_TIME_STRING, 0));
		DATE_PROTOTYPE.addProperty("toLocaleDateString", new FunctionObject(
				ID_TO_TIME_STRING, 0));
		DATE_PROTOTYPE.addProperty("toLocaleTimeString", new FunctionObject(
				ID_TO_TIME_STRING, 0));
		DATE_PROTOTYPE.addProperty("getTime",
				new FunctionObject(ID_GET_TIME, 0));
		DATE_PROTOTYPE.addProperty("getFullYear", new FunctionObject(
				ID_GET_FULL_YEAR, 0));
		DATE_PROTOTYPE.addProperty("getUTCFullYear", new FunctionObject(
				ID_GET_UTC_FULL_YEAR, 0));
		DATE_PROTOTYPE.addProperty("getMonth", new FunctionObject(ID_GET_MONTH,
				0));
		DATE_PROTOTYPE.addProperty("getUTCMonth", new FunctionObject(
				ID_GET_UTC_MONTH, 0));
		DATE_PROTOTYPE.addProperty("getDate",
				new FunctionObject(ID_GET_DATE, 0));
		DATE_PROTOTYPE.addProperty("getUTCDate", new FunctionObject(
				ID_GET_UTC_DATE, 0));
		DATE_PROTOTYPE.addProperty("getDay", new FunctionObject(ID_GET_DAY, 0));
		DATE_PROTOTYPE.addProperty("getUTCDay", new FunctionObject(
				ID_GET_UTC_DAY, 0));
		DATE_PROTOTYPE.addProperty("getHours", new FunctionObject(ID_GET_HOURS,
				0));
		DATE_PROTOTYPE.addProperty("getUTCHours", new FunctionObject(
				ID_GET_UTC_HOURS, 0));
		DATE_PROTOTYPE.addProperty("getMinutes", new FunctionObject(
				ID_GET_MINUTES, 0));
		DATE_PROTOTYPE.addProperty("getUTCMinutes", new FunctionObject(
				ID_GET_UTC_MINUTES, 0));
		DATE_PROTOTYPE.addProperty("getSeconds", new FunctionObject(
				ID_GET_SECONDS, 0));
		DATE_PROTOTYPE.addProperty("getUTCSeconds", new FunctionObject(
				ID_GET_UTC_SECONDS, 0));
		DATE_PROTOTYPE.addProperty("getMilliseconds", new FunctionObject(
				ID_GET_MILLISECONDS, 0));
		DATE_PROTOTYPE.addProperty("getUTCMilliseconds", new FunctionObject(
				ID_GET_UTC_MILLISECONDS, 0));
		DATE_PROTOTYPE.addProperty("getTimezoneOffset", new FunctionObject(
				ID_GET_TIMEZONE_OFFSET, 0));
		DATE_PROTOTYPE.addProperty("setTime",
				new FunctionObject(ID_SET_TIME, 1));
		DATE_PROTOTYPE.addProperty("setMilliseconds", new FunctionObject(
				ID_SET_MILLISECONDS, 1));
		DATE_PROTOTYPE.addProperty("setUTCMilliseconds", new FunctionObject(
				ID_SET_UTC_MILLISECONDS, 1));
		DATE_PROTOTYPE.addProperty("setSeconds", new FunctionObject(
				ID_SET_SECONDS, 2));
		DATE_PROTOTYPE.addProperty("setUTCSeconds", new FunctionObject(
				ID_SET_UTC_SECONDS, 2));
		DATE_PROTOTYPE.addProperty("setMinutes", new FunctionObject(
				ID_SET_MINUTES, 3));
		DATE_PROTOTYPE.addProperty("setUTCMinutes", new FunctionObject(
				ID_SET_UTC_MINUTES, 3));
		DATE_PROTOTYPE.addProperty("setHours", new FunctionObject(ID_SET_HOURS,
				1));
		DATE_PROTOTYPE.addProperty("setUTCHours", new FunctionObject(
				ID_SET_UTC_HOURS, 1));
		DATE_PROTOTYPE.addProperty("setDate",
				new FunctionObject(ID_SET_DATE, 1));
		DATE_PROTOTYPE.addProperty("setUTCDate", new FunctionObject(
				ID_SET_UTC_DATE, 1));
		DATE_PROTOTYPE.addProperty("setMonth", new FunctionObject(ID_SET_MONTH,
				1));
		DATE_PROTOTYPE.addProperty("setUTCMonth", new FunctionObject(
				ID_SET_UTC_MONTH, 1));
		DATE_PROTOTYPE.addProperty("setFullYear", new FunctionObject(
				ID_SET_FULL_YEAR, 1));
		DATE_PROTOTYPE.addProperty("setUTCFullYear", new FunctionObject(
				ID_SET_UTC_FULL_YEAR, 1));
		DATE_PROTOTYPE.addProperty("toUTCString", new FunctionObject(
				ID_TO_UTC_STRING, 0));
		DATE_PROTOTYPE.addProperty("toGMTString", new FunctionObject(
				ID_TO_UTC_STRING, 0));
	}

	public Calendar time = Calendar.getInstance();

	public DateObject() {
		super(OBJECT_PROTOTYPE);
	}

	public void evalNative(int index, VMStack stack, int sp, int parCount) {
		switch (index) {
		case ID_TO_DATE_STRING:
		case ID_TO_LOCALE_DATE_STRING:
			stack.setObject(sp, toString(false, true, false));
			break;

		case ID_TO_TIME_STRING:
		case ID_TO_LOCALE_TIME_STRING:
			stack.setObject(sp, toString(false, false, true));
			break;
		case ID_VALUE_OF:
		case ID_GET_TIME:
			stack.setNumber(sp, time.getTime().getTime());
			break;

		case ID_GET_DATE:
			stack.setNumber(sp, get(false, Calendar.DAY_OF_MONTH));
			break;

		case ID_GET_FULL_YEAR:
			stack.setNumber(sp, get(false, Calendar.YEAR));
			break;

		case ID_GET_MONTH:
			stack.setNumber(sp, get(false, Calendar.MONTH));
			break;

		case ID_GET_DAY:
			stack.setNumber(sp, get(false, Calendar.DAY_OF_WEEK));
			break;

		case ID_GET_HOURS:
			stack.setNumber(sp, get(false, Calendar.HOUR_OF_DAY));
			break;

		case ID_GET_MINUTES:
			stack.setNumber(sp, get(false, Calendar.MINUTE));
			break;

		case ID_GET_SECONDS:
			stack.setNumber(sp, get(false, Calendar.SECOND));
			break;

		case ID_GET_MILLISECONDS:
			stack.setNumber(sp, get(false, Calendar.MILLISECOND));
			break;

		case ID_GET_UTC_FULL_YEAR:
			stack.setNumber(sp, get(true, Calendar.YEAR));
			break;

		case ID_GET_UTC_MONTH:
			stack.setNumber(sp, get(true, Calendar.MONTH));
			break;

		case ID_GET_UTC_DATE:
			stack.setNumber(sp, get(true, Calendar.DAY_OF_MONTH));
			break;

		case ID_GET_UTC_DAY:
			stack.setNumber(sp, get(true, Calendar.DAY_OF_WEEK));
			break;

		case ID_GET_UTC_HOURS:
			stack.setNumber(sp, get(true, Calendar.HOUR_OF_DAY));
			break;

		case ID_GET_UTC_MINUTES:
			stack.setNumber(sp, get(true, Calendar.MINUTE));
			break;

		case ID_GET_UTC_SECONDS:
			stack.setNumber(sp, get(true, Calendar.SECOND));
			break;

		case ID_GET_UTC_MILLISECONDS:
			stack.setNumber(sp, get(true, Calendar.MILLISECOND));
			break;

		case ID_GET_TIMEZONE_OFFSET:
			stack.setNumber(sp, TimeZone.getDefault().getOffset(
					time.getTime().getTime() >= 0 ? 1 : 0,
					time.get(Calendar.YEAR),
					time.get(Calendar.MONTH),
					time.get(Calendar.DAY_OF_MONTH),
					time.get(Calendar.DAY_OF_WEEK),
					time.get(Calendar.HOUR_OF_DAY) * 3600000
							+ time.get(Calendar.MINUTE) * 60000
							+ time.get(Calendar.SECOND) * 1000
							+ time.get(Calendar.MILLISECOND)));
			break;

		case ID_SET_TIME:
			time.setTime(new Date((long) stack.getNumber(sp + 2)));
			break;

		case ID_SET_MILLISECONDS:
			setTime(false, Double.NaN, Double.NaN, Double.NaN, stack
					.getNumber(sp + 2));
			break;

		case ID_SET_SECONDS:
			setTime(false, Double.NaN, Double.NaN, stack.getNumber(sp + 2),
					stack.getNumber(sp + 3));
			break;

		case ID_SET_MINUTES:
			setTime(false, Double.NaN, stack.getNumber(sp + 2), stack
					.getNumber(sp + 3), stack.getNumber(sp + 4));
			break;

		case ID_SET_HOURS:
			setTime(false, stack.getNumber(sp + 2), stack.getNumber(sp + 3),
					stack.getNumber(sp + 4), stack.getNumber(sp + 5));
			break;

		case ID_SET_DATE:
			setDate(false, Double.NaN, Double.NaN, stack.getNumber(sp + 2));
			break;

		case ID_SET_MONTH:
			setDate(false, Double.NaN, stack.getNumber(sp + 2), stack
					.getNumber(sp + 3));
			break;

		case ID_SET_FULL_YEAR:
			setDate(false, stack.getNumber(sp + 2), stack.getNumber(sp + 3),
					stack.getNumber(sp + 4));
			break;

		case ID_SET_UTC_MILLISECONDS:
			setTime(true, Double.NaN, Double.NaN, Double.NaN, stack
					.getNumber(sp + 2));
			break;

		case ID_SET_UTC_SECONDS:
			setTime(true, Double.NaN, Double.NaN, stack.getNumber(sp + 2),
					stack.getNumber(sp + 3));
			break;

		case ID_SET_UTC_MINUTES:
			setTime(true, Double.NaN, stack.getNumber(sp + 2), stack
					.getNumber(sp + 3), stack.getNumber(sp + 4));
			break;

		case ID_SET_UTC_HOURS:
			setTime(true, stack.getNumber(sp + 2), stack.getNumber(sp + 3),
					stack.getNumber(sp + 4), stack.getNumber(sp + 5));
			break;

		case ID_SET_UTC_DATE:
			setDate(true, Double.NaN, Double.NaN, stack.getNumber(sp + 2));
			break;

		case ID_SET_UTC_MONTH:
			setDate(true, Double.NaN, stack.getNumber(sp + 2), stack
					.getNumber(sp + 3));
			break;

		case ID_SET_UTC_FULL_YEAR:
			setDate(true, stack.getNumber(sp + 2), stack.getNumber(sp + 3),
					stack.getNumber(sp + 4));
			break;

		case ID_TO_UTC_STRING:
			stack.setObject(sp, toString(true, true, true));
			break;
		default:
			if (parentPrototype != null)
				parentPrototype.evalNative(index, stack, sp, parCount);
			break;
		}
	}

	int get(boolean utc, int field) {
		time.setTimeZone(utc ? TimeZone.getTimeZone("GMT") : TimeZone
				.getDefault());
		int i = time.get(field);
		return field == Calendar.DAY_OF_WEEK ? i - 1 : i;
	}

	void setDate(boolean utc, double year, double month, double date) {
		time.setTimeZone(utc ? TimeZone.getTimeZone("GMT") : TimeZone
				.getDefault());

		if (!Double.isNaN(year)) {
			time.set(Calendar.YEAR, (int) year);
		}
		if (!Double.isNaN(month)) {
			time.set(Calendar.MONTH, (int) month);
		}
		if (!Double.isNaN(date)) {
			time.set(Calendar.DAY_OF_MONTH, (int) date);
		}
	}

	void setTime(boolean utc, double hours, double minutes, double seconds,
			double ms) {
		time.setTimeZone(utc ? TimeZone.getTimeZone("GMT") : TimeZone
				.getDefault());

		if (!Double.isNaN(hours)) {
			time.set(Calendar.HOUR_OF_DAY, (int) hours);
		}
		if (!Double.isNaN(minutes)) {
			time.set(Calendar.MINUTE, (int) minutes);
		}
		if (!Double.isNaN(seconds)) {
			time.set(Calendar.SECOND, (int) seconds);
		}
		if (!Double.isNaN(ms)) {
			time.set(Calendar.MILLISECOND, (int) ms);
		}
	}

	/**
	 * Returns a string representation of the date stored in this object.
	 * 
	 * @param utc
	 *            if true, return time in UTC, otherwise as local time
	 * @param date
	 *            if true, the date is included in the output
	 * @param time
	 *            if true, the time is included in the output
	 * @return the string representation of this date object
	 */
	public String toString(boolean utc, boolean addDate, boolean addTime) {
		StringBuffer buf = new StringBuffer();

		if (addDate) {
			append(buf, utc, Calendar.YEAR);
			buf.append('-');
			append(buf, utc, Calendar.MONTH);
			buf.append('-');
			append(buf, utc, Calendar.DAY_OF_MONTH);

			if (addTime) {
				buf.append(' ');
			}
		}

		if (addTime) {
			append(buf, utc, Calendar.HOUR_OF_DAY);
			buf.append(':');
			append(buf, utc, Calendar.MINUTE);
			buf.append(':');
			append(buf, utc, Calendar.SECOND);

			if (utc && addDate) {
				buf.append(" GMT");
			}
		}

		return buf.toString();
	}

	/**
	 * Helper method to append two-digit numbers to a string buffer.
	 * 
	 * @param buf
	 *            The target buffer to write to
	 * @param utc
	 *            utc if true, local time zone if false
	 * @param field
	 *            the value to append
	 */
	private void append(StringBuffer buf, boolean utc, int field) {
		int i = get(utc, field);
		if (i < 10) {
			buf.append('0');
		}
		buf.append(i);
	}

	/**
	 * Returns a string representation of this date object (including both, date
	 * and time).
	 */
	public String toString() {
		return toString(true, true, true);
	}
}
