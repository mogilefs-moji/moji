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

import static fm.last.moji.impl.NetworkingConfiguration.INFINITE_TIMEOUT;
import static java.net.Proxy.NO_PROXY;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.StringUtils;

import fm.last.moji.Moji;
import fm.last.moji.MojiDeviceStatus;
import fm.last.moji.MojiFile;
import fm.last.moji.impl.DefaultMojiFactory;
import fm.last.moji.impl.NetworkingConfiguration;
import fm.last.moji.tracker.impl.InetSocketAddressFactory;
import fm.last.moji.tracker.pool.MultiHostTrackerPool;

/**
 * A {@link fm.last.moji.Moji Moji} delegate that exposes pool properties and is easily configured in Spring.
 */
public class SpringMojiBean implements Moji {

  private String addressesCsv;
  private String domain;

  private Moji moji;
  private MultiHostTrackerPool poolingTrackerFactory;
  private Proxy proxy = NO_PROXY;
  private int httpConnectTimeout = INFINITE_TIMEOUT;
  private int httpReadTimeout = INFINITE_TIMEOUT;
  private int trackerReadTimeout = INFINITE_TIMEOUT;
  private int trackerConnectTimeout = INFINITE_TIMEOUT;
  private Boolean testOnReturn;
  private Boolean testOnBorrow;
  private Integer maxIdle;
  private Long maxWait;
  private Integer maxActive;

  public SpringMojiBean() {
  }

  /** Retained for API compatibility only - use {@link SpringMojiBean#SpringMojiBean()} instead. */
  @Deprecated
  public SpringMojiBean(String addressesCsv, String domain) {
    this(addressesCsv, NO_PROXY, domain);
  }

  /** Retained for API compatibility only - use {@link SpringMojiBean#SpringMojiBean()} instead. */
  @Deprecated
  public SpringMojiBean(String addressesCsv, Proxy proxy, String domain) {
    this.addressesCsv = addressesCsv;
    this.proxy = proxy;
    this.domain = domain;
  }

  /**
   * Automatically called by Spring after the properties have been set.
   */
  @PostConstruct
  public void initialise() {
    if (StringUtils.isBlank(addressesCsv)) {
      throw new IllegalStateException("addressesCsv not set");
    }
    if (StringUtils.isBlank(domain)) {
      throw new IllegalStateException("domain not set");
    }
    NetworkingConfiguration netConfig = createNetworkConfiguration();
    Set<InetSocketAddress> addresses = InetSocketAddressFactory.newAddresses(addressesCsv);
    createTrackerPool(netConfig, addresses);
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

  @Override
  public List<MojiDeviceStatus> getDeviceStatuses() throws IOException {
    return moji.getDeviceStatuses();
  }

  /**
   * See: {@link fm.last.moji.tracker.TrackerFactory#getProxy()}.
   */
  public Proxy getProxy() {
    return proxy;
  }

  /**
   * See: {@link fm.last.moji.tracker.TrackerFactory#getAddresses()}.
   */
  public Set<InetSocketAddress> getAddresses() {
    return poolingTrackerFactory.getAddresses();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#close()}.
   */
  @PreDestroy
  public void close() throws Exception {
    poolingTrackerFactory.close();
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getMaxActive()}.
   */
  public int getMaxActive() {
    return maxActive;
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#setMaxActive(int)}. Setting this value after
   * {@link #initialise()} has been called will have no effect.
   */
  public void setMaxActive(int maxActive) {
    this.maxActive = maxActive;
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getMaxWait()}.
   */
  public long getMaxWait() {
    return maxWait;
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#setMaxWait(long)}. Setting this value after
   * {@link #initialise()} has been called will have no effect.
   */
  public void setMaxWait(long maxWait) {
    this.maxWait = maxWait;
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getMaxIdle()}.
   */
  public int getMaxIdle() {
    return maxIdle;
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#setMaxIdle(int)}. Setting this value after
   * {@link #initialise()} has been called will have no effect.
   */
  public void setMaxIdle(int maxIdle) {
    this.maxIdle = maxIdle;
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getTestOnBorrow()}.
   */
  public boolean getTestOnBorrow() {
    return testOnBorrow;
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#setTestOnBorrow(boolean)}. Setting this value after
   * {@link #initialise()} has been called will have no effect.
   */
  public void setTestOnBorrow(boolean testOnBorrow) {
    this.testOnBorrow = testOnBorrow;
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getTestOnReturn()}.
   */
  public boolean getTestOnReturn() {
    return testOnReturn;
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#setTestOnReturn(boolean)}. Setting this value after
   * {@link #initialise()} has been called will have no effect.
   */
  public void setTestOnReturn(boolean testOnReturn) {
    this.testOnReturn = testOnReturn;
  }

  /**
   * See: {@link org.apache.commons.pool.impl.GenericKeyedObjectPool#getNumActive()}.
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

  public String getAddressesCsv() {
    return addressesCsv;
  }

  /**
   * Setting this value after {@link #initialise()} has been called will have no effect.
   */
  public void setAddressesCsv(String addressesCsv) {
    this.addressesCsv = addressesCsv;
  }

  public String getDomain() {
    return domain;
  }

  /**
   * Setting this value after {@link #initialise()} has been called will have no effect.
   */
  public void setDomain(String domain) {
    this.domain = domain;
  }

  public int getTrackerConnectTimeout() {
    return trackerConnectTimeout;
  }

  /**
   * Setting this value after {@link #initialise()} has been called will have no effect.
   */
  public void setTrackerConnectTimeout(int trackerConnectTimeout) {
    this.trackerConnectTimeout = trackerConnectTimeout;
  }

  public int getTrackerReadTimeout() {
    return trackerReadTimeout;
  }

  /**
   * @deprecated Kept for backward compatibility only
   */
  @Deprecated
  public void setTrackerSoTimeout(int trackerReadTimeout) {
    setTrackerReadTimeout(trackerReadTimeout);
  }

  /**
   * Setting this value after {@link #initialise()} has been called will have no effect.
   */
  public void setTrackerReadTimeout(int trackerReadTimeout) {
    this.trackerReadTimeout = trackerReadTimeout;
  }

  public int getHttpConnectTimeout() {
    return httpConnectTimeout;
  }

  /**
   * Setting this value after {@link #initialise()} has been called will have no effect.
   */
  public void setHttpConnectTimeout(int httpConnectTimeout) {
    this.httpConnectTimeout = httpConnectTimeout;
  }

  public int getHttpReadTimeout() {
    return httpReadTimeout;
  }

  /**
   * Setting this value after {@link #initialise()} has been called will have no effect.
   */
  public void setHttpReadTimeout(int httpReadTimeout) {
    this.httpReadTimeout = httpReadTimeout;
  }

  /**
   * Setting this value after {@link #initialise()} has been called will have no effect.
   */
  public void setProxy(Proxy proxy) {
    this.proxy = proxy;
  }

  private NetworkingConfiguration createNetworkConfiguration() {
    return new NetworkingConfiguration.Builder().proxy(proxy).httpConnectTimeout(httpConnectTimeout)
        .httpReadTimeout(httpReadTimeout).trackerConnectTimeout(trackerConnectTimeout)
        .trackerReadTimeout(trackerReadTimeout).build();
  }

  private void createTrackerPool(NetworkingConfiguration netConfig, Set<InetSocketAddress> addresses) {
    poolingTrackerFactory = new MultiHostTrackerPool(addresses, netConfig);
    if (testOnBorrow != null) {
      poolingTrackerFactory.setTestOnBorrow(testOnBorrow);
    }
    if (testOnReturn != null) {
      poolingTrackerFactory.setTestOnReturn(testOnReturn);
    }
    if (maxActive != null) {
      poolingTrackerFactory.setMaxActive(maxActive);
    }
    if (maxIdle != null) {
      poolingTrackerFactory.setMaxIdle(maxIdle);
    }
    if (maxWait != null) {
      poolingTrackerFactory.setMaxWait(maxWait);
    }
  }

}
