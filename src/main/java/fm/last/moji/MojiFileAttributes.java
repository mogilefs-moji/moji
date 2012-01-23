package fm.last.moji;


// - file: 54400-original
// class: default
// devcount: 2
// domain: mpint
// fid: 658
// key: 54400-original
// length: 33
// - http://127.0.0.1:7500/dev2/0/000/000/0000000658.fid
// - http://127.0.0.1:7500/dev1/0/000/000/0000000658.fid
public interface MojiFileAttributes {

  String getStorageClass();

  int getDeviceCount();

  long getLength();

  int getFid();

  String getDomain();

  String getKey();

}
