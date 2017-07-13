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

enum DeviceStatusField {
  UTILIZATION("utilization", Float.class),
  OBSERVED_STATE("observed_state", String.class),
  CAPACITY_TOTAL("mb_total", Long.class),
  CAPACITY_USED("mb_used", Long.class),
  CAPACITY_FREE("mb_free", Long.class),
  REJECT_BAD_MD5("reject_bad_md5", Boolean.class),
  WEIGHT("weight", Integer.class),
  DEVICE_ID("devid", Integer.class),
  STATUS("status", String.class),
  HOST_ID("hostid", Integer.class);

  private final String fieldName;
  private final Class<?> destinationType;

  private DeviceStatusField(String fieldName, Class<?> destinationType) {
    this.fieldName = fieldName.toLowerCase();
    this.destinationType = destinationType;
  }

  String getFieldName() {
    return fieldName;
  }

  Class<?> getDestinationType() {
    return destinationType;
  }

}
