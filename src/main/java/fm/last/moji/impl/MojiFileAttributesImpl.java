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

import java.util.Map;

import fm.last.moji.MojiFileAttributes;

class MojiFileAttributesImpl implements MojiFileAttributes {

  private final String storageClass;
  private final int deviceCount;
  private final long length;
  private final long fid;
  private final String domain;
  private final String key;
  private final String checksum;

  MojiFileAttributesImpl(Map<String, String> valueMap) {
    length = Long.parseLong(valueMap.get("length"));
    domain = valueMap.get("domain");
    fid = Long.parseLong(valueMap.get("fid"));
    deviceCount = Integer.parseInt(valueMap.get("devcount"));
    storageClass = valueMap.get("class");
    key = valueMap.get("key");
    checksum = valueMap.get("checksum");
  }

  @Override
  public String getStorageClass() {
    return storageClass;
  }

  @Override
  public int getDeviceCount() {
    return deviceCount;
  }

  @Override
  public long getLength() {
    return length;
  }

  @Override
  public long getFid() {
    return fid;
  }

  @Override
  public String getDomain() {
    return domain;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public String getChecksum() {
    return checksum;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("MojiFileAttributesImpl [domain=");
    builder.append(domain);
    builder.append(", key=");
    builder.append(key);
    builder.append(", storageClass=");
    builder.append(storageClass);
    builder.append(", length=");
    builder.append(length);
    builder.append(", fid=");
    builder.append(fid);
    builder.append(", checksum=");
    builder.append(checksum);
    builder.append(", deviceCount=");
    builder.append(deviceCount);
    builder.append("]");
    return builder.toString();
  }

}
