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
package fm.last.moji.local;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

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

  @Override
  public String getChecksum() {
    InputStream in = null;
    try {
      in = mojiFile.getInputStream();
      return String.format("MD5:%s", Hex.encodeHexString(DigestUtils.md5(in)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

}
