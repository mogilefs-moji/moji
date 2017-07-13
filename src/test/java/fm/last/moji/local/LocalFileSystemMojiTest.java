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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fm.last.moji.MojiFile;

public class LocalFileSystemMojiTest {

  private static final String DEFAULT_DOMAIN = "domain";
  private static final String DEFAULT_CLASS = "defaultClass";
  private static final String EXISTING_FILE_WITH_STORAGECLASS = "1234";
  private static final String EXISTING_FILE_WITHOUT_STORAGECLASS = "abcd";

  @Rule
  public final TemporaryFolder temporaryFolder = new TemporaryFolder();
  private File baseDir;

  private LocalFileSystemMoji moji;

  @Before
  public void setUp() throws IOException {
    baseDir = temporaryFolder.newFolder("mojiBaseDir");
    moji = new LocalFileSystemMoji(baseDir, DEFAULT_DOMAIN);
    MojiFile mojiFile = moji.getFile(EXISTING_FILE_WITH_STORAGECLASS, DEFAULT_CLASS);
    addFileToMoji(mojiFile);
    mojiFile = moji.getFile(EXISTING_FILE_WITHOUT_STORAGECLASS);
    addFileToMoji(mojiFile);
  }

  private void addFileToMoji(MojiFile mojiFile) throws IOException {
    assertFalse(mojiFile.exists());
    File file = temporaryFolder.newFile(mojiFile.getKey());
    moji.copyToMogile(file, mojiFile);
    assertTrue(mojiFile.exists());
  }

  @Test
  public void getFileWithStorageClass() throws Exception {
    MojiFile mojiFile = moji.getFile(EXISTING_FILE_WITH_STORAGECLASS, DEFAULT_CLASS);
    assertTrue(mojiFile.exists());
    mojiFile = moji.getFile(EXISTING_FILE_WITH_STORAGECLASS);
    assertFalse(mojiFile.exists());
  }

  @Test
  public void getFileWithoutStorageClass() throws Exception {
    MojiFile mojiFile = moji.getFile(EXISTING_FILE_WITHOUT_STORAGECLASS);
    assertTrue(mojiFile.exists());
    mojiFile = moji.getFile(EXISTING_FILE_WITHOUT_STORAGECLASS, DEFAULT_CLASS);
    assertFalse(mojiFile.exists());
  }

  @Test
  public void listFileWithStorageClass() throws Exception {
    List<MojiFile> mojiFiles = moji.list(EXISTING_FILE_WITH_STORAGECLASS);
    assertEquals(1, mojiFiles.size());
    MojiFile mojiFile = mojiFiles.get(0);
    assertTrue(mojiFile.exists());
  }

  @Test
  public void listFileWithoutStorageClass() throws Exception {
    List<MojiFile> mojiFiles = moji.list(EXISTING_FILE_WITHOUT_STORAGECLASS);
    assertEquals(1, mojiFiles.size());
    MojiFile mojiFile = mojiFiles.get(0);
    assertTrue(mojiFile.exists());
  }

}
