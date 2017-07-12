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
package fm.last.moji.tracker.pool;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.moji.impl.NetworkingConfiguration;
import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.TrackerException;
import fm.last.moji.tracker.TrackerFactory;
import fm.last.moji.tracker.impl.AbstractTrackerFactory;
import fm.last.moji.tracker.impl.CommunicationException;

/**
 * {@link fm.last.moji.tracker.TrackerFactory TrackerFactory} implementation that provides a
 * {@link fm.last.moji.tracker.Tracker Tracker} connection pool that can distribute requests across many hosts.
 */
public class MultiHostTrackerPool implements TrackerFactory {

  private static final Logger log = LoggerFactory.getLogger(MultiHostTrackerPool.class);

  private final NetworkingConfiguration netConfig;
  private final GenericKeyedObjectPool<ManagedTrackerHost, BorrowedTracker> pool;
  private final List<ManagedTrackerHost> managedHosts;

  /**
   * Creates a tracker pool for the given host addresses and use the supplied network proxy.
   * 
   * @param addresses Tracker host addresses.
   * @param proxy Network proxy - use Proxy.NO_PROXY if a proxy isn't needed.
   */
  @Deprecated
  public MultiHostTrackerPool(Set<InetSocketAddress> addresses, Proxy proxy) {
    this(addresses, new NetworkingConfiguration.Builder().proxy(proxy).build());
  }

  /**
   * Creates a tracker pool for the given host addresses and use the supplied network proxy.
   * 
   * @param addresses Tracker host addresses.
   * @param netConfig The networking configuration.
   */
  public MultiHostTrackerPool(Set<InetSocketAddress> addresses, NetworkingConfiguration netConfig) {
    this.netConfig = netConfig;
    managedHosts = new ArrayList<ManagedTrackerHost>();
    for (InetSocketAddress address : addresses) {
      managedHosts.add(new ManagedTrackerHost(address));
    }
    AbstractTrackerFactory delegateTrackerFactory = new AbstractTrackerFactory(netConfig);
    KeyedPoolableObjectFactory<ManagedTrackerHost, BorrowedTracker> objectFactory = new BorrowedTrackerObjectPoolFactory(
        delegateTrackerFactory, this);
    pool = new GenericKeyedObjectPool<ManagedTrackerHost, BorrowedTracker>(objectFactory);
    log.debug("Pool created");
  }

  /*
   * For tests only
   */
  MultiHostTrackerPool(List<ManagedTrackerHost> managedHosts, NetworkingConfiguration netConfig,
      GenericKeyedObjectPool<ManagedTrackerHost, BorrowedTracker> pool) {
    this.managedHosts = managedHosts;
    this.netConfig = netConfig;
    this.pool = pool;
  }

  @Override
  public Tracker getTracker() throws TrackerException {
    ManagedTrackerHost managedHost = nextHost();
    Tracker tracker = null;
    try {
      tracker = pool.borrowObject(managedHost);
    } catch (Exception e) {
      managedHost.markAsFailed();
      throw new CommunicationException(String.format("Unable connect to tracker %s", managedHost), e);
    }
    return tracker;
  }

  @Override
  public Set<InetSocketAddress> getAddresses() {
    Set<InetSocketAddress> addresses = new HashSet<InetSocketAddress>();
    for (ManagedTrackerHost host : managedHosts) {
      addresses.add(host.getAddress());
    }
    return Collections.unmodifiableSet(new HashSet<InetSocketAddress>(addresses));
  }

  @Override
  @Deprecated
  public Proxy getProxy() {
    return netConfig.getProxy();
  }

  @Override
  public NetworkingConfiguration getNetworkingConfiguration() {
    return netConfig;
  }

  /**
   * Returns the status of the hosts managed by this pool.
   * 
   * @return A list of hosts statuses.
   */
  public List<ManagedTrackerHost> getManagedHosts() {
    return managedHosts;
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#setMaxActive(int)}
   */
  public void setMaxActive(int maxActive) {
    pool.setMaxActive(maxActive);
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getMaxWait()}
   */
  public long getMaxWait() {
    return pool.getMaxWait();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#setMaxWait(long)}
   */
  public void setMaxWait(long maxWait) {
    pool.setMaxWait(maxWait);
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getMaxIdle()}
   */
  public void setMaxIdle(int maxIdle) {
    pool.setMaxIdle(maxIdle);
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getTestOnBorrow()}
   */
  public boolean getTestOnBorrow() {
    return pool.getTestOnBorrow();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#setTestOnBorrow(boolean)}
   */
  public void setTestOnBorrow(boolean testOnBorrow) {
    pool.setTestOnBorrow(testOnBorrow);
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getTestOnReturn()}
   */
  public boolean getTestOnReturn() {
    return pool.getTestOnReturn();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#setTestOnReturn(boolean)}
   */
  public void setTestOnReturn(boolean testOnReturn) {
    pool.setTestOnReturn(testOnReturn);
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getNumActive()}
   */
  public int getNumActive() {
    return pool.getNumActive();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getNumIdle()}
   */
  public int getNumIdle() {
    return pool.getNumIdle();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getMaxActive()}
   */
  public int getMaxActive() {
    return pool.getMaxActive();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getMaxIdle()}
   */
  public int getMaxIdle() {
    return pool.getMaxIdle();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#close()}
   */
  public void close() throws Exception {
    pool.close();
  }

  void invalidateTracker(BorrowedTracker borrowedTracker) throws Exception {
    pool.invalidateObject(borrowedTracker.getHost(), borrowedTracker);
  }

  void returnTracker(BorrowedTracker borrowedTracker) throws Exception {
    pool.returnObject(borrowedTracker.getHost(), borrowedTracker);
  }

  private ManagedTrackerHost nextHost() throws TrackerException {
    return Collections.max(managedHosts, HostPriorityOrder.INSTANCE);
  }

}
