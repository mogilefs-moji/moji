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
import static org.junit.Assert.assertThat;
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
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.moji.impl.NetworkingConfiguration;
import fm.last.moji.tracker.impl.CommunicationException;

@RunWith(MockitoJUnitRunner.class)
public class MultiHostTrackerPoolTest {

  @Mock
  private NetworkingConfiguration netConfig;
  @Mock
  private GenericKeyedObjectPool pool;
  @Mock
  private ManagedTrackerHost managedHost1;
  @Mock
  private ManagedTrackerHost managedHost2;
  @Mock
  private InetSocketAddress mockAddress1;
  @Mock
  private InetSocketAddress mockAddress2;
  @Mock
  private BorrowedTracker mockBorrowedTracker1;
  @Mock
  private BorrowedTracker mockBorrowedTracker2;

  private MultiHostTrackerPool trackerPool;
  private List<ManagedTrackerHost> managedHosts;

  @Before
  public void setup() {
    when(managedHost1.getAddress()).thenReturn(mockAddress1);
    when(managedHost2.getAddress()).thenReturn(mockAddress2);
    managedHosts = Arrays.asList(managedHost1, managedHost2);

    when(mockBorrowedTracker1.getHost()).thenReturn(managedHost1);
    when(mockBorrowedTracker2.getHost()).thenReturn(managedHost2);
    trackerPool = new MultiHostTrackerPool(managedHosts, netConfig, pool);
  }

  @Test(expected = CommunicationException.class)
  public void getTrackerConvertsException() throws Exception {
    when(pool.borrowObject(any(ManagedTrackerHost.class))).thenThrow(new IllegalStateException());
    trackerPool.getTracker();
  }

  @Test
  public void getAddresses() {
    Set<InetSocketAddress> actual = trackerPool.getAddresses();
    Set<InetSocketAddress> expected = new HashSet<InetSocketAddress>(Arrays.asList(mockAddress1, mockAddress2));
    assertThat(actual, is(expected));
  }

  @Test
  public void expectedHostPreference() throws Exception {
    when(managedHost1.getLastUsed()).thenReturn(2L);
    when(managedHost1.getLastFailed()).thenReturn(1L);
    when(managedHost2.getLastUsed()).thenReturn(2L);
    when(managedHost2.getLastFailed()).thenReturn(0L);

    when(pool.borrowObject(managedHost1)).thenReturn(mockBorrowedTracker1);
    when(pool.borrowObject(managedHost2)).thenReturn(mockBorrowedTracker2);

    BorrowedTracker first = (BorrowedTracker) trackerPool.getTracker();
    assertThat(first, is(mockBorrowedTracker2));

    when(managedHost2.getLastFailed()).thenReturn(10L);
    BorrowedTracker second = (BorrowedTracker) trackerPool.getTracker();
    assertThat(second, is(mockBorrowedTracker1));
  }

  @Test
  public void invalidateTracker() throws Exception {
    trackerPool.invalidateTracker(mockBorrowedTracker1);
    verify(pool).invalidateObject(managedHost1, mockBorrowedTracker1);
  }

  @Test
  public void returnTracker() throws Exception {
    trackerPool.returnTracker(mockBorrowedTracker1);
    verify(pool).returnObject(managedHost1, mockBorrowedTracker1);
  }

  @Test
  public void closeDelegates() throws Exception {
    trackerPool.close();
    verify(pool).close();
  }

}
