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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import fm.last.moji.impl.NetworkingConfiguration;
import fm.last.moji.tracker.impl.CommunicationException;

@RunWith(MockitoJUnitRunner.class)
public class MultiHostTrackerPoolTest {

  @Mock
  private NetworkingConfiguration mockNetConfig;
  @Mock
  private GenericKeyedObjectPool<ManagedTrackerHost, BorrowedTracker> mockPool;
  @Mock
  private ManagedTrackerHost mockManagedHost1;
  @Mock
  private ManagedTrackerHost mockManagedHost2;
  @Mock
  private BorrowedTracker mockBorrowedTracker1;
  @Mock
  private BorrowedTracker mockBorrowedTracker2;

  private MultiHostTrackerPool trackerPool;
  private List<ManagedTrackerHost> managedHosts;
  private InetSocketAddress address1;
  private InetSocketAddress address2;

  @Before
  public void setup() {
    address1 = new InetSocketAddress(10001);
    address2 = new InetSocketAddress(10002);

    when(mockManagedHost1.getAddress()).thenReturn(address1);
    when(mockManagedHost2.getAddress()).thenReturn(address2);
    managedHosts = Arrays.asList(mockManagedHost1, mockManagedHost2);

    when(mockBorrowedTracker1.getHost()).thenReturn(mockManagedHost1);
    when(mockBorrowedTracker2.getHost()).thenReturn(mockManagedHost2);
    trackerPool = new MultiHostTrackerPool(managedHosts, mockNetConfig, mockPool);
  }

  @Test(expected = CommunicationException.class)
  public void getTrackerConvertsException() throws Exception {
    when(mockPool.borrowObject(any(ManagedTrackerHost.class))).thenThrow(new IllegalStateException());
    trackerPool.getTracker();
  }

  @Test
  public void getAddresses() {
    Set<InetSocketAddress> actual = trackerPool.getAddresses();
    Set<InetSocketAddress> expected = new HashSet<InetSocketAddress>(Arrays.asList(address1, address2));
    assertThat(actual, is(expected));
  }

  @Test
  public void expectedHostPreference() throws Exception {
    when(mockManagedHost1.getLastUsed()).thenReturn(2L);
    when(mockManagedHost1.getLastFailed()).thenReturn(1L);
    when(mockManagedHost2.getLastUsed()).thenReturn(2L);
    when(mockManagedHost2.getLastFailed()).thenReturn(0L);

    when(mockPool.borrowObject(mockManagedHost1)).thenReturn(mockBorrowedTracker1);
    when(mockPool.borrowObject(mockManagedHost2)).thenReturn(mockBorrowedTracker2);

    BorrowedTracker first = (BorrowedTracker) trackerPool.getTracker();
    assertThat(first, is(mockBorrowedTracker2));

    when(mockManagedHost2.getLastFailed()).thenReturn(10L);
    BorrowedTracker second = (BorrowedTracker) trackerPool.getTracker();
    assertThat(second, is(mockBorrowedTracker1));
  }

  @Test
  public void expectedTrackerIfFirstFail() throws Exception {
    when(mockManagedHost1.getLastUsed()).thenReturn(2L);
    when(mockManagedHost1.getLastFailed()).thenReturn(1L);
    when(mockManagedHost2.getLastUsed()).thenReturn(2L);
    when(mockManagedHost2.getLastFailed()).thenReturn(0L);
    Mockito.doAnswer(new Answer<Object>() {

      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        ManagedTrackerHost ms = (ManagedTrackerHost) invocation.getMock();
        when(ms.getLastFailed()).thenReturn(10L);
        return null;
      }
    }).when(mockManagedHost2).markAsFailed();

    when(mockPool.borrowObject(mockManagedHost1)).thenReturn(mockBorrowedTracker1);
    when(mockPool.borrowObject(mockManagedHost2)).thenThrow(new IllegalStateException());
    try {
      trackerPool.getTracker();
      fail("Not throw exception");
    } catch (CommunicationException e) {

    }

    BorrowedTracker first = (BorrowedTracker) trackerPool.getTracker();
    assertThat(first, is(mockBorrowedTracker1));

    BorrowedTracker second = (BorrowedTracker) trackerPool.getTracker();
    assertThat(second, is(mockBorrowedTracker1));

    // timer reset
    when(mockManagedHost2.getLastFailed()).thenReturn(0L);
    Mockito.doReturn(mockBorrowedTracker2).when(mockPool).borrowObject(mockManagedHost2);

    BorrowedTracker third = (BorrowedTracker) trackerPool.getTracker();
    assertThat(third, is(mockBorrowedTracker2));

  }

  @Test
  public void invalidateTracker() throws Exception {
    trackerPool.invalidateTracker(mockBorrowedTracker1);
    verify(mockPool).invalidateObject(mockManagedHost1, mockBorrowedTracker1);
  }

  @Test
  public void returnTracker() throws Exception {
    trackerPool.returnTracker(mockBorrowedTracker1);
    verify(mockPool).returnObject(mockManagedHost1, mockBorrowedTracker1);
  }

  @Test
  public void closeDelegates() throws Exception {
    trackerPool.close();
    verify(mockPool).close();
  }

}
