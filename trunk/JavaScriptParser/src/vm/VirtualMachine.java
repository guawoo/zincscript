package vm;

import java.util.Enumeration;

public class VirtualMachine {
	/**
	 * Evaluate this function. The this-pointer, function object and parameters
	 * must be on stack (sp + 0 = context, sp + 1=function, sp + 2 = first param
	 * etc.). The result is expected at sp + 0.
	 */
	public void run(JsArray stack, int sp, int actualParameterCount) {
		// 对于实际参数数量小于期望参数数量的情况, 缺失的参数用用null代替
		for (int i = actualParameterCount; i < expectedParameterCount; i++) {
			stack.setObject(sp + i + 2, null);
		}

		JsObject thisPtr = stack.getJsObject(sp);

		if (byteCode == null) {
			thisPtr.evalNative(index, stack, sp, actualParameterCount);
			return;
		}

		// sp initially points to context
		// bp points to parameter 0. context is at bp-2, lambda at bp-1

		sp += 2;
		int bp = sp;

		JsObject context;

		// note: arguments available here only!
		if (localNames != null) {
			context = new JsObject(JsObject.OBJECT_PROTOTYPE);
			context.scopeChain = this.context;
			JsArguments args = new JsArguments(this, context);
			for (int i = 0; i < expectedParameterCount; i++) {
				context.addVar(localNames[i], stack.getObject(sp + i));
				args.addVar("" + i, new Integer(i));
			}
			for (int i = expectedParameterCount; i < this.localNames.length; i++) {
				context.addVar(localNames[i], null);
			}
			for (int i = expectedParameterCount; i < actualParameterCount; i++) {
				args.setObject("" + i, stack.getObject(bp + i));
			}
			args.setNumber("length", actualParameterCount);
			args.setObject("callee", this);
			context.addVar("arguments", args);
		} else {
			context = this.context;
			sp += expectedParameterCount + varCount;
		}

		int initialSp = sp;
		int opcode;
		byte[] byteCode = this.byteCode;
		int pc = 0;
		int end = byteCode.length;

		try {
			while (pc < end) {
				opcode = byteCode[pc++];

				if (opcode < 0) {
					int imm;

					if ((opcode & 1) == 0) {
						imm = byteCode[pc++];
					} else {
						imm = (byteCode[pc] << 8) | (byteCode[pc + 1] & 255);
						pc += 2;
					}

					switch ((opcode & 0x0ff) >>> 1) {
					case XOP_ADD:
						stack.setNumber(sp - 1, stack.getNumber(sp - 1) + imm);
						break;

					case XOP_TRY_CALL:
						try {
							sp = sp - imm - 2; // on stack: context, lambda,
							// params
							JsFunction m = (JsFunction) stack.getObject(sp + 1);
							m.eval(stack, sp, imm);
							stack.setBoolean(sp + 1, true);
							sp += 2;
						} catch (JsException e) {
							stack.setObject(sp++, e.getError());
							stack.setBoolean(sp++, false); // not successfull
						} catch (Exception e) {
							stack.setObject(sp++, new JsError(e));
							stack.setBoolean(sp++, false); // not successfull
						}
						break;

					case XOP_CALL:
						sp = sp - imm - 2; // on stack: context, lambda, params
						JsFunction m = (JsFunction) stack.getObject(sp + 1);
						m.eval(stack, sp++, imm);
						// System.out.println("Ret val received: "
						// + stack.getObject(sp-1)+" sp: "+sp);
						break;

					case XOP_PUSH_FN:
						stack.setObject(sp++, new JsFunction(
								functionLiterals[imm], context));
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
						stack.setNumber(sp++, numberLiterals[imm]);
						break;

					case XOP_PUSH_STR:
						// System.out.println("String:" +
						// stringList[(int)param]);
						stack.setObject(sp++, stringLiterals[imm]);
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
						JsArray arr = (JsArray) stack.getObject(sp - 2);
						stack.copy(sp - 1, arr, arr.size());
						// ((Array)
						// stack.getObject(sp-2)).addElement(stack.getObject(sp-1));
						sp--;
						break;

					case OP_ASR:
						stack.setNumber(sp - 2,
								(stack.getInt(sp - 2) & 0xffffffffL) >>> (stack
										.getInt(sp - 1) & 0x1f));
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
						stack.setBoolean(sp - 2, stack.getJsObject(sp - 2)
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
						stack.setObject(sp - 1, ((JsObject) stack
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
						JsObject ctx = stack.getJsObject(sp - 2);
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
						if (o instanceof JsArray && stack.isNumber(sp - 2)) {
							int i = stack.getInt(sp - 2);
							stack.setObject(sp - 2, i >= 0
									&& i <= ((JsArray) o).size() ? Boolean.TRUE
									: Boolean.FALSE);
							sp--;
							break;
						}
						if (o instanceof JsObject) {
							stack
									.setObject(
											sp - 2,
											((JsObject) o)
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
						stack.setObject(sp++, new JsArray());
						break;

					case OP_NEW:
						JsFunction constructor = ((JsFunction) stack
								.getObject(sp - 1));
						ctx = constructor.factory
								.newInstance(constructor.factoryTypeId);
						stack.setObject(sp - 1, ctx);
						stack.setObject(sp++, ctx);
						stack.setObject(sp++, constructor);
						break;

					case OP_NEW_OBJ:
						stack.setObject(sp++, new JsObject(OBJECT_PROTOTYPE));
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
						stack.setObject(sp++, JsSystem.JS_NULL);
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

						ctx = stack.getJsObject(sp - 3);
						ctx.vmSetOperation(stack, sp - 2, sp - 1);

						// key = (String) stack.getObject(sp-2);
						// Object curr = ctx.getRaw(key);
						// System.out.println("SetMember KC ctx: "+ctx);
						// System.out.println("SetMember name: "+stack.getObject(sp-2));
						// System.out.println("SetMember value: "+stack.getObject(sp-1));

						sp -= 2; // leave value on the stack(!)
						break;

					case OP_SET:
						ctx = stack.getJsObject(sp - 2);
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
						throw new JsException(stack.getJsObject(sp));

					case OP_WITH_START:
						JsObject nc = new JsObject((JsObject) stack
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
						JsObject p = stack.getJsObject(sp - 1);
						if (p instanceof JsFunction && o instanceof JsObject) {
							JsObject j = ((JsObject) o);
							p = ((JsFunction) p).prototype;
							while (j.__proto__ != null && j.__proto__ != p) {
								j = j.__proto__;
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
			JsException jse;
			if (e instanceof JsException) {
				jse = (JsException) e;
			} else {
				e.printStackTrace();
				jse = new JsException(e);
			}
			if (jse.pc == -1) {
				jse.pc = pc - 1;
				jse.lineNumber = getLineNumber(pc - 1);
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
							+ bp + " varCount: " + varCount + " parCount: "
							+ actualParameterCount);
		}
		return;
	}
}
