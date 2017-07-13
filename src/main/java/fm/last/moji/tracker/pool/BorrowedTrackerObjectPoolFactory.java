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

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.TrackerException;
import fm.last.moji.tracker.impl.AbstractTrackerFactory;

class BorrowedTrackerObjectPoolFactory extends BaseKeyedPoolableObjectFactory<ManagedTrackerHost, BorrowedTracker> {

  private static final Logger log = LoggerFactory.getLogger(BorrowedTrackerObjectPoolFactory.class);

  private final AbstractTrackerFactory trackerFactory;
  private final MultiHostTrackerPool trackerPool;

  BorrowedTrackerObjectPoolFactory(AbstractTrackerFactory trackerFactory, MultiHostTrackerPool trackerPool) {
    this.trackerFactory = trackerFactory;
    this.trackerPool = trackerPool;
  }

  @Override
  public BorrowedTracker makeObject(ManagedTrackerHost key) throws Exception {
    ManagedTrackerHost host = key;
    Tracker delegateTracker = trackerFactory.newTracker(host.getAddress());
    BorrowedTracker borrowedTracker = new BorrowedTracker(host, delegateTracker, trackerPool);
    log.debug("Requested new tracker instance: {}", key);
    return borrowedTracker;
  }

  @Override
  public void destroyObject(ManagedTrackerHost key, BorrowedTracker value) throws Exception {
    BorrowedTracker borrowed = value;
    if (borrowed.getLastException() != null) {
      log.debug("Error occurred on tracker: {}", borrowed.getLastException().getMessage());
      borrowed.getHost().markAsFailed();
    }
    log.debug("Destroying {}", borrowed);
    borrowed.reallyClose();
  }

  @Override
  public boolean validateObject(ManagedTrackerHost key, BorrowedTracker value) {
    BorrowedTracker borrowed = value;
    log.debug("Validating {}", borrowed);
    try {
      borrowed.noop();
    } catch (TrackerException e) {
      // returning false will result in a destroyObject invocation
      // The address will then be marked out of service
      return false;
    }
    return true;
  }

}