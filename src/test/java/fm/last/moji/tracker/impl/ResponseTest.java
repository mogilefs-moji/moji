/*
 * Copyright 2009 Last.fm
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
package fm.last.moji.tracker.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ResponseTest {

  @Test
  public void example() {
    Response response = new Response(ResponseStatus.OK,
        "path2=http://127.0.0.61:7500/dev469/0/173/153/0173153702.fid&path1="
            + "http://127.0.0.62:7500/dev490/0/173/153/0173153702.fid&paths=2");
    assertThat(response.getStatus(), is(ResponseStatus.OK));
    assertThat(response.getValue("paths"), is("2"));
    assertThat(response.getValue("path1"), is("http://127.0.0.62:7500/dev490/0/173/153/0173153702.fid"));
    assertThat(response.getValue("path2"), is("http://127.0.0.61:7500/dev469/0/173/153/0173153702.fid"));
    assertThat(response.getMessage(), is(nullValue()));
  }

  @Test
  public void badPair() {
    Response response = new Response(ResponseStatus.OK,
        "path2=http://127.0.0.61:7500/dev469/0/173/153/0173153702.fid&path1"
            + "http://127.0.0.62:7500/dev490/0/173/153/0173153702.fid&paths=2");
    assertThat(response.getStatus(), is(ResponseStatus.OK));
    assertThat(response.getValue("paths"), is("2"));
    assertThat(response.getValue("path1"), is(nullValue()));
    assertThat(response.getValue("path2"), is("http://127.0.0.61:7500/dev469/0/173/153/0173153702.fid"));
    assertThat(response.getMessage(), is(nullValue()));
  }

  @Test
  public void error() {
    Response response = new Response(ResponseStatus.ERROR, "message");
    assertThat(response.getStatus(), is(ResponseStatus.ERROR));
    assertThat(response.getMessage(), is("message"));
  }

}
