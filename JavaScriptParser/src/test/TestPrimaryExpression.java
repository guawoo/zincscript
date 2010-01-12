package test;

import parser.Parser;
import jmunit.framework.cldc11.TestCase;
import ast.Program;

public class TestPrimaryExpression extends TestCase {
	private Parser parser = Parser.getInstace();

	public TestPrimaryExpression() {
		super(2, "TestPrimaryExpression");
	}

	public void test(int testNumber) throws Throwable {
		Program program = null;
		switch (testNumber) {
		case 0:
			program = parser.parseProgram("null;");
			printProgram(program);
			break;
		case 1:
			program = parser.parseProgram("var i = 0.8;");
			printProgram(program);
			break;
		}
	}

	private void printProgram(Program program) {
		if (program != null) {
			System.out.println(program.toString());
		} else {
			System.out.println("program is NULL");
		}
	}
}
