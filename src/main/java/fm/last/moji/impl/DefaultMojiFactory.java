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

import fm.last.moji.Moji;
import fm.last.moji.MojiFactory;
import fm.last.moji.tracker.TrackerFactory;

/**
 * Creates a {@link fm.last.moji.Moji Moji} instance.
 */
public class DefaultMojiFactory implements MojiFactory {

  private final String defaultDomain;
  private final TrackerFactory trackerFactory;
  private final HttpConnectionFactory httpFactory;

  public DefaultMojiFactory(TrackerFactory trackerFactory, String defaultDomain) {
    this.trackerFactory = trackerFactory;
    this.defaultDomain = defaultDomain;
    httpFactory = new HttpConnectionFactory(trackerFactory.getNetworkingConfiguration());
  }

  @Override
  public Moji getInstance() {
    return new MojiImpl(trackerFactory, httpFactory, defaultDomain);
  }

  @Override
  public Moji getInstance(String domain) throws IOException {
    return new MojiImpl(trackerFactory, httpFactory, domain);
  }

}
