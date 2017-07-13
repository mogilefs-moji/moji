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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyChar;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.moji.tracker.TrackerException;

@RunWith(MockitoJUnitRunner.class)
public class RequestHandlerTest {

  @Mock
  private Writer mockWriter;
  @Mock
  private BufferedReader mockReader;
  private RequestHandler handler;

  @Test
  public void write() throws TrackerException {
    BufferedReader reader = new BufferedReader(new StringReader("OK r1=x&r2=y"));
    StringWriter writer = new StringWriter();
    handler = new RequestHandler(writer, reader);

    Request request = new Request.Builder(2).command("mock").arg("arg1", "one").arg("arg2", 2).build();
    handler.performRequest(request);

    assertEquals("mock arg1=one&arg2=2\r\n", writer.toString());
  }

  @Test
  public void okRead() throws TrackerException {
    BufferedReader reader = new BufferedReader(new StringReader("OK r1=x&r2=y"));
    StringWriter writer = new StringWriter();
    handler = new RequestHandler(writer, reader);

    Request request = new Request.Builder(2).command("mock").arg("arg1", "one").arg("arg2", 2).build();
    Response response = handler.performRequest(request);

    assertEquals(ResponseStatus.OK, response.getStatus());
    assertEquals("x", response.getValue("r1"));
    assertEquals("y", response.getValue("r2"));
    assertNull(response.getMessage());
  }

  @Test
  public void errorRead() throws TrackerException {
    BufferedReader reader = new BufferedReader(new StringReader("ERR problem"));
    StringWriter writer = new StringWriter();
    handler = new RequestHandler(writer, reader);

    Request request = new Request.Builder(2).command("mock").arg("arg1", "one").arg("arg2", 2).build();
    Response response = handler.performRequest(request);

    assertEquals(ResponseStatus.ERROR, response.getStatus());
    assertEquals("problem", response.getMessage());
  }

  @Test(expected = TrackerException.class)
  public void badResponse() throws TrackerException {
    BufferedReader reader = new BufferedReader(new StringReader("ERR"));
    StringWriter writer = new StringWriter();
    handler = new RequestHandler(writer, reader);

    Request request = new Request.Builder(2).command("mock").arg("arg1", "one").arg("arg2", 2).build();
    handler.performRequest(request);
  }

  @Test(expected = TrackerException.class)
  public void badResponse2() throws TrackerException {
    BufferedReader reader = new BufferedReader(new StringReader("XXX problem"));
    StringWriter writer = new StringWriter();
    handler = new RequestHandler(writer, reader);

    Request request = new Request.Builder(2).command("mock").arg("arg1", "one").arg("arg2", 2).build();
    handler.performRequest(request);
  }

  @Test(expected = TrackerException.class)
  public void ioExceptionRead() throws IOException {
    when(mockReader.readLine()).thenThrow(new IOException());
    StringWriter writer = new StringWriter();
    handler = new RequestHandler(writer, mockReader);

    Request request = new Request.Builder(2).command("mock").arg("arg1", "one").arg("arg2", 2).build();
    handler.performRequest(request);
  }

  @Test(expected = TrackerException.class)
  public void ioExceptionWrite() throws IOException {
    doThrow(new IOException()).when(mockWriter).write(anyInt());
    doThrow(new IOException()).when(mockWriter).write(anyChar());
    doThrow(new IOException()).when(mockWriter).write(argThat(new TrueMatcher<char[]>()));
    doThrow(new IOException()).when(mockWriter).write(argThat(new TrueMatcher<String>()));
    doThrow(new IOException()).when(mockWriter).write(argThat(new TrueMatcher<char[]>()), anyInt(), anyInt());
    doThrow(new IOException()).when(mockWriter).write(argThat(new TrueMatcher<String>()), anyInt(), anyInt());
    BufferedReader reader = new BufferedReader(new StringReader("ERR problem"));
    handler = new RequestHandler(mockWriter, reader);

    Request request = new Request.Builder(2).command("mock").arg("arg1", "one").arg("arg2", 2).build();
    handler.performRequest(request);
  }

  @Test
  public void close() throws IOException {
    handler = new RequestHandler(mockWriter, mockReader);
    handler.close();
    verify(mockReader).close();
    verify(mockWriter).close();
  }

  private class TrueMatcher<T> extends BaseMatcher<T> {

    @Override
    public boolean matches(Object arg0) {
      return true;
    }

    @Override
    public void describeTo(Description arg0) {
    }
  }

}
