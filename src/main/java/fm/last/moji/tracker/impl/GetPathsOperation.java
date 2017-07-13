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

import static fm.last.moji.tracker.impl.ErrorCode.UNKNOWN_KEY;
import static fm.last.moji.tracker.impl.ResponseStatus.OK;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fm.last.moji.tracker.TrackerException;
import fm.last.moji.tracker.UnknownKeyException;

class GetPathsOperation {

  private final String domain;
  private final String key;
  private final boolean verify;
  private final RequestHandler requestHandler;
  private List<URL> paths;

  GetPathsOperation(RequestHandler requestHandler, String domain, String key, boolean verify) {
    this.domain = domain;
    this.key = key;
    this.verify = verify;
    this.requestHandler = requestHandler;
    paths = Collections.emptyList();
  }

  public void execute() throws TrackerException {
    Request request = new Request.Builder(3).command("get_paths").arg("domain", domain).arg("key", key)
        .arg("noverify", !verify).build();
    Response response = requestHandler.performRequest(request);
    if (response.getStatus() != OK) {
      if (UNKNOWN_KEY.isContainedInLine(response.getMessage())) {
        throw new UnknownKeyException(domain, key);
      }
      throw new TrackerException(response.getMessage());
    } else {
      paths = extractReturnValue(response);
    }
  }

  List<URL> getPaths() {
    return paths;
  }

  private List<URL> extractReturnValue(Response response) throws TrackerException {
    int pathCount = Integer.parseInt(response.getValue("paths"));
    List<URL> urls = new ArrayList<URL>(pathCount);
    for (int i = 1; i <= pathCount; i++) {
      URL url;
      try {
        url = new URL(response.getValue("path" + i));
        urls.add(url);
      } catch (MalformedURLException e) {
        throw new TrackerException(e);
      }
    }
    return urls;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("GetPathsCommand [domain=");
    builder.append(domain);
    builder.append(", key=");
    builder.append(key);
    builder.append(", verify=");
    builder.append(verify);
    builder.append("]");
    return builder.toString();
  }

}
