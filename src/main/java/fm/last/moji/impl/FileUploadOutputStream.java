/*
 * Copyright 2012 Last.fm
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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
import fm.last.moji.tracker.TrackerFactory;
import fm.last.moji.tracker.impl.CommunicationException;

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
    long size = -1L;
    try {
      try {
        delegate.flush();
        size = delegate.getByteCount();
      } finally {
        try {
          delegate.close();
        } finally {
          try {
            String message = httpConnection.getResponseMessage();
            int code = httpConnection.getResponseCode();
            if (HttpURLConnection.HTTP_OK != code && HttpURLConnection.HTTP_CREATED != code) {
              throw new IOException(code + " " + message);
            } else {
              log.debug("Status: HTTP {} - {}", code, message);
            }
          } finally {
            httpConnection.disconnect();
          }
        }
      }
    } finally {
      log.debug("Bytes written: {}", size);
      Tracker tracker = null;
      CommunicationException lastException = null;
      int maxAttempts = trackerFactory.getAddresses().size();
      boolean ok = false;
      try {
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
          try {
            tracker = trackerFactory.getTracker();
            log.debug("executing createClose {},{}", key, domain);
            if (maxAttempts > 1) {
              log.debug("Attempt #{}", attempt);
            }
            tracker.createClose(key, domain, destination, size);
            ok = true;
          } catch (CommunicationException e) {
            lastException = e;
          } finally {
            if (tracker != null) {

              tracker.close();
            }
          }
        }
      } finally {
        unlockQuietly(writeLock);
      }
      if (!ok) {
        if (maxAttempts > 1) {
          log.debug("All {} attempts failed", maxAttempts);
        }

        throw lastException;
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
