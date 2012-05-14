/*
 * Copyright 2012 Last.fm
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package fm.last.moji.integration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import fm.last.moji.MojiFile;
import fm.last.moji.spring.SpringMojiBean;
import fm.last.moji.tracker.UnknownKeyException;

@Ignore("abstract")
abstract public class AbstractMojiIT {

  private final List<MojiFile> mojiFiles = new ArrayList<MojiFile>();

  SpringMojiBean moji;
  String keyPrefix;
  String storageClassA;
  String storageClassB;

  @Before
  public void setUp() throws Exception {
    String env = System.getProperty("env", "");
    if (!"".equals(env)) {
      env = "." + env;
    }
    Properties properties = new Properties();
    properties.load(getClass().getResourceAsStream("/moji.properties" + env));

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

}
