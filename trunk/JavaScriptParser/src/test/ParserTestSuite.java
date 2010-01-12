package test;

import jmunit.framework.cldc11.TestSuite;

public class ParserTestSuite extends TestSuite {
	/**
	 * TestSuite Class constructor initializes the test suite.
	 */
	public ParserTestSuite() {
		super("ParserTestSuite");
		this.setupSuite();
	}

	/**
	 * This method adds all suite test cases to be run.
	 */
	private void setupSuite() {
		// JMUnit-BEGIN
		add(new TestPrimaryExpression());
		// JMUnit-END
	}

}
