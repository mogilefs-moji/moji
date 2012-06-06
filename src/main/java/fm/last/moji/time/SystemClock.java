package fm.last.moji.time;

/**
 * {@link Clock} implementation that delegates to {@link System#currentTimeMillis()}.
 */
public enum SystemClock implements Clock {
  INSTANCE;

  @Override
  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }

}
