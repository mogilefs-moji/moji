package fm.last.moji.tracker;

public class UnknownCommandException extends TrackerException {

  private static final long serialVersionUID = 1L;

  private final String command;

  public UnknownCommandException(String command) {
    super("command=" + command);
    this.command = command;
  }

  public String getCommand() {
    return command;
  }

}
