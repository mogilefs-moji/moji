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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.moji.MojiFileAttributes;
import fm.last.moji.tracker.Destination;
import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.TrackerException;
import fm.last.moji.tracker.TrackerFactory;

@RunWith(MockitoJUnitRunner.class)
public class MojiFileImplTest {

  private static final String DOMAIN = "domain";
  private static final String STORAGE_CLASS = "storageClass";
  private static final String STORAGE_CLASS_2 = "newStorageClass";
  private static final String KEY = "key";
  private static final String KEY_2 = "newKey";

  @Mock
  private Tracker mockTracker;
  @Mock
  private TrackerFactory mockTrackerFactory;
  @Mock
  private HttpConnectionFactory mockHttpFactory;
  @Mock
  private Executor mockExecutor;
  @Mock
  private HttpURLConnection mockUrlConnection;
  @Mock
  private InputStream mockInputStream;
  @Mock
  private OutputStream mockOutputStream;
  @Mock
  private InetSocketAddress mockAddress;

  private MojiFileImpl file;

  @Before
  public void init() throws TrackerException {
    when(mockTrackerFactory.getTracker()).thenReturn(mockTracker);
    when(mockTrackerFactory.getAddresses()).thenReturn(Collections.singleton(mockAddress));
    file = new MojiFileImpl(KEY, DOMAIN, STORAGE_CLASS, mockTrackerFactory, mockHttpFactory);
  }

  @Test
  public void existsCommand() throws IOException {
    file.setExecutor(mockExecutor);
    file.exists();
    ArgumentCaptor<ExistsCommand> captor = ArgumentCaptor.forClass(ExistsCommand.class);
    verify(mockExecutor).executeCommand(captor.capture());
    assertThat(captor.getValue().key, is(KEY));
    assertThat(captor.getValue().domain, is(DOMAIN));
  }

  @Test
  public void existsReturn() throws IOException {
    List<URL> paths = Collections.singletonList(new URL("http://localhost:80/"));
    when(mockTracker.getPaths(KEY, DOMAIN)).thenReturn(paths);
    boolean exists = file.exists();
    assertThat(exists, is(true));
  }

  @Test
  public void deleteCommand() throws IOException {
    file.setExecutor(mockExecutor);
    file.delete();
    ArgumentCaptor<DeleteCommand> captor = ArgumentCaptor.forClass(DeleteCommand.class);
    verify(mockExecutor).executeCommand(captor.capture());
    assertThat(captor.getValue().key, is(KEY));
    assertThat(captor.getValue().domain, is(DOMAIN));
  }

  @Test
  public void renameCommand() throws IOException {
    file.setExecutor(mockExecutor);
    file.rename(KEY_2);
    ArgumentCaptor<RenameCommand> captor = ArgumentCaptor.forClass(RenameCommand.class);
    verify(mockExecutor).executeCommand(captor.capture());
    assertThat(captor.getValue().key, is(KEY));
    assertThat(captor.getValue().domain, is(DOMAIN));
    assertThat(captor.getValue().newKey, is(KEY_2));
  }

  @Test
  public void renameChangesKeyInFile() throws IOException {
    assertThat(file.getKey(), is(KEY));
    file.rename(KEY_2);
    assertThat(file.getKey(), is(KEY_2));
  }

  @Test
  public void renameKeyNotModifiedOnError() throws IOException {
    file.setExecutor(mockExecutor);
    doThrow(new IOException()).when(mockExecutor).executeCommand(any(RenameCommand.class));
    assertThat(file.getKey(), is(KEY));
    try {
      file.rename(KEY_2);
    } catch (IOException ignored) {
    }
    assertThat(file.getKey(), is(KEY));
  }

  @Test
  public void storageClassCommand() throws IOException {
    file.setExecutor(mockExecutor);
    file.modifyStorageClass(STORAGE_CLASS_2);
    ArgumentCaptor<UpdateStorageClassCommand> captor = ArgumentCaptor.forClass(UpdateStorageClassCommand.class);
    verify(mockExecutor).executeCommand(captor.capture());
    assertThat(captor.getValue().key, is(KEY));
    assertThat(captor.getValue().domain, is(DOMAIN));
    assertThat(captor.getValue().newStorageClass, is(STORAGE_CLASS_2));
  }

  @Test
  public void lengthCommand() throws IOException {
    file.setExecutor(mockExecutor);
    file.length();
    ArgumentCaptor<FileLengthCommand> captor = ArgumentCaptor.forClass(FileLengthCommand.class);
    verify(mockExecutor).executeCommand(captor.capture());
    assertThat(captor.getValue().key, is(KEY));
    assertThat(captor.getValue().domain, is(DOMAIN));
  }

  @Test
  public void lengthReturn() throws IOException {
    URL path = new URL("http://localhost:80/");
    List<URL> paths = Collections.singletonList(path);
    when(mockTracker.getPaths(KEY, DOMAIN)).thenReturn(paths);
    when(mockHttpFactory.newConnection(path)).thenReturn(mockUrlConnection);
    when(mockUrlConnection.getHeaderField("Content-Length")).thenReturn("74634654");

    // check that whatever we have delegates to the expected stream
    long length = file.length();
    assertThat(length, is(74634654L));
  }

  @Test
  public void getInputStreamCommand() throws IOException {
    file.setExecutor(mockExecutor);
    file.getInputStream();
    ArgumentCaptor<GetInputStreamCommand> captor = ArgumentCaptor.forClass(GetInputStreamCommand.class);
    verify(mockExecutor).executeCommand(captor.capture());
    assertThat(captor.getValue().key, is(KEY));
    assertThat(captor.getValue().domain, is(DOMAIN));
  }

  @Test
  public void getInputStreamCommandReturn() throws IOException {
    URL path = new URL("http://localhost:80/");
    List<URL> paths = Collections.singletonList(path);
    when(mockTracker.getPaths(KEY, DOMAIN)).thenReturn(paths);
    when(mockHttpFactory.newConnection(path)).thenReturn(mockUrlConnection);
    when(mockUrlConnection.getInputStream()).thenReturn(mockInputStream);

    // check that whatever we have delegates to the expected stream
    InputStream inputStream = file.getInputStream();
    byte[] myBuffer = new byte[2];
    inputStream.read(myBuffer, 2, 43);
    verify(mockInputStream).read(myBuffer, 2, 43);
  }

  @Test
  public void getOutputStreamCommand() throws IOException {
    file.setExecutor(mockExecutor);
    file.getOutputStream();
    ArgumentCaptor<GetOutputStreamCommand> captor = ArgumentCaptor.forClass(GetOutputStreamCommand.class);
    verify(mockExecutor).executeCommand(captor.capture());
    assertThat(captor.getValue().key, is(KEY));
    assertThat(captor.getValue().domain, is(DOMAIN));
    assertThat(captor.getValue().storageClass, is(STORAGE_CLASS));
  }

  @Test
  public void getOutputStreamCommandReturn() throws IOException {
    URL path = new URL("http://localhost:80/");
    Destination destination = new Destination(path, 2, 4);
    List<Destination> destinations = Collections.singletonList(destination);
    when(mockTracker.createOpen(KEY, DOMAIN, STORAGE_CLASS)).thenReturn(destinations);
    when(mockHttpFactory.newConnection(path)).thenReturn(mockUrlConnection);
    when(mockUrlConnection.getOutputStream()).thenReturn(mockOutputStream);

    // check that whatever we have delegates to the expected stream
    OutputStream outputStream = file.getOutputStream();
    byte[] myBuffer = new byte[2];
    outputStream.write(myBuffer, 3, 5);
    verify(mockOutputStream).write(myBuffer, 3, 5);
  }

  @Test
  public void getPaths() throws IOException {
    List<URL> trackerPaths = Collections.singletonList(new URL("http://www.last.fm/1/2"));
    when(mockTracker.getPaths(KEY, DOMAIN)).thenReturn(trackerPaths);
    List<URL> paths = file.getPaths();
    assertThat(paths, is(trackerPaths));
  }

  @Test
  public void fileInfo() throws IOException {
    Map<String, String> responseValues = new HashMap<String, String>();
    responseValues.put("domain", "domain2");
    responseValues.put("key", "key2");
    responseValues.put("class", "default");
    responseValues.put("length", "100");
    responseValues.put("devcount", "2");
    responseValues.put("fid", "5645");

    when(mockTracker.fileInfo(KEY, DOMAIN)).thenReturn(responseValues);

    MojiFileAttributes attributes = file.getAttributes();
    assertEquals(attributes.getDomain(), "domain2");
    assertEquals(attributes.getKey(), "key2");
    assertEquals(attributes.getStorageClass(), "default");
    assertEquals(attributes.getLength(), 100L);
    assertEquals(attributes.getDeviceCount(), 2);
    assertEquals(attributes.getFid(), 5645L);
  }

}
