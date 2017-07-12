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
package fm.last.moji.tracker.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

enum ErrorCode {
  UNKNOWN_KEY("unknown_key"), UNKNOWN_COMMAND("unknown_command"), KEY_EXISTS("key_exists"), UNKNOWN_CLASS(
      "unreg_class", "class_not_found"), NONE_MATCH("none_match");

  private Set<String> messages;

  private ErrorCode(String... messages) {
    this.messages = new HashSet<String>(Arrays.asList(messages));
  }

  boolean isContainedInLine(String line) {
    if (line == null || line.isEmpty()) {
      return false;
    }
    for (String message : messages) {
      if (line.contains(message)) {
        return true;
      }
    }
    return false;
  }

}
