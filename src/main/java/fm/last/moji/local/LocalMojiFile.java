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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

import fm.last.moji.MojiFile;
import fm.last.moji.MojiFileAttributes;

class LocalMojiFile implements MojiFile {

  private final String domain;
  private final File baseDir;
  private String key;
  private final LocalFileNamingStrategy namingStrategy;
  private String storageClass;
  File file;

  LocalMojiFile(LocalFileNamingStrategy namingStrategy, File baseDir, String domain, String key, String storageClass) {
    this.namingStrategy = namingStrategy;
    this.baseDir = baseDir;
    this.key = key;
    this.domain = domain;
    this.storageClass = storageClass;
    file = new File(baseDir, namingStrategy.newFileName(domain, key, storageClass));
  }

  LocalMojiFile(LocalFileNamingStrategy namingStrategy, File baseFolder, String domain, String key) {
    this(namingStrategy, baseFolder, domain, key, "");
  }

  @Override
  public boolean exists() throws IOException {
    return file.exists();
  }

  @Override
  public void delete() throws IOException {
    if (!file.exists()) {
      throw new FileNotFoundException(file.getCanonicalPath());
    }
    file.delete();
  }

  @Override
  public InputStream getInputStream() throws IOException {
    if (!file.exists()) {
      throw new FileNotFoundException(file.getCanonicalPath());
    }
    return new FileInputStream(file);
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    if (!file.exists()) {
      file.createNewFile();
    }
    return new FileOutputStream(file);
  }

  @Override
  public void copyToFile(File destination) throws IOException {
    if (!file.exists()) {
      throw new FileNotFoundException(file.getCanonicalPath());
    }
    FileUtils.copyFile(file, destination);
  }

  @Override
  public long length() throws IOException {
    if (!file.exists()) {
      throw new FileNotFoundException(file.getCanonicalPath());
    }
    return file.length();
  }

  @Override
  public void rename(String newKey) throws IOException {
    if (!file.exists()) {
      throw new FileNotFoundException(file.getCanonicalPath());
    }
    File destination = new File(baseDir, namingStrategy.newFileName(domain, newKey, storageClass));
    file.renameTo(destination);
    file = destination;
    key = newKey;
  }

  @Override
  public List<URL> getPaths() throws IOException {
    return Collections.singletonList(file.toURI().toURL());
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public String getDomain() {
    return domain;
  }

  @Override
  public void modifyStorageClass(String newStorageClass) throws IOException {
    if (storageClass == null) {
      throw new IllegalArgumentException("storageClass == null");
    }
    if (!file.exists()) {
      throw new FileNotFoundException(file.getCanonicalPath());
    }
    file.renameTo(new File(baseDir, namingStrategy.newFileName(domain, key, newStorageClass)));
    this.storageClass = newStorageClass;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("LocalMogileFile [domain=");
    builder.append(domain);
    builder.append(", key=");
    builder.append(key);
    builder.append(", file=");
    builder.append(file);
    builder.append(", storageClass=");
    builder.append(storageClass);
    builder.append("]");
    return builder.toString();
  }

  @Override
  public MojiFileAttributes getAttributes() throws IOException {
    return new LocalMojiFileAttributes(this);
  }

  String getStorageClass() {
    return storageClass;
  }

}
