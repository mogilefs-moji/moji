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

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MojiFileAttributesImplTest {

  @Test
  public void typical() {
    Map<String, String> valueMap = new HashMap<String, String>();
    valueMap.put("domain", "domain2");
    valueMap.put("key", "key2");
    valueMap.put("class", "default");
    valueMap.put("length", "100");
    valueMap.put("devcount", "2");
    valueMap.put("fid", "5645");
    valueMap.put("checksum", "MD5:f178b0937249db0b7ea324c19bae5c79");

    MojiFileAttributesImpl attributes = new MojiFileAttributesImpl(valueMap);
    assertEquals(attributes.getDomain(), "domain2");
    assertEquals(attributes.getKey(), "key2");
    assertEquals(attributes.getStorageClass(), "default");
    assertEquals(attributes.getLength(), 100L);
    assertEquals(attributes.getDeviceCount(), 2);
    assertEquals(attributes.getFid(), 5645L);
    assertEquals(attributes.getChecksum(), "MD5:f178b0937249db0b7ea324c19bae5c79");
  }

  @Test
  public void noChecksum() {
    Map<String, String> valueMap = new HashMap<String, String>();
    valueMap.put("domain", "domain2");
    valueMap.put("key", "key2");
    valueMap.put("class", "default");
    valueMap.put("length", "100");
    valueMap.put("devcount", "2");
    valueMap.put("fid", "5645");

    MojiFileAttributesImpl attributes = new MojiFileAttributesImpl(valueMap);
    assertEquals(attributes.getDomain(), "domain2");
    assertEquals(attributes.getKey(), "key2");
    assertEquals(attributes.getStorageClass(), "default");
    assertEquals(attributes.getLength(), 100L);
    assertEquals(attributes.getDeviceCount(), 2);
    assertEquals(attributes.getFid(), 5645L);
    assertEquals(attributes.getChecksum(), null);
  }

}
