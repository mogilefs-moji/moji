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
package fm.last.moji.tracker.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.moji.tracker.Destination;
import fm.last.moji.tracker.KeyExistsAlreadyException;
import fm.last.moji.tracker.TrackerException;
import fm.last.moji.tracker.UnknownCommandException;
import fm.last.moji.tracker.UnknownKeyException;

@RunWith(MockitoJUnitRunner.class)
public class TrackerImplTest {

  private static final String STORAGE_CLASS = "storageClass";
  private static final String KEY = "key";
  private static final String DOMAIN = "domain";
  private static final long SIZE = 0;
  private static final String NEW_KEY = "newKey";

  @Mock
  private Socket mockSocket;
  @Mock
  private RequestHandler mockRequestHandler;
  @Mock
  private Response mockResponse;

  private TrackerImpl tracker;
  private final ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);

  @Before
  public void setUp() throws IOException {
    tracker = new TrackerImpl(mockSocket, mockRequestHandler);
    when(mockResponse.getStatus()).thenReturn(ResponseStatus.OK);
    when(mockRequestHandler.performRequest(requestCaptor.capture())).thenReturn(mockResponse);
  }

  @Test
  public void getPaths() throws Exception {
    // See: GetPathsOperationTest
    when(mockResponse.getValue("paths")).thenReturn("0");
    List<URL> paths = tracker.getPaths(KEY, DOMAIN);
    assertThat(paths.size(), is(0));
  }

  @Test
  public void fileInfoRequest() throws Exception {
    Map<String, String> attributes = tracker.fileInfo(KEY, DOMAIN);

    Request request = requestCaptor.getValue();
    assertThat(request.getCommand(), is("file_info"));
    assertThat(request.getArguments().size(), is(2));
    assertThat(request.getArguments().get("domain"), is(DOMAIN));
    assertThat(request.getArguments().get("key"), is(KEY));

    assertThat(attributes.isEmpty(), is(true));
  }

  @Test(expected = UnknownCommandException.class)
  public void fileInfoOldMogileVersion() throws Exception {
    when(mockResponse.getStatus()).thenReturn(ResponseStatus.ERROR);
    when(mockResponse.getMessage()).thenReturn("unknown_command");
    tracker.fileInfo(KEY, DOMAIN);
  }

  @Test(expected = UnknownKeyException.class)
  public void fileInfoUnknownKey() throws Exception {
    when(mockResponse.getStatus()).thenReturn(ResponseStatus.ERROR);
    when(mockResponse.getMessage()).thenReturn("unknown_key");
    tracker.fileInfo(KEY, DOMAIN);
  }

  @Test
  public void createOpen() throws Exception {
    // See: CreateOpenOperationTest
    when(mockResponse.getValue("dev_count")).thenReturn("0");
    when(mockResponse.getValue("fid")).thenReturn("0");
    List<Destination> destinations = tracker.createOpen(KEY, DOMAIN, STORAGE_CLASS);
    assertThat(destinations.size(), is(0));
  }

  @Test
  public void deleteRequest() throws Exception {
    tracker.delete(KEY, DOMAIN);
    Request request = requestCaptor.getValue();
    assertThat(request.getCommand(), is("delete"));
    assertThat(request.getArguments().size(), is(2));
    assertThat(request.getArguments().get("domain"), is(DOMAIN));
    assertThat(request.getArguments().get("key"), is(KEY));
  }

  @Test(expected = UnknownKeyException.class)
  public void deleteUnknownKey() throws Exception {
    when(mockResponse.getStatus()).thenReturn(ResponseStatus.ERROR);
    when(mockResponse.getMessage()).thenReturn("unknown_key");
    tracker.delete(KEY, DOMAIN);
  }

  @Test(expected = TrackerException.class)
  public void deleteFails() throws Exception {
    when(mockResponse.getStatus()).thenReturn(ResponseStatus.ERROR);
    when(mockResponse.getMessage()).thenReturn("something else");
    try {
      tracker.delete(KEY, DOMAIN);
    } catch (UnknownKeyException ignored) {
    }
  }

  @Test
  public void updateStorageClassRequest() throws Exception {
    tracker.updateStorageClass(KEY, DOMAIN, STORAGE_CLASS);
    Request request = requestCaptor.getValue();
    assertThat(request.getCommand(), is("updateclass"));
    assertThat(request.getArguments().size(), is(3));
    assertThat(request.getArguments().get("domain"), is(DOMAIN));
    assertThat(request.getArguments().get("key"), is(KEY));
    assertThat(request.getArguments().get("class"), is(STORAGE_CLASS));
  }

  @Test(expected = UnknownKeyException.class)
  public void updateStorageClassUnknownKey() throws Exception {
    when(mockResponse.getStatus()).thenReturn(ResponseStatus.ERROR);
    when(mockResponse.getMessage()).thenReturn("unknown_key");
    tracker.updateStorageClass(KEY, DOMAIN, STORAGE_CLASS);
  }

  @Test(expected = TrackerException.class)
  public void updateStorageClassFails() throws Exception {
    when(mockResponse.getStatus()).thenReturn(ResponseStatus.ERROR);
    when(mockResponse.getMessage()).thenReturn("something else");
    try {
      tracker.updateStorageClass(KEY, DOMAIN, STORAGE_CLASS);
    } catch (UnknownKeyException ignored) {
    }
  }

  @Test
  public void renameRequest() throws Exception {
    tracker.rename(KEY, DOMAIN, NEW_KEY);
    Request request = requestCaptor.getValue();
    assertThat(request.getCommand(), is("rename"));
    assertThat(request.getArguments().size(), is(3));
    assertThat(request.getArguments().get("domain"), is(DOMAIN));
    assertThat(request.getArguments().get("from_key"), is(KEY));
    assertThat(request.getArguments().get("to_key"), is(NEW_KEY));
  }

  @Test(expected = UnknownKeyException.class)
  public void renameUnknownKey() throws Exception {
    when(mockResponse.getStatus()).thenReturn(ResponseStatus.ERROR);
    when(mockResponse.getMessage()).thenReturn("unknown_key");
    tracker.rename(KEY, DOMAIN, NEW_KEY);
  }

  @Test(expected = KeyExistsAlreadyException.class)
  public void renameKeyExists() throws Exception {
    when(mockResponse.getStatus()).thenReturn(ResponseStatus.ERROR);
    when(mockResponse.getMessage()).thenReturn("key_exists");
    tracker.rename(KEY, DOMAIN, NEW_KEY);
  }

  @Test(expected = TrackerException.class)
  public void renameFails() throws Exception {
    when(mockResponse.getStatus()).thenReturn(ResponseStatus.ERROR);
    when(mockResponse.getMessage()).thenReturn("something else");
    try {
      tracker.rename(KEY, DOMAIN, NEW_KEY);
    } catch (UnknownKeyException ignored) {
    } catch (KeyExistsAlreadyException ignored) {
    }
  }

  @Test
  public void noopRequest() throws Exception {
    tracker.noop();
    Request request = requestCaptor.getValue();
    assertThat(request.getCommand(), is("noop"));
    assertThat(request.getArguments().size(), is(0));
  }

  @Test(expected = TrackerException.class)
  public void noopFails() throws Exception {
    when(mockResponse.getStatus()).thenReturn(ResponseStatus.ERROR);
    when(mockResponse.getMessage()).thenReturn("unknown_key");
    tracker.noop();
  }

  @Test
  public void createCloseRequest() throws Exception {
    Destination destination = new Destination(new URL("http://www.last.fm/1/"), 23, 32L);
    tracker.createClose(KEY, DOMAIN, destination, SIZE);
    Request request = requestCaptor.getValue();
    assertThat(request.getCommand(), is("create_close"));
    assertThat(request.getArguments().size(), is(6));
    assertThat(request.getArguments().get("domain"), is(DOMAIN));
    assertThat(request.getArguments().get("key"), is(KEY));
    assertThat(request.getArguments().get("size"), is(Long.toString(SIZE)));
    assertThat(request.getArguments().get("devid"), is("23"));
    assertThat(request.getArguments().get("path"), is("http://www.last.fm/1/"));
    assertThat(request.getArguments().get("fid"), is("32"));
  }

  @Test(expected = TrackerException.class)
  public void createCloseFails() throws Exception {
    when(mockResponse.getStatus()).thenReturn(ResponseStatus.ERROR);
    when(mockResponse.getMessage()).thenReturn("unknown_key");
    tracker.noop();
  }

  @Test
  public void close() throws IOException {
    tracker.close();
    verify(mockSocket).close();
    verify(mockRequestHandler).close();
  }

  @Test
  public void deviceStatusesRequest() throws Exception {
    Map<String, Map<String, String>> deviceStatuses = tracker.getDeviceStatuses(DOMAIN);

    Request request = requestCaptor.getValue();
    assertThat(request.getCommand(), is("get_devices"));
    assertThat(request.getArguments().size(), is(1));
    assertThat(request.getArguments().get("domain"), is(DOMAIN));

    assertThat(deviceStatuses.isEmpty(), is(true));
  }

}
