package fm.last.moji.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fm.last.moji.MojiDevicesStatusAttributes;

public class MojiDevicesStatusAttributesImpl implements MojiDevicesStatusAttributes {
  private List<MojiDeviceStatus> devices;

  public MojiDevicesStatusAttributesImpl(Map<String, String> valueMap) {
    devices = new LinkedList<MojiDeviceStatus>();
    Map<String, MojiDeviceStatus> deviceNameToDevice = new HashMap<String, MojiDeviceStatus>();
    for (Map.Entry<String, String> entry : valueMap.entrySet()) {
      String key = entry.getKey();
      // ignoring the attribute of number of devices
      if (!key.equalsIgnoreCase("devices")) {
        String devName = key.substring(0, key.indexOf("_"));
        String attributeName = key.substring(key.indexOf("_") + 1);
        if (deviceNameToDevice.get(devName) == null) {
          MojiDeviceStatus newDevice = new MojiDeviceStatus();
          newDevice.setDeviceName(devName);
          deviceNameToDevice.put(devName, newDevice);
        }
        MojiDeviceStatus device = deviceNameToDevice.get(devName);
        parseAttributeAndSetOnDevice(device, attributeName, entry.getValue());
      }
    }

    devices = new ArrayList<MojiDeviceStatus>(deviceNameToDevice.values());
  }

  private void parseAttributeAndSetOnDevice(MojiDeviceStatus device, String attributeName, String attributeValue) {
    // example for value map: dev1_observed_state=writeable, dev1_mb_total=28461, dev1_utilization=0.00,
    // dev2_mb_total=28461, dev2_reject_bad_md5=1, dev1_mb_used=3439... etc
    if (attributeName.equalsIgnoreCase("utilization")) {
      device.setUtilization(Float.parseFloat(attributeValue));
    } else if (attributeName.equalsIgnoreCase("observed_state")) {
      device.setObservedState(attributeValue);
    } else if (attributeName.equalsIgnoreCase("mb_total")) {
      device.setTotalMB(Long.valueOf(attributeValue));
    } else if (attributeName.equalsIgnoreCase("reject_bad_md5")) {
      device.setRejectBadMD5(Integer.valueOf(attributeValue));
    } else if (attributeName.equalsIgnoreCase("mb_used")) {
      device.setUsedMB(Long.valueOf(attributeValue));
    } else if (attributeName.equalsIgnoreCase("weight")) {
      device.setWeight(Integer.valueOf(attributeValue));
    } else if (attributeName.equalsIgnoreCase("devid")) {
      device.setDevId(Integer.valueOf(attributeValue));
    } else if (attributeName.equalsIgnoreCase("mb_free")) {
      device.setFreeMB(Long.valueOf(attributeValue));
    } else if (attributeName.equalsIgnoreCase("status")) {
      device.setDevStatus(attributeValue);
    } else if (attributeName.equalsIgnoreCase("hostid")) {
      device.setHostId(Integer.valueOf(attributeValue));
    }
  }

  @Override
  public List<MojiDeviceStatus> getDevices() {
    return devices;
  }

}
