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
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.moji.tracker.TrackerException;
import fm.last.moji.tracker.UnknownKeyException;

@RunWith(MockitoJUnitRunner.class)
public class GetPathsOperationTest {

  private static final String DOMAIN = "domain";
  private static final String KEY = "key";
  private static final String PATH_COUNT = "2";
  private static final String PATH_1 = "http://www.last.fm/1/";
  private static final String PATH_2 = "http://www.last.fm/2/";

  @Mock
  private RequestHandler mockRequestHandler;
  @Mock
  private Response mockResponse;
  @Mock
  private Response mockEmptyResponse;
  @Mock
  private Response mockUnknownKeyResponse;
  @Mock
  private Response mockFailResponse;

  private ArgumentCaptor<Request> captorRequest;
  private GetPathsOperation operation;

  @Before
  public void setUp() throws TrackerException {
    captorRequest = ArgumentCaptor.forClass(Request.class);
    when(mockResponse.getStatus()).thenReturn(ResponseStatus.OK);
    when(mockResponse.getValue("paths")).thenReturn(PATH_COUNT);
    when(mockResponse.getValue("path1")).thenReturn(PATH_1);
    when(mockResponse.getValue("path2")).thenReturn(PATH_2);

    when(mockEmptyResponse.getStatus()).thenReturn(ResponseStatus.OK);
    when(mockEmptyResponse.getValue("paths")).thenReturn("0");

    when(mockUnknownKeyResponse.getStatus()).thenReturn(ResponseStatus.ERROR);
    when(mockUnknownKeyResponse.getMessage()).thenReturn("unknown_key unknown key");

    when(mockFailResponse.getStatus()).thenReturn(ResponseStatus.ERROR);
    when(mockFailResponse.getMessage()).thenReturn("unexpected error");
  }

  @Test
  public void requestNormal() throws TrackerException, MalformedURLException {
    when(mockRequestHandler.performRequest(captorRequest.capture())).thenReturn(mockResponse);

    operation = new GetPathsOperation(mockRequestHandler, DOMAIN, KEY, false);
    operation.execute();

    Request request = captorRequest.getValue();
    assertThat(request.getCommand(), is("get_paths"));
    assertThat(request.getArguments().size(), is(3));
    assertThat(request.getArguments().get("domain"), is(DOMAIN));
    assertThat(request.getArguments().get("key"), is(KEY));
    assertThat(request.getArguments().get("noverify"), is("1"));
  }

  @Test
  public void requestNormalVerify() throws TrackerException, MalformedURLException {
    when(mockRequestHandler.performRequest(captorRequest.capture())).thenReturn(mockResponse);

    operation = new GetPathsOperation(mockRequestHandler, DOMAIN, KEY, true);
    operation.execute();

    Request request = captorRequest.getValue();
    assertThat(request.getCommand(), is("get_paths"));
    assertThat(request.getArguments().size(), is(3));
    assertThat(request.getArguments().get("domain"), is(DOMAIN));
    assertThat(request.getArguments().get("key"), is(KEY));
    assertThat(request.getArguments().get("noverify"), is("0"));
  }

  @Test
  public void responseNormal() throws TrackerException, MalformedURLException {
    when(mockRequestHandler.performRequest(captorRequest.capture())).thenReturn(mockResponse);

    operation = new GetPathsOperation(mockRequestHandler, DOMAIN, KEY, true);
    operation.execute();

    List<URL> paths = operation.getPaths();
    assertThat(paths.size(), is(2));
    assertThat(paths.get(0), is(new URL(PATH_1)));
    assertThat(paths.get(1), is(new URL(PATH_2)));
  }

  @Test
  public void zeroPaths() throws TrackerException {
    when(mockRequestHandler.performRequest(captorRequest.capture())).thenReturn(mockEmptyResponse);

    operation = new GetPathsOperation(mockRequestHandler, DOMAIN, KEY, true);
    operation.execute();

    List<URL> paths = operation.getPaths();
    assertThat(paths.size(), is(0));
  }

  @Test(expected = UnknownKeyException.class)
  public void unknownKey() throws TrackerException {
    when(mockRequestHandler.performRequest(captorRequest.capture())).thenReturn(mockUnknownKeyResponse);

    operation = new GetPathsOperation(mockRequestHandler, DOMAIN, KEY, true);
    operation.execute();
  }

  @Test(expected = TrackerException.class)
  public void unexpectedError() throws TrackerException {
    when(mockRequestHandler.performRequest(captorRequest.capture())).thenReturn(mockFailResponse);

    operation = new GetPathsOperation(mockRequestHandler, DOMAIN, KEY, true);
    operation.execute();
  }

}
