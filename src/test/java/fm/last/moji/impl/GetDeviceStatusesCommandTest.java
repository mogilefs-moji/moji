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
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.moji.MojiDeviceStatus;
import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.TrackerException;

@RunWith(MockitoJUnitRunner.class)
public class GetDeviceStatusesCommandTest {

  @Mock
  private Tracker mockTracker;
  private GetDeviceStatusesCommand command;

  @Before
  public void init() throws TrackerException {
    command = new GetDeviceStatusesCommand("domain");

    Map<String, String> device1params = new HashMap<String, String>();
    device1params.put("utilization", "0.5");
    device1params.put("observed_state", "ok");
    device1params.put("mb_total", "100");
    device1params.put("mb_used", "51");
    device1params.put("mb_free", "49");
    device1params.put("reject_bad_md5", "1");
    device1params.put("weight", "10");
    device1params.put("devid", "1");
    device1params.put("status", "failed");
    device1params.put("hostid", "4");

    Map<String, String> device2params = new HashMap<String, String>();
    device2params.put("utilization", "0.1");
    device2params.put("observed_state", "error");
    device2params.put("mb_total", "200");
    device2params.put("mb_used", "51");
    device2params.put("mb_free", "149");
    device2params.put("reject_bad_md5", "0");
    device2params.put("weight", "5");
    device2params.put("devid", "2");
    device2params.put("status", "ok");
    device2params.put("hostid", "8");

    Map<String, Map<String, String>> statuses = new HashMap<String, Map<String, String>>();
    statuses.put("dev1", device1params);
    statuses.put("dev2", device2params);

    when(mockTracker.getDeviceStatuses("domain")).thenReturn(statuses);
  }

  @Test
  public void typical() throws Exception {
    command.executeWithTracker(mockTracker);

    List<MojiDeviceStatus> statuses = command.getStatuses();
    assertThat(statuses.size(), is(2));

    MojiDeviceStatus device1Status = statuses.get(0);
    assertThat(device1Status.getCapacityFreeBytes(), is(51380224L));
    assertThat(device1Status.getCapacityTotalBytes(), is(104857600L));
    assertThat(device1Status.getCapacityUsedBytes(), is(53477376L));
    assertThat(device1Status.getDeviceName(), is("dev1"));
    assertThat(device1Status.getHostId(), is(4));
    assertThat(device1Status.getId(), is(1));
    assertThat(device1Status.getObservedState(), is("ok"));
    assertThat(device1Status.getRejectBadMd5(), is(true));
    assertThat(device1Status.getStatus(), is("failed"));
    assertThat(device1Status.getUtilization(), is(0.5f));
    assertThat(device1Status.getWeight(), is(10));

    MojiDeviceStatus device2Status = statuses.get(1);
    assertThat(device2Status.getCapacityFreeBytes(), is(156237824L));
    assertThat(device2Status.getCapacityTotalBytes(), is(209715200L));
    assertThat(device2Status.getCapacityUsedBytes(), is(53477376L));
    assertThat(device2Status.getDeviceName(), is("dev2"));
    assertThat(device2Status.getHostId(), is(8));
    assertThat(device2Status.getId(), is(2));
    assertThat(device2Status.getObservedState(), is("error"));
    assertThat(device2Status.getRejectBadMd5(), is(false));
    assertThat(device2Status.getStatus(), is("ok"));
    assertThat(device2Status.getUtilization(), is(0.1f));
    assertThat(device2Status.getWeight(), is(5));
  }

  @Test
  public void empty() throws Exception {
    Map<String, Map<String, String>> parameters = Collections.emptyMap();
    when(mockTracker.getDeviceStatuses("domain")).thenReturn(parameters);

    command.executeWithTracker(mockTracker);

    List<MojiDeviceStatus> statuses = command.getStatuses();
    assertThat(statuses.isEmpty(), is(true));
  }

}
