package fm.last.moji.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.moji.MojiDeviceStatus;
import fm.last.moji.tracker.Tracker;

class GetDeviceStatusesCommand implements MojiCommand {

  private final String domain;
  private List<MojiDeviceStatus> deviceStatuses;

  GetDeviceStatusesCommand(String domain) {
    this.domain = domain;
    deviceStatuses = Collections.emptyList();
  }

  @Override
  public void executeWithTracker(Tracker tracker) throws IOException {
    Map<String, Map<String, String>> parametersByDevice = tracker.getDeviceStatuses(domain);
    List<MojiDeviceStatus> statuses = new ArrayList<MojiDeviceStatus>(parametersByDevice.size());
    for (Map.Entry<String, Map<String, String>> deviceParameters : parametersByDevice.entrySet()) {
      statuses.add(new MojiDeviceStatusImpl(deviceParameters.getKey(), deviceParameters.getValue()));
    }
    deviceStatuses = Collections.unmodifiableList(statuses);
  }

  List<MojiDeviceStatus> getStatuses() {
    return deviceStatuses;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder(getClass().getSimpleName());
    builder.append(" [domain=");
    builder.append(domain);
    builder.append(", deviceStatuses=");
    builder.append(deviceStatuses);
    builder.append("]");
    return builder.toString();
  }

}
