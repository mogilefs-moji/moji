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

import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.net.URL;

import org.junit.Test;

public class RequestTest {

  @Test
  public void example() throws Exception {
    Request request = new Request.Builder(5).command("mycommand").arg("bool", true).arg("integer", 2).arg("long", 12L)
        .arg("string", "/=&URL").arg("url", new URL("http://localhost:80/x.do?what=12&do")).build();
    StringWriter writer = new StringWriter();
    request.writeTo(writer);
    String wire = writer.toString();
    assertTrue(wire.startsWith("mycommand "));
    assertTrue(wire.contains("bool=1"));
    assertTrue(wire.contains("long=12"));
    assertTrue(wire.contains("integer=2"));
    assertTrue(wire.contains("url=http%3A%2F%2Flocalhost%3A80%2Fx.do%3Fwhat%3D12%26do"));
    assertTrue(wire.contains("string=%2F%3D%26URL"));
  }

}
