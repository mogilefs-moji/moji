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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SpringMojiBeanTest {

  private SpringMojiBean bean;

  @Before
  public void init() {
    bean = new SpringMojiBean();
    bean.setAddressesCsv("localhost:80");
    bean.setDomain("domain");
    bean.initialise();
  }

  @After
  public void close() throws Exception {
    bean.close();
  }

  @Test
  public void defaultProxy() {
    assertEquals(Proxy.NO_PROXY, bean.getProxy());
  }

  @Test
  public void address() {
    Set<InetSocketAddress> addresses = bean.getAddresses();
    assertEquals(1, addresses.size());
    InetSocketAddress address = addresses.iterator().next();
    assertEquals("localhost", address.getHostName());
    assertEquals(address.getPort(), 80);
  }

  @Test
  public void getFile() {
    assertNotNull(bean.getFile("123"));
  }

  @Test
  public void getFileWithStorageClass() {
    assertNotNull(bean.getFile("123", "class"));
  }

}
