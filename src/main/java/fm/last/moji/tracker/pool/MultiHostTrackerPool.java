/*
 * Copyright 2009 Last.fm
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package fm.last.moji.tracker.pool;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.moji.impl.NetworkingConfiguration;
import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.TrackerException;
import fm.last.moji.tracker.TrackerFactory;
import fm.last.moji.tracker.impl.AbstractTrackerFactory;

/**
 * {@link fm.last.moji.tracker.TrackerFactory TrackerFactory} implementation that provides a
 * {@link fm.last.moji.tracker.Tracker Tracker} connection pool that can distribute requests across many hosts.
 */
public class MultiHostTrackerPool implements TrackerFactory {

  private static final Logger log = LoggerFactory.getLogger(MultiHostTrackerPool.class);
  private static final HostPriorityComparator PRIORITY_COMPARATOR = new HostPriorityComparator();

  private final NetworkingConfiguration netConfig;
  private final GenericKeyedObjectPool pool;
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
    KeyedPoolableObjectFactory objectFactory = new BorrowedTrackerObjectPoolFactory();
    pool = new GenericKeyedObjectPool(objectFactory);
    log.debug("Pool created");
  }

  @Override
  public Tracker getTracker() throws TrackerException {
    ManagedTrackerHost managedHost = nextHost();
    Tracker tracker = null;
    try {
      tracker = (Tracker) pool.borrowObject(managedHost);
    } catch (Exception e) {
      throw new TrackerException(e);
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

  private ManagedTrackerHost nextHost() throws TrackerException {
    ManagedTrackerHost managedHost = null;
    synchronized (managedHosts) {
      Collections.sort(managedHosts, PRIORITY_COMPARATOR);
      managedHost = managedHosts.get(managedHosts.size() - 1);
    }
    return managedHost;
  }

  private class BorrowedTrackerObjectPoolFactory extends BaseKeyedPoolableObjectFactory {

    private final AbstractTrackerFactory delegateFactory;

    BorrowedTrackerObjectPoolFactory() {
      delegateFactory = new AbstractTrackerFactory(netConfig);
    }

    @Override
    public Object makeObject(Object key) throws Exception {
      ManagedTrackerHost host = (ManagedTrackerHost) key;
      Tracker delegate = delegateFactory.newTracker(host.getAddress());
      BorrowedTracker borrowedTracker = new BorrowedTracker(host, delegate, pool);
      log.debug("Requested new tracker instance: {}", key);
      return borrowedTracker;
    }

    @Override
    public void destroyObject(Object key, Object obj) throws Exception {
      BorrowedTracker borrowed = (BorrowedTracker) obj;
      if (borrowed.getLastException() != null) {
        log.debug("Error occurred on tracker: {}", borrowed.getLastException().getMessage());
        borrowed.getHost().markAsFailed();
      }
      log.debug("Destroying {}", borrowed);
      borrowed.reallyClose();
    }

    @Override
    public boolean validateObject(Object key, Object obj) {
      BorrowedTracker borrowed = (BorrowedTracker) obj;
      log.debug("Validating {}", borrowed);
      try {
        borrowed.noop();
      } catch (TrackerException e) {
        // returning false will result in a destroyObject invocation
        // The address will then be marked out of service
        return false;
      }
      return true;
    }

  }

}
