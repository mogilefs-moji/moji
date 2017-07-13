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
import java.util.List;
import java.util.Map;

/**
 * Service interface of the MogileFS 'backend'.
 * 
 * @see <a href="http://cpansearch.perl.org/src/DORMANDO/MogileFS-Client-1.14/lib/MogileFS/Client.pm">Perl MogileFS client</a>.
 */
public interface Tracker {

  List<URL> getPaths(String key, String domain) throws TrackerException;

  Map<String, String> fileInfo(String key, String domain) throws TrackerException;

  List<Destination> createOpen(String key, String domain, String storageClass) throws TrackerException;

  void createClose(String key, String domain, Destination destination, long size) throws TrackerException;

  /**
   * Delete a key from MogileFS in the given domain.
   * 
   * @param key The key to delete.
   * @param domain The domain in which the key resides.
   * @throws TrackerException If there was a problem deleting the key.
   */
  void delete(String key, String domain) throws TrackerException;

  /**
   * Rename file (key) in MogileFS from <code>oldKey</code> to <code>newKey</code>.
   * 
   * @param oldKey The key to rename.
   * @param domain The domain in which the old key resides.
   * @param newKey The new key.
   * @throws TrackerException If there was a problem deleting the key.
   */
  void rename(String oldKey, String domain, String newKey) throws TrackerException;

  /**
   * Update the storage class of a pre-existing file, causing the file to become more or less replicated
   * 
   * @param key The key of the file to modify.
   * @param domain The domain in which the key resides.
   * @param newStorageClass The new storage class.
   * @throws TrackerException If there was a problem updaing the storage class.
   */
  void updateStorageClass(String key, String domain, String newStorageClass) throws TrackerException;

  void noop() throws TrackerException;

  /**
   * Closes the resources used by this tracker. Pooled implementations may just return the tracker to the pool.
   */
  void close();

  /**
   * Get a list of keys matching a given prefix in the target domain.
   * 
   * @param domain The domain in which to perform the key search.
   * @param keyPrefix The key prefix to match against.
   * @param limit The maximim number of matches to return.
   * @return A list of matched keys, or an empty list if there were no matches.
   * @throws TrackerException If there was a problem matching.
   */
  List<String> list(String domain, String keyPrefix, Integer limit) throws TrackerException;

  /**
   * Get a list of parameters in key value format that describe each device
   * 
   * @param domain The domain in which to query the devices
   * @return A list of parameters by device
   * @throws TrackerException
   */
  public Map<String, Map<String, String>> getDeviceStatuses(String domain) throws TrackerException;

}
