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

import java.io.Serializable;
import java.util.Comparator;

/**
 * Sort order: failed least, then least recently used
 */
enum HostPriorityOrder implements Comparator<ManagedTrackerHost>, Serializable {
  INSTANCE;

  @Override
  public int compare(ManagedTrackerHost a, ManagedTrackerHost b) {
    long failTime1 = a.getLastFailed();
    long failTime2 = b.getLastFailed();
    if (failTime1 == failTime2) {
      // they both failed at the same time (or not at all) so we just want to
      // priotitise the least recently used.
      long useTime1 = a.getLastUsed();
      long useTime2 = b.getLastUsed();
      if (useTime1 == useTime2) {
        return 0;
      } else if (useTime1 > useTime2) {
        // 'a' was used more recently than 'b' - reduce the priority and
        // chose 'b' before it
        return -1;
      }
      return 1;
    } else if (failTime1 < failTime2) {
      // 'b' failed more recently - so we prioritise 'a' in the hopes that it
      // has had longer to recover
      return 1;
    }
    // 'a' failed more recently - reduce its priority
    return -1;
  }

}
