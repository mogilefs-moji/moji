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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.moji.tracker.Tracker;

@RunWith(MockitoJUnitRunner.class)
public class FileLengthCommandTest {

  @Mock
  private HttpConnectionFactory mockHttpConnectionFactory;
  @Mock
  private HttpURLConnection mockHttpConnection;
  @Mock
  private Tracker mockTracker;
  private FileLengthCommand command;
  private URL url1;
  private URL url2;

  @Before
  public void setUp() throws Exception {
    url1 = new URL("http://127.0.0.1/domain/key/4.fid");
    url2 = new URL("http://127.0.0.1/domain/key/5.fid");
    command = new FileLengthCommand(mockHttpConnectionFactory, "key", "domain");

    when(mockHttpConnectionFactory.newConnection(url1)).thenReturn(mockHttpConnection);
  }

  @Test
  public void typical() throws Exception {
    when(mockHttpConnection.getHeaderField("Content-Length")).thenReturn("42");
    when(mockTracker.getPaths("key", "domain")).thenReturn(Collections.singletonList(url1));
    command.executeWithTracker(mockTracker);

    assertEquals(42, command.getLength());
    verify(mockHttpConnection).disconnect();
  }

  @Test
  public void noContentLengthHeader() throws Exception {
    when(mockHttpConnection.getHeaderField("Content-Length")).thenReturn(null);
    when(mockTracker.getPaths("key", "domain")).thenReturn(Collections.singletonList(url1));
    command.executeWithTracker(mockTracker);

    assertEquals(0, command.getLength());
    verify(mockHttpConnection).disconnect();
  }

  @Test
  public void fileSizeExceedsInt() throws Exception {
    when(mockHttpConnection.getHeaderField("Content-Length")).thenReturn("2147483648");
    when(mockTracker.getPaths("key", "domain")).thenReturn(Collections.singletonList(url1));
    command.executeWithTracker(mockTracker);

    assertEquals(Integer.MAX_VALUE + 1L, command.getLength());
  }

  @Test(expected = FileNotFoundException.class)
  public void noPaths() throws Exception {
    when(mockTracker.getPaths("key", "domain")).thenReturn(new ArrayList<URL>());
    command.executeWithTracker(mockTracker);
  }

  @Test
  public void firstPathFails() throws Exception {
    when(mockHttpConnectionFactory.newConnection(url1)).thenThrow(new IOException());
    when(mockHttpConnectionFactory.newConnection(url2)).thenReturn(mockHttpConnection);

    when(mockHttpConnection.getHeaderField("Content-Length")).thenReturn("42");
    when(mockTracker.getPaths("key", "domain")).thenReturn(Arrays.asList(url1, url2));
    command.executeWithTracker(mockTracker);

    assertEquals(42, command.getLength());
    verify(mockHttpConnection).disconnect();
  }

}
