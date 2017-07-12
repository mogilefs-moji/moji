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
package fm.last.moji;

import java.net.Proxy;

import org.junit.Test;

import fm.last.moji.impl.DefaultMojiFactory;
import fm.last.moji.tracker.TrackerFactory;
import fm.last.moji.tracker.impl.SingleHostTrackerFactory;

public class MojiInstantiationTest {

  @Test(timeout = 2000)
  public void delete() throws Exception {
    FakeMogileFsServer server = null;
    try {
      FakeMogileFsServer.Builder builder = new FakeMogileFsServer.Builder();
      builder.whenRequestContains("delete ", "key=myKey", "domain=myDomain").thenRespond("OK ");
      server = builder.build();
      TrackerFactory trackerFactory = new SingleHostTrackerFactory(server.getInetSocketAddress(), Proxy.NO_PROXY);
      DefaultMojiFactory mojiFactory = new DefaultMojiFactory(trackerFactory, "myDomain");
      Moji moji = mojiFactory.getInstance();
      MojiFile file = moji.getFile("myKey");
      file.delete();
    } finally {
      server.close();
    }
  }

}
