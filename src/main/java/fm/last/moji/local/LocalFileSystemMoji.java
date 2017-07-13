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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

import fm.last.moji.Moji;
import fm.last.moji.MojiDeviceStatus;
import fm.last.moji.MojiFile;

/**
 * A simple {@link fm.last.moji.Moji Moji} implementation that uses the local filesystem for storage. This is intended
 * for testing only.
 */
public class LocalFileSystemMoji implements Moji {

  private final File baseFolder;
  private final LocalFileNamingStrategy namingStrategy;
  private final String domain;

  public LocalFileSystemMoji(File baseFolder, String domain) {
    this(baseFolder, domain, new Base64FileNamingStrategy(baseFolder));
  }

  public LocalFileSystemMoji(File baseFolder, String domain, LocalFileNamingStrategy namingStrategy) {
    createBaseFolderIfNeeded(baseFolder);
    this.baseFolder = baseFolder;
    this.domain = domain;
    this.namingStrategy = namingStrategy;
  }

  public File getBaseFolder() {
    return baseFolder;
  }

  public LocalFileNamingStrategy getNamingStrategy() {
    return namingStrategy;
  }

  public String getDomain() {
    return domain;
  }

  @Override
  public MojiFile getFile(String key) {
    return new LocalMojiFile(namingStrategy, baseFolder, domain, key);
  }

  @Override
  public MojiFile getFile(String key, String storageClass) {
    return new LocalMojiFile(namingStrategy, baseFolder, domain, key, storageClass);
  }

  @Override
  public void copyToMogile(File source, MojiFile destination) throws IOException {
    LocalMojiFile localDestination = (LocalMojiFile) destination;
    FileUtils.copyFile(source, localDestination.file);
  }

  @Override
  public List<MojiFile> list(final String keyPrefix) {
    File[] files = baseFolder.listFiles(namingStrategy.filterForPrefix(domain, keyPrefix));
    List<MojiFile> mojiFiles = new ArrayList<MojiFile>(files.length);
    for (File file : files) {
      String key = namingStrategy.keyForFileName(file.getName());
      String storageClass = namingStrategy.storageClassForFileName(file.getName());
      mojiFiles.add(new LocalMojiFile(namingStrategy, baseFolder, domain, key, storageClass));
    }
    return mojiFiles;
  }

  @Override
  public List<MojiFile> list(String keyPrefix, int limit) {
    List<MojiFile> list = list(keyPrefix);
    int count = limit > list.size() ? list.size() : limit;
    List<MojiFile> mojiFiles = new ArrayList<MojiFile>();
    for (int i = 0; i < count; i++) {
      mojiFiles.add(list.get(i));
    }
    return mojiFiles;
  }

  /**
   * Always returns an empty list.
   */
  @Override
  public List<MojiDeviceStatus> getDeviceStatuses() {
    return Collections.emptyList();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("LocalFileSystemMoji [baseFolder=");
    builder.append(baseFolder);
    builder.append(", domain=");
    builder.append(domain);
    builder.append(", namingStrategy=");
    builder.append(namingStrategy);
    builder.append("]");
    return builder.toString();
  }

  private void createBaseFolderIfNeeded(File baseFolder) {
    boolean exists = baseFolder.exists();
    if (!exists) {
      boolean mkdirs = baseFolder.mkdirs();
      if (!mkdirs) {
        throw new IllegalStateException("Could not create base directory: " + baseFolder.getAbsolutePath());
      }
    }
  }

}
