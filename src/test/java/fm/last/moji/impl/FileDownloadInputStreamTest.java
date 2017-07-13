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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.Lock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FileDownloadInputStreamTest {

  @Mock
  private InputStream mockInputStream;
  @Mock
  private Lock mockReadLock;
  private FileDownloadInputStream stream;

  @Before
  public void setUp() {
    stream = new FileDownloadInputStream(mockInputStream, mockReadLock);
  }

  @Test
  public void readDelegates() throws IOException {
    stream.read();
    verify(mockInputStream).read();
  }

  @Test
  public void readByteArrayDelegates() throws IOException {
    byte[] b = new byte[4];
    stream.read(b);
    verify(mockInputStream).read(b);
  }

  @Test
  public void readByteArrayWithOffsetDelegates() throws IOException {
    byte[] b = new byte[4];
    stream.read(b, 2, 4);
    verify(mockInputStream).read(b, 2, 4);
  }

  @Test
  public void skipDelegates() throws IOException {
    stream.skip(1);
    verify(mockInputStream).skip(1);
  }

  @Test
  public void availableDelegates() throws IOException {
    stream.available();
    verify(mockInputStream).available();
  }

  @Test
  public void closeDelegates() throws IOException {
    stream.close();
    verify(mockInputStream).close();
    verify(mockReadLock).unlock();
  }

  @Test
  public void closeReleasesLockEvenOnError() throws IOException {
    doThrow(new IOException()).when(mockInputStream).close();
    try {
      stream.close();
    } catch (Exception e) {
    }
    verify(mockReadLock).unlock();
  }

  @Test
  public void markDelegates() {
    stream.mark(21);
    verify(mockInputStream).mark(21);
  }

  @Test
  public void resetDelegates() throws IOException {
    stream.reset();
    verify(mockInputStream).reset();
  }

  @Test
  public void markSupportedDelegates() {
    stream.markSupported();
    verify(mockInputStream).markSupported();
  }

}
