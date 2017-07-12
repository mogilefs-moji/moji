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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.moji.MojiFile;
import fm.last.moji.MojiFileAttributes;
import fm.last.moji.tracker.TrackerFactory;

class MojiFileImpl implements MojiFile {

  private static final Logger log = LoggerFactory.getLogger(MojiFileImpl.class);

  private final String domain;
  private final TrackerFactory trackerFactory;
  private final HttpConnectionFactory httpFactory;
  private final ReadWriteLock lock;
  private Executor executor;
  private String storageClass;
  private String key;

  MojiFileImpl(String key, String domain, String storageClass, TrackerFactory trackerFactory,
      HttpConnectionFactory httpFactory) {
    this.key = key;
    this.domain = domain;
    this.storageClass = storageClass;
    this.trackerFactory = trackerFactory;
    this.httpFactory = httpFactory;
    executor = new Executor(trackerFactory);
    lock = new ReentrantReadWriteLock();
  }

  @Override
  public boolean exists() throws IOException {
    log.debug("exists() : {}", this);
    boolean exists = false;
    try {
      lock.readLock().lock();
      ExistsCommand command = new ExistsCommand(key, domain);
      executor.executeCommand(command);
      exists = command.getExists();
      log.debug("exists() -> {}", exists);
    } finally {
      lock.readLock().unlock();
    }
    return exists;
  }

  @Override
  public void delete() throws IOException {
    log.debug("delete() : {}", this);
    try {
      lock.writeLock().lock();
      DeleteCommand command = new DeleteCommand(key, domain);
      executor.executeCommand(command);
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public long length() throws IOException {
    log.debug("length() : {}", this);
    long length = -1L;
    try {
      lock.readLock().lock();
      FileLengthCommand command = new FileLengthCommand(httpFactory, key, domain);
      executor.executeCommand(command);
      length = command.getLength();
      log.debug("length() -> {}", length);
    } finally {
      lock.readLock().unlock();
    }
    return length;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    log.debug("getInputStream() : {}", this);
    InputStream inputStream = null;
    try {
      Lock readLock = lock.readLock();
      readLock.lock();
      GetInputStreamCommand command = new GetInputStreamCommand(key, domain, httpFactory, readLock);
      executor.executeCommand(command);
      inputStream = command.getInputStream();
      log.debug("getInputStream() -> {}", inputStream);
    } catch (Throwable e) {
      unlockQuietly(lock.readLock());
      IOUtils.closeQuietly(inputStream);
      if (e instanceof IOException) {
        throw (IOException) e;
      } else {
        throw new RuntimeException(e);
      }
    }
    // calling close will release the lock
    return inputStream;
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    log.debug("getOutputStream() : {}", this);
    OutputStream outputStream = null;
    try {
      Lock writeLock = lock.writeLock();
      writeLock.lock();
      GetOutputStreamCommand command = new GetOutputStreamCommand(trackerFactory, httpFactory, key, domain,
          storageClass, writeLock);
      executor.executeCommand(command);
      outputStream = command.getOutputStream();
      log.debug("getOutputStream() -> {}", outputStream);
    } catch (Throwable e) {
      unlockQuietly(lock.writeLock());
      IOUtils.closeQuietly(outputStream);
      if (e instanceof IOException) {
        throw (IOException) e;
      } else {
        throw new RuntimeException(e);
      }
    }
    // calling close will release the lock
    return outputStream;
  }

  @Override
  public void rename(String newKey) throws IOException {
    log.debug("rename() : {}", this);
    try {
      lock.writeLock().lock();
      RenameCommand command = new RenameCommand(key, domain, newKey);
      executor.executeCommand(command);
      key = newKey;
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void modifyStorageClass(String newStorageClass) throws IOException {
    log.debug("setStorageClass() : {}", this);
    if (storageClass == null) {
      throw new IllegalArgumentException("storageClass == null");
    }
    try {
      lock.writeLock().lock();
      UpdateStorageClassCommand command = new UpdateStorageClassCommand(key, domain, newStorageClass);
      executor.executeCommand(command);
      storageClass = newStorageClass;
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public List<URL> getPaths() throws IOException {
    log.debug("getPaths() : {}", this);
    List<URL> paths = Collections.emptyList();
    try {
      lock.readLock().lock();
      GetPathsCommand command = new GetPathsCommand(key, domain);
      executor.executeCommand(command);
      paths = command.getPaths();
      log.debug("getPaths() -> {}", paths);
    } finally {
      lock.readLock().unlock();
    }
    return paths;
  }

  @Override
  public MojiFileAttributes getAttributes() throws IOException {
    log.debug("getAttributes() : {}", this);
    MojiFileAttributes attributes = null;
    try {
      lock.readLock().lock();
      GetAttributesCommand command = new GetAttributesCommand(key, domain);
      executor.executeCommand(command);
      attributes = command.getAttributes();
      log.debug("getAttributes() -> {}", attributes);
    } finally {
      lock.readLock().unlock();
    }
    return attributes;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public String getDomain() {
    return domain;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("MogileFileImpl [domain=");
    builder.append(domain);
    builder.append(", key=");
    builder.append(key);
    builder.append("]");
    return builder.toString();
  }

  @Override
  public void copyToFile(File destination) throws IOException {
    InputStream inputStream = null;
    inputStream = getInputStream();
    // buffers internally and closes
    FileUtils.copyInputStreamToFile(inputStream, destination);
  }

  void setExecutor(Executor executor) {
    this.executor = executor;
  }

  private void unlockQuietly(Lock lock) {
    try {
      lock.unlock();
    } catch (IllegalMonitorStateException e) {
    }
  }

}
