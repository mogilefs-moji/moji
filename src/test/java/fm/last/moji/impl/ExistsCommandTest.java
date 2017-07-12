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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.UnknownKeyException;

@RunWith(MockitoJUnitRunner.class)
public class ExistsCommandTest {

  @Mock
  private Tracker mockTracker;
  private ExistsCommand command;

  @Before
  public void init() {
    command = new ExistsCommand("key", "domain");
  }

  @Test
  public void onePath() throws Exception {
    List<URL> paths = Collections.singletonList(new URL("http://www.last.fm"));
    when(mockTracker.getPaths("key", "domain")).thenReturn(paths);
    command.executeWithTracker(mockTracker);
    assertTrue(command.getExists());
  }

  @Test
  public void zeroPaths() throws Exception {
    List<URL> paths = Collections.emptyList();
    when(mockTracker.getPaths("key", "domain")).thenReturn(paths);
    command.executeWithTracker(mockTracker);
    assertFalse(command.getExists());
  }

  @Test
  public void unknownKeyException() throws Exception {
    UnknownKeyException e = new UnknownKeyException("key", "domain");
    when(mockTracker.getPaths("key", "domain")).thenThrow(e);
    command.executeWithTracker(mockTracker);
    assertFalse(command.getExists());
  }

}
