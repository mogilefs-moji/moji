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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.moji.Moji;
import fm.last.moji.MojiDeviceStatus;
import fm.last.moji.MojiFile;
import fm.last.moji.tracker.TrackerFactory;

class MojiImpl implements Moji {

  private static final Logger log = LoggerFactory.getLogger(MojiImpl.class);

  private final TrackerFactory trackerFactory;
  private final HttpConnectionFactory httpFactory;
  private final String domain;
  private final Executor executor;

  MojiImpl(TrackerFactory trackerFactory, HttpConnectionFactory httpFactory, String domain) {
    this.domain = domain;
    this.httpFactory = httpFactory;
    this.trackerFactory = trackerFactory;
    executor = new Executor(trackerFactory);
  }

  @Override
  public MojiFile getFile(String key) {
    log.debug("new {}()", MojiFileImpl.class.getSimpleName());
    return new MojiFileImpl(key, domain, "", trackerFactory, httpFactory);
  }

  @Override
  public MojiFile getFile(String key, String storageClass) {
    if (storageClass == null) {
      throw new IllegalArgumentException("storageClass == null");
    }
    log.debug("new {}() with storage class", MojiFileImpl.class.getSimpleName());
    return new MojiFileImpl(key, domain, storageClass, trackerFactory, httpFactory);
  }

  @Override
  public void copyToMogile(File source, MojiFile destination) throws IOException {
    OutputStream outputStream = null;
    InputStream inputStream = new FileInputStream(source);
    try {
      outputStream = destination.getOutputStream();
      IOUtils.copy(inputStream, outputStream); // buffers internally
      outputStream.flush();
    } finally {
      IOUtils.closeQuietly(inputStream);
      IOUtils.closeQuietly(outputStream);
    }
  }

  @Override
  public List<MojiFile> list(String keyPrefix) throws IOException {
    log.debug("list() : {}", keyPrefix);
    List<MojiFile> list = null;
    ListFilesCommand command = new ListFilesCommand(this, keyPrefix, domain);
    executor.executeCommand(command);
    list = command.getFileList();
    log.debug("list() -> {}", list);
    return list;
  }

  @Override
  public List<MojiFile> list(String keyPrefix, int limit) throws IOException {
    log.debug("list() : {}, {}", keyPrefix, limit);
    List<MojiFile> list = null;
    ListFilesCommand command = new ListFilesCommand(this, keyPrefix, domain, limit);
    executor.executeCommand(command);
    list = command.getFileList();
    log.debug("list() -> {}", list);
    return list;
  }

  @Override
  public List<MojiDeviceStatus> getDeviceStatuses() throws IOException {
    log.debug("getDevicesStatus : {}", this);
    GetDeviceStatusesCommand command = new GetDeviceStatusesCommand(domain);
    executor.executeCommand(command);
    List<MojiDeviceStatus> statuses = command.getStatuses();
    log.debug("getDevicesStatus() -> {}", statuses);
    return statuses;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("MojiImpl [domain=");
    builder.append(domain);
    builder.append(", trackerFactory=");
    builder.append(trackerFactory);
    builder.append("]");
    return builder.toString();
  }

}
