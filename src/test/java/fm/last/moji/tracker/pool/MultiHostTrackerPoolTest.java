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

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.moji.impl.NetworkingConfiguration;
import fm.last.moji.tracker.TrackerException;
import fm.last.moji.tracker.impl.CommunicationException;

@RunWith(MockitoJUnitRunner.class)
public class MultiHostTrackerPoolTest {

  @Mock
  private InetSocketAddress mockAddress;

  private NetworkingConfiguration configuration;
  private MultiHostTrackerPool pool;

  @Before
  public void setup() {
    configuration = new NetworkingConfiguration();
    pool = new MultiHostTrackerPool(Collections.singleton(mockAddress), configuration);
  }

  @Test(expected = CommunicationException.class)
  public void getTrackerConvertsException() throws TrackerException {
    pool.getTracker();
  }

  @Test
  public void getAddresses() {
    Set<InetSocketAddress> actual = pool.getAddresses();
    assertThat(actual, is(Collections.singleton(mockAddress)));
  }

}
