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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fm.last.moji.Moji;
import fm.last.moji.MojiFile;
import fm.last.moji.tracker.Tracker;

class ListFilesCommand implements MojiCommand {

  final String keyPrefix;
  final String domain;
  final Integer limit;
  private List<MojiFile> files;
  private final Moji moji;

  ListFilesCommand(Moji moji, String keyPrefix, String domain, int limit) {
    this(moji, keyPrefix, domain, Integer.valueOf(limit));
  }

  ListFilesCommand(Moji moji, String keyPrefix, String domain) {
    this(moji, keyPrefix, domain, null);
  }

  private ListFilesCommand(Moji moji, String keyPrefix, String domain, Integer limit) {
    this.moji = moji;
    this.keyPrefix = keyPrefix;
    this.domain = domain;
    this.limit = limit;
    files = Collections.emptyList();
  }

  @Override
  public void executeWithTracker(Tracker tracker) throws IOException {
    List<String> keys = tracker.list(domain, keyPrefix, limit);
    if (!keys.isEmpty()) {
      files = new ArrayList<MojiFile>(keys.size());
      for (String key : keys) {
        MojiFile file = moji.getFile(key);
        files.add(file);
      }
    }
  }

  List<MojiFile> getFileList() {
    return files;
  }

}
