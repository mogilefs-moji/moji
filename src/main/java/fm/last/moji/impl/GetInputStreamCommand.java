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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.locks.Lock;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.moji.tracker.Tracker;

class GetInputStreamCommand implements MojiCommand {

  private static final Logger log = LoggerFactory.getLogger(GetInputStreamCommand.class);

  final String key;
  final String domain;
  private final HttpConnectionFactory httpFactory;
  private final Lock readLock;
  private InputStream stream;

  GetInputStreamCommand(String key, String domain, HttpConnectionFactory httpFactory, Lock readLock) {
    this.key = key;
    this.domain = domain;
    this.httpFactory = httpFactory;
    this.readLock = readLock;
  }

  @Override
  public void executeWithTracker(Tracker tracker) throws IOException {
    List<URL> paths = tracker.getPaths(key, domain);
    if (paths.isEmpty()) {
      throw new FileNotFoundException("key=" + key + ", domain=" + domain);
    }
    IOException lastException = null;
    for (URL path : paths) {
      try {
        log.debug("Opened: {}", path);
        HttpURLConnection urlConnection = httpFactory.newConnection(path);
        stream = new FileDownloadInputStream(urlConnection.getInputStream(), readLock);
        return;
      } catch (IOException e) {
        log.debug("Failed to open input -> {}", path);
        log.debug("Exception was: ", e);
        lastException = e;
        IOUtils.closeQuietly(stream);
      }
    }
    throw lastException;
  }

  InputStream getInputStream() {
    return stream;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("GetInputStreamCommand [domain=");
    builder.append(domain);
    builder.append(", key=");
    builder.append(key);
    builder.append("]");
    return builder.toString();
  }

}
