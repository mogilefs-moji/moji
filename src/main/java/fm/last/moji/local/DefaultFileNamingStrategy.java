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
import java.io.FilenameFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated Does not support 'storage class' and has character restrictions - use a
 *             {@link fm.last.moji.local.Base64FileNamingStrategy Base32LocalFileNamingStrategy} instead.
 */
@Deprecated
class DefaultFileNamingStrategy implements LocalFileNamingStrategy {

  private static final Logger log = LoggerFactory.getLogger(DefaultFileNamingStrategy.class);

  private final File baseFolder;

  DefaultFileNamingStrategy(File baseFolder) {
    this.baseFolder = baseFolder;
  }

  @Override
  public String newFileName(String domain, String key, String storageClass) {
    return domain + "-" + key + ".dat";
  }

  @Override
  public String domainForFileName(String fileName) {
    int dashPosition = fileName.indexOf('-');
    String domain = fileName.substring(0, dashPosition);
    return domain;
  }

  @Override
  public String keyForFileName(String fileName) {
    int dashPosition = fileName.indexOf('-');
    int periodPosition = fileName.lastIndexOf('.');
    String key = fileName.substring(dashPosition + 1, periodPosition);
    return key;
  }

  @Override
  public File folderForDomain(String domain) {
    return baseFolder;
  }

  @Override
  public FilenameFilter filterForPrefix(String domain, final String keyPrefix) {
    return new KeyPrefixFileNameFilter(domain, keyPrefix);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DefaultFileNamingStrategy [baseFolder=");
    builder.append(baseFolder);
    builder.append("]");
    return builder.toString();
  }

  private final class KeyPrefixFileNameFilter implements FilenameFilter {
    private final String keyPrefix;
    private final String domain;

    private KeyPrefixFileNameFilter(String domain, String keyPrefix) {
      this.domain = domain;
      this.keyPrefix = keyPrefix;
    }

    @Override
    public boolean accept(File dir, String name) {
      if (!baseFolder.equals(dir)) {
        return false;
      }
      if (name.startsWith(".")) {
        return false;
      }
      if (!name.startsWith(domain + "-")) {
        return false;
      }
      String key = keyForFileName(name);
      return key.startsWith(keyPrefix);
    }
  }

  @Override
  public String storageClassForFileName(String fileName) {
    log.warn("This implementation does not support the persisting of storage classes!", getClass().getSimpleName());
    return null;
  }

}
