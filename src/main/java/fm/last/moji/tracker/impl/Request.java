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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import static fm.last.moji.tracker.impl.Charsets.UTF_8;

class Request {

  private static final Logger log = LoggerFactory.getLogger(Request.class);

  private final String command;
  private final Map<String, String> arguments;

  private Request(Builder builder) {
    command = builder.command;
    arguments = builder.arguments;
  }

  String getCommand() {
    return command;
  }

  Map<String, String> getArguments() {
    return arguments;
  }

  void writeTo(Writer writer) throws IOException {
    StringBuilder wire = new StringBuilder();
    wire.append(command);
    wire.append(' ');
    boolean first = true;
    for (Entry<String, String> entry : arguments.entrySet()) {
      if (first) {
        first = false;
      } else {
        wire.append('&');
      }
      try {
        wire.append(URLEncoder.encode(entry.getKey(), UTF_8.value()));
        wire.append('=');
        wire.append(URLEncoder.encode(entry.getValue(), UTF_8.value()));
      } catch (UnsupportedEncodingException ignored) {
      }
    }
    wire.append('\r');
    wire.append('\n');
    log.debug("Sent: {}", wire);
    writer.write(wire.toString());
    writer.flush();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Request [command=");
    builder.append(command);
    builder.append(", arguments=");
    builder.append(arguments);
    builder.append("]");
    return builder.toString();
  }

  static class Builder {

    private final Map<String, String> arguments;
    private String command;

    Builder(int expectedSize) {
      arguments = new LinkedHashMap<String, String>(expectedSize);
    }

    Builder command(String command) {
      this.command = command;
      return this;
    }

    Builder arg(String key, String value) {
      arguments.put(key, value);
      return this;
    }

    Builder arg(String key, int value) {
      arguments.put(key, Integer.toString(value));
      return this;
    }

    Builder arg(String key, long value) {
      arguments.put(key, Long.toString(value));
      return this;
    }

    Builder arg(String key, boolean value) {
      arguments.put(key, value ? "1" : "0");
      return this;
    }

    Builder arg(String key, URL value) {
      arguments.put(key, value.toString());
      return this;
    }

    Request build() {
      return new Request(this);
    }
  }

}
