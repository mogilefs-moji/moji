package fm.last.moji.impl;

import java.io.IOException;
import java.util.Map;

import fm.last.moji.MojiFileAttributes;
import fm.last.moji.tracker.Tracker;

class GetAttributesCommand implements MojiCommand {

  private final String key;
  private final String domain;
  private MojiFileAttributes attributes;

  GetAttributesCommand(String key, String domain) {
    this.key = key;
    this.domain = domain;
  }

  @Override
  public void executeWithTracker(Tracker tracker) throws IOException {
    Map<String, String> valueMap = tracker.fileInfo(key, domain);
    if (!valueMap.isEmpty()) {
      attributes = new MojiFileAttributesImpl(valueMap);
    }
  }

  MojiFileAttributes getAttributes() {
    return attributes;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("GetAttributesCommand [key=");
    builder.append(key);
    builder.append(", domain=");
    builder.append(domain);
    builder.append("]");
    return builder.toString();
  }

}
