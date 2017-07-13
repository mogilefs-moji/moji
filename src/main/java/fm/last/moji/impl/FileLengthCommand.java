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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.moji.tracker.Tracker;

class FileLengthCommand implements MojiCommand {

  private static final Logger log = LoggerFactory.getLogger(FileLengthCommand.class);

  private final HttpConnectionFactory httpFactory;
  final String key;
  final String domain;
  private long length = -1L;

  FileLengthCommand(HttpConnectionFactory httpFactory, String key, String domain) {
    this.httpFactory = httpFactory;
    this.key = key;
    this.domain = domain;
  }

  @Override
  public void executeWithTracker(Tracker tracker) throws IOException {
    List<URL> paths = tracker.getPaths(key, domain);
    if (!paths.isEmpty()) {
      HttpURLConnection httpConnection = null;
      IOException lastException = null;
      for (URL path : paths) {
        try {
          log.debug("HTTP HEAD -> {}", path);
          httpConnection = httpFactory.newConnection(path);
          httpConnection.setRequestMethod("HEAD");
          length = getContentLength(httpConnection);
          log.debug("Content-Length: {}", length);
          return;
        } catch (IOException e) {
          log.debug("Failed to open input -> {}", path);
          log.debug("Exception was: ", e);
          lastException = e;
        } finally {
          if (httpConnection != null) {
            httpConnection.disconnect();
          }
        }
      }
      throw lastException;
    } else {
      log.debug("No paths found for domain={},key={} - throwing", domain, key);
      throw new FileNotFoundException("domain=" + domain + ",key=" + key);
    }
  }

  long getLength() {
    return length;
  }

  String getKey() {
    return key;
  }

  String getDomain() {
    return domain;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("FileLengthCommand [domain=");
    builder.append(domain);
    builder.append(", key=");
    builder.append(key);
    builder.append(", length=");
    builder.append(length);
    builder.append("]");
    return builder.toString();
  }

  /* Since getContentLengthLong() only available since Java 7, we do the conversion here. */
  private long getContentLength(HttpURLConnection httpConnection) {
    long length = 0;
    String rawLength = httpConnection.getHeaderField("Content-Length");

    // Some webdav server, e.g., apache, does not return content-length header when the file size is zero
    if(rawLength == null) {
      log.debug("No Content-Length header found, assume the length is 0");
      return 0;
    }

    try {
      length = Long.parseLong(rawLength);
    } catch (NumberFormatException e) {
      log.debug("Failed to parse Content-Length: {}", rawLength);
      length = -1L;
    }
    return length;
  }

}
