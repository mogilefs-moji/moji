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
import java.util.concurrent.locks.Lock;

import org.apache.commons.io.input.CountingInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FileDownloadInputStream extends InputStream {

  private static final Logger log = LoggerFactory.getLogger(FileDownloadInputStream.class);

  private final CountingInputStream delegate;
  private final Lock readLock;

  FileDownloadInputStream(InputStream delegate, Lock readLock) {
    this.readLock = readLock;
    this.delegate = new CountingInputStream(delegate);
  }

  @Override
  public int read() throws IOException {
    return delegate.read();
  }

  @Override
  public int read(byte[] b) throws IOException {
    return delegate.read(b);
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return delegate.read(b, off, len);
  }

  @Override
  public long skip(long n) throws IOException {
    return delegate.skip(n);
  }

  @Override
  public int available() throws IOException {
    return delegate.available();
  }

  @Override
  public void close() throws IOException {
    log.debug("Read {} bytes", delegate.getByteCount());
    try {
      delegate.close();
    } finally {
      unlockQuietly(readLock);
    }
  }

  @Override
  public void mark(int readlimit) {
    delegate.mark(readlimit);
  }

  @Override
  public void reset() throws IOException {
    delegate.reset();
  }

  @Override
  public boolean markSupported() {
    return delegate.markSupported();
  }

  private void unlockQuietly(Lock lock) {
    try {
      lock.unlock();
    } catch (IllegalMonitorStateException e) {
    }
  }

}
