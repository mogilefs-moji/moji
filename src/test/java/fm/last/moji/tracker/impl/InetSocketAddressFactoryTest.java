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

import static org.junit.Assert.assertEquals;

import java.net.InetSocketAddress;

import org.junit.Test;

public class InetSocketAddressFactoryTest {

  @Test
  public void numeric1() {
    InetSocketAddress address = InetSocketAddressFactory.newAddress("0.0.0.0:80");
    assertEquals("0.0.0.0", address.getHostName());
    assertEquals(80, address.getPort());
  }

  @Test
  public void numeric2() {
    InetSocketAddress address = InetSocketAddressFactory.newAddress("255.255.255.255:65535");
    assertEquals("255.255.255.255", address.getHostName());
    assertEquals(65535, address.getPort());
  }

  @Test(expected = IllegalArgumentException.class)
  public void numericBadPort1() {
    InetSocketAddressFactory.newAddress("255.255.255.255:65536");
  }

  @Test(expected = IllegalArgumentException.class)
  public void numericBadAddress1() {
    InetSocketAddressFactory.newAddress("255.255.255.256:65535");
  }

  @Test(expected = IllegalArgumentException.class)
  public void numericBadPort2() {
    InetSocketAddressFactory.newAddress("255.255.255.255:-80");
  }

  @Test(expected = IllegalArgumentException.class)
  public void numericBadAddress2() {
    InetSocketAddressFactory.newAddress("255.-2.255.255:80");
  }

  @Test
  public void alpha1() {
    InetSocketAddress address = InetSocketAddressFactory.newAddress("www.google.com:80");
    assertEquals("www.google.com", address.getHostName());
    assertEquals(80, address.getPort());
  }

  @Test
  public void alpha2() {
    InetSocketAddress address = InetSocketAddressFactory.newAddress("localhost:80");
    assertEquals("localhost", address.getHostName());
    assertEquals(80, address.getPort());
  }

  @Test(expected = IllegalArgumentException.class)
  public void badAlpha1() {
    InetSocketAddressFactory.newAddress("local:host:80");
  }

}
