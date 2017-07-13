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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import fm.last.moji.Moji;
import fm.last.moji.MojiFile;
import fm.last.moji.tracker.Tracker;

@RunWith(MockitoJUnitRunner.class)
public class ListFilesCommandTest {

  private static final String DOMAIN = "domain";
  private static final String KEY_PREFIX = "key";

  @Mock
  private Moji mockMoji;
  @Mock
  private Tracker mockTracker;

  @Before
  public void setUp() {
    when(mockMoji.getFile(anyString())).thenAnswer(new Answer<MojiFile>() {
      @Override
      public MojiFile answer(InvocationOnMock invocation) throws Throwable {
        Object[] arguments = invocation.getArguments();
        MojiFile mock = mock(MojiFile.class);
        when(mock.getKey()).thenReturn((String) arguments[0]);
        return mock;
      }
    });
  }

  @Test
  public void list() throws IOException {
    List<String> keys = Arrays.asList(new String[] { "key1", "key2", "key3" });
    when(mockTracker.list(DOMAIN, KEY_PREFIX, null)).thenReturn(keys);
    ListFilesCommand command = new ListFilesCommand(mockMoji, KEY_PREFIX, DOMAIN);
    command.executeWithTracker(mockTracker);
    List<MojiFile> fileList = command.getFileList();
    assertThat(fileList.size(), is(3));
    assertThat(fileList.get(0).getKey(), is("key1"));
    assertThat(fileList.get(1).getKey(), is("key2"));
    assertThat(fileList.get(2).getKey(), is("key3"));
  }

  @Test
  public void listWithLimit() throws IOException {
    List<String> keys = Collections.singletonList("key1");
    when(mockTracker.list(DOMAIN, KEY_PREFIX, 1)).thenReturn(keys);
    ListFilesCommand command = new ListFilesCommand(mockMoji, KEY_PREFIX, DOMAIN, 1);
    command.executeWithTracker(mockTracker);
    List<MojiFile> fileList = command.getFileList();
    assertThat(fileList.size(), is(1));
    assertThat(fileList.get(0).getKey(), is("key1"));
  }

}
