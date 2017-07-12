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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

public class InetSocketAddressFactory {

  private InetSocketAddressFactory() {
  }

  public static Set<InetSocketAddress> newAddresses(String addressesCsv) {
    Set<String> addresses = new HashSet<String>();
    for (String addressElement : addressesCsv.split(",")) {
      addresses.add(addressElement.trim());
    }
    Set<InetSocketAddress> socketAddresses = new HashSet<InetSocketAddress>();
    for (String address : addresses) {
      InetSocketAddress socketAddress = newAddress(address);
      socketAddresses.add(socketAddress);
    }
    return socketAddresses;
  }

  public static InetSocketAddress newAddress(String addressString) {
    // TODO: Default mogile port
    String[] parts = addressString.split(":");
    String host = parts[0];
    int port = Integer.valueOf(parts[1]);
    InetAddress address;
    try {
      address = InetAddress.getByName(host);
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException("Invalid '<host>:<port>': '" + addressString + "'", e);
    }
    InetSocketAddress socketAddress = new InetSocketAddress(address, port);
    return socketAddress;
  }

}
