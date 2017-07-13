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
package fm.last.moji.tracker;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Set;

import fm.last.moji.impl.NetworkingConfiguration;

/**
 * Provides usable {@link Tracker} instances for communicating with the MogileFS 'backend'.
 */
public interface TrackerFactory {

  /**
   * Gets a new usable {@link Tracker}.
   * 
   * @return A valid tracker.
   * @throws TrackerException If there was a problem obtaining a tracker.
   */
  Tracker getTracker() throws TrackerException;

  /**
   * The host addresses of {@link Tracker Trackers} that this factory may return.
   * 
   * @return A set of host addresses.
   */
  Set<InetSocketAddress> getAddresses();

  /**
   * The network proxy used by the {@link Tracker Trackers} returned from this factory.
   * 
   * @return The proxy, or {@link Proxy#NO_PROXY NO_PROXY} if no proxy has been set.
   */
  @Deprecated
  Proxy getProxy();

  NetworkingConfiguration getNetworkingConfiguration();

}
