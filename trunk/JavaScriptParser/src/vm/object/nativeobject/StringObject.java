package vm.object.nativeobject;

import vm.VMStack;
import vm.object.VMObject;

/**
 * @author Jarod Yv
 * @see ECMA-262 98页~106页 15.5.String Objects
 */
public class StringObject extends VMObject {

	private static final int ID_FROM_CHAR_CODE = 39;
	private static final int ID_CHAR_AT = 40;
	private static final int ID_CHAR_CODE_AT = 41;
	private static final int ID_CONCAT = 42;
	private static final int ID_INDEX_OF = 43;
	private static final int ID_LAST_INDEX_OF = 44;
	private static final int ID_LOCALE_COMPARE = 45;
	private static final int ID_MATCH = 46;
	private static final int ID_REPLACE = 47;
	private static final int ID_SEARCH = 48;
	private static final int ID_SLICE = 49;
	private static final int ID_SPLIT = 50;
	private static final int ID_SUBSTRING = 51;
	private static final int ID_TO_LOWER_CASE = 52;
	private static final int ID_TO_LOCALE_LOWER_CASE = 53;
	private static final int ID_TO_UPPER_CASE = 54;
	private static final int ID_TO_LOCALE_UPPER_CASE = 55;
	private static final int ID_LENGTH = 56;
	private static final int ID_LENGTH_SET = 57;

	public static final StringObject STRING_PROTOTYPE = new StringObject();
	static {
		STRING_PROTOTYPE.addProperty("charAt",
				new FunctionObject(ID_CHAR_AT, 1));
		STRING_PROTOTYPE.addProperty("charCodeAt", new FunctionObject(
				ID_CHAR_CODE_AT, 1));
		STRING_PROTOTYPE
				.addProperty("concat", new FunctionObject(ID_CONCAT, 1));
		STRING_PROTOTYPE.addProperty("indexOf", new FunctionObject(ID_INDEX_OF,
				2));
		STRING_PROTOTYPE.addProperty("lastIndexOf", new FunctionObject(
				ID_LAST_INDEX_OF, 2));
		STRING_PROTOTYPE.addProperty("localeCompare", new FunctionObject(
				ID_LOCALE_COMPARE, 1));
		STRING_PROTOTYPE.addProperty("replace", new FunctionObject(ID_REPLACE,
				2));
		STRING_PROTOTYPE
				.addProperty("search", new FunctionObject(ID_SEARCH, 1));
		STRING_PROTOTYPE.addProperty("slice", new FunctionObject(ID_SLICE, 2));
		STRING_PROTOTYPE.addProperty("substring", new FunctionObject(
				ID_SUBSTRING, 2));
		STRING_PROTOTYPE.addProperty("toLowerCase", new FunctionObject(
				ID_TO_LOWER_CASE, 0));
		STRING_PROTOTYPE.addProperty("toLocaleLowerCase", new FunctionObject(
				ID_TO_LOCALE_LOWER_CASE, 0));
		STRING_PROTOTYPE.addProperty("toUpperCase", new FunctionObject(
				ID_TO_UPPER_CASE, 0));
		STRING_PROTOTYPE.addProperty("toLocaleUpperCase", new FunctionObject(
				ID_TO_LOCALE_UPPER_CASE, 0));
		STRING_PROTOTYPE.addProperty("length",
				new FunctionObject(ID_LENGTH, -1));
	}

	public StringObject() {
		super(OBJECT_PROTOTYPE);
	}

	public void evalNative(int index, VMStack stack, int sp, int parCount) {
		switch (index) {
		case ID_FROM_CHAR_CODE:
			char[] chars = new char[parCount];
			for (int i = 0; i < parCount; i++) {
				chars[i] = (char) stack.getInt(sp + 2 + i);
			}
			stack.setObject(sp, new String(chars));
			chars = null;
			break;
		case ID_CHAR_AT:
			String str = stack.getString(sp);
			int i = stack.getInt(sp + 2);
			stack.setObject(sp, i < 0 || i >= str.length() ? "" : str
					.substring(i, i + 1));
			str = null;
			break;
		case ID_CHAR_CODE_AT:
			str = stack.getString(sp);
			i = stack.getInt(sp + 2);
			stack.setNumber(sp, i < 0 || i >= str.length() ? Double.NaN : str
					.charAt(i));
			str = null;
			break;
		case ID_CONCAT:
			StringBuffer sb = new StringBuffer(stack.getString(sp));
			for (i = 0; i < parCount; i++) {
				sb.append(stack.getString(sp + i + 2));
			}
			stack.setObject(sp, sb.toString());
			sb = null;
			break;
		case ID_INDEX_OF:
			stack.setNumber(sp, stack.getString(sp).indexOf(
					stack.getString(sp + 2), stack.getInt(sp + 3)));
			break;
		case ID_LAST_INDEX_OF:
			str = stack.getString(sp);
			String find = stack.getString(sp + 2);
			double d = stack.getNumber(sp + 3);
			int max = (Double.isNaN(d)) ? str.length() : (int) d;
			int best = -1;
			while (true) {
				int found = str.indexOf(find, best + 1);
				if (found == -1 || found > max) {
					break;
				}
				best = found;
			}
			stack.setNumber(sp, best);
			str = null;
			find = null;
			break;
		case ID_LOCALE_COMPARE:
			stack.setNumber(sp, stack.getString(sp).compareTo(
					stack.getString(sp + 2)));
			break;
		case ID_MATCH:
		case ID_REPLACE:
		case ID_SEARCH:
			throw new RuntimeException("Regexp NYI");
		case ID_SLICE:
			str = stack.getString(sp);
			int len = str.length();
			int start = stack.getInt(sp + 2);
			int end = stack.isNull(sp + 3) ? len : stack.getInt(sp + 3);
			if (start < 0) {
				start = Math.max(len + start, 0);
			}
			if (end < 0) {
				end = Math.max(len + start, 0);
			}
			if (start > len) {
				start = len;
			}
			if (end > len) {
				end = len;
			}
			if (end < start) {
				end = start;
			}
			stack.setObject(sp, str.substring(start, end));
			str = null;
			break;
		case ID_SPLIT:
			str = stack.getString(sp);
			String sep = stack.getString(sp + 2);
			double limit = stack.getNumber(sp + 3);
			if (Double.isNaN(limit)) {
				limit = Integer.MAX_VALUE;
			}
			ArrayObject arrayObj = new ArrayObject();
			if (sep.length() == 0) {
				if (str.length() < limit) {
					limit = str.length();
				}
				for (i = 0; i < limit; i++) {
					arrayObj.setObject(i, str.substring(i, i + 1));
				}
			} else {
				int cut0 = 0;
				while (cut0 < str.length() && arrayObj.size() < limit) {
					int cut = str.indexOf(sep, cut0);
					if (cut == -1) {
						cut = str.length();
					}
					arrayObj.setObject(arrayObj.size(), str
							.substring(cut0, cut));
					cut0 = cut + sep.length();
				}
			}
			stack.setObject(sp, arrayObj);
			str = null;
			sep = null;
			arrayObj = null;
			break;
		case ID_SUBSTRING:
			str = stack.getString(sp);
			len = str.length();
			start = stack.getInt(sp + 2);
			end = stack.isNull(sp + 3) ? len : stack.getInt(sp + 3);
			if (start > end) {
				int tmp = end;
				end = start;
				start = tmp;
			}
			start = Math.min(Math.max(0, start), len);
			end = Math.min(Math.max(0, end), len);
			stack.setObject(sp, str.substring(start, end));
			str = null;
			break;
		case ID_TO_LOWER_CASE:
		case ID_TO_LOCALE_LOWER_CASE:
			stack.setObject(sp, stack.getString(sp + 2).toLowerCase());
			break;
		case ID_TO_UPPER_CASE:
		case ID_TO_LOCALE_UPPER_CASE:
			stack.setObject(sp, stack.getString(sp + 2).toUpperCase());
			break;
		case ID_LENGTH:
			stack.setInt(sp, toString().length());
			break;
		case ID_LENGTH_SET:
			// cannot be changed!
			break;
		default:
			if (parentPrototype != null)
				parentPrototype.evalNative(index, stack, sp, parCount);
			break;
		}
	}
}
