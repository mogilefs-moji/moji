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
package fm.last.moji.spring;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import fm.last.moji.FakeMogileFsServer;
import fm.last.moji.MojiFile;

public class SpringMojiBeanInstantiationTest {

  @Test(timeout = 2000)
  public void delete() throws Exception {
    FakeMogileFsServer server = null;
    try {
      FakeMogileFsServer.Builder builder = new FakeMogileFsServer.Builder();
      builder.whenRequestContains("delete ", "key=myKey", "domain=myDomain").thenRespond("OK ");
      server = builder.build();
      SpringMojiBean bean = new SpringMojiBean();
      bean.setAddressesCsv(server.getAddressAsString());
      bean.setDomain("myDomain");
      bean.initialise();
      MojiFile file = bean.getFile("myKey");
      file.delete();
      assertThat(bean.getNumIdle(), is(1));
    } finally {
      server.close();
    }
  }

}
