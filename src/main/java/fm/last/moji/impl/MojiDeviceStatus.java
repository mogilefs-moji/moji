package fm.last.moji.impl;
/**
 * Representation for the MogileFS device
 */
public class MojiDeviceStatus {
  /**
   * Percentage of utilization in float of scale .00
   */
  private Float utilization;

  private String observedState;

  private Long totalMB;

  private Integer rejectBadMD5;

  private Long usedMB;

  private Integer weight;

  private Integer devId;

  private String devStatus;

  private Integer hostId;

  private String deviceName;

  private Long freeMB;

  public Float getUtilization() {
    return utilization;
  }

  public void setUtilization(Float utilization) {
    this.utilization = utilization;
  }

  public String getObservedState() {
    return observedState;
  }

  public void setObservedState(String observedState) {
    this.observedState = observedState;
  }

  public Long getTotalMB() {
    return totalMB;
  }

  public void setTotalMB(Long totalMB) {
    this.totalMB = totalMB;
  }

  public Integer getRejectBadMD5() {
    return rejectBadMD5;
  }

  public void setRejectBadMD5(Integer rejectBadMD5) {
    this.rejectBadMD5 = rejectBadMD5;
  }

  public Long getUsedMB() {
    return usedMB;
  }

  public void setUsedMB(Long usedMB) {
    this.usedMB = usedMB;
  }

  public Integer getWeight() {
    return weight;
  }

  public void setWeight(Integer weight) {
    this.weight = weight;
  }

  public Integer getDevId() {
    return devId;
  }

  public void setDevId(Integer devId) {
    this.devId = devId;
  }

  public String getDevStatus() {
    return devStatus;
  }

  public void setDevStatus(String devStatus) {
    this.devStatus = devStatus;
  }

  public Integer getHostId() {
    return hostId;
  }

  public void setHostId(Integer hostId) {
    this.hostId = hostId;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public Long getFreeMB() {
    return freeMB;
  }

  public void setFreeMB(Long freeMB) {
    this.freeMB = freeMB;
  }

}
