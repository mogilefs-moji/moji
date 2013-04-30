package fm.last.moji;

public interface MojiDeviceStatus {

  Long getCapacityFreeMb();

  String getDeviceName();

  Integer getHostId();

  String getStatus();

  Integer getId();

  Integer getWeight();

  Long getCapacityUsedMb();

  Boolean getRejectBadMd5();

  Long getCapacityTotalMb();

  String getObservedState();

  Float getUtilization();

}
