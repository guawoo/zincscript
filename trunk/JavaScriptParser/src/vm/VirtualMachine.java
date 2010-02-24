package vm;

import interpreter.CompilationInterpreter;
import interpreter.DeclarationInterpreter;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import parser.Parser;
import parser.ParserException;
import vm.object.VMObject;
import vm.object.VMObjectFactory;
import vm.object.buildin.GlobalObject;
import vm.object.nativeobject.ArrayObject;
import vm.object.nativeobject.ErrorObject;
import vm.object.nativeobject.FunctionObject;
import ast.Program;

/**
 * <code>VirtualMachine</code>
 * 实现了一个虚拟机。它既是JavaScript解析器的封装，同时也是JavaScript的执行引擎和运行时环境。
 * <p>
 * 从编译原理角度来看，<code>VirtualMachine</code>
 * 更多的是属于后端，尽管它对前端的词法分析器、语法分析器和中间代码生成器进行了封装，但<code>VirtualMachine</code>
 * 的主要功能还是用于执行由前端解析生成的三地址中间代码。
 * 
 * @author Jarod Yv
 */
public class VirtualMachine {

	// ///////////////////////////// 操作指令 /////////////////////////////
	public static final int OP_NOP = 0x00;// 空指令，不做任何事情
	public static final int OP_ADD = 0x01;// 算数加法指令
	public static final int OP_AND = 0x02;// 按位与指令
	public static final int OP_APPEND = 0X03;// 追加数组元素指令
	public static final int OP_ASR = 0x04;// 算数右移指令
	public static final int OP_ENUM = 0x05;// 将keys的一个枚举压入栈
	public static final int OP_IN = 0x06;// 判断ps+2中的元素是否包含在sp+1指向的对象中
	public static final int OP_DIV = 0x07;// 算数除法指令
	public static final int OP_DUP = 0x08;// 复制栈顶的元素
	public static final int OP_EQEQ = 0x09;// 比较两元素是否相等
	public static final int OP_CTX_GET = 0x0a;// 从当前上下文读取
	public static final int OP_GET = 0x0b;// 从栈内环境读取
	public static final int OP_CTX = 0x0c;// 将当前上下文压栈
	public static final int OP_DEL = 0x0d;// 
	public static final int OP_GT = 0x0e;// 比较前一个元素是否大于后一个元素
	public static final int OP_THROW = 0x0F;// 将栈顶元素封装进VMRuntimeException并抛出

	public static final int OP_INC = 0x10;// 自加指令
	public static final int OP_DEC = 0x11;// 自减指令
	public static final int OP_LT = 0x12;// 比较前一个元素是否小于后一个元素
	public static final int OP_MOD = 0x13;// 取模指令
	public static final int OP_MUL = 0x14;// 乘法指令
	public static final int OP_NEG = 0x15;// 负数指令
	public static final int OP_NEW_ARR = 0x16;// 向栈顶压入一个新的数组对象
	public static final int OP_NEW_OBJ = 0x17;// 向栈顶压入一个新的对象
	public static final int OP_NEW = 0x18;// 压入一个新对象
	public static final int OP_NOT = 0x19;// 逻辑非指令
	public static final int OP_OR = 0x1a;// 按位或指令
	public static final int OP_DROP = 0x1b;// 移除栈顶元素
	public static final int OP_PUSH_TRUE = 0x1c;// 将true压入栈顶
	public static final int OP_PUSH_FALSE = 0x1d;// 将false压入栈顶
	public static final int OP_RET = 0x1e;// 从一个函数调用中返回
	public static final int OP_CTX_SET = 0x1f;// 将变量设置给上下文

	public static final int OP_SET_KC = 0x020;// 
	public static final int OP_SET = 0x021;//
	public static final int OP_SHL = 0x022;// 无符号左移
	public static final int OP_SHR = 0x023;// 无符号右移
	public static final int OP_SUB = 0x024;// 减法指令
	public static final int OP_SWAP = 0x025;// 交换栈顶的2个元素
	public static final int OP_PUSH_THIS = 0x026;// 将this指针压入栈顶
	public static final int OP_PUSH_NULL = 0x027;// 将null压入栈顶
	public static final int OP_PUSH_UNDEF = 0x028;// 将一个未定义对象压入栈顶
	public static final int OP_DDUP = 0x29;// 复制栈顶头2个元素
	public static final int OP_ROT = 0x2A;// 轮换栈顶3个元素的位置
	public static final int OP_EQEQEQ = 0x2B;// 判断2个元素是否严格相等
	public static final int OP_XOR = 0x2C;// 异或指令
	public static final int OP_INV = 0x2D;// 取反指令
	public static final int OP_WITH_START = 0x2E;
	public static final int OP_WITH_END = 0x2F;

	public static final int OP_ABOVE = 0x30;// 获取栈顶下第二了元素
	public static final int OP_INSTANCEOF = 0x31;// instanceof指令
	public static final int OP_TYPEOF = 0x32;// typeof指令
	public static final int OP_PUSH_GLOBAL = 0x33;
	// ///////////////////////////////////////////////////////////////////

	// ///////////////// 以下是需要跟一个立即数的特殊操作符 /////////////////
	public static final int XOP_TRY_CALL = 0xE6 >>> 1;
	public static final int XOP_ADD = 0xE8 >>> 1; // 将立即数加到栈顶，务必要与OP_ADD区分开
	public static final int XOP_PUSH_FN = 0xEA >>> 1;
	public static final int XOP_PUSH_NUM = 0xEC >>> 1;
	public static final int XOP_GO = 0xEE >>> 1;
	public static final int XOP_IF = 0xF0 >>> 1;
	public static final int XOP_CALL = 0xF2 >>> 1;
	public static final int XOP_LCL_GET = 0xF6 >>> 1;
	public static final int XOP_LCL_SET = 0xF8 >>> 1;
	public static final int XOP_NEXT = 0xFA >>> 1;
	public static final int XOP_PUSH_INT = 0xFC >>> 1;
	public static final int XOP_PUSH_STR = 0xFE >>> 1;
	// ///////////////////////////////////////////////////////////////////

	// ////////////////////////////// 块标识 //////////////////////////////
	public static final byte BLOCK_END = 0x00;
	public static final byte BLOCK_GLOBAL_STRING_TABLE = 0x10;
	public static final byte BLOCK_NUMBER_LITERALS = 0x20;
	public static final byte BLOCK_STRING_LITERALS = 0x30;
	public static final byte BLOCK_REGEX_LITERALS = 0x40;
	public static final byte BLOCK_FUNCTION_LITERALS = 0x50;
	public static final byte BLOCK_LOCAL_VARIABLE_NAMES = 0x60;
	public static final byte BLOCK_BYTE_CODE = 0x70;
	public static final byte BLOCK_LINE_NUMBERS = 0x7F;
	// ///////////////////////////////////////////////////////////////////

	// ///////////////////////////// 对象类型 /////////////////////////////
	public static final int TYPE_UNDEFINED = 0;
	public static final int TYPE_NULL = 1;
	public static final int TYPE_OBJECT = 2;
	public static final int TYPE_BOOLEAN = 3;
	public static final int TYPE_NUMBER = 4;
	public static final int TYPE_STRING = 5;
	public static final int TYPE_FUNCTION = 6;
	// ///////////////////////////////////////////////////////////////////
	/**
	 * JavaScript的对象类型。当使用 <code><b>typeof</b></code>运算符时，返回相应类型的名称。
	 * <p>
	 * 注意：你可能发现数组中有2个"object"，这是因为{@link #TYPE_NULL}和{@link #TYPE_OBJECT}
	 * 都映射到object
	 */
	private static final String[] TYPE_NAMES = { "undefined", "object",
			"object", "boolean", "number", "string", "function" };

	private FunctionObject mainFunction = null;

	private GlobalObject global = null;

	private byte[] byteCode = null;

	/**
	 * 载入脚本
	 * 
	 * @param script
	 *            脚本源代码
	 * @throws ParserException
	 */
	public void loadScript(String script) throws ParserException {
		// 对脚本进行词法语法分析，生成抽象语法树
		Parser parser = Parser.getInstace();
		Program program = parser.parseProgram(script);
		parser = null;
		// 根据抽象语法树生成中间代码
		DeclarationInterpreter declarationInterpreter = new DeclarationInterpreter();
		declarationInterpreter.interpret(program);
		declarationInterpreter = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			CompilationInterpreter compilationInterpreter = new CompilationInterpreter(
					dos);
			compilationInterpreter.interpret(program);
			dos.flush();
			byteCode = baos.toByteArray();
		} catch (Exception e) {
			byteCode = null;
		} finally {
			try {
				baos.close();
			} catch (IOException e) {
			} finally {
				baos = null;
			}
			try {
				dos.close();
			} catch (IOException e) {
			} finally {
				dos = null;
			}
		}
	}

	/**
	 * 根据中间代码生成可执行对象(方法)。
	 * <p>
	 * 这些可执行对象是一个个{@link FunctionObject}
	 * 对象,因为JavaScript的执行是以函数(function)为核心的。所有的可执行对象({@link FunctionObject}
	 * )以树的形式进行组织，最上层是一个虚拟的<b>main</b>函数。这个虚拟的<b>main</b>函数是program级的，
	 * 我们可以将一个JavaScript脚本本身就看做是一个大的函数。
	 * 
	 * @param dis
	 *            ByteCode流
	 * @param globalStringTable
	 *            全局字符串表
	 * @return 可执行对象({@link FunctionObject})树
	 * @throws IOException
	 */
	private FunctionObject generateFunctionObject(DataInputStream dis,
			String[] globalStringTable) throws IOException {
		FunctionObject function = new FunctionObject(-1, -1);

		int flags = 0;
		loop: while (true) {
			int blockType = dis.read();
			int count;
			switch (blockType) {
			case BLOCK_GLOBAL_STRING_TABLE:// 读取全局字符串表
				count = dis.readUnsignedShort();
				globalStringTable = new String[count];
				for (int i = 0; i < count; i++) {
					globalStringTable[i] = dis.readUTF();
				}
				break;
			case BLOCK_STRING_LITERALS:// 读取字符串定义
				count = dis.readUnsignedShort();
				String[] stringLiterals = new String[count];
				for (int i = 0; i < count; i++) {
					stringLiterals[i] = globalStringTable[dis.readShort()];
				}
				function.stringLiterals = stringLiterals;
				stringLiterals = null;
				break;
			case BLOCK_NUMBER_LITERALS:// 读取数字定义
				count = dis.readUnsignedShort();
				double[] numberLiterals = new double[count];
				for (int i = 0; i < count; i++) {
					numberLiterals[i] = dis.readDouble();
				}
				function.numberLiterals = numberLiterals;
				numberLiterals = null;
				break;
			case BLOCK_FUNCTION_LITERALS:// 读取函数定义
				count = dis.readUnsignedShort();
				FunctionObject[] functionLiterals = new FunctionObject[count];
				for (int i = 0; i < count; i++) {
					FunctionObject newFunction = generateFunctionObject(dis,
							globalStringTable);
					functionLiterals[i] = newFunction;
					newFunction = null;
				}
				function.functionLiterals = functionLiterals;
				functionLiterals = null;
				break;
			case BLOCK_LOCAL_VARIABLE_NAMES:// 读取本地变量名
				count = dis.readUnsignedShort();
				String[] localNames = new String[count];
				for (int i = 0; i < count; i++) {
					localNames[i] = globalStringTable[dis.readShort()];
				}
				function.localVariableNames = localNames;
				localNames = null;
				break;
			case BLOCK_BYTE_CODE:// 读取byte code
				int varCount = dis.readUnsignedShort();
				int expectedParameterCount = dis.readUnsignedShort();
				varCount -= expectedParameterCount;
				function.varCount = varCount;
				function.expectedParameterCount = expectedParameterCount;
				flags = dis.read();
				byte[] byteCode = new byte[dis.readShort()];
				dis.readFully(byteCode);
				function.byteCode = byteCode;
				byteCode = null;
				break;
			case BLOCK_LINE_NUMBERS:
				count = dis.readUnsignedShort();
				int[] lineNumbers = new int[count * 2];
				for (int i = 0; i < count; i++) {
					lineNumbers[i << 1] = dis.readUnsignedShort();
					lineNumbers[(i << 1) + 1] = dis.readUnsignedShort();
				}
				function.lineNumbers = lineNumbers;
				lineNumbers = null;
				break;
			case BLOCK_END:
				break loop;
			default:
				throw new IOException("Illegal Block type "
						+ Integer.toString(blockType, 16));
			}
		}

		if ((flags & 0x01) == 0) {
			if (function.localVariableNames == null) {
				function.localVariableNames = new String[0];
			}
		} else {
			function.localVariableNames = null;
		}

		return function;
	}

	public Object exec(DataInputStream dis) throws IOException {
		mainFunction = new FunctionObject(-1, -1);
		mainFunction = generateFunctionObject(dis, null);
		ArrayObject stack = new ArrayObject();
		stack.setObject(0, global);
		stack.setObject(1, mainFunction);
		stack.setObject(2, null);

		eval(stack, 1, 0);
		return stack.getObject(3);
	}

	/**
	 * Evaluate this function. The this-pointer, function object and parameters
	 * must be on stack (sp + 0 = context, sp + 1=function, sp + 2 = first param
	 * etc.). The result is expected at sp + 0.
	 */
	public void eval(ArrayObject stack, int sp, int actualParameterCount) {
		VMObject thisPtr = stack.getVMObject(sp);
		FunctionObject function = (FunctionObject) stack.getVMObject(sp + 1);
		// 对于实际参数数量小于期望参数数量的情况, 缺失的参数用null代替
		for (int i = actualParameterCount; i < function.expectedParameterCount; i++) {
			stack.setObject(sp + i + 2, null);
		}

		if (function.byteCode == null) {
			thisPtr.evalNative(function.index, stack, sp, actualParameterCount);
			return;
		}

		// sp initially points to context
		// bp points to parameter 0. context is at bp-2, lambda at bp-1

		sp += 2;
		int bp = sp;

		VMObject context = null;

		// note: arguments available here only!
		if (function.localVariableNames != null) {
			context = new VMObject(VMObject.OBJECT_PROTOTYPE);
			context.scopeChain = function.context;
			VMArgument args = new VMArgument(function, context);
			for (int i = 0; i < function.expectedParameterCount; i++) {
				context.addProperty(function.localVariableNames[i], stack
						.getObject(sp + i));
				args.addVar(String.valueOf(i), new Integer(i));
			}
			for (int i = function.expectedParameterCount; i < function.localVariableNames.length; i++) {
				context.addProperty(function.localVariableNames[i], null);
			}
			for (int i = function.expectedParameterCount; i < actualParameterCount; i++) {
				args.setObject(String.valueOf(i), stack.getObject(bp + i));
			}
			args.setNumber("length", actualParameterCount);
			args.setCaller(function);
			context.addProperty("arguments", args);
			args = null;
		} else {
			context = function.context;
			sp += function.expectedParameterCount + function.varCount;
		}

		int initialSp = sp;
		int opcode;
		byte[] byteCode = function.byteCode;
		int pc = 0;
		int end = byteCode.length;

		try {
			while (pc < end) {
				opcode = byteCode[pc++];

				if (opcode < 0) {
					int imm;

					if ((opcode & 0x01) == 0) {
						imm = byteCode[pc++];
					} else {
						imm = (byteCode[pc] << 8) | (byteCode[pc + 1] & 0xFF);
						pc += 2;
					}

					switch ((opcode & 0xFF) >>> 1) {
					case XOP_ADD:
						stack.setNumber(sp - 1, stack.getNumber(sp - 1) + imm);
						break;

					case XOP_TRY_CALL:
						try {
							sp = sp - imm - 2; // on stack: context, lambda,
							// params
							FunctionObject m = (FunctionObject) stack
									.getObject(sp + 1);
							m.eval(stack, sp, imm);
							stack.setBoolean(sp + 1, true);
							sp += 2;
						} catch (VMRuntimeException e) {
							stack.setObject(sp++, e.getError());
							stack.setBoolean(sp++, false); // not successfull
						} catch (Exception e) {
							stack.setObject(sp++, new ErrorObject());
							stack.setBoolean(sp++, false); // not successfull
						}
						break;

					case XOP_CALL:
						sp = sp - imm - 2; // on stack: context, lambda, params
						FunctionObject m = (FunctionObject) stack
								.getObject(sp + 1);
						m.eval(stack, sp++, imm);
						// System.out.println("Ret val received: "
						// + stack.getObject(sp-1)+" sp: "+sp);
						break;

					case XOP_PUSH_FN:
						stack.setObject(sp++, new FunctionObject(
								function.functionLiterals[imm], context));
						break;

					case XOP_GO:
						pc += imm;
						break;

					case XOP_IF:
						if (!stack.getBoolean(--sp)) {
							pc += imm;
						}
						break;

					case XOP_PUSH_INT:
						stack.setNumber(sp++, imm);
						break;

					case XOP_LCL_GET:
						stack.copy(bp + imm, stack, sp++);
						break;

					// case XOP_LOCAL_INC:
					// stack.setFP(sp - 1, stack.getFP(sp - 1)
					// + stack.getFP(bp + imm));
					// // fall-through!
					case XOP_LCL_SET:
						stack.copy(sp - 1, stack, bp + imm);
						break;

					case XOP_NEXT:
						Enumeration e = (Enumeration) stack.getObject(sp - 1);
						if (e.hasMoreElements()) {
							stack.setObject(sp++, e.nextElement());
						} else {
							pc += imm;
						}
						break;
					case XOP_PUSH_NUM:
						stack.setNumber(sp++, function.numberLiterals[imm]);
						break;

					case XOP_PUSH_STR:
						// System.out.println("String:" +
						// stringList[(int)param]);
						stack.setObject(sp++, function.stringLiterals[imm]);
						break;

					default:
						throw new RuntimeException("Illegal opcode: "
								+ Integer.toString(opcode & 0xff, 16)
								+ " par: " + imm);
					} // switch
				} else {
					switch (opcode) {

					case OP_ADD:
						if (stack.isNumber(sp - 2) && stack.isNumber(sp - 1)) {
							stack.setNumber(sp - 2, stack.getNumber(sp - 2)
									+ stack.getNumber(sp - 1));
						} else {
							stack.setObject(sp - 2, stack.getString(sp - 2)
									+ stack.getString(sp - 1));
						}
						sp--;
						break;

					case OP_AND:
						stack.setNumber(sp - 2, stack.getInt(sp - 2)
								& stack.getInt(sp - 1));
						sp--;
						break;

					case OP_APPEND:
						ArrayObject arr = (ArrayObject) stack.getObject(sp - 2);
						stack.copy(sp - 1, arr, arr.size());
						// ((Array)
						// stack.getObject(sp-2)).addElement(stack.getObject(sp-1));
						sp--;
						arr = null;
						break;

					case OP_ASR:
						stack.setNumber(sp - 2,
								(stack.getInt(sp - 2) & 0xFFFFFFFFL) >>> (stack
										.getInt(sp - 1) & 0x1F));
						sp--;
						break;

					case OP_CTX_GET:
						context.vmGetOperation(stack, sp - 1, sp - 1);
						break;

					case OP_CTX_SET:
						context.vmSetOperation(stack, sp - 1, sp - 2);
						sp--; // take away name, not value
						break;

					case OP_CTX:
						stack.setObject(sp++, context);
						break;

					case OP_DEC:
						stack.setNumber(sp - 1, stack.getNumber(sp - 1) - 1);
						break;

					case OP_DEL:
						stack.setBoolean(sp - 2, stack.getVMObject(sp - 2)
								.delete(stack.getString(sp - 1)));
						sp--;
						break;

					case OP_DIV:
						stack.setNumber(sp - 2, stack.getNumber(sp - 2)
								/ stack.getNumber(sp - 1));
						sp--;
						break;

					case OP_DROP:
						sp--;
						break;

					case OP_DUP:
						stack.copy(sp - 1, stack, sp);
						sp++;
						break;

					case OP_DDUP:
						stack.copy(sp - 2, stack, sp, 2);
						sp += 2;
						break;

					case OP_ENUM:
						stack.setObject(sp - 1, ((VMObject) stack
								.getObject(sp - 1)).keys());
						break;

					case OP_EQEQEQ:
						if (stack.getType(sp - 2) != stack.getType(sp - 1)) {
							sp--;
							stack.setObject(sp - 1, Boolean.FALSE);
							break;
						}
						// fall-trough

					case OP_EQEQ:
						// System.out.println(""+stack.getObject(sp-2)+ " = "+
						// stack.getObject(sp-1));

						int tX = stack.getType(sp - 2);
						int tY = stack.getType(sp - 1);

						if (tX == tY) {
							switch (tX) {
							case TYPE_UNDEFINED:
							case TYPE_NULL:
								stack.setObject(sp - 2, Boolean.TRUE);
								break;
							case TYPE_NUMBER:
								stack.setBoolean(sp - 2, stack
										.getNumber(sp - 2) == stack
										.getNumber(sp - 1));
								break;

							default:
								stack.setBoolean(sp - 2, stack
										.getObject(sp - 2).equals(
												stack.getObject(sp - 1)));
							}
						} else {
							boolean result;
							if ((tX == TYPE_UNDEFINED && tY == TYPE_NULL)
									|| (tX == TYPE_NULL && tY == TYPE_UNDEFINED)) {
								result = true;
							} else if (tX == TYPE_NUMBER || tY == TYPE_NUMBER) {
								result = stack.getNumber(sp - 2) == stack
										.getNumber(sp - 1);
							} else if ((tX == TYPE_STRING && tY == TYPE_OBJECT)
									|| tX == TYPE_OBJECT && tY == TYPE_STRING) {
								result = stack.getString(sp - 2).equals(
										stack.getString(sp - 1));
							} else {
								result = false;
							}
							stack.setBoolean(sp - 2, result);
						}
						sp--;
						break;

					case OP_GET:
						VMObject ctx = stack.getVMObject(sp - 2);
						// System.out.println("GetMember ctx: "+ctx);
						// System.out.println("GetMember name: " +
						// stack.getObject(sp - 1));
						ctx.vmGetOperation(stack, sp - 1, sp - 2);
						sp--;
						break;

					case OP_GT:
						if (stack.isNumber(sp - 2) && stack.isNumber(sp - 1)) {
							stack.setObject(sp - 2,
									stack.getNumber(sp - 2) > stack
											.getNumber(sp - 1) ? Boolean.TRUE
											: Boolean.FALSE);
						} else {
							stack
									.setObject(
											sp - 2,
											stack.getString(sp - 2).compareTo(
													stack.getString(sp - 1)) > 0 ? Boolean.TRUE
													: Boolean.FALSE);
						}
						sp--;
						break;

					case OP_IN:
						Object o = stack.getObject(sp - 1);
						if (o instanceof ArrayObject && stack.isNumber(sp - 2)) {
							int i = stack.getInt(sp - 2);
							stack
									.setObject(
											sp - 2,
											i >= 0
													&& i <= ((ArrayObject) o)
															.size() ? Boolean.TRUE
													: Boolean.FALSE);
							sp--;
							break;
						}
						if (o instanceof VMObject) {
							stack
									.setObject(
											sp - 2,
											((VMObject) o)
													.getRawInPrototypeChain(stack
															.getString(sp - 2)) == null ? Boolean.TRUE
													: Boolean.FALSE);
							sp--;
							break;
						}
						stack.setObject(sp - 2, Boolean.FALSE);
						sp--;
						break;

					case OP_INC:
						stack.setNumber(sp - 1, stack.getNumber(sp - 1) + 1);
						break;

					case OP_INV:
						stack.setInt(sp - 1, ~stack.getInt(sp - 1));
						break;

					case OP_LT:
						if (stack.isNumber(sp - 2) && stack.isNumber(sp - 1)) {
							stack.setObject(sp - 2,
									stack.getNumber(sp - 2) < stack
											.getNumber(sp - 1) ? Boolean.TRUE
											: Boolean.FALSE);
						} else {
							stack
									.setObject(
											sp - 2,
											stack.getString(sp - 2).compareTo(
													stack.getString(sp - 1)) < 0 ? Boolean.TRUE
													: Boolean.FALSE);
						}
						sp--;
						break;

					case OP_MOD:
						stack.setNumber(sp - 2,
								(stack.getNumber(sp - 2) % (stack
										.getNumber(sp - 1))));
						sp--;
						break;

					case OP_MUL:
						stack.setNumber(sp - 2, stack.getNumber(sp - 2)
								* stack.getNumber(sp - 1));
						sp--;
						break;

					case OP_NEW_ARR:
						stack.setObject(sp++, new ArrayObject());
						break;

					case OP_NEW:
						FunctionObject constructor = ((FunctionObject) stack
								.getObject(sp - 1));
						ctx = VMObjectFactory
								.newInstance(constructor.factoryTypeId);
						stack.setObject(sp - 1, ctx);
						stack.setObject(sp++, ctx);
						stack.setObject(sp++, constructor);
						break;

					case OP_NEW_OBJ:
						stack.setObject(sp++, new VMObject(null));
						break;

					case OP_NEG:
						stack.setNumber(sp - 1, -stack.getNumber(sp - 1));
						break;

					case OP_NOT:
						stack.setObject(sp - 1,
								stack.getBoolean(sp - 1) ? Boolean.FALSE
										: Boolean.TRUE);
						break;

					case OP_OR:
						stack.setNumber(sp - 2, stack.getInt(sp - 2)
								| stack.getInt(sp - 1));
						sp--;
						break;

					case OP_PUSH_FALSE:
						stack.setObject(sp++, Boolean.FALSE);
						break;

					case OP_PUSH_GLOBAL:
						stack.setObject(sp++, stack.getObject(0));
						break;

					case OP_PUSH_NULL:
						stack.setObject(sp++, VMUtils.EMPTY_OBJECT);
						break;

					case OP_PUSH_THIS:
						stack.setObject(sp++, thisPtr);
						break;

					case OP_PUSH_TRUE:
						stack.setObject(sp++, Boolean.TRUE);
						break;

					case OP_PUSH_UNDEF:
						stack.setObject(sp++, null);
						break;

					case OP_RET:
						// System.out.println("sp: "+sp+" returning:
						// "+stack.getObject(sp-1));
						stack.copy(sp - 1, stack, bp - 2);
						return;

					case OP_ROT:
						stack.copy(sp - 3, stack, sp - 2, 3);
						stack.copy(sp, stack, sp - 3);
						break;

					case OP_SET_KC:
						// ctx: sp-3
						// property name: sp-2
						// value to set: sp-1;

						ctx = stack.getVMObject(sp - 3);
						ctx.vmSetOperation(stack, sp - 2, sp - 1);

						// key = (String) stack.getObject(sp-2);
						// Object curr = ctx.getRaw(key);
						// System.out.println("SetMember KC ctx: "+ctx);
						// System.out.println("SetMember name: "+stack.getObject(sp-2));
						// System.out.println("SetMember value: "+stack.getObject(sp-1));

						sp -= 2; // leave value on the stack(!)
						break;

					case OP_SET:
						ctx = stack.getVMObject(sp - 2);
						ctx.vmSetOperation(stack, sp - 1, sp - 3);

						// key = (String) stack.getObject(sp-1);
						// curr = ctx.getRaw(key);

						// System.out.println("SetMember KV ctx: "+ctx);
						// System.out.println("SetMember name: "+stack.getObject(sp-1));
						// System.out.println("SetMember value: "+stack.getObject(sp-3));

						sp -= 2; // leave value on the stack(!)
						break;

					case OP_SHR:
						stack.setNumber(sp - 2, stack.getInt(sp - 2) >> (stack
								.getInt(sp - 1) & 0x1f));
						sp--;
						break;

					case OP_SHL:
						stack.setNumber(sp - 2, stack.getInt(sp - 2) << (stack
								.getInt(sp - 1) & 0x1f));
						sp--;
						break;

					case OP_SUB:
						stack.setNumber(sp - 2, stack.getNumber(sp - 2)
								- stack.getNumber(sp - 1));
						sp--;
						break;

					case OP_SWAP:
						stack.swap(sp - 1, sp - 2);
						break;

					case OP_THROW:
						// line number is added in try..catch below
						throw new VMRuntimeException(stack.getVMObject(sp));

					case OP_WITH_START:
						VMObject nc = new VMObject((VMObject) stack
								.getObject(sp - 1));
						nc.scopeChain = context;
						context = nc;
						sp--;
						break;

					case OP_WITH_END:
						context = context.scopeChain;
						break;

					case OP_TYPEOF:
						stack.setObject(sp - 1, TYPE_NAMES[stack
								.getType(sp - 1)]);
						break;

					case OP_INSTANCEOF:
						o = stack.getObject(sp - 2);
						VMObject p = stack.getVMObject(sp - 1);
						if (p instanceof FunctionObject
								&& o instanceof VMObject) {
							VMObject j = ((VMObject) o);
							p = ((FunctionObject) p).parentPrototype;
							while (j.parentPrototype != null
									&& j.parentPrototype != p) {
								j = j.parentPrototype;
							}
							stack.setBoolean(sp - 2, j != null);
						} else {
							stack.setObject(sp - 2, Boolean.FALSE);
						}
						sp--;
						break;

					case OP_XOR:
						stack.setNumber(sp - 2, stack.getInt(sp - 2)
								^ stack.getInt(sp - 1));
						sp--;
						break;

					default:
						throw new RuntimeException("Illegal opcode: '"
								+ ((char) opcode) + "'/" + opcode);
					}
				}
			}
		} catch (Exception e) {
			VMRuntimeException jse;
			if (e instanceof VMRuntimeException) {
				jse = (VMRuntimeException) e;
			} else {
				e.printStackTrace();
				jse = new VMRuntimeException(e);
			}
			if (jse.pc == -1) {
				jse.pc = pc - 1;
				jse.lineNumber = function.getLineNumber(jse.pc);
			}
			throw jse;
		}

		if (sp == initialSp + 1) {
			// System.out.println("sp: "+sp+" returning: "+stack.getObject(sp-1));
			stack.copy(sp - 1, stack, bp - 2);
		} else if (sp == initialSp) {
			// System.out.println("sp: "+sp+" returning NULL");
			stack.setObject(bp - 2, null);
		} else {
			throw new RuntimeException(
					"too much or too little on the stack; sp: " + sp + " bp: "
							+ bp + " varCount: " + function.varCount
							+ " parCount: " + actualParameterCount);
		}
		return;
	}
}
