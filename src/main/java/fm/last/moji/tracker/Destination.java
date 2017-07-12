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
package fm.last.moji.tracker;

import java.net.URL;

/**
 * Represents a MogileFS remote file location.
 */
public class Destination {

  private final URL path;
  private final int devId;
  private final long fid;

  public Destination(URL path, int devId, long fid) {
    this.path = path;
    this.devId = devId;
    this.fid = fid;
  }

  public URL getPath() {
    return path;
  }

  public int getDevId() {
    return devId;
  }

  public long getFid() {
    return fid;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Destination [path=");
    builder.append(path);
    builder.append(", devId=");
    builder.append(devId);
    builder.append(", fid=");
    builder.append(fid);
    builder.append("]");
    return builder.toString();
  }

}
