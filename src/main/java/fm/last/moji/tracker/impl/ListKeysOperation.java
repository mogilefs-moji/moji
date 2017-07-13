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

import static fm.last.moji.tracker.impl.ErrorCode.NONE_MATCH;
import static fm.last.moji.tracker.impl.ResponseStatus.OK;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fm.last.moji.tracker.TrackerException;

class ListKeysOperation {

  private final RequestHandler requestHandler;
  private final String domain;
  private final String keyPrefix;
  private final Integer limit;
  private List<String> keys;

  ListKeysOperation(RequestHandler requestHandler, String domain, String keyPrefix, Integer limit) {
    this.requestHandler = requestHandler;
    this.domain = domain;
    this.keyPrefix = keyPrefix;
    this.limit = limit;
    keys = Collections.emptyList();
  }

  void execute() throws TrackerException {
    Request request = buildRequest();
    Response response = requestHandler.performRequest(request);
    if (response.getStatus() != OK) {
      if (!NONE_MATCH.isContainedInLine(response.getMessage())) {
        throw new TrackerException(response.getMessage());
      }
    } else {
      keys = extractReturnValue(response);
    }
  }

  List<String> getKeys() {
    return keys;
  }

  private Request buildRequest() {
    Request.Builder builder = new Request.Builder(3).command("list_keys").arg("domain", domain)
        .arg("prefix", keyPrefix);
    if (limit != null) {
      builder.arg("limit", limit);
    }
    Request request = builder.build();
    return request;
  }

  private List<String> extractReturnValue(Response response) {
    int keyCount = Integer.parseInt(response.getValue("key_count"));
    List<String> keys = new ArrayList<String>();
    for (int i = 1; i <= keyCount; i++) {
      keys.add(response.getValue("key_" + i));
    }
    return keys;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ListKeysOperation [domain=");
    builder.append(domain);
    builder.append(", keyPrefix=");
    builder.append(keyPrefix);
    builder.append(", limit=");
    builder.append(limit);
    builder.append("]");
    return builder.toString();
  }

}
