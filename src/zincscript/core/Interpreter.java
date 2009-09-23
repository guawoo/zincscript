package zincscript.core;

import java.util.Hashtable;
import zincscript.lib.*;
import zincscript.util.ArrayList;

/**
 * <code>Interpreter</code> 是ZincScript的解释执行引擎
 * 
 * @author Jarod Yv
 */
public class Interpreter {

	/**
	 * 用于保存函数的定义
	 * 
	 * @author Jarod Yv
	 */
	class FuncEntry {
		int startLine; // 函数起始的行号
		int endLine; // 函数结束的行号
		ArrayList paramNames; // 参数名列表
		Hashtable params; // 参数

		public FuncEntry() {
			startLine = 0;
			endLine = 0;
			paramNames = new ArrayList(4);
			params = new Hashtable();
		}

		public String toString() {
			String s = new String();

			s = startLine + " ";
			s = s + endLine + " ";
			s = s + paramNames + " ";
			s = s + params;

			return s;
		}
	}

	/**
	 * 定义了数组的数据结构
	 * 
	 * @author Jarod Yv
	 */
	class Array {
		int dimension;// 维度
		ArrayList data = null;// 数据

		public Array(int di) {
			dimension = di;
			data = new ArrayList();
		}

		public void setValue(ArrayList index, Object value) {
			if (index == null)
				return;
			dimension = index.size();
			Integer in = (Integer) index.remove(0);
			int m = in.intValue();
			if (index.size() > 0) {
				if (m >= data.size()) {
					for (int i = data.size(); i <= m; i++)
						data.add(null);
				}

				Array a = (Array) data.get(m);
				if (a == null)
					a = new Array(index.size());
				data.set(m, a);
				a.setValue(index, value);
				a = null;
			} else {
				if (m >= data.size()) {
					for (int i = data.size(); i <= m; i++) {
						data.add(null);
					}
				}
				data.set(m, value);
			}
			in = null;
		}

		public Object getValue(ArrayList index) {
			if (index == null)
				return null;
			Integer in = (Integer) index.remove(0);
			int m = in.intValue();
			if (index.size() > 0) {
				if (m < data.size()) {
					Array a = (Array) data.get(m);
					return a.getValue(index);
				} else {
					return null;
				}
			} else {
				if (m < data.size()) {
					return data.get(m);
				} else {
					return null;
				}
			}
		}
	}

	/**
	 * 解析运算表达式的一种高效的做法是构建表达式树<br>
	 * 表达式树节点，用于解析表达式
	 * 
	 * @author Jarod Yv
	 */
	class ExpressionNode {

		public static final byte OPERATOR = 0;
		public static final byte VALUE = 1;

		/**
		 * 节点类型 0-运算符 1-数值
		 * 
		 * @see OPERATOR
		 * @see VALUE
		 */
		public byte type;

		/** 节点的值 */
		public Object value;

		/** 左子树 */
		public ExpressionNode left;

		/** 右子树 */
		public ExpressionNode right;

		/** 父节点 */
		public ExpressionNode parent;

		public String toString() {
			return new String("Type=" + type + " Value=" + value);
		}

		/**
		 * 向节点树中添加子节点。添加原则是如果左子树为空，则添加到左子树，如果不为空添加到右子树
		 * 
		 * @param child
		 *            子节点
		 */
		public void addChild(ExpressionNode child) {
			if (left == null) {
				left = child;
			} else if (right == null) {
				right = child;
			}
			child.parent = this;
		}
	}

	/* 解析执行引擎的外部封装 */
	private ZincScript scriptEngine = null;
	/* 脚本装载器 */
	private ScriptLoader scriptLoader = null;
	/* 语法解析器 */
	private Parser parser = null;
	/* 当前运算符 */
	private Operator operator = null;
	/* 函数的本地变量 */
	private Hashtable localVars = null;
	/* 全局变量 */
	private Hashtable globalVars = null;
	/* 函数列表 */
	private Hashtable funcs = null;
	/* 返回结果 */
	private Object returnValue = null;
	/* 解析到的最大行号 */
	private int maxLine = 0;

	/**
	 * @param scriptEngine
	 */
	public Interpreter(ZincScript scriptEngine) {
		this.scriptEngine = scriptEngine;
		localVars = new Hashtable();
		funcs = new Hashtable();
		operator = Operator.getOperator();
		globalVars = null;
	}

	/*
	 * 仅用于函数调用
	 */
	private Interpreter(ZincScript scriptEngine, Hashtable vars,
			Hashtable globalVars, Hashtable funcs) {
		this.localVars = vars;
		this.globalVars = globalVars;
		this.funcs = funcs;
		this.operator = Operator.getOperator();
		this.scriptEngine = scriptEngine;
	}

	/**
	 * 设置脚本文件
	 * 
	 * @param scriptLoader
	 *            脚本文件
	 */
	public void setCode(ScriptLoader scriptLoader) {
		this.scriptLoader = scriptLoader;
	}

	/**
	 * 解释执行一段脚本
	 * 
	 * @param from
	 *            解释的开始位置
	 * @param to
	 *            解释的结束位置,遇到return立即结束
	 */
	public Object interprete(int from, int to) throws ZSException {
		if (scriptLoader == null || scriptLoader.totalLineNum() <= from)
			return null;

		maxLine = to;
		scriptLoader.setCurrentLine(from);
		String currentLine = scriptLoader.getCurrentLine();
		if (currentLine == null)
			return null;
		checkLine(currentLine);// 检测引号和括号正确关闭
		parser = new Parser(currentLine);
		currentLine = null;
		getNextToken();// 获取第一个关键字的类型
		while (parser.type != Syntax.EOF_TYPE) {
			try {
				interpreteImpl();
			} catch (ZSException e) {
				if (e.getType() == ZSException.RETURN_EXCEPTION)
					return returnValue;
				else
					throw e;
			}
			getNextToken();
		}
		return returnValue;
	}

	/**
	 * 重置解释器状态
	 */
	public void reset() {
		if (localVars != null) {
			localVars.clear();
		}
		if (globalVars != null) {
			globalVars.clear();
		}
		if (funcs != null) {
			funcs.clear();
		}
		if (parser != null) {
			parser.reset();
		}
		returnValue = null;
	}

	public void release() {
		reset();
		localVars = null;
		globalVars = null;
		if (parser != null) {
			parser.release();
			parser = null;
		}
		scriptLoader = null;
		scriptEngine = null;
	}

	/*
	 * 根据关键字类型的不同，调用不同的解释执行方法
	 */
	private void interpreteImpl() throws ZSException {
		switch (parser.type) {
		case Syntax.KEYWORD_IF_TYPE:
		case Syntax.KEYWORD_WHILE_TYPE:
		case Syntax.KEYWORD_INT_TYPE:
		case Syntax.KEYWORD_STRING_TYPE:
		case Syntax.KEYWORD_ARRAY_TYPE:
		case Syntax.KEYWORD_DEF_TYPE:
		case Syntax.KEYWORD_EXIT_TYPE:
		case Syntax.KEYWORD_END_TYPE:
		case Syntax.KEYWORD_RETURN_TYPE:
			interpreteKeyWord();
			break;
		case Syntax.FUNC_TYPE:
			interpreteFunctionCall();
			break;
		case Syntax.ARRAY_ASSIGN_TYPE:
			interpreteArrayAssign();
			break;
		case Syntax.VAR_TYPE:
			interpreteAssign();
			break;
		case Syntax.EOL_TYPE:
			parser.next();
			break;
		case Syntax.EOF_TYPE:
			// TODO 执行一些必要的清理工作
			break;
		// default:
		// parseError("无法识别的关键字");
		}
	}

	/*
	 * 处理函数调用
	 */
	private void interpreteFunctionCall() throws ZSException {
		String functionName = null;
		functionName = (String) parser.value;
		getNextToken();// 函数名后应当跟（
		interpreteCallFunction(functionName);
		getNextToken();
	}

	/*
	 * 处理数组赋值
	 */
	private void interpreteArrayAssign() throws ZSException {
		int index = 0;
		String name = (String) parser.value;
		Array a = (Array) getVar(name);
		name = null;
		int di = a.dimension;
		int i = 0;
		while (i < di) {
			getNextToken(); // 应当是[
			getNextToken(); // 应当是数组长度
			index = ((Integer) interpreteExpression()).intValue();
			if (i < di - 1) {
				if (index < a.data.size()) {
					a = (Array) a.data.get(index);
				} else {
					for (int z = a.data.size(); z <= index; z++)
						a.data.add(null);
					Array tempArray = new Array(di - i);
					a.data.set(index, tempArray);
					a = tempArray;
					tempArray = null;
				}
			}
			i++;
		}

		getNextToken();
		if (parser.type != Syntax.OPERATOR_EQUAL_TYPE) {
			handleError("应当是 '='");
		} else {
			getNextToken();
			Object val = interpreteExpression();
			if (index >= a.data.size()) {
				for (int z = a.data.size(); z <= index; z++)
					a.data.add(null);
			}
			a.data.set(index, val);
			val = null;
		}
		a = null;
	}

	/**
	 * 根据关键字的不同，解释执行不同的关键字功能
	 * 
	 * @throws ZSException
	 * @throws ReturnException
	 */
	private void interpreteKeyWord() throws ZSException {
		switch (parser.type) {
		case Syntax.KEYWORD_INT_TYPE:
		case Syntax.KEYWORD_STRING_TYPE:
		case Syntax.KEYWORD_ARRAY_TYPE:
			defineVar();
			break;
		case Syntax.KEYWORD_IF_TYPE:
			interpreteIf();
			break;
		case Syntax.KEYWORD_WHILE_TYPE:
			interpreteWhile();
			break;
		case Syntax.KEYWORD_RETURN_TYPE:
			interpreteReturn();
			break;
		case Syntax.KEYWORD_DEF_TYPE:
			defineFunction();
			break;
		case Syntax.KEYWORD_EXIT_TYPE:
			interpreteLoad();
			break;
		case Syntax.KEYWORD_END_TYPE:
			break;
		default:
			handleError("无法识别的关键字");
		}
	}

	/*
	 * 处理返回语句
	 */
	private void interpreteReturn() throws ZSException {
		getNextToken();
		returnValue = interpreteExpression();
		throw new ZSException(ZSException.RETURN_EXCEPTION);
	}

	/*
	 * 处理动态装载脚本语句
	 */
	private void interpreteLoad() throws ZSException {
		getNextToken();
		returnValue = interpreteExpression();
		throw new ZSException(ZSException.INTERRUPTION_EXCEPTION,
				(String) returnValue);
	}

	/*
	 * 解释赋值语句
	 */
	private void interpreteAssign() throws ZSException {
		String name = (String) parser.value;// 变量名
		Object value = null;

		getNextToken();// 获取变量名后的符号,应该是=
		if (parser.type != Syntax.OPERATOR_EQUAL_TYPE) {// 不是=肯定错误
			handleError("后面应该跟 '='");
		} else {
			getNextToken();// 获取变量值
			value = interpreteExpression();// 计算变量值
			if (hasVar(name)) {
				setVar(name, value);
			} else {
				throw new ZSException("无法识别的外部变量: " + name);
			}
		}
	}

	/**
	 * 函数调用
	 * 
	 * @param name
	 *            函数名
	 * @param params
	 *            参数列表
	 * @return
	 * @throws ZSException
	 */
	public Object callFunction(String name, ArrayList params)
			throws ZSException {
		FuncEntry func = null;
		Object value = null;
		int n;
		int oldLine;

		if (funcs.containsKey(name)) {
			func = (FuncEntry) funcs.get(name);
			int paramNum = params == null ? 0 : params.size();
			if (func.paramNames.size() != paramNum) {
				handleError("函数" + name + "应当有" + func.paramNames.size()
						+ "个参数,但只找到了" + params.size() + "个参数。");
			}

			Interpreter p;
			Hashtable locals = new Hashtable();

			for (n = 0; n < func.paramNames.size(); n++) {
				locals.put(func.paramNames.get(n), params.get(n));
			}
			p = globalVars == null ? new Interpreter(scriptEngine, locals,
					localVars, funcs) : new Interpreter(scriptEngine, locals,
					globalVars, funcs);
			oldLine = scriptLoader.getCurLine();
			p.setCode(scriptLoader);
			value = p.interprete(func.startLine + 1, func.endLine);

			scriptLoader.setCurrentLine(oldLine);
		} else {// 没有在函数列表中找到指定名称的函数，则尝试调用库函数
			try {
				value = callLibFunction(name, params);
			} catch (ZSException e) {
				if (e.getType() == ZSException.INTERRUPTION_EXCEPTION)
					throw e;
				else
					handleError(e.getMessage());
			}
		}
		return value;
	}

	/*
	 * 调用库函数
	 * 
	 * @param name 函数名
	 * 
	 * @param params 参数列表
	 * 
	 * @return 函数执行结果
	 */
	private Object callLibFunction(String name, ArrayList params)
			throws ZSException {
		AbstactLib lib = null;
		if (name.startsWith("_zss"))
			lib = StdLib.getInstance();
		else if (name.startsWith("_zsg"))
			lib = GraphicsLib.getInstance();
		if (lib != null)
			return lib.callFunction(name, params);
		else
			throw new ZSException("函数" + name + "不存在");
	}

	/*
	 * 函数调用
	 * 
	 * @param functionName 函数名
	 * 
	 * @return
	 * 
	 * @throws FSException
	 */
	private Object interpreteCallFunction(String functionName)
			throws ZSException {
		ArrayList params = new ArrayList(4);// 传入的参数列表
		// 获取传入的参数
		do {
			getNextToken();
			if (parser.type == ',') {
				getNextToken();
			} else if (parser.type == ')') {
				break;
			}
			params.add(interpreteExpression());
		} while (parser.type == ',');

		return callFunction(functionName, params);
	}

	// 函数定义
	private void defineFunction() throws ZSException {
		FuncEntry fDef = new FuncEntry();
		Object value = null;
		String name, funcName;

		fDef.startLine = scriptLoader.getCurLine();

		getNextToken();

		if (parser.type != Syntax.FUNC_TYPE) {
			handleError("函数格式不正确");
		}
		funcName = (String) parser.value;
		getNextToken();

		if (parser.type != '(') {
			handleError("函数名后应当跟括号");
		}

		getNextToken();
		// 解析参数列表
		while (parser.type != ')') {
			if (parser.type != Syntax.KEYWORD_INT_TYPE
					&& parser.type != Syntax.KEYWORD_STRING_TYPE) {
				handleError("非法参数类型");
			}

			value = null;

			if (parser.type == Syntax.KEYWORD_INT_TYPE) {
				value = new Integer(0);
			} else if (parser.type == Syntax.KEYWORD_STRING_TYPE) {
				value = new String("");
			}

			getNextToken();

			if (parser.type != Syntax.VAR_TYPE) {
				handleError("缺少参数名");
			}

			name = (String) parser.value;

			fDef.paramNames.add(name);
			fDef.params.put(name, value);

			getNextToken();
			if (parser.type == ',')
				getNextToken();
		}

		// 跳过整个函数体
		int endNum = 0;
		while (parser.type != Syntax.EOF_TYPE) {
			getNextToken();
			if (parser.type == Syntax.KEYWORD_IF_TYPE
					|| parser.type == Syntax.KEYWORD_WHILE_TYPE)
				endNum++;
			if (parser.type == Syntax.KEYWORD_END_TYPE) {
				endNum--;
				if (endNum < 0)
					break;
			}
			if (parser.type == Syntax.KEYWORD_DEF_TYPE)
				handleError("暂时不能处理嵌套函数");
		}

		fDef.endLine = scriptLoader.getCurLine();
		getNextToken();

		funcs.put(funcName, fDef);

	}

	/*
	 * 解析表达式
	 * 
	 * @return
	 * 
	 * @throws FSException
	 */
	private Object interpreteExpression() throws ZSException {
		ExpressionNode curNode = null;
		Object val = null;
		boolean end = false;
		// boolean skipTok = false;
		boolean negate = false; // 用于标示符号是负数而不是减法运算
		boolean not = false;// 用于标示!号是取反而不是逻辑非
		boolean prevOp = true;// 用于标示数字前面有孤立运算符比如(-,!)

		while (!end) {
			switch (parser.type) {
			case Syntax.NUMBER_TYPE:
			case Syntax.STRING_TYPE:
			case Syntax.VAR_TYPE:
			case Syntax.FUNC_TYPE:
			case Syntax.ARRAY_ASSIGN_TYPE:
				if (!prevOp) {
					handleError("非法运算符");
				} else {
					val = null;
					ExpressionNode node = new ExpressionNode();
					node.type = ExpressionNode.VALUE;
					switch (parser.type) {
					case Syntax.NUMBER_TYPE:
						val = (Integer) parser.value;
						break;
					case Syntax.STRING_TYPE:
						val = (String) parser.value;
						break;
					case Syntax.FUNC_TYPE:
						String name = (String) parser.value;
						getNextToken();
						val = interpreteCallFunction(name);
						name = null;
						break;
					case Syntax.ARRAY_ASSIGN_TYPE:
						name = (String) parser.value;
						Array a = (Array) getVar(name);
						int dimension = a.dimension;
						ArrayList indexs = new ArrayList(dimension);
						int i = 0;
						while (i < dimension) {
							getNextToken(); // 应当是[
							getNextToken(); // 应当是数组下标
							Object index = interpreteExpression();
							indexs.add(index);
							index = null;
							i++;
						}

						try {
							val = a.getValue(indexs);
						} catch (Exception e) {
							handleError(e.getMessage());
						} finally {
							a = null;
							name = null;
							indexs = null;
						}
						break;
					case Syntax.VAR_TYPE:
						String var = (String) parser.value;
						if (hasVar(var)) {
							val = getVar(var);
						} else {
							new ZSException("无法识别的外部变量:" + var);
						}
						var = null;
						break;
					}

					// 处理否定
					if (not) {
						if (val instanceof Integer) {
							if (((Integer) val).intValue() != 0) {
								val = new Integer(0);
							} else {
								val = new Integer(1);
							}
							not = false;
						} else {
							handleError("非法数据");
						}
					}

					// 处理负数
					if (negate) {
						if (val instanceof Integer) {
							val = new Integer(-((Integer) val).intValue());
						} else {
							handleError("非法数据");
						}
					}

					node.value = val;

					if (curNode != null) {
						curNode.addChild(node);
					}
					curNode = node;
					prevOp = false;
				}
				break;
			case Syntax.LOGICAL_EQUAL_TYPE:
			case Syntax.LOGICAL_NEGTIVE_TYPE:
			case Syntax.LOGICAL_AND_TYPE:
			case Syntax.LOGICAL_OR_TYPE:
			case Syntax.OPERATOR_MULT_TYPE:
			case Syntax.OPERATOR_DIV_TYPE:
			case Syntax.OPERATOR_MOD_TYPE:
			case Syntax.OPERATOR_PLUS_TYPE:
			case Syntax.OPERATOR_MINUS_TYPE:
			case Syntax.OPERATOR_GT_TYPE:
			case Syntax.OPERATOR_GTE_TYPE:
			case Syntax.OPERATOR_LT_TYPE:
			case Syntax.OPERATOR_LTE_TYPE:
			case Syntax.OPERATOR_NEGTIVE_TYPE:
				if (prevOp) {// 一元运算符
					if (parser.type == Syntax.OPERATOR_MINUS_TYPE) {
						negate = true;// -号在此处不表示减法运算，而表示负数
					} else if (parser.type == Syntax.OPERATOR_NEGTIVE_TYPE) {
						not = true; // 单独运用!号表示取反
					} else {
						handleError("非法表达式");
					}
				} else {
					ExpressionNode node = new ExpressionNode();
					node.type = ExpressionNode.OPERATOR;// 类型为运算符
					node.value = new Integer(parser.type);// 装入运算符类型
					if (curNode.parent != null) {
						// 处理优先级
						int curPrio = getPrio(parser.type);// 当前优先级
						int parPrio = getPrio(((Integer) curNode.parent.value)
								.intValue());// 当前节点优先级
						if (curPrio <= parPrio) {// 如果当前优先级低于或等于当前节点优先级
							node.parent = curNode.parent.parent;
							node.left = curNode.parent;
							if (curNode.parent.parent != null) {
								curNode.parent.parent.right = node;
							}

							curNode.parent = node;
							curNode = node;
						} else {
							curNode.parent.right = node;
							node.left = curNode;
							node.parent = curNode.parent;
							curNode.parent = node;
							curNode = node;
						}
					} else {
						node.left = curNode;
						curNode.parent = node;
						curNode = node;
					}
					prevOp = true;
				}
				break;
			case '(':
				getNextToken();
				val = interpreteExpression();

				ExpressionNode node = new ExpressionNode();
				node.value = val;
				node.type = ExpressionNode.VALUE;

				if (curNode != null) {
					curNode.addChild(node);
				}
				curNode = node;

				prevOp = false;
				break;

			default:
				end = true;

			}
			if (!end) {
				parser.next();
			}
		}

		// 回到刚刚构建的树的根
		if (curNode == null)
			handleError("没有成功构建起表达式树");
		while (curNode.parent != null) {
			curNode = curNode.parent;
		}
		return traversalExpressionTree(curNode);
	}

	private int getPrio(int op) {
		return ((Integer) Syntax.opPrio.get(new Integer(op))).intValue();
	}

	/*
	 * 根据传入的表达式后序遍历树计算表达式的值
	 * 
	 * @param node
	 * 
	 * @return
	 * 
	 * @throws FSException
	 */
	private Object traversalExpressionTree(ExpressionNode node)
			throws ZSException {
		if (node.type == ExpressionNode.VALUE) {
			// 如果传入的树的根是值类型，说明传入的树就是一个孤立的值，没有运算，直接返回这个值
			return node.value;
		}
		Object leftValue = null;
		Object rightValue = null;
		leftValue = traversalExpressionTree(node.left);// 递归遍历左子树
		rightValue = traversalExpressionTree(node.right);// 递归遍历右子树
		// 对两子树或子节点进行算数或逻辑运算
		return operator.operate(((Integer) node.value).intValue(), leftValue,
				rightValue);
	}

	/*
	 * 解析if语句
	 * 
	 * @throws ZSException
	 * 
	 * @throws ReturnException
	 */
	private void interpreteIf() throws ZSException {
		Integer value = null;
		int depth = 0;
		// boolean then = false;

		getNextToken();
		try {
			value = (Integer) interpreteExpression();// 计算if语句的条件
		} catch (ClassCastException cce) {
			handleError("If后面的条件需要返回一个整型值");
			return;
		}

		if (value != null && value.intValue() != 0) {// 条件为真
			getNextToken();
			int endNum = 0;
			while ((parser.type != Syntax.KEYWORD_ELSE_TYPE)
					&& (parser.type != Syntax.EOF_TYPE)
					&& (parser.type != Syntax.KEYWORD_ELSIF_TYPE)) {
				// 解析if后的语句
				interpreteImpl();
				getNextToken();
				if (parser.type == Syntax.KEYWORD_IF_TYPE
						|| parser.type == Syntax.KEYWORD_WHILE_TYPE)
					endNum++;
				else if (parser.type == Syntax.KEYWORD_END_TYPE) {
					endNum--;
					if (endNum < 0)
						break;
				}
			}
			if (parser.type == Syntax.KEYWORD_ELSE_TYPE
					|| parser.type == Syntax.KEYWORD_ELSIF_TYPE) {// if后有else或elsif
				depth = 1;
				do {
					getNextToken();
					if (parser.type == Syntax.KEYWORD_IF_TYPE
							|| parser.type == Syntax.KEYWORD_WHILE_TYPE)
						depth++;// 有嵌套
					else if (parser.type == Syntax.KEYWORD_END_TYPE)
						depth--;
					else if (parser.type == Syntax.EOF_TYPE)
						handleError("找不到end");
				} while (depth > 0);
			}
			// getNextToken();
		} else {// 条件为假，跳到else语句
			depth = 1;
			do {
				getNextToken();
				if (parser.type == Syntax.KEYWORD_IF_TYPE
						|| parser.type == Syntax.KEYWORD_WHILE_TYPE)
					depth++;
				else if ((parser.type == Syntax.KEYWORD_END_TYPE))
					depth--;
				else if ((parser.type == Syntax.KEYWORD_ELSE_TYPE || parser.type == Syntax.KEYWORD_ELSIF_TYPE)
						&& depth == 1)
					depth--;
				else if (parser.type == Syntax.EOF_TYPE)
					handleError("找不到end");
			} while (depth > 0);

			if (parser.type == Syntax.KEYWORD_ELSE_TYPE) {
				getNextToken();
				getNextToken();
				// 执行else后面的语句
				int endNum = 0;
				while (true) {
					interpreteImpl();
					getNextToken();
					if (parser.type == Syntax.KEYWORD_IF_TYPE
							|| parser.type == Syntax.KEYWORD_WHILE_TYPE)
						endNum++;
					else if (parser.type != Syntax.KEYWORD_END_TYPE) {
						endNum--;
						if (endNum < 0)
							break;
					}
				}
				// getNextToken();
			} else if (parser.type == Syntax.KEYWORD_ELSIF_TYPE) {
				interpreteIf();
			}
		}
	}

	/*
	 * 解释执行while语句
	 */
	private void interpreteWhile() throws ZSException {
		int startLine = scriptLoader.getCurLine();
		getNextToken();
		Integer value = (Integer) interpreteExpression();
		getNextToken();

		while (value.intValue() != 0) {
			while (parser.type != Syntax.EOF_TYPE
					&& parser.type != Syntax.KEYWORD_END_TYPE) {
				interpreteImpl();
				getNextToken();
			}

			scriptLoader.setCurrentLine(startLine);// 设置循环执行的起始语句位置
			resetTokens();
			getNextToken();
			value = (Integer) interpreteExpression();
			getNextToken();
		}
		// 跳过循环
		int depth = 1;
		do {
			if (parser.type == Syntax.KEYWORD_WHILE_TYPE
					|| parser.type == Syntax.KEYWORD_IF_TYPE)
				depth++;
			if (parser.type == Syntax.KEYWORD_END_TYPE)
				depth--;
			if (parser.type == Syntax.EOF_TYPE)
				handleError("找不到end");
			getNextToken();
		} while (depth > 0);

		// getNextToken();

	}

	/*
	 * 定义变量
	 * 
	 * @throws FSException
	 */
	private void defineVar() throws ZSException {
		int type = parser.type;
		String name = null;

		if (type != Syntax.KEYWORD_INT_TYPE
				&& type != Syntax.KEYWORD_STRING_TYPE
				&& type != Syntax.KEYWORD_ARRAY_TYPE) {
			handleError("应当是 'int','string','array'");
		}

		do {
			getNextToken();// 获取变量名
			if (type == Syntax.KEYWORD_ARRAY_TYPE) {
				if (parser.type != Syntax.ARRAY_ASSIGN_TYPE)
					handleError("变量名错误");
			} else {
				if (parser.type != Syntax.VAR_TYPE)
					handleError("变量名错误");
			}

			name = (String) parser.value;
			switch (type) {
			case Syntax.KEYWORD_INT_TYPE:
				addVar(name, new Integer(0));// int默认为0
				getNextToken();// 读取变量名后的信息
				break;
			case Syntax.KEYWORD_STRING_TYPE:
				addVar(name, new String(""));// String默认为""
				getNextToken();// 读取变量名后的信息
				break;
			case Syntax.KEYWORD_ARRAY_TYPE:
				// getNextToken();
				int di = 0;
				do {
					getNextToken();// 应该是[
					if (parser.type != '[')
						break;
					getNextToken();// 应该是]
					if (parser.type != ']')
						break;
					di++;
				} while (parser.type != Syntax.EOL_TYPE);
				addVar(name, new Array(di));// 数组默认为null
				break;
			}

			if (parser.type == Syntax.OPERATOR_EQUAL_TYPE) {// 如果是=
				getNextToken();// 读取变量值
				if (type == Syntax.KEYWORD_ARRAY_TYPE) {
					// 数组赋值比较特殊，单独处理
					int dimension = 0;
					while (parser.type == '{') {
						dimension++;
					}
					if (dimension == 0)
						handleError("数组赋值后应用大括号");
					else {
						Object o = getVar(name);
						Array a = null;
						if (o != null && o instanceof Array)
							a = (Array) o;
						if (a != null) {
							a.dimension = dimension;
						}
					}
				} else {
					setVar(name, interpreteExpression());// 设置变量值
				}
			} else if (parser.type != ',' && parser.type != Syntax.EOL_TYPE) {
				handleError("应该是 ','");
			}
		} while (parser.type != Syntax.EOL_TYPE);
	}

	/*
	 * 生成错误信息
	 * 
	 * @param s
	 * 
	 * @throws ZSException
	 */
	private void handleError(String s) throws ZSException {
		StringBuffer sb = new StringBuffer();
		sb.append("第");
		int curLine = scriptLoader.getCurLine();
		sb.append(curLine);
		sb.append("行存在错误:\n\t");
		sb.append(s);
		sb.append("\n\t错误语句:");
		sb.append(scriptLoader.getLine(curLine));
		sb.append("\n\t当前关键字:");
		sb.append(parser.toString());

		throw new ZSException(sb.toString());
	}

	/*
	 * 获取下一个单词的类型
	 */
	private void getNextToken() {
		if (parser.type == Syntax.EOL_TYPE) {// 一行结束
			if (scriptLoader.getCurLine() < maxLine) {// 没到达到脚本的结尾
				scriptLoader.setCurrentLine(scriptLoader.getCurLine() + 1);
				parser.setString(scriptLoader.getCurrentLine());
				parser.next();
			} else {// 整个脚本解释执行完成
				parser.type = Syntax.EOF_TYPE;
			}
		} else {
			parser.next();
		}
	}

	private void resetTokens() {
		parser.setString(scriptLoader.getCurrentLine());
		parser.next();
	}

	/*
	 * 加入变量名-变量值
	 * 
	 * @param name 变量名
	 * 
	 * @param value 变量值
	 * 
	 * @throws FSException
	 */
	private void addVar(String name, Object value) throws ZSException {
		if (localVars.containsKey(name)) {
			handleError("变量 " + name + "已经存在");
		}
		localVars.put(name, value);
	}

	/*
	 * 获取变量的值
	 * 
	 * @param name 变量值
	 * 
	 * @return
	 */
	public Object getVar(String name) {
		if (localVars != null && localVars.containsKey(name)) {
			return localVars.get(name);
		} else {
			if (globalVars != null && globalVars.containsKey(name)) {
				return globalVars.get(name);
			}
		}

		return null;
	}

	/**
	 * 设置变量
	 * 
	 * @param name
	 *            变量名
	 * @param value
	 *            变量值
	 * @throws GSException
	 */
	public void setVar(String name, Object value) throws ZSException {
		Object obj;
		if (value == null)
			return;
		// parseError("给变量" + name + "设置的值为null");
		if (localVars.containsKey(name)) {
			obj = localVars.get(name);
			if (value.getClass() != obj.getClass()) {
				handleError("数据类型不匹配");
			}
			localVars.remove(name);
			localVars.put(name, value);
		} else if (globalVars.containsKey(name)) {
			obj = globalVars.get(name);
			if (value.getClass() != obj.getClass()) {
				handleError("数据类型不合法");
			}
			globalVars.remove(name);
			globalVars.put(name, value);
		}

	}

	// private void setArrayValue(String name, ArrayList index, Object value)
	// throws ZSException {
	// Object obj = null;
	// if (value == null)
	// parseError("给变量" + name + "设置的值为null");
	// if (vars.containsKey(name)) {
	// obj = vars.get(name);
	// if (obj instanceof Array) {
	// Array a = (Array) obj;
	// a.setValue(index, value);
	// a = null;
	// } else {
	// parseError("数据类型不匹配");
	// }
	// } else if (globalVars.containsKey(name)) {
	// obj = globalVars.get(name);
	// if (obj instanceof Array) {
	// Array a = (Array) obj;
	// a.setValue(index, value);
	// a = null;
	// } else {
	// parseError("数据类型不合法");
	// }
	// }
	// obj = null;
	// }

	// private Object getArrayValue(String name, ArrayList indexs)
	// throws ZSException {
	// Object o = null;
	// if (vars != null && vars.containsKey(name)) {
	// o = vars.get(name);
	// } else {
	// if (globalVars != null && globalVars.containsKey(name)) {
	// o = globalVars.get(name);
	// }
	// }
	// if (o != null && o instanceof Array) {
	// Array array = (Array) o;
	// return array.getValue(indexs);
	// } else {
	// parseError("数据类型不合法");
	// return null;
	// }
	// }

	/*
	 * 判断变量是否有变量
	 * 
	 * @param name
	 * 
	 * @return
	 */
	private boolean hasVar(String name) {
		return (localVars != null && localVars.containsKey(name))
				|| (globalVars != null && globalVars.containsKey(name));
	}

	/**
	 * 获取返回值
	 * 
	 * @return
	 */
	public Object getReturnValue() {
		return returnValue;
	}

	/**
	 * 强制退出
	 * 
	 * @param o
	 * @throws ZSException
	 */
	public void exit(Object o) throws ZSException {
		returnValue = o;
		throw new ZSException(ZSException.INTERRUPTION_EXCEPTION);
	}

	/*
	 * 检测脚本语句是否正确关闭了括号和引号
	 * 
	 * @param line 脚本行代码
	 * 
	 * @throws FSException
	 */
	private void checkLine(String line) throws ZSException {
		boolean inQuotes = false;
		int brCount = 0;
		char chars[];
		int n;

		if (line != null) {
			if (!line.trim().startsWith("#")) {// 不是注释
				chars = line.toCharArray();
				for (n = 0; n < chars.length; n++) {
					if (inQuotes) {
						if (chars[n] == '"') {
							if (n >= 1) {
								if (chars[n - 1] != '\\') {
									inQuotes = false;
								}
							}
						}
					} else {
						if (chars[n] == '(') {
							brCount++;
						} else if (chars[n] == ')') {
							brCount--;
						} else if (chars[n] == '"') {
							if (n >= 1) {
								if (chars[n - 1] != '\\') {
									inQuotes = true;
								}
							}
						}
					}
				}

				if (inQuotes) {
					handleError("Mismatched quotes");
				}

				if (brCount != 0) {
					handleError("Mismatched brackets");
				}
			}
		}

	}

}
