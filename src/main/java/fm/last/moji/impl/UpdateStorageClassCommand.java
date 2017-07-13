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

import fm.last.moji.tracker.Tracker;

class UpdateStorageClassCommand implements MojiCommand {

  final String key;
  final String domain;
  final String newStorageClass;

  UpdateStorageClassCommand(String key, String domain, String newStorageClass) {
    this.key = key;
    this.domain = domain;
    this.newStorageClass = newStorageClass;
  }

  @Override
  public void executeWithTracker(Tracker tracker) throws IOException {
    tracker.updateStorageClass(key, domain, newStorageClass);
  }

}
