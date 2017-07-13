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
package fm.last.moji.tracker.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.moji.tracker.TrackerException;

@RunWith(MockitoJUnitRunner.class)
public class GetDeviceStatusesOperationTest {

  private static final String DOMAIN = "domain";

  @Mock
  private RequestHandler mockRequestHandler;
  @Mock
  private Response mockResponse;
  @Mock
  private Response mockEmptyResponse;
  @Mock
  private Response mockFailResponse;

  @Captor
  private ArgumentCaptor<Request> captorRequest;

  private GetDeviceStatusesOperation operation;

  @Before
  public void setUp() throws TrackerException {
    when(mockResponse.getStatus()).thenReturn(ResponseStatus.OK);
    when(mockResponse.getValue("devices")).thenReturn("2");

    Map<String, String> valueMap = new HashMap<String, String>();
    valueMap.put("dev1_utilization", "0.5");
    valueMap.put("dev1_observed_state", "ok");
    valueMap.put("dev1_mb_total", "100");
    valueMap.put("dev1_mb_used", "51");
    valueMap.put("dev1_mb_free", "49");
    valueMap.put("dev1_reject_bad_md5", "1");
    valueMap.put("dev1_weight", "10");
    valueMap.put("dev1_devid", "1");
    valueMap.put("dev1_status", "failed");
    valueMap.put("dev1_hostid", "1");

    valueMap.put("dev2_utilization", "0.1");
    valueMap.put("dev2_observed_state", "error");
    valueMap.put("dev2_mb_total", "200");
    valueMap.put("dev2_mb_used", "51");
    valueMap.put("dev2_mb_free", "149");
    valueMap.put("dev2_reject_bad_md5", "1");
    valueMap.put("dev2_weight", "5");
    valueMap.put("dev2_devid", "2");
    valueMap.put("dev2_status", "ok");
    valueMap.put("dev2_hostid", "2");

    when(mockResponse.getValueMap()).thenReturn(valueMap);

    when(mockEmptyResponse.getStatus()).thenReturn(ResponseStatus.OK);

    when(mockFailResponse.getStatus()).thenReturn(ResponseStatus.ERROR);
    when(mockFailResponse.getMessage()).thenReturn("unexpected error");
  }

  @Test
  public void requestNormal() throws TrackerException {
    when(mockRequestHandler.performRequest(captorRequest.capture())).thenReturn(mockResponse);

    operation = new GetDeviceStatusesOperation(mockRequestHandler, DOMAIN);
    operation.execute();

    Request request = captorRequest.getValue();
    assertThat(request.getCommand(), is("get_devices"));
    assertThat(request.getArguments().size(), is(1));
    assertThat(request.getArguments().get("domain"), is(DOMAIN));
  }

  @Test
  public void responseNormal() throws TrackerException {
    when(mockRequestHandler.performRequest(captorRequest.capture())).thenReturn(mockResponse);

    operation = new GetDeviceStatusesOperation(mockRequestHandler, DOMAIN);
    operation.execute();

    Map<String, Map<String, String>> parametersByDevice = operation.getParametersByDevice();
    assertThat(parametersByDevice.size(), is(2));

    Map<String, String> device1params = parametersByDevice.get("dev1");
    assertThat(device1params.size(), is(10));
    assertThat(device1params.get("utilization"), is("0.5"));
    assertThat(device1params.get("observed_state"), is("ok"));
    assertThat(device1params.get("mb_total"), is("100"));
    assertThat(device1params.get("mb_used"), is("51"));
    assertThat(device1params.get("mb_free"), is("49"));
    assertThat(device1params.get("reject_bad_md5"), is("1"));
    assertThat(device1params.get("weight"), is("10"));
    assertThat(device1params.get("devid"), is("1"));
    assertThat(device1params.get("status"), is("failed"));
    assertThat(device1params.get("hostid"), is("1"));

    Map<String, String> device2params = parametersByDevice.get("dev2");
    assertThat(device2params.size(), is(10));
    assertThat(device2params.get("utilization"), is("0.1"));
    assertThat(device2params.get("observed_state"), is("error"));
    assertThat(device2params.get("mb_total"), is("200"));
    assertThat(device2params.get("mb_used"), is("51"));
    assertThat(device2params.get("mb_free"), is("149"));
    assertThat(device2params.get("reject_bad_md5"), is("1"));
    assertThat(device2params.get("weight"), is("5"));
    assertThat(device2params.get("devid"), is("2"));
    assertThat(device2params.get("status"), is("ok"));
    assertThat(device2params.get("hostid"), is("2"));
  }

  @Test
  public void emptyResponse() throws TrackerException {
    when(mockResponse.getValue("devices")).thenReturn("0");

    Map<String, String> valueMap = Collections.emptyMap();
    when(mockResponse.getValueMap()).thenReturn(valueMap);

    when(mockRequestHandler.performRequest(captorRequest.capture())).thenReturn(mockResponse);

    operation = new GetDeviceStatusesOperation(mockRequestHandler, DOMAIN);
    operation.execute();

    Map<String, Map<String, String>> parametersByDevice = operation.getParametersByDevice();
    assertThat(parametersByDevice.size(), is(0));
  }

  @Test(expected = TrackerException.class)
  public void unexpectedError() throws TrackerException {
    when(mockRequestHandler.performRequest(captorRequest.capture())).thenReturn(mockFailResponse);

    operation = new GetDeviceStatusesOperation(mockRequestHandler, DOMAIN);
    operation.execute();
  }

}
