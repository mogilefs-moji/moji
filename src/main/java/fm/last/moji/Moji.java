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
package fm.last.moji;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The Moji entry point. A representation of a MogileFS domain that allows interactions with remote files.
 * <p>
 * Example usage:
 * <p>
 * 
 * <pre>
 * MojiFactory factory = new PropertyMojiFactory();
 * Moji moji = factory.getInstance();
 * MojiFile file = moji.getFile(&quot;some-key&quot;);
 * file.copyToFile(new File(&quot;localFile.dat&quot;));
 * </pre>
 */
public interface Moji {

  /**
   * Creates an abstract representation of a remote MogileFS file for the given key.
   * 
   * @param key MogileFS file key.
   * @return Representation of the remote file.
   */
  MojiFile getFile(String key);

  /**
   * Creates an abstract representation of a remote MogileFS file for the given key. When the file content is modified
   * the file will also be assigned the specified storage class. Note that storage class parameter has no effect when
   * reading files.
   * 
   * @param key MogileFS file key.
   * @param storageClass The storage class to which a new file will be assigned.
   * @return Representation of the remote file.
   */
  MojiFile getFile(String key, String storageClass);

  /**
   * Copies a local source file to the given remote MogileFS destination file.
   * 
   * @param source The local source file.
   * @param destination The remote destination of the file.
   * @throws IOException If there was a problem writing the file.
   */
  void copyToMogile(File source, MojiFile destination) throws IOException;

  /**
   * Get remote MogileFS file representations that match the given key prefix.
   * 
   * @param keyPrefix The Key prefix to match remote files on.
   * @return A list of matching MofileFS file representations or an empty list if there were no matches.
   * @throws IOException If there was a problem communicating with MogileFS.
   */
  List<MojiFile> list(String keyPrefix) throws IOException;

  /**
   * Get a bounded list of remote MogileFS file representations that match the given key prefix.
   * 
   * @param keyPrefix The Key prefix to match remote files on.
   * @param limit The maximum number of files to return.
   * @return A list of matching MofileFS file representations or an empty list if there were no matches.
   * @throws IOException If there was a problem communicating with MogileFS.
   */
  List<MojiFile> list(String keyPrefix, int limit) throws IOException;

  /**
   * Get a list of all remote MogileFS devices and their status
   * 
   * @return A list of MogileFS device status representations
   * @throws IOException
   */
  List<MojiDeviceStatus> getDeviceStatuses() throws IOException;

}
