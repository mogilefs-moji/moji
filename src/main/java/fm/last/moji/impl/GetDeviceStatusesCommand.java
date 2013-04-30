package fm.last.moji.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.moji.MojiDeviceStatus;
import fm.last.moji.tracker.Tracker;

class GetDeviceStatusesCommand implements MojiCommand {

  private static final Logger log = LoggerFactory.getLogger(GetDeviceStatusesCommand.class);

  private final String domain;
  private List<MojiDeviceStatus> deviceStatuses;

  GetDeviceStatusesCommand(String domain) {
    this.domain = domain;
    deviceStatuses = Collections.emptyList();
  }

  @Override
  public void executeWithTracker(Tracker tracker) throws IOException {
    Map<String, String> valueMap = tracker.getDeviceStatuses(domain);
    if (!valueMap.isEmpty()) {
      Map<String, Map<String, String>> parametersByDevice = new HashMap<String, Map<String, String>>();
      for (Map.Entry<String, String> entry : valueMap.entrySet()) {
        String parameterName = entry.getKey();
        boolean parameterAdded = false;
        // ignoring the parameter of number of devices
        if (!"devices".equalsIgnoreCase(parameterName)) {
          int delimiterPosition = parameterName.indexOf("_");
          if (parameterName.length() > 2 && delimiterPosition >= 0) {
            String deviceName = parameterName.substring(0, parameterName.indexOf("_"));
            parameterName = parameterName.substring(parameterName.indexOf("_") + 1).toLowerCase();
            Map<String, String> parameters = parametersByDevice.get(deviceName);
            if (parameters == null) {
              parameters = new HashMap<String, String>(DeviceStatusField.values().length);
              parametersByDevice.put(deviceName, parameters);
            }
            parameters.put(parameterName, entry.getValue());
            parameterAdded = true;
          }
        }
        if (!parameterAdded) {
          log.debug("Ignoring attribute named: " + parameterName);
        }
      }
      List<MojiDeviceStatus> statuses = new ArrayList<MojiDeviceStatus>(parametersByDevice.size());
      for (Map.Entry<String, Map<String, String>> deviceParameters : parametersByDevice.entrySet()) {
        statuses.add(new MojiDeviceStatusImpl(deviceParameters.getKey(), deviceParameters.getValue()));
      }
      deviceStatuses = Collections.unmodifiableList(statuses);
    }
  }

  List<MojiDeviceStatus> getStatuses() {
    return deviceStatuses;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("GetDeviceStatusesCommand [domain=");
    builder.append(domain);
    builder.append(", deviceStatuses=");
    builder.append(deviceStatuses);
    builder.append("]");
    return builder.toString();
  }

}
