/*
 * Copyright 2009 Last.fm
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
package fm.last.moji.tracker.pool;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the status of a given tracker host. Knows when it last failed, when the last successful request occurred.
 */
public class ManagedTrackerHost {

  private static final Logger log = LoggerFactory.getLogger(ManagedTrackerHost.class);

  private static final int ONE_MINUTE_IN_MS = 60000;

  private final AtomicLong lastUsed = new AtomicLong();
  private final AtomicLong lastFailed = new AtomicLong();
  private final InetSocketAddress address;
  private Timer resetTimer;
  private ResetTask resetTask;

  ManagedTrackerHost(InetSocketAddress address) {
    this.address = address;
  }

  /**
   * The address of the host that this object manages.
   * 
   * @return Host address
   */
  public InetSocketAddress getAddress() {
    lastUsed.set(System.currentTimeMillis());
    return address;
  }

  /**
   * The time when this host was last used.
   * 
   * @return Date/Time formatted string
   */
  public String getLastUsedTime() {
    return formatTime(getLastUsed());
  }

  /**
   * The time when an operation on this host last failed.
   * 
   * @return Date/Time formatted string
   */
  public String getLastFailedTime() {
    return formatTime(getLastFailed());
  }

  long getLastUsed() {
    return lastUsed.get();
  }

  long getLastFailed() {
    return lastFailed.get();
  }

  void markAsFailed() {
    lastFailed.set(System.currentTimeMillis());
    synchronized (resetTimer) {
      if (resetTask != null) {
        resetTask.cancel();
      }
      log.debug("Scheduling reset of {} in {}ms", address, ONE_MINUTE_IN_MS);
      resetTimer.schedule(new ResetTask(), ONE_MINUTE_IN_MS);
    }
  }

  void markSuccess() {
    lastFailed.set(0);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (address == null ? 0 : address.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof ManagedTrackerHost)) {
      return false;
    }
    ManagedTrackerHost other = (ManagedTrackerHost) obj;
    if (address == null) {
      if (other.address != null) {
        return false;
      }
    } else if (!address.equals(other.address)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ManagedTrackerAddress [address=");
    builder.append(address);
    builder.append(", lastUsed=");
    builder.append(formatTime(lastUsed.get()));
    builder.append(", lastFailed=");
    builder.append(formatTime(lastFailed.get()));
    builder.append("]");
    return builder.toString();
  }

  private String formatTime(long time) {
    if (time == 0L) {
      return "<never>";
    }
    return new Date(time).toString();
  }

  private final class ResetTask extends TimerTask {
    @Override
    public void run() {
      lastFailed.set(0);
      log.debug("Reset failure monitor for {}", address);
    }
  }

}
