package fm.last.moji.impl;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.moji.MojiDevicesStatusAttributes;
import fm.last.moji.tracker.Tracker;

public class DevicesStatusCommand implements MojiCommand {
  private static final Logger log = LoggerFactory.getLogger(DevicesStatusCommand.class);

  private final String domain;
  private MojiDevicesStatusAttributes attributes;

  DevicesStatusCommand(String domain) {
    this.domain = domain;
  }

  @Override
  public void executeWithTracker(Tracker tracker) throws IOException {
    Map<String, String> valueMap = tracker.getDevicesStatus(domain);
    if (!valueMap.isEmpty()) {
      attributes = new MojiDevicesStatusAttributesImpl(valueMap);
    }
  }

  public MojiDevicesStatusAttributes getAttributes() {
    return attributes;
  }
}
