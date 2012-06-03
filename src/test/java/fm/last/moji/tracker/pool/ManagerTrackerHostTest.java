/*
 * Copyright 2012 Last.fm
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
package fm.last.moji.tracker.pool;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.net.InetSocketAddress;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ManagerTrackerHostTest {

  private ManagedTrackerHost managedTrackerHost;

  @Mock
  InetSocketAddress mockAddress;

  int timeOutInMs = 1000;
  @Before
  public void init() {
    managedTrackerHost = new ManagedTrackerHost(mockAddress);
    managedTrackerHost.setTimeToMakeReady(timeOutInMs);
  }

  @Test
  public void markFailedAndRestoreUnfailed() throws Exception {
    assertThat(managedTrackerHost.getLastFailed(), is(0L));
    managedTrackerHost.markAsFailed();
    assertThat(managedTrackerHost.getLastFailed(), not(0L));
    Thread.sleep(timeOutInMs / 2);
    assertThat(managedTrackerHost.getLastFailed(), not(0L));
    managedTrackerHost.markAsFailed();
    Thread.sleep(timeOutInMs / 2 + timeOutInMs / 4);
    assertThat(managedTrackerHost.getLastFailed(), not(0L));
    Thread.sleep(timeOutInMs / 2);
    assertThat(managedTrackerHost.getLastFailed(), is(0L));
  }



}
