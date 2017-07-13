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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.moji.tracker.Destination;
import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.TrackerException;
import fm.last.moji.tracker.impl.CommunicationException;

@RunWith(MockitoJUnitRunner.class)
public class BorrowedTrackerTest {

  private static final String KEY1 = "key1";
  private static final String DOMAIN = "domain";
  private static final String KEY2 = "key2";
  private static final String STORAGE_CLASS = "class";
  private static final int SIZE = 1;
  @Mock
  private Tracker mockTracker;
  @Mock
  private MultiHostTrackerPool mockPool;
  @Mock
  private Destination mockDestination;
  @Mock
  private ManagedTrackerHost mockHost;

  private BorrowedTracker borrowedTracker;

  @Before
  public void init() {
    borrowedTracker = new BorrowedTracker(mockHost, mockTracker, mockPool);
  }

  @Test
  public void closeReturnsToPool() throws Exception {
    borrowedTracker.close();
    verify(mockPool).returnTracker(borrowedTracker);
    verifyZeroInteractions(mockTracker);
  }

  @Test
  public void closeWithErrorInvalidates() throws Exception {
    doThrow(new CommunicationException()).when(mockTracker).noop();
    try {
      borrowedTracker.noop();
    } catch (CommunicationException e) {
    }
    borrowedTracker.close();
    verify(mockPool).invalidateTracker(borrowedTracker);
  }

  @Test
  public void reallyCloseDelegates() throws Exception {
    borrowedTracker.reallyClose();
    verify(mockTracker).close();
    verifyZeroInteractions(mockPool);
  }

  @Test
  public void getPathsDelegates() throws TrackerException {
    borrowedTracker.getPaths(KEY1, DOMAIN);
    verify(mockTracker).getPaths(KEY1, DOMAIN);
  }

  @Test
  public void getFileInfoDelegates() throws TrackerException {
    borrowedTracker.fileInfo(KEY1, DOMAIN);
    verify(mockTracker).fileInfo(KEY1, DOMAIN);
  }

  @Test
  public void createOpenDelegates() throws TrackerException {
    borrowedTracker.createOpen(KEY1, DOMAIN, STORAGE_CLASS);
    verify(mockTracker).createOpen(KEY1, DOMAIN, STORAGE_CLASS);
  }

  @Test
  public void createCloseDelegates() throws TrackerException {
    borrowedTracker.createClose(KEY1, DOMAIN, mockDestination, SIZE);
    verify(mockTracker).createClose(KEY1, DOMAIN, mockDestination, SIZE);
  }

  @Test
  public void deleteDelegates() throws TrackerException {
    borrowedTracker.delete(KEY1, DOMAIN);
    verify(mockTracker).delete(KEY1, DOMAIN);
  }

  @Test
  public void renameDelegates() throws TrackerException {
    borrowedTracker.rename(KEY1, DOMAIN, KEY2);
    verify(mockTracker).rename(KEY1, DOMAIN, KEY2);

  }

  @Test
  public void updateStorageClassDelegates() throws TrackerException {
    borrowedTracker.updateStorageClass(KEY1, DOMAIN, STORAGE_CLASS);
    verify(mockTracker).updateStorageClass(KEY1, DOMAIN, STORAGE_CLASS);
  }

  @Test
  public void noopDelegates() throws TrackerException {
    borrowedTracker.noop();
    verify(mockTracker).noop();
  }

}
