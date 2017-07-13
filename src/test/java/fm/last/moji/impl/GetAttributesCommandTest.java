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
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.moji.MojiFileAttributes;
import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.UnknownCommandException;
import fm.last.moji.tracker.UnknownKeyException;

@RunWith(MockitoJUnitRunner.class)
public class GetAttributesCommandTest {

  @Mock
  private Tracker mockTracker;
  private GetAttributesCommand command;

  @Before
  public void init() {
    command = new GetAttributesCommand("key", "domain");
  }

  @Test
  public void oneAttribute() throws Exception {
    Map<String, String> responseValues = new HashMap<String, String>();
    responseValues.put("domain", "domain2");
    responseValues.put("key", "key2");
    responseValues.put("class", "default");
    responseValues.put("length", "100");
    responseValues.put("devcount", "2");
    responseValues.put("fid", "5645");

    when(mockTracker.fileInfo("key", "domain")).thenReturn(responseValues);
    command.executeWithTracker(mockTracker);

    MojiFileAttributes attributes = command.getAttributes();
    assertEquals(attributes.getDomain(), "domain2");
    assertEquals(attributes.getKey(), "key2");
    assertEquals(attributes.getStorageClass(), "default");
    assertEquals(attributes.getLength(), 100L);
    assertEquals(attributes.getDeviceCount(), 2);
    assertEquals(attributes.getFid(), 5645L);
  }

  @Test
  public void noFileInfo() throws Exception {
    Map<String, String> attributes = Collections.emptyMap();
    when(mockTracker.fileInfo("key", "domain")).thenReturn(attributes);
    command.executeWithTracker(mockTracker);
    assertNull(command.getAttributes());
  }

  @Test(expected = UnknownKeyException.class)
  public void unknownKeyException() throws Exception {
    UnknownKeyException e = new UnknownKeyException("key", "domain");
    when(mockTracker.fileInfo("key", "domain")).thenThrow(e);
    command.executeWithTracker(mockTracker);
  }

  @Test(expected = UnknownCommandException.class)
  public void unknownCommandException() throws Exception {
    UnknownCommandException e = new UnknownCommandException("file_info");
    when(mockTracker.fileInfo("key", "domain")).thenThrow(e);
    command.executeWithTracker(mockTracker);
  }

}
