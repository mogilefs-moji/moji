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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.moji.tracker.TrackerException;

class RequestHandler {

  private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

  private final Writer writer;
  private final BufferedReader reader;

  RequestHandler(Writer writer, BufferedReader reader) {
    this.writer = writer;
    this.reader = reader;
  }

  Response performRequest(Request request) throws CommunicationException {
    Response response = null;
    String line = null;

    try {
      log.debug("{}", request);
      request.writeTo(writer);
      line = reader.readLine();
      log.debug("Read: {}", line);
      response = createResponseFromLine(line);
      log.debug("{}", response);
    } catch (IOException e) {
      throw new CommunicationException(e);
    }
    return response;
  }

  void close() {
    IOUtils.closeQuietly(reader);
    IOUtils.closeQuietly(writer);
  }

  private Response createResponseFromLine(String line) throws TrackerException {
    if (line == null) {
      throw new TrackerException("Empty response from tracker");
    }
    int firstSpace = line.indexOf(' ');
    if (firstSpace < 0) {
      throw new TrackerException("Invalid response from tracker: '" + line + "'");
    }
    ResponseStatus status = ResponseStatus.valueOfCode(line.substring(0, firstSpace));
    if (status == null) {
      throw new TrackerException("Invalid response from tracker: '" + line + "'");
    }
    String payload = line.substring(firstSpace + 1);
    Response response = new Response(status, payload);
    return response;
  }

}
