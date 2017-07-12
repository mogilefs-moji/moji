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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.locks.Lock;

import org.apache.commons.io.output.CountingOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.moji.tracker.Destination;
import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.TrackerException;
import fm.last.moji.tracker.TrackerFactory;

class FileUploadOutputStream extends OutputStream {

  private static final Logger log = LoggerFactory.getLogger(FileUploadOutputStream.class);

  private static final int CHUNK_LENGTH = 4096;
  private final Destination destination;
  private final TrackerFactory trackerFactory;
  private final String key;
  private final String domain;
  private final Lock writeLock;
  private final HttpURLConnection httpConnection;
  private final CountingOutputStream delegate;
  private long size = -1L;

  FileUploadOutputStream(TrackerFactory trackerFactory, HttpConnectionFactory httpFactory, String key, String domain,
      Destination destination, Lock writeLock) throws IOException {
    this.destination = destination;
    this.trackerFactory = trackerFactory;
    this.domain = domain;
    this.key = key;
    this.writeLock = writeLock;

    log.debug("HTTP PUT -> opening chunked stream -> {}", destination.getPath());
    httpConnection = httpFactory.newConnection(destination.getPath());
    httpConnection.setRequestMethod("PUT");
    httpConnection.setChunkedStreamingMode(CHUNK_LENGTH);
    httpConnection.setDoOutput(true);
    delegate = new CountingOutputStream(httpConnection.getOutputStream());
  }

  @Override
  public void write(int b) throws IOException {
    delegate.write(b);
  }

  @Override
  public void write(byte[] b) throws IOException {
    delegate.write(b);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    delegate.write(b, off, len);
  }

  @Override
  public void flush() throws IOException {
    delegate.flush();
  }

  @Override
  public void close() throws IOException {
    log.debug("Close called on {}", this);
    try {
      flushAndClose();
      trackerCreateClose();
    } finally {
      unlockQuietly(writeLock);
    }
  }

  /**
   * Send create_close command to tracker to finish mogilefs file write procedure
   *
   * @throws TrackerException If the create_close command fails.
   */
  private void trackerCreateClose() throws TrackerException {
    /*
     * Fixed the maxAttempts = 2 so the behavior is just retry once. If there is only one tracker, it gives the tracker
     * a second chance. If there are multiple trackers, it just tries one other tracker, but does not waste time trying
     * all available trackers.
     */
    int maxAttempts = 2;
    TrackerException lastException = null;
    for (int attempt = 0; attempt < maxAttempts; attempt++) {
      Tracker tracker = null;
      try {
        tracker = trackerFactory.getTracker();
        tracker.createClose(key, domain, destination, size);
        return;
      } catch (TrackerException e) {
        lastException = e;
        /*
         * Call attention to the user. User should diagnose the issue as soon as possible to prevent additional latency
         * caused by retry, or before all trackers are down.
         */
        log.warn("create_close attempt {} failed", attempt + 1, e);
      } finally {
        if (tracker != null) {
          tracker.close();
        }
      }
    }

    log.error("All {} attempts to create_close failed", maxAttempts);
    throw lastException;
  }

  private void flushAndClose() throws IOException {
    try {
      delegate.flush();
      size = delegate.getByteCount();
      log.debug("Bytes written: {}", size);
      int code = httpConnection.getResponseCode();
      if (HttpURLConnection.HTTP_OK != code && HttpURLConnection.HTTP_CREATED != code) {
        String message = httpConnection.getResponseMessage();
        throw new IOException(
            "HTTP Error during flush: " + code + ", " + message + ", peer: '{" + httpConnection + "}'");
      }
    } finally {
      try {
        delegate.close();
      } catch (Exception e) {
        log.warn("Error closing stream", e);
      }
      try {
        httpConnection.disconnect();
      } catch (Exception e) {
        log.warn("Error closing connection", e);
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("FileUploadOutputStream [domain=");
    builder.append(domain);
    builder.append(", key=");
    builder.append(key);
    builder.append(", destination=");
    builder.append(destination);
    builder.append("]");
    return builder.toString();
  }

  private void unlockQuietly(Lock lock) {
    try {
      lock.unlock();
    } catch (IllegalMonitorStateException e) {
    }
  }

}
