package fm.last.moji.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fm.last.moji.MojiDeviceStatus;
import fm.last.moji.tracker.Tracker;

class DevicesStatusCommand implements MojiCommand {

  private final String domain;
  private List<MojiDeviceStatus> deviceStatuses;

  DevicesStatusCommand(String domain) {
    this.domain = domain;
    deviceStatuses = Collections.emptyList();
  }

  @Override
  public void executeWithTracker(Tracker tracker) throws IOException {
    Map<String, String> valueMap = tracker.getDevicesStatuses(domain);
    if (!valueMap.isEmpty()) {
      Map<String, Map<String, String>> attributesByDevice = new HashMap<String, Map<String, String>>();
      for (Map.Entry<String, String> entry : valueMap.entrySet()) {
        String key = entry.getKey();
        // ignoring the attribute of number of devices
        if (!key.equalsIgnoreCase("devices")) {
          String deviceName = key.substring(0, key.indexOf("_"));
          String attributeName = key.substring(key.indexOf("_") + 1).toLowerCase();
          Map<String, String> attributes = attributesByDevice.get(deviceName);
          if (attributes == null) {
            attributes = new HashMap<String, String>(DeviceStatusField.values().length);
            attributesByDevice.put(deviceName, attributes);
          }
          attributes.put(attributeName, entry.getValue());
        }
      }
      List<MojiDeviceStatus> statuses = new ArrayList<MojiDeviceStatus>(attributesByDevice.size());
      for (Map.Entry<String, Map<String, String>> deviceAttributes : attributesByDevice.entrySet()) {
        statuses.add(new MojiDeviceStatusImpl(deviceAttributes.getKey(), deviceAttributes.getValue()));
      }
      deviceStatuses = Collections.unmodifiableList(statuses);
    }
  }

  List<MojiDeviceStatus> getStatuses() {
    return deviceStatuses;
  }

}
