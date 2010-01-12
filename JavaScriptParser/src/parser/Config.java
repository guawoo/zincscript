package parser;

public class Config {
  /**
   * Private constructor to prevent instantiation.
   */
  private Config() {
  }

  /**
   * Enable all debugging.
   */
  public static final boolean DEBUG_ALL = false;

  /**
   * Display the rewritten source before execution.
   */
  public static final boolean DEBUG_SOURCE = DEBUG_ALL | false;

  /**
   * Display the parse tree before execution.
   */
  public static final boolean DEBUG_PARSETREE = DEBUG_ALL | false;

  /**
   * Display the disassembly before execution.
   */
  public static final boolean DEBUG_DISSASSEMBLY = DEBUG_ALL | false;

  /**
   * Fast locals support.
   */
  public static final boolean FASTLOCALS = true;

  /**
   * Line number support.
   */
  public static final boolean LINENUMBER = true;
}
