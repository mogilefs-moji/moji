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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
