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

import java.net.Proxy;

public class NetworkingConfiguration {

  public static final int INFINITE_TIMEOUT = 0;

  private Proxy proxy = Proxy.NO_PROXY;
  private int trackerConnectTimeout = INFINITE_TIMEOUT;
  private int trackerReadTimeout = INFINITE_TIMEOUT;
  private int httpConnectTimeout = INFINITE_TIMEOUT;
  private int httpReadTimeout = INFINITE_TIMEOUT;

  public static class Builder {

    private final NetworkingConfiguration building;

    public Builder() {
      building = new NetworkingConfiguration();
    }

    public Builder proxy(Proxy proxy) {
      building.setProxy(proxy);
      return this;
    }

    public Builder trackerConnectTimeout(int timeout) {
      building.setTrackerConnectTimeout(timeout);
      return this;
    }

    public Builder trackerReadTimeout(int timeout) {
      building.setTrackerReadTimeout(timeout);
      return this;
    }

    public Builder httpConnectTimeout(int timeout) {
      building.setHttpConnectTimeout(timeout);
      return this;
    }

    public Builder httpReadTimeout(int timeout) {
      building.setHttpReadTimeout(timeout);
      return this;
    }

    public NetworkingConfiguration build() {
      return building;
    }

  }

  public Proxy getProxy() {
    return proxy;
  }

  public void setProxy(Proxy proxy) {
    this.proxy = proxy;
  }

  public int getTrackerConnectTimeout() {
    return trackerConnectTimeout;
  }

  public void setTrackerConnectTimeout(int trackerConnectTimeout) {
    this.trackerConnectTimeout = trackerConnectTimeout;
  }

  public int getTrackerReadTimeout() {
    return trackerReadTimeout;
  }

  public void setTrackerReadTimeout(int trackerReadTimeout) {
    this.trackerReadTimeout = trackerReadTimeout;
  }

  public int getHttpConnectTimeout() {
    return httpConnectTimeout;
  }

  public void setHttpConnectTimeout(int httpConnectTimeout) {
    this.httpConnectTimeout = httpConnectTimeout;
  }

  public int getHttpReadTimeout() {
    return httpReadTimeout;
  }

  public void setHttpReadTimeout(int httpReadTimeout) {
    this.httpReadTimeout = httpReadTimeout;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("NetworkingConfiguration [proxy=");
    builder.append(proxy);
    builder.append(", trackerConnectTimeout=");
    builder.append(trackerConnectTimeout);
    builder.append(", trackerReadTimeout=");
    builder.append(trackerReadTimeout);
    builder.append(", httpConnectTimeout=");
    builder.append(httpConnectTimeout);
    builder.append(", httpReadTimeout=");
    builder.append(httpReadTimeout);
    builder.append("]");
    return builder.toString();
  }

}
