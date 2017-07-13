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
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

class Base64FileNamingStrategy implements LocalFileNamingStrategy {

  private static final String UTF_8 = "UTF-8";
  private final File baseFolder;

  Base64FileNamingStrategy(File baseFolder) {
    this.baseFolder = baseFolder;
  }

  @Override
  public String newFileName(String domain, String key, String storageClass) {
    Base64 base64 = new Base64(-1);
    String name = encode(base64, domain) + "-" + encode(base64, key) + "-" + encode(base64, storageClass) + ".dat";
    return name;
  }

  @Override
  public String domainForFileName(String fileName) {
    String encodedDomain = fileName.split("[-\\.]")[0];
    Base64 base64 = new Base64(-1);
    String domain = decode(base64, encodedDomain);
    return domain;
  }

  @Override
  public String keyForFileName(String fileName) {
    String encodedKey = fileName.split("[-\\.]")[1];
    Base64 base64 = new Base64(-1);
    String key = decode(base64, encodedKey);
    return key;
  }

  @Override
  public String storageClassForFileName(String fileName) {
    String encodedStorageClass = fileName.split("[-\\.]")[2];
    Base64 base64 = new Base64(-1);
    String storageClass = decode(base64, encodedStorageClass);
    return storageClass;
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
    builder.append("Base64FileNamingStrategy [baseFolder=");
    builder.append(baseFolder);
    builder.append("]");
    return builder.toString();
  }

  private final class KeyPrefixFileNameFilter implements FilenameFilter {
    private final String keyPrefix;
    private final String encodedDomain;
    private final Base64 base64;

    private KeyPrefixFileNameFilter(String domain, String keyPrefix) {
      base64 = new Base64(-1);
      this.encodedDomain = encode(base64, domain);
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
      if (!name.startsWith(encodedDomain + "-")) {
        return false;
      }
      String key = keyForFileName(name);
      return key.startsWith(keyPrefix);
    }
  }

  private String encode(Base64 base64, String string) {
    try {
      return base64.encodeAsString(string.getBytes(UTF_8)).replace('\\', '|');
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  private String decode(Base64 base64, String string) {
    try {
      return new String(base64.decode(string.replace('|', '\\').getBytes(UTF_8)), UTF_8);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

}
