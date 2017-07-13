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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import fm.last.moji.Moji;
import fm.last.moji.MojiFactory;
import fm.last.moji.tracker.TrackerFactory;
import fm.last.moji.tracker.impl.InetSocketAddressFactory;
import fm.last.moji.tracker.pool.MultiHostTrackerPool;

/**
 * Creates a {@link fm.last.moji.Moji Moji} instance using configuration information obtained from the following
 * properties:
 * <p>
 * <ul>
 * <li><code>moji.domain</code></li>
 * <li><code>moji.tracker.hosts</code></li>
 * <li><code>moji.tracker.connect.timeout</code> (optional)</li>
 * <li><code>moji.tracker.so.timeout</code> (optional)</li>
 * <li><code>moji.http.connect.timeout</code> (optional)</li>
 * <li><code>moji.http.read.timeout</code> (optional)</li>
 * </ul>
 * <p>
 * The properties are loaded from a <code>/moji.properties</code> classpath resource by default. The resource path can
 * be specified by setting the <code>moji.properties.resource.path</code> system property.
 */
public class PropertyMojiFactory implements MojiFactory {

  public static final String RESOURCE_PATH_PROPERTY = "moji.properties.resource.path";
  private static final String DEFAULT_RESOURCE_PATH = "/moji.properties";

  private static final String HOSTS_PROPERTY = "moji.tracker.hosts";
  private static final String DOMAIN_PROPERTY = "moji.domain";
  private static final String TRACKER_CONNECT_T_O_PROPERTY = "moji.tracker.connect.timeout";
  private static final String TRACKER_READ_T_O_PROPERTY = "moji.tracker.so.timeout";
  private static final String HTTP_CONNECT_T_O_PROPERTY = "moji.http.connect.timeout";
  private static final String HTTP_READ_T_O_PROPERTY = "moji.http.read.timeout";

  private final NetworkingConfiguration netConfig;
  private volatile boolean initialised;
  private String defaultDomain;
  private TrackerFactory trackerFactory;
  private HttpConnectionFactory httpFactory;
  private final String propertiesPath;

  public PropertyMojiFactory(String propertiesPath, Proxy proxy) throws IOException {
    this.propertiesPath = System.getProperty(RESOURCE_PATH_PROPERTY, propertiesPath);
    netConfig = new NetworkingConfiguration.Builder().proxy(proxy).build();
  }

  public PropertyMojiFactory(String propertiesPath) throws IOException {
    this(propertiesPath, Proxy.NO_PROXY);
  }

  public PropertyMojiFactory(Proxy proxy) throws IOException {
    this(DEFAULT_RESOURCE_PATH, proxy);
  }

  public PropertyMojiFactory() throws IOException {
    this(DEFAULT_RESOURCE_PATH, Proxy.NO_PROXY);
  }

  @Override
  public Moji getInstance() throws IOException {
    initialise();
    return new MojiImpl(trackerFactory, httpFactory, defaultDomain);
  }

  @Override
  public Moji getInstance(String domain) throws IOException {
    initialise();
    return new MojiImpl(trackerFactory, httpFactory, domain);
  }

  private void initialise() throws IOException {
    synchronized (this) {
      if (!initialised) {
        Properties properties = loadProperties();
        String addressesCsv = getHosts(properties);
        defaultDomain = getDomain(properties);
        Set<InetSocketAddress> addresses = InetSocketAddressFactory.newAddresses(addressesCsv);

        netConfig.setHttpConnectTimeout(getTimeout(HTTP_CONNECT_T_O_PROPERTY, properties));
        netConfig.setHttpReadTimeout(getTimeout(HTTP_READ_T_O_PROPERTY, properties));
        netConfig.setTrackerConnectTimeout(getTimeout(TRACKER_CONNECT_T_O_PROPERTY, properties));
        netConfig.setTrackerReadTimeout(getTimeout(TRACKER_READ_T_O_PROPERTY, properties));

        trackerFactory = new MultiHostTrackerPool(addresses, netConfig);
        httpFactory = new HttpConnectionFactory(trackerFactory.getNetworkingConfiguration());
        initialised = true;
      }
    }
  }

  private Properties loadProperties() throws IOException {
    Properties properties = new Properties();
    InputStream stream = getClass().getResourceAsStream(propertiesPath);
    try {
      properties.load(stream);
    } finally {
      IOUtils.closeQuietly(stream);
    }
    return properties;
  }

  private String getDomain(Properties properties) {
    String domain = properties.getProperty(DOMAIN_PROPERTY);
    if (domain == null || domain.isEmpty()) {
      throw new IllegalStateException(DOMAIN_PROPERTY + " cannot be empty or null");
    }
    return domain;
  }

  private String getHosts(Properties properties) {
    String host = properties.getProperty(HOSTS_PROPERTY);
    if (host == null || host.isEmpty()) {
      throw new IllegalStateException(HOSTS_PROPERTY + " cannot be empty or null");
    }
    return host;
  }

  private int getTimeout(String propertyName, Properties properties) {
    int timeout = 0;
    try {
      timeout = Integer.parseInt(properties.getProperty(propertyName, "0"));
    } catch (NumberFormatException e) {
      throw new IllegalStateException(propertyName + " must be an integer.");
    }
    return timeout;
  }

}
