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
package fm.last.moji.integration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;

import fm.last.commons.test.file.DataFolder;
import fm.last.commons.test.file.RootDataFolder;
import fm.last.moji.MojiFile;
import fm.last.moji.spring.SpringMojiBean;
import fm.last.moji.tracker.UnknownKeyException;

@Ignore("abstract")
abstract public class AbstractMojiIT {

  @Rule
  public static DataFolder dataFolder = new RootDataFolder();

  private final List<MojiFile> mojiFiles = new ArrayList<MojiFile>();

  static SpringMojiBean moji;
  static String keyPrefix;
  static String storageClassA;
  static String storageClassB;

  @BeforeClass
  static public void initMogileTestData() throws Exception {
    initMoji();
    clearTestData();
    uploadFile("fileOfKnownSize", dataFolder.getFile("fileOfKnownSize.dat"));
    uploadFile("attributes", dataFolder.getFile("fileOfKnownSize.dat"));
    uploadFile("mogileFileCopyToFile", dataFolder.getFile("mogileFileCopyToFile.dat"));
    uploadNewRandomFile("overwriteThenReadBack");
    uploadNewRandomFile("exists");
    uploadNewRandomFile("notExistsAfterDelete");
    uploadNewRandomFile("rename");
    uploadNewRandomFile("renameExistingKey1");
    uploadNewRandomFile("renameExistingKey2");
    uploadNewRandomFile("updateStorageClass");
    uploadNewRandomFile("updateStorageClassToUnknown");
    uploadNewRandomFile("list1");
    uploadNewRandomFile("list2");
    uploadNewRandomFile("list3");
    uploadNewRandomFile("getPaths");
  }

  @Before
  public void setUp() throws Exception {
    initMoji();
  }

  @After
  public void tearDown() throws Exception {
    for (MojiFile file : mojiFiles) {
      if (file != null) {
        try {
          file.delete();
        } catch (UnknownKeyException e) {
        }
      }
    }
    moji.close();
  }

  void writeDataToMogileFile(MojiFile file, String data) throws IOException {
    OutputStream streamToMogile = null;
    StringReader toUpload = null;
    try {
      toUpload = new StringReader(data);
      streamToMogile = file.getOutputStream();
      IOUtils.copy(toUpload, streamToMogile);
      streamToMogile.flush();
    } finally {
      IOUtils.closeQuietly(streamToMogile);
      IOUtils.closeQuietly(toUpload);
    }
  }

  String downloadDataFromMogileFile(MojiFile file) throws IOException {
    StringWriter downloaded = null;
    InputStream streamFromMogile = null;
    try {
      downloaded = new StringWriter();
      streamFromMogile = file.getInputStream();
      IOUtils.copy(streamFromMogile, downloaded);
    } finally {
      IOUtils.closeQuietly(downloaded);
      IOUtils.closeQuietly(streamFromMogile);
    }
    return downloaded.toString();
  }

  MojiFile getFile(String key) {
    MojiFile file = moji.getFile(key);
    mojiFiles.add(file);
    return file;
  }

  MojiFile getFile(String key, String storageClass) {
    MojiFile file = moji.getFile(key, storageClass);
    mojiFiles.add(file);
    return file;
  }

  String newData() {
    return RandomStringUtils.randomAscii(RandomUtils.nextInt(4096) + 512);
  }

  String newKey(String suffix) {
    return keyPrefix + suffix;
  }

  String newKey() {
    return newKey(RandomStringUtils.randomAlphanumeric(16));
  }

  private static void clearTestData() throws IOException {
    List<MojiFile> files = moji.list(keyPrefix);
    for (MojiFile file : files) {
      file.delete();
    }
  }

  private static void uploadNewRandomFile(String key) throws IOException {
    MojiFile file = moji.getFile(keyPrefix + key, storageClassA);
    InputStream is = null;
    OutputStream os = null;
    try {
      is = new ByteArrayInputStream(UUID.randomUUID().toString().getBytes());
      os = file.getOutputStream();
      IOUtils.copy(is, os);
    } finally {
      IOUtils.closeQuietly(is);
      IOUtils.closeQuietly(os);
    }
  }

  private static void uploadFile(String key, File fileToUpload) throws IOException {
    MojiFile file = moji.getFile(keyPrefix + key, storageClassA);
    moji.copyToMogile(fileToUpload, file);
  }

  private static void initMoji() throws IOException {
    String env = System.getProperty("env", "");
    if (!"".equals(env)) {
      env = "." + env;
    }
    Properties properties = new Properties();
    properties.load(AbstractMojiIT.class.getResourceAsStream("/moji.properties" + env));

    String hosts = properties.getProperty("moji.tracker.hosts");
    String domain = properties.getProperty("moji.domain");

    keyPrefix = properties.getProperty("test.moji.key.prefix");
    storageClassA = properties.getProperty("test.moji.class.a");
    storageClassB = properties.getProperty("test.moji.class.b");

    moji = new SpringMojiBean();
    moji.setAddressesCsv(hosts);
    moji.setDomain(domain);
    moji.initialise();
    moji.setTestOnBorrow(true);
  }
}
