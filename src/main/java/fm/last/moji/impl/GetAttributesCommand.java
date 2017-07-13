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
import java.util.Map;

import fm.last.moji.MojiFileAttributes;
import fm.last.moji.tracker.Tracker;

class GetAttributesCommand implements MojiCommand {

  private final String key;
  private final String domain;
  private MojiFileAttributes attributes;

  GetAttributesCommand(String key, String domain) {
    this.key = key;
    this.domain = domain;
  }

  @Override
  public void executeWithTracker(Tracker tracker) throws IOException {
    Map<String, String> valueMap = tracker.fileInfo(key, domain);
    if (!valueMap.isEmpty()) {
      attributes = new MojiFileAttributesImpl(valueMap);
    }
  }

  MojiFileAttributes getAttributes() {
    return attributes;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("GetAttributesCommand [key=");
    builder.append(key);
    builder.append(", domain=");
    builder.append(domain);
    builder.append("]");
    return builder.toString();
  }

}
