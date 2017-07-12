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

import static fm.last.moji.tracker.impl.Charsets.UTF_8;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Response {

  private static final Logger log = LoggerFactory.getLogger(Response.class);

  private final Map<String, String> values;
  private final ResponseStatus status;
  private final String message;

  Response(ResponseStatus status, String payload) {
    this.status = status;
    if (status == ResponseStatus.OK) {
      values = decodePayload(payload);
      message = null;
    } else {
      message = payload;
      values = Collections.emptyMap();
    }
  }

  ResponseStatus getStatus() {
    return status;
  }

  Map<String, String> getValueMap() {
    return values;
  }

  String getValue(String key) {
    return values.get(key);
  }

  String getMessage() {
    return message;
  }

  private Map<String, String> decodePayload(String encoded) {
    HashMap<String, String> map = new HashMap<String, String>();
    try {
      if (encoded == null || encoded.length() == 0) {
        return map;
      }
      String[] parts = encoded.split("&");
      for (String part : parts) {
        String[] pair = part.split("=");
        if (pair.length != 2) {
          log.error("Poorly encoded string: {} ", encoded);
          continue;
        }
        map.put(pair[0], URLDecoder.decode(pair[1], UTF_8.value()));
      }
      return Collections.unmodifiableMap(map);
    } catch (UnsupportedEncodingException e) {
      log.error("Problem decoding response", e);
      return null;
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Response [status=");
    builder.append(status);
    builder.append(", values=");
    builder.append(values);
    builder.append(", message=");
    builder.append(message);
    builder.append("]");
    return builder.toString();
  }

}
