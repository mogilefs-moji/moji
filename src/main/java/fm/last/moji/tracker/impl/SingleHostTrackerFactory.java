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

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;
import java.util.Set;

import fm.last.moji.impl.NetworkingConfiguration;
import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.TrackerException;
import fm.last.moji.tracker.TrackerFactory;

/**
 * Single host tracker factory that creates a new connection on each {@link #getTracker()} request. Do not use this
 * directly - only as a building block for pooling {@link TrackerFactory TrackerFactorys}.
 */
public class SingleHostTrackerFactory implements TrackerFactory {

  private final AbstractTrackerFactory delegateFactory;
  private final InetSocketAddress address;

  /**
   * Creates a tracker factory for the given host address and use the supplied network proxy.
   * 
   * @param address Tracker host address.
   * @param proxy Network proxy - use Proxy.NO_PROXY if a proxy isn't needed.
   */
  @Deprecated
  public SingleHostTrackerFactory(InetSocketAddress address, Proxy proxy) {
    this(address, new NetworkingConfiguration.Builder().proxy(proxy).build());
  }

  /**
   * Creates a tracker factory for the given host address and use the supplied network config.
   * 
   * @param address Tracker host address
   * @param netConfig Network configuration.
   */
  public SingleHostTrackerFactory(InetSocketAddress address, NetworkingConfiguration netConfig) {
    delegateFactory = new AbstractTrackerFactory(netConfig);
    this.address = address;
  }

  @Override
  public Tracker getTracker() throws TrackerException {
    return delegateFactory.newTracker(address);
  }

  @Override
  public Set<InetSocketAddress> getAddresses() {
    return Collections.singleton(address);
  }

  @Override
  @Deprecated
  public Proxy getProxy() {
    return delegateFactory.getProxy();
  }

  @Override
  public NetworkingConfiguration getNetworkingConfiguration() {
    return delegateFactory.getNetworkingConfiguration();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("SingleHostTrackerFactory [networkingConfiguration=");
    builder.append(delegateFactory.getNetworkingConfiguration());
    builder.append("]");
    return builder.toString();
  }

}
