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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetSocketAddress;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.TrackerException;
import fm.last.moji.tracker.impl.AbstractTrackerFactory;
import fm.last.moji.tracker.impl.CommunicationException;

@RunWith(MockitoJUnitRunner.class)
public class BorrowedTrackerObjectPoolFactoryTest {

  @Mock
  private AbstractTrackerFactory mockTrackerFactory;
  @Mock
  private MultiHostTrackerPool mockTrackerPool;
  @Mock
  private ManagedTrackerHost mockManagedHost;
  @Mock
  private BorrowedTracker mockBorrowedTracker;
  @Mock
  private Tracker mockTracker;

  private BorrowedTrackerObjectPoolFactory factory;
  private InetSocketAddress socketAddress;

  @Before
  public void setup() {
    factory = new BorrowedTrackerObjectPoolFactory(mockTrackerFactory, mockTrackerPool);
    when(mockBorrowedTracker.getHost()).thenReturn(mockManagedHost);
    socketAddress = new InetSocketAddress("localhost", 17476);
  }

  @Test
  public void validateObjectSuccess() throws TrackerException {
    boolean isValid = factory.validateObject(mockManagedHost, mockBorrowedTracker);
    assertThat(isValid, is(true));
    verify(mockBorrowedTracker).noop();
  }

  @Test
  public void validateObjectFailures() throws TrackerException {
    doThrow(new TrackerException()).when(mockBorrowedTracker).noop();
    boolean isValid = factory.validateObject(mockManagedHost, mockBorrowedTracker);
    assertThat(isValid, is(false));
  }

  @Test
  public void destroyObjectNormal() throws Exception {
    factory.destroyObject(mockManagedHost, mockBorrowedTracker);
    verify(mockBorrowedTracker).reallyClose();
  }

  @Test
  public void destroyObjectFailed() throws Exception {
    when(mockBorrowedTracker.getLastException()).thenReturn(new CommunicationException());
    factory.destroyObject(mockManagedHost, mockBorrowedTracker);
    verify(mockManagedHost).markAsFailed();
    verify(mockBorrowedTracker).reallyClose();
  }

  @Test
  public void makeObject() throws Exception {
    when(mockTrackerFactory.newTracker(socketAddress)).thenReturn(mockTracker);
    when(mockManagedHost.getAddress()).thenReturn(socketAddress);
    BorrowedTracker newTracker = (BorrowedTracker) factory.makeObject(mockManagedHost);
    newTracker.noop();

    assertThat(newTracker.getHost(), is(mockManagedHost));
    verify(mockTracker).noop();
  }

}
