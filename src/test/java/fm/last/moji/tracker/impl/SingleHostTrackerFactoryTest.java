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
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.TrackerException;

public class SingleHostTrackerFactoryTest {

  private SingleHostTrackerFactory factory;
  private ServerSocket serverSocket;
  private InetSocketAddress address;

  @Before
  public void init() throws IOException {
    serverSocket = new ServerSocket(0);
    address = new InetSocketAddress(serverSocket.getInetAddress(), serverSocket.getLocalPort());
    factory = new SingleHostTrackerFactory(address, Proxy.NO_PROXY);
  }

  @After
  public void tearDown() throws IOException {
    serverSocket.close();
  }

  @Test
  public void proxy() {
    assertEquals(Proxy.NO_PROXY, factory.getProxy());
  }

  @Test
  public void getTracker() throws TrackerException {
    Tracker tracker = factory.getTracker();
    assertNotNull(tracker);
  }

  @Test
  public void getAddresses() {
    Set<InetSocketAddress> addresses = factory.getAddresses();
    assertEquals(1, addresses.size());
    InetSocketAddress actualAddress = addresses.iterator().next();
    assertEquals(address.getHostName(), actualAddress.getHostName());
    assertEquals(address.getPort(), actualAddress.getPort());
  }

}
