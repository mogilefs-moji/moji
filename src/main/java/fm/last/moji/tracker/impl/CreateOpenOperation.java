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
package fm.last.moji.tracker.impl;

import static fm.last.moji.tracker.impl.ErrorCode.UNKNOWN_CLASS;
import static fm.last.moji.tracker.impl.ErrorCode.UNKNOWN_KEY;
import static fm.last.moji.tracker.impl.ResponseStatus.OK;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fm.last.moji.tracker.Destination;
import fm.last.moji.tracker.TrackerException;
import fm.last.moji.tracker.UnknownStorageClassException;

class CreateOpenOperation {
  private final String domain;
  private final String key;
  private final String storageClass;
  private final boolean multipleDestinations;
  private final RequestHandler requestHandler;

  private List<Destination> destinations;

  CreateOpenOperation(RequestHandler requestHandler, String domain, String key, String storageClass,
      boolean multipleDestinations) {
    this.requestHandler = requestHandler;
    this.domain = domain;
    this.key = key;
    this.storageClass = storageClass;
    this.multipleDestinations = multipleDestinations;
    destinations = Collections.emptyList();
  }

  public void execute() throws TrackerException {
    Request request = buildRequest();
    Response response = requestHandler.performRequest(request);
    if (response.getStatus() != OK) {
      if (UNKNOWN_CLASS.isContainedInLine(response.getMessage())) {
        throw new UnknownStorageClassException(storageClass);
      }
      if (!UNKNOWN_KEY.isContainedInLine(response.getMessage())) {
        throw new TrackerException(response.getMessage());
      }
    } else {
      extractReturnValues(response);
    }
  }

  List<Destination> getDestinations() {
    return destinations;
  }

  private Request buildRequest() {
    Request.Builder builder = new Request.Builder(4).command("create_open").arg("domain", domain).arg("key", key)
        .arg("multi_dest", multipleDestinations);
    if (storageClass != null && !storageClass.isEmpty()) {
      builder.arg("class", storageClass);
    }
    Request request = builder.build();
    return request;
  }

  private void extractReturnValues(Response response) throws TrackerException {
    long fid = Long.parseLong(response.getValue("fid"));
    int pathCount = Integer.parseInt(response.getValue("dev_count"));
    destinations = new ArrayList<Destination>(pathCount);

    for (int i = 1; i <= pathCount; i++) {
      URL url;
      Integer devId;
      try {
        url = new URL(response.getValue("path_" + i));
        devId = Integer.valueOf(response.getValue("devid_" + i));
        destinations.add(new Destination(url, devId, fid));
      } catch (MalformedURLException e) {
        throw new TrackerException(e);
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("CreateOpenCommand [domain=");
    builder.append(domain);
    builder.append(", key=");
    builder.append(key);
    builder.append(", storageClass=");
    builder.append(storageClass);
    builder.append(", multipleDestinations=");
    builder.append(multipleDestinations);
    builder.append("]");
    return builder.toString();
  }

}
