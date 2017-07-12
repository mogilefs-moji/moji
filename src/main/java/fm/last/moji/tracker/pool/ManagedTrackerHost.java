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

import static java.util.concurrent.TimeUnit.MINUTES;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.commons.lang.time.Clock;

/**
 * Manages the status of a given tracker host. Knows when it last failed, when the last successful request occurred.
 */
public class ManagedTrackerHost {

  private static final Logger log = LoggerFactory.getLogger(ManagedTrackerHost.class);

  private final AtomicLong lastUsed;
  private final AtomicLong lastFailed;
  private final InetSocketAddress address;
  private int hostRetryInterval;
  private TimeUnit hostRetryIntervalTimeUnit;
  private final Timer resetTimer;
  private final Clock clock;
  private ResetTask resetTask;
  ResetTaskFactory resetTaskFactory;

  ManagedTrackerHost(InetSocketAddress address, Timer resetTimer, Clock clock) {
    lastUsed = new AtomicLong();
    lastFailed = new AtomicLong();
    hostRetryInterval = 1;
    hostRetryIntervalTimeUnit = MINUTES;
    this.resetTimer = resetTimer;
    resetTaskFactory = new ResetTaskFactory();
    this.clock = clock;
    this.address = address;
  }

  ManagedTrackerHost(InetSocketAddress address) {
    this(address, new Timer(true), Clock.INSTANCE);
  }

  /**
   * Set delay until a failed tracker is once more marked as ready.
   */
  void setHostRetryInterval(int interval, TimeUnit timeUnit) {
    hostRetryInterval = interval;
    hostRetryIntervalTimeUnit = timeUnit;
  }

  /**
   * The address of the host that this object manages.
   * 
   * @return Host address
   */
  public InetSocketAddress getAddress() {
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
    lastFailed.set(clock.currentTimeMillis());
    synchronized (resetTimer) {
      if (resetTask != null) {
        resetTask.cancel();
      }
      log.debug("Scheduling reset of {} in {} {}", new Object[] { address, hostRetryInterval,
        hostRetryIntervalTimeUnit.name().toLowerCase() });
      resetTask = resetTaskFactory.newInstance();
      resetTimer.schedule(resetTask, hostRetryIntervalTimeUnit.toMillis(hostRetryInterval));
    }
  }

  void markSuccess() {
    lastUsed.set(clock.currentTimeMillis());
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

  class ResetTask extends TimerTask {

    ResetTask() {
    }

    @Override
    public void run() {
      lastFailed.set(0);
      log.debug("Reset failure monitor for {}", address);
    }
  }

  class ResetTaskFactory {
    public ResetTask newInstance() {
      return new ResetTask();
    }
  }

}
