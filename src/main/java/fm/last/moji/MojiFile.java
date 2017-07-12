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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

/**
 * An abstract representation of a remote MogileFS file.
 */
public interface MojiFile {

  /**
   * Determines whether or not the file represented by this object exists in MogileFS.
   * 
   * @return <code>true</code> if the file exists in MogileFS, <code>false</code> otherwise.
   * @throws IOException If there was a problem communicating with MogileFS.
   */
  boolean exists() throws IOException;

  /**
   * Deletes this file in MogileFS if it exists. If the file doesn't exists then this method will return without error.
   * 
   * @throws IOException If there was a problem communicating with MogileFS.
   */
  void delete() throws IOException;

  /**
   * Assigns a new key to the MogileFS file represented by this object.
   * 
   * @param key The new key for the file.
   * @throws fm.last.moji.tracker.UnknownKeyException If this file doesn't exist in MogileFS.
   * @throws fm.last.moji.tracker.KeyExistsAlreadyException If the new key already points to a file in MogileFS.
   * @throws IOException If there was a problem communicating with MogileFS.
   */
  void rename(String key) throws IOException;

  /**
   * Gets an InputStream to the content of the MogileFS file represented by this object.
   * 
   * @throws fm.last.moji.tracker.UnknownKeyException If this file doesn't exist in MogileFS.
   * @throws IOException If there was a problem communicating with MogileFS.
   */
  InputStream getInputStream() throws IOException;

  /**
   * Gets an OutputStream for writing content to the MogileFS file represented by this object. If the file does not
   * exist then it will be created.
   * 
   * @throws IOException If there was a problem communicating with MogileFS.
   */
  OutputStream getOutputStream() throws IOException;

  /**
   * Copies the content of the file represented by this object to a local file destination.
   * 
   * @param file
   * @throws fm.last.moji.tracker.UnknownKeyException If this file doesn't exist in MogileFS.
   * @throws IOException
   */
  void copyToFile(File file) throws IOException;

  /**
   * Returns the length in bytes of this remote file.
   * 
   * @return File length in bytes.
   * @throws fm.last.moji.tracker.UnknownKeyException If this file doesn't exist in MogileFS.
   * @throws IOException If there was a problem communicating with MogileFS.
   */
  long length() throws IOException;

  /**
   * Assigns this file to the specified storage class.
   * 
   * @param storageClass The new storage class
   * @throws fm.last.moji.tracker.UnknownKeyException If this file doesn't exist in MogileFS.
   * @throws fm.last.moji.tracker.UnknownStorageClassException If the specified storage class is not defined in
   *           MogileFS.
   * @throws IOException If there was a problem communicating with MogileFS.
   */
  void modifyStorageClass(String storageClass) throws IOException;

  /**
   * Returns a list of storage node paths from which this file can be accessed.
   * 
   * @return A list of storage node paths or an empty list.
   * @throws fm.last.moji.tracker.UnknownKeyException If this file doesn't exist in MogileFS.
   * @throws IOException If there was a problem communicating with MogileFS.
   */
  List<URL> getPaths() throws IOException;

  /**
   * The key of this file in MogileFS.
   * 
   * @return This files key.
   */
  String getKey();

  /**
   * The MogileFS domain in which this file is located.
   * 
   * @return This files key.
   */
  String getDomain();

  /**
   * @throws IOException
   */
  MojiFileAttributes getAttributes() throws IOException;

}
