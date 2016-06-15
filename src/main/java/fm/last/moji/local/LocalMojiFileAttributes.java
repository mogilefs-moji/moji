package fm.last.moji.local;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import fm.last.moji.MojiFileAttributes;

class LocalMojiFileAttributes implements MojiFileAttributes {

  private final LocalMojiFile mojiFile;
  private final long length;
  private final List<URL> paths;

  public LocalMojiFileAttributes(LocalMojiFile mojiFile) throws IOException {
    this.mojiFile = mojiFile;
    length = mojiFile.length();
    paths = mojiFile.getPaths();
  }

  @Override
  public String getStorageClass() {
    return mojiFile.getStorageClass();
  }

  @Override
  public int getDeviceCount() {
    return paths.size();
  }

  @Override
  public long getLength() {
    return length;
  }

  @Override
  public long getFid() {
    return 0;
  }

  @Override
  public String getDomain() {
    return mojiFile.getDomain();
  }

  @Override
  public String getKey() {
    return mojiFile.getKey();
  }

}
