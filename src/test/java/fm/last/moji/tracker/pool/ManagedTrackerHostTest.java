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
package fm.last.moji.tracker.pool;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.commons.lang.time.Clock;
import fm.last.moji.tracker.pool.ManagedTrackerHost.ResetTask;

@RunWith(MockitoJUnitRunner.class)
public class ManagedTrackerHostTest {

  @Mock
  private InetSocketAddress mockAddress;
  @Mock
  private Timer mockTimer;
  @Mock
  private ManagedTrackerHost.ResetTask mockTask;
  @Mock
  private ManagedTrackerHost.ResetTaskFactory mockTaskFactory;
  @Mock
  private Clock mockClock;

  private ManagedTrackerHost.ResetTask actualTask;
  private ManagedTrackerHost managedTrackerHost;

  @Before
  public void setup() {
    managedTrackerHost = new ManagedTrackerHost(mockAddress, mockTimer, mockClock);
    managedTrackerHost.setHostRetryInterval(10, MILLISECONDS);
    managedTrackerHost.resetTaskFactory = mockTaskFactory;

    actualTask = managedTrackerHost.new ResetTask();

    doNothing().when(mockTimer).schedule(any(TimerTask.class), eq(1L));
    when(mockClock.currentTimeMillis()).thenReturn(1L, 2L, 3L);
    when(mockTaskFactory.newInstance()).thenReturn(mockTask, actualTask);
  }

  @Test
  public void hostMarkedAsFailedAndThenRetried() throws Exception {
    assertThat(managedTrackerHost.getLastFailed(), is(0L));

    managedTrackerHost.markAsFailed();
    verify(mockTimer).schedule(mockTask, 10L);
    assertThat(managedTrackerHost.getLastFailed(), is(1L));

    managedTrackerHost.markAsFailed();
    verify(mockTask).cancel();
    verify(mockTimer).schedule(actualTask, 10L);
    assertThat(managedTrackerHost.getLastFailed(), is(2L));

    actualTask.run();
    assertThat(managedTrackerHost.getLastFailed(), is(0L));

    verify(mockTimer, times(2)).schedule(any(ResetTask.class), eq(10L));
    verify(mockTaskFactory, times(2)).newInstance();
    verifyNoMoreInteractions(mockTaskFactory, mockTimer);
  }

  @Test
  public void lastUsed() {
    assertThat(managedTrackerHost.getLastUsed(), is(0L));
    managedTrackerHost.markSuccess();
    assertThat(managedTrackerHost.getLastUsed(), is(1L));
  }

  @Test
  public void markSuccess() {
    managedTrackerHost.markAsFailed();
    assertThat(managedTrackerHost.getLastFailed(), is(1L));
    managedTrackerHost.markSuccess();
    assertThat(managedTrackerHost.getLastFailed(), is(0L));
  }

}
