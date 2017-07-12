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

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.moji.tracker.Tracker;

@RunWith(MockitoJUnitRunner.class)
public class RenameCommandTest {

  @Mock
  private Tracker mockTracker;
  private RenameCommand command;

  @Before
  public void init() {
    command = new RenameCommand("key", "domain", "newKey");
  }

  @Test
  public void delegatesToTracker() throws Exception {
    command.executeWithTracker(mockTracker);
    verify(mockTracker).rename("key", "domain", "newKey");
  }

}
