package zincfish.zincscript;

import java.util.Hashtable;

/**
 * <code>Syntax</code>定义了语法规则，其中包括合法的字符集、关键字集和算符优先级，供解析解释时使用<br>
 * 在语法设计上，参考了目前流行的脚本语Ruby的语法
 * 
 * @author Jarod Yv
 */
public final class Syntax {

	public static final String ALLOW_WORD_START = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_";
	public static final String ALLOW_WORD = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890._";

	public static final String IF = "if";
	public static final String ELSE_IF = "elsif";
	public static final String ELSE = "else";
	public static final String WHILE = "while";
	public static final String DEF = "def";
	public static final String END = "end";
	public static final String RETURN = "return";
	public static final String EXIT = "exit";
	public static final String INT = "int";
	public static final String STRING = "string";
	public static final String ARRAY = "array";

	public static final int EOL_TYPE = 0x0100;
	public static final int EOF_TYPE = 0x0101;
	public static final int VAR_TYPE = 0x0102;
	public static final int STRING_TYPE = 0x0103;
	public static final int FUNC_TYPE = 0x0104;
	public static final int ARRAY_ASSIGN_TYPE = 0x0105;

	public static final int KEYWORD_EXIT_TYPE = 0x0200;
	public static final int KEYWORD_IF_TYPE = 0x0201;
	public static final int KEYWORD_ELSE_TYPE = 0x0202;
	public static final int KEYWORD_ELSIF_TYPE = 0x0203;
	public static final int KEYWORD_WHILE_TYPE = 0x0204;
	public static final int KEYWORD_DEF_TYPE = 0x0205;
	public static final int KEYWORD_END_TYPE = 0x0206;
	public static final int KEYWORD_RETURN_TYPE = 0x0207;
	public static final int KEYWORD_INT_TYPE = 0x0208;
	public static final int KEYWORD_STRING_TYPE = 0x0209;
	public static final int KEYWORD_ARRAY_TYPE = 0x020a;

	public static final int NUMBER_TYPE = 0x0400;

	public static final int OPERATOR_PLUS_TYPE = 0x0500;
	public static final int OPERATOR_MINUS_TYPE = 0x0501;
	public static final int OPERATOR_MULT_TYPE = 0x0502;
	public static final int OPERATOR_DIV_TYPE = 0x0503;
	public static final int OPERATOR_MOD_TYPE = 0x0504;
	public static final int OPERATOR_GT_TYPE = 0x0505;
	public static final int OPERATOR_LT_TYPE = 0x0506;
	public static final int OPERATOR_EQUAL_TYPE = 0x0507;
	public static final int OPERATOR_GTE_TYPE = 0x0508;
	public static final int OPERATOR_LTE_TYPE = 0x0509;
	public static final int OPERATOR_NEGTIVE_TYPE = 0x050a;

	public static final int LOGICAL_AND_TYPE = 0x0700;
	public static final int LOGICAL_OR_TYPE = 0x0701;
	public static final int LOGICAL_EQUAL_TYPE = 0x0702;
	public static final int LOGICAL_NEGTIVE_TYPE = 0x0703;

	public static Hashtable opPrio = null; // 运算符优先级

	static {
		if (opPrio == null) {
			opPrio = new Hashtable();
			// 优先级从低到高
			// (||) < (&&) < (==,!=,>,>=,<,<=) < (+,-) < (*./,%)
			opPrio.put(new Integer(LOGICAL_OR_TYPE), new Integer(1));
			opPrio.put(new Integer(LOGICAL_AND_TYPE), new Integer(2));
			opPrio.put(new Integer(LOGICAL_EQUAL_TYPE), new Integer(5));
			opPrio.put(new Integer(LOGICAL_NEGTIVE_TYPE), new Integer(5));
			opPrio.put(new Integer(OPERATOR_GT_TYPE), new Integer(5));
			opPrio.put(new Integer(OPERATOR_GTE_TYPE), new Integer(5));
			opPrio.put(new Integer(OPERATOR_LT_TYPE), new Integer(5));
			opPrio.put(new Integer(OPERATOR_LTE_TYPE), new Integer(5));
			opPrio.put(new Integer(OPERATOR_PLUS_TYPE), new Integer(10));
			opPrio.put(new Integer(OPERATOR_MINUS_TYPE), new Integer(10));
			opPrio.put(new Integer(OPERATOR_MULT_TYPE), new Integer(20));
			opPrio.put(new Integer(OPERATOR_DIV_TYPE), new Integer(20));
			opPrio.put(new Integer(OPERATOR_MOD_TYPE), new Integer(20));
		}
	}

}
