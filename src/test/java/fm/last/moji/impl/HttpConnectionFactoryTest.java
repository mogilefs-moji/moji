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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HttpConnectionFactoryTest {

  private HttpConnectionFactory factory;
  private ServerSocket serverSocket;
  private InetSocketAddress address;
  private HttpURLConnection connection;

  @Before
  public void init() throws IOException {
    serverSocket = new ServerSocket(0);
    address = new InetSocketAddress(serverSocket.getInetAddress(), serverSocket.getLocalPort());
    factory = new HttpConnectionFactory(Proxy.NO_PROXY);
  }

  @After
  public void tearDown() throws IOException {
    try {
      connection.disconnect();
    } finally {
      serverSocket.close();
    }
  }

  @Test
  public void newConnection() throws Exception {
    connection = factory.newConnection(new URL("http://" + address.getHostName() + ":" + address.getPort() + "/"));
    assertThat(connection, is(not(nullValue())));
  }

}
