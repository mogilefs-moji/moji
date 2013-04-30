package fm.last.moji.impl;

enum DeviceStatusField {
  UTILIZATION("utilization", Float.class),
  OBSERVED_STATE("observed_state", String.class),
  CAPACITY_TOTAL("mb_total", Long.class),
  CAPACITY_USED("mb_used", Long.class),
  CAPACITY_FREE("mb_free", Long.class),
  REJECT_BAD_MD5("reject_bad_md5", Boolean.class),
  WEIGHT("weight", Integer.class),
  DEVICE_ID("devid", Integer.class),
  STATUS("status", String.class),
  HOST_ID("hostid", Integer.class);

  private final String fieldName;
  private final Class<?> destinationType;

  private DeviceStatusField(String fieldName, Class<?> destinationType) {
    this.fieldName = fieldName;
    this.destinationType = destinationType;
  }

  String getFieldName() {
    return fieldName.toLowerCase();
  }

  Class<?> getDestinationType() {
    return destinationType;
  }

}
