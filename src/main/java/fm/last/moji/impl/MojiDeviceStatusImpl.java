/**
 * Copyright (C) 2012-2017 Last.fm & The "mogilefs-moji" committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fm.last.moji.impl;

import static fm.last.moji.impl.DeviceStatusField.CAPACITY_FREE;
import static fm.last.moji.impl.DeviceStatusField.CAPACITY_TOTAL;
import static fm.last.moji.impl.DeviceStatusField.CAPACITY_USED;
import static fm.last.moji.impl.DeviceStatusField.DEVICE_ID;
import static fm.last.moji.impl.DeviceStatusField.HOST_ID;
import static fm.last.moji.impl.DeviceStatusField.OBSERVED_STATE;
import static fm.last.moji.impl.DeviceStatusField.REJECT_BAD_MD5;
import static fm.last.moji.impl.DeviceStatusField.STATUS;
import static fm.last.moji.impl.DeviceStatusField.UTILIZATION;
import static fm.last.moji.impl.DeviceStatusField.WEIGHT;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.commons.lang.units.IecByteUnit;
import fm.last.moji.MojiDeviceStatus;

class MojiDeviceStatusImpl implements MojiDeviceStatus {

  private static final Logger log = LoggerFactory.getLogger(MojiDeviceStatusImpl.class);

  private final Map<String, String> valueMap;
  private final String deviceName;

  MojiDeviceStatusImpl(String deviceName, Map<String, String> valueMap) {
    this.deviceName = deviceName;
    this.valueMap = valueMap;
  }

  /**
   * Percentage of utilization in float of scale .00
   */
  @Override
  public Float getUtilization() {
    return (Float) getValue(UTILIZATION);
  }

  @Override
  public String getObservedState() {
    return (String) getValue(OBSERVED_STATE);
  }

  @Override
  public Boolean getRejectBadMd5() {
    return (Boolean) getValue(REJECT_BAD_MD5);
  }

  /**
   * Converted from mebibytes - will always be a multiple of 1048576.
   */
  @Override
  public Long getCapacityUsedBytes() {
    Long used = (Long) getValue(CAPACITY_USED);
    if (used == null) {
      return null;
    }
    return IecByteUnit.MEBIBYTES.toBytes(used);
  }

  /**
   * Converted from mebibytes - will always be a multiple of 1048576.
   */
  @Override
  public Long getCapacityFreeBytes() {
    Long free = (Long) getValue(CAPACITY_FREE);
    if (free == null) {
      return null;
    }
    return IecByteUnit.MEBIBYTES.toBytes(free);
  }

  /**
   * Converted from mebibytes - will always be a multiple of 1048576.
   */
  @Override
  public Long getCapacityTotalBytes() {
    Long total = (Long) getValue(CAPACITY_TOTAL);
    if (total == null) {
      return null;
    }
    return IecByteUnit.MEBIBYTES.toBytes(total);
  }

  @Override
  public Integer getWeight() {
    return (Integer) getValue(WEIGHT);
  }

  @Override
  public Integer getId() {
    return (Integer) getValue(DEVICE_ID);
  }

  @Override
  public String getStatus() {
    return (String) getValue(STATUS);
  }

  @Override
  public Integer getHostId() {
    return (Integer) getValue(HOST_ID);
  }

  @Override
  public String getDeviceName() {
    return deviceName;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder(deviceName);
    builder.append('[');
    boolean first = true;
    for (DeviceStatusField field : DeviceStatusField.values()) {
      if (first) {
        first = false;
      } else {
        builder.append(',');
      }
      builder.append(field);
      builder.append('=');
      builder.append(getValue(field));
    }
    builder.append(']');
    return builder.toString();
  }

  private Object getValue(DeviceStatusField field) {
    String value = valueMap.get(field.getFieldName());
    if (value == null) {
      log.debug("Field " + field + " not present in status response.");
      return null;
    }
    Class<?> destinationType = field.getDestinationType();
    try {
      if (destinationType == String.class) {
        return value;
      } else if (destinationType == Float.class) {
        return Float.parseFloat(value);
      } else if (destinationType == Long.class) {
        return Long.parseLong(value);
      } else if (destinationType == Integer.class) {
        return Integer.parseInt(value);
      } else if (destinationType == Boolean.class) {
        return Integer.parseInt(value) > 0;
      }
    } catch (NumberFormatException e) {
      log.warn("Could not convert " + field + " field value to " + destinationType + ": " + value, e);
    }
    throw new IllegalStateException("Unknown conversion type " + destinationType + " for field: " + field);
  }

}
