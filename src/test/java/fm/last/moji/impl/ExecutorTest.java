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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.TrackerException;
import fm.last.moji.tracker.TrackerFactory;
import fm.last.moji.tracker.UnknownKeyException;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorTest {

  @Mock
  private Tracker mockTracker;
  @Mock
  private TrackerFactory mockFactory;
  @Mock
  private MojiCommand mockCommand;
  @Mock
  private InetSocketAddress mockAddress;

  private Executor executor;

  @Before
  public void init() throws TrackerException {
    when(mockFactory.getTracker()).thenReturn(mockTracker);
    when(mockFactory.getAddresses()).thenReturn(Collections.singleton(mockAddress));
    executor = new Executor(mockFactory);
  }

  @Test
  public void executeCommandOk() throws Exception {
    executor.executeCommand(mockCommand);
    verify(mockFactory).getTracker();
    verify(mockCommand).executeWithTracker(mockTracker);
    verify(mockTracker).close();
  }

  @Test(expected = UnknownKeyException.class)
  public void trackerClosedOnUnknownKeyException() throws Exception {
    UnknownKeyException e = new UnknownKeyException("domain", "key");
    doThrow(e).when(mockCommand).executeWithTracker(mockTracker);

    executor.executeCommand(mockCommand);
    verify(mockTracker).close();
  }

  @Test(expected = IOException.class)
  public void trackerClosedOnIOException() throws Exception {
    IOException e = new IOException();
    doThrow(e).when(mockCommand).executeWithTracker(mockTracker);

    executor.executeCommand(mockCommand);
    verify(mockTracker).close();
  }

}
