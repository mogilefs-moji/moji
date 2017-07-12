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
package fm.last.moji.tracker.pool;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.moji.tracker.Destination;
import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.TrackerException;
import fm.last.moji.tracker.impl.CommunicationException;

class BorrowedTracker implements Tracker {

  private static final Logger log = LoggerFactory.getLogger(BorrowedTracker.class);

  private final Tracker delegate;
  private final MultiHostTrackerPool pool;
  private final ManagedTrackerHost host;
  private CommunicationException lastException;

  BorrowedTracker(ManagedTrackerHost host, Tracker delegate, MultiHostTrackerPool pool) {
    this.delegate = delegate;
    this.host = host;
    this.pool = pool;
  }

  @Override
  public List<URL> getPaths(String key, String domain) throws TrackerException {
    List<URL> paths = Collections.emptyList();
    try {
      paths = delegate.getPaths(key, domain);
      host.markSuccess();
    } catch (CommunicationException e) {
      lastException = e;
      throw e;
    }
    return paths;
  }

  @Override
  public Map<String, String> fileInfo(String key, String domain) throws TrackerException {
    Map<String, String> attributes = Collections.emptyMap();
    try {
      attributes = delegate.fileInfo(key, domain);
      host.markSuccess();
    } catch (CommunicationException e) {
      lastException = e;
      throw e;
    }
    return attributes;
  }

  @Override
  public List<Destination> createOpen(String key, String domain, String storageClass) throws TrackerException {
    List<Destination> destinations = Collections.emptyList();
    try {
      destinations = delegate.createOpen(key, domain, storageClass);
      host.markSuccess();
    } catch (CommunicationException e) {
      lastException = e;
      throw e;
    }
    return destinations;
  }

  @Override
  public void createClose(String key, String domain, Destination destination, long size) throws TrackerException {
    try {
      delegate.createClose(key, domain, destination, size);
      host.markSuccess();
    } catch (CommunicationException e) {
      lastException = e;
      throw e;
    }
  }

  @Override
  public void delete(String key, String domain) throws TrackerException {
    try {
      delegate.delete(key, domain);
      host.markSuccess();
    } catch (CommunicationException e) {
      lastException = e;
      throw e;
    }
  }

  @Override
  public void rename(String key, String domain, String newKey) throws TrackerException {
    try {
      delegate.rename(key, domain, newKey);
      host.markSuccess();
    } catch (CommunicationException e) {
      lastException = e;
      throw e;
    }
  }

  @Override
  public void updateStorageClass(String key, String domain, String newStorageClass) throws TrackerException {
    try {
      delegate.updateStorageClass(key, domain, newStorageClass);
      host.markSuccess();
    } catch (CommunicationException e) {
      lastException = e;
      throw e;
    }
  }

  @Override
  public void noop() throws TrackerException {
    try {
      delegate.noop();
      host.markSuccess();
    } catch (CommunicationException e) {
      lastException = e;
      throw e;
    }
  }

  @Override
  public List<String> list(String domain, String keyPrefix, Integer limit) throws TrackerException {
    List<String> keys = Collections.emptyList();
    try {
      keys = delegate.list(domain, keyPrefix, limit);
      host.markSuccess();
    } catch (CommunicationException e) {
      lastException = e;
      throw e;
    }
    return keys;
  }

  @Override
  public Map<String, Map<String, String>> getDeviceStatuses(String domain) throws TrackerException {
    Map<String, Map<String, String>> parametersByDevice = Collections.emptyMap();
    try {
      parametersByDevice = delegate.getDeviceStatuses(domain);
      host.markSuccess();
    } catch (CommunicationException e) {
      lastException = e;
      throw e;
    }
    return parametersByDevice;
  }

  @Override
  public void close() {
    try {
      if (lastException != null) {
        log.debug("Invalidating: {}", lastException);
        try {
          pool.invalidateTracker(this);
        } finally {
          delegate.close();
        }
      } else {
        log.debug("Returning to pool");
        pool.returnTracker(this);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  void reallyClose() {
    log.debug("Really closing");
    delegate.close();
  }

  CommunicationException getLastException() {
    return lastException;
  }

  ManagedTrackerHost getHost() {
    return host;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("BorrowedTracker [host=");
    builder.append(host);
    builder.append(", lastException=");
    builder.append(lastException);
    builder.append(", delegate=");
    builder.append(delegate);
    builder.append(", pool=");
    builder.append(pool);
    builder.append("]");
    return builder.toString();
  }

}
