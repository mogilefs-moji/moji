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

  MojiFileAttributesImpl(Map<String, String> valueMap) {
    length = Long.parseLong(valueMap.get("length"));
    domain = valueMap.get("domain");
    fid = Long.parseLong(valueMap.get("fid"));
    deviceCount = Integer.parseInt(valueMap.get("devcount"));
    storageClass = valueMap.get("class");
    key = valueMap.get("key");
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
    builder.append(", deviceCount=");
    builder.append(deviceCount);
    builder.append("]");
    return builder.toString();
  }

}
