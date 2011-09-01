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
package fm.last.moji.spring;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Set;

import fm.last.moji.Moji;
import fm.last.moji.MojiFile;
import fm.last.moji.impl.DefaultMojiFactory;
import fm.last.moji.tracker.impl.InetSocketAddressFactory;
import fm.last.moji.tracker.pool.MultiHostTrackerPool;

/**
 * A {@link fm.last.moji.Moji Moji} delegate that exposes pool properties and is easily configured in Spring.
 */
public class SpringMojiBean implements Moji {

  private final Moji moji;
  private final MultiHostTrackerPool poolingTrackerFactory;

  public SpringMojiBean(String addressesCsv, String domain) {
    this(addressesCsv, Proxy.NO_PROXY, domain);
  }

  public SpringMojiBean(String addressesCsv, Proxy proxy, String domain) {
    Set<InetSocketAddress> addresses = InetSocketAddressFactory.newAddresses(addressesCsv);
    poolingTrackerFactory = new MultiHostTrackerPool(addresses, proxy);
    DefaultMojiFactory factory = new DefaultMojiFactory(poolingTrackerFactory, domain);
    moji = factory.getInstance();
  }

  @Override
  public MojiFile getFile(String key) {
    return moji.getFile(key);
  }

  @Override
  public MojiFile getFile(String key, String storageClass) {
    return moji.getFile(key, storageClass);
  }

  @Override
  public void copyToMogile(File source, MojiFile destination) throws IOException {
    moji.copyToMogile(source, destination);
  }

  @Override
  public List<MojiFile> list(String keyPrefix) throws IOException {
    return moji.list(keyPrefix);
  }

  @Override
  public List<MojiFile> list(String keyPrefix, int limit) throws IOException {
    return moji.list(keyPrefix, limit);
  }

  /**
   * See: {@link fm.last.moji.tracker.TrackerFactory#getProxy()}
   */
  public Proxy getProxy() {
    return poolingTrackerFactory.getProxy();
  }

  /**
   * See: {@link fm.last.moji.tracker.TrackerFactory#getAddresses()}
   */
  public Set<InetSocketAddress> getAddresses() {
    return poolingTrackerFactory.getAddresses();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#close()}
   */
  public void close() throws Exception {
    poolingTrackerFactory.close();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getMaxActive()}
   */
  public int getMaxActive() {
    return poolingTrackerFactory.getMaxActive();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#setMaxActive(int)}
   */
  public void setMaxActive(int maxActive) {
    poolingTrackerFactory.setMaxActive(maxActive);
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getMaxWait()}
   */
  public long getMaxWait() {
    return poolingTrackerFactory.getMaxWait();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#setMaxWait(long)}
   */
  public void setMaxWait(long maxWait) {
    poolingTrackerFactory.setMaxWait(maxWait);
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getMaxIdle()}
   */
  public int getMaxIdle() {
    return poolingTrackerFactory.getMaxIdle();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#setMaxIdle(int)}
   */
  public void setMaxIdle(int maxIdle) {
    poolingTrackerFactory.setMaxIdle(maxIdle);
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getTestOnBorrow()}
   */
  public boolean getTestOnBorrow() {
    return poolingTrackerFactory.getTestOnBorrow();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#setTestOnBorrow(boolean)}
   */
  public void setTestOnBorrow(boolean testOnBorrow) {
    poolingTrackerFactory.setTestOnBorrow(testOnBorrow);
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getTestOnReturn()}
   */
  public boolean getTestOnReturn() {
    return poolingTrackerFactory.getTestOnReturn();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#setTestOnReturn(boolean)}
   */
  public void setTestOnReturn(boolean testOnReturn) {
    poolingTrackerFactory.setTestOnReturn(testOnReturn);
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getNumActive()}
   */
  public int getNumActive() {
    return poolingTrackerFactory.getNumActive();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getNumIdle()}
   */
  public int getNumIdle() {
    return poolingTrackerFactory.getNumIdle();
  }

}
