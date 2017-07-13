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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.TrackerFactory;
import fm.last.moji.tracker.impl.CommunicationException;

class Executor {

  private static final Logger log = LoggerFactory.getLogger(Executor.class);

  private final TrackerFactory trackerFactory;
  private int maxAttempts;

  Executor(TrackerFactory trackerFactory) {
    this.trackerFactory = trackerFactory;
    maxAttempts = trackerFactory.getAddresses().size();
  }

  public void executeCommand(MojiCommand command) throws IOException {
    Tracker tracker = null;
    CommunicationException lastException = null;
    for (int attempt = 0; attempt < maxAttempts; attempt++) {
      try {
        tracker = trackerFactory.getTracker();
        log.debug("executing {}", command);
        if (maxAttempts > 1) {
          log.debug("Attempt #{}", attempt);
        }
        command.executeWithTracker(tracker);
        return;
      } catch (CommunicationException e) {
        lastException = e;
      } finally {
        if (tracker != null) {
          tracker.close();
        }
      }
    }
    if (maxAttempts > 1) {
      log.debug("All {} attempts failed", maxAttempts);
    }
    throw lastException;
  }

  void setMaxAttempts(int maxAttempts) {
    this.maxAttempts = maxAttempts;
  }

}
