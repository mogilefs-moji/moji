/*
 * Copyright 2012 Last.fm
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
package fm.last.moji.impl;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.locks.Lock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.moji.tracker.Destination;
import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.TrackerFactory;

@RunWith(MockitoJUnitRunner.class)
public class FileUploadOutputStreamTest {

  private static final String KEY = "key";
  private static final String DOMAIN = "domain";
  @Mock
  private TrackerFactory mockTrackerFactory;
  @Mock
  private HttpConnectionFactory mockHttpFactory;
  @Mock
  private Destination mockDestination;
  @Mock
  private HttpURLConnection mockHttpConnection;
  @Mock
  private OutputStream mockOutputStream;
  @Mock
  private InputStream mockInputStream;
  @Mock
  private Tracker mockTracker;
  @Mock
  private Lock mockWriteLock;
  private FileUploadOutputStream stream;

  @Before
  public void setUp() throws IOException {
    URL url = new URL("http://www.last.fm/");
    when(mockDestination.getPath()).thenReturn(url);
    when(mockHttpFactory.newConnection(url)).thenReturn(mockHttpConnection);
    when(mockHttpConnection.getInputStream()).thenReturn(mockInputStream);
    when(mockHttpConnection.getOutputStream()).thenReturn(mockOutputStream);
    when(mockHttpConnection.getResponseMessage()).thenReturn("message");
    when(mockHttpConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
    when(mockTrackerFactory.getTracker()).thenReturn(mockTracker);

    stream = new FileUploadOutputStream(mockTrackerFactory, mockHttpFactory, KEY, DOMAIN, mockDestination,
        mockWriteLock);
  }

  @Test
  public void httpConnectionSetUp() throws IOException {
    verify(mockHttpConnection).setRequestMethod("PUT");
    verify(mockHttpConnection).setChunkedStreamingMode(4096);
    verify(mockHttpConnection).setDoOutput(true);
  }

  @Test
  public void everyThingCloses() throws IOException {
    stream.write(1);
    stream.close();

    verify(mockOutputStream).flush();
    verify(mockOutputStream).close();
    verify(mockHttpConnection).disconnect();
    verify(mockTracker).createClose(KEY, DOMAIN, mockDestination, 1);
    verify(mockTracker).close();
    verify(mockWriteLock).unlock();
  }

  @Test
  public void everyThingClosesEvenOnFail() throws IOException {
    doThrow(new RuntimeException()).when(mockOutputStream).flush();
    doThrow(new RuntimeException()).when(mockOutputStream).close();
    when(mockHttpConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);
    doThrow(new RuntimeException()).when(mockInputStream).close();
    doThrow(new RuntimeException()).when(mockHttpConnection).disconnect();
    doThrow(new RuntimeException()).when(mockTracker).createClose(KEY, DOMAIN, mockDestination, 1);

    try {
      stream.write(1);
      stream.close();
    } catch (Exception e) {
    }

    verify(mockOutputStream).flush();
    verify(mockOutputStream).close();
    verify(mockHttpConnection).disconnect();
    verify(mockWriteLock).unlock();
  }

  @Test
  public void writeIntDelegates() throws IOException {
    stream.write(1);
    verify(mockOutputStream).write(1);
  }

  @Test
  public void writeByteArrayDelegates() throws IOException {
    byte[] b = new byte[4];
    stream.write(b);
    verify(mockOutputStream).write(b);
  }

  @Test
  public void writeByteArrayWithOffsetDelegates() throws IOException {
    byte[] b = new byte[4];
    stream.write(b, 2, 4);
    verify(mockOutputStream).write(b, 2, 4);
  }

  @Test
  public void flushDelegates() throws IOException {
    stream.flush();
    verify(mockOutputStream).flush();
  }

  @Test
  public void countOnWrite() throws IOException {
    byte[] b = new byte[] { 1, 2, 3, 4, 5 };
    stream.write(b);
    stream.flush();
    stream.close();
    verify(mockTracker).createClose(KEY, DOMAIN, mockDestination, 5);
  }

}
