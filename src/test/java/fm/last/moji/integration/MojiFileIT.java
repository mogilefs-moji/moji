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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fm.last.moji.MojiFile;
import fm.last.moji.MojiFileAttributes;
import fm.last.moji.tracker.KeyExistsAlreadyException;
import fm.last.moji.tracker.UnknownKeyException;
import fm.last.moji.tracker.UnknownStorageClassException;

public class MojiFileIT extends AbstractMojiIT {

  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  @Test
  public void writeNewThenReadBack() throws Exception {
    MojiFile newFile = getFile(newKey());
    assertFalse(newFile.exists());

    String data = newData();

    writeDataToMogileFile(newFile, data);
    assertTrue(newFile.exists());

    assertEquals(data, downloadDataFromMogileFile(newFile));
  }

  @Test
  public void overwriteThenReadBack() throws Exception {
    MojiFile existingFile = getFile(newKey("overwriteThenReadBack"));
    String overwrite = newData();

    writeDataToMogileFile(existingFile, overwrite);
    assertEquals(overwrite, downloadDataFromMogileFile(existingFile));
  }

  @Test
  public void writeNewWithStorageClassThenReadBack() throws Exception {
    MojiFile newFile = getFile(newKey(), storageClassA);
    assertFalse(newFile.exists());

    String data = newData();

    writeDataToMogileFile(newFile, data);
    assertTrue(newFile.exists());
    assertEquals(newFile.getAttributes().getStorageClass(), storageClassA);

    assertEquals(data, downloadDataFromMogileFile(newFile));
  }

  @Test(expected = UnknownStorageClassException.class)
  public void writeWithstorageClassUnknown() throws IOException {
    MojiFile fileInUnknownClass = getFile(newKey(), "madeup" + RandomStringUtils.randomAlphanumeric(8));
    assertFalse(fileInUnknownClass.exists());

    String data = newData();

    writeDataToMogileFile(fileInUnknownClass, data);
  }

  @Test
  public void fileSize() throws IOException {
    MojiFile fileWithSize = getFile(newKey("fileOfKnownSize"));
    assertEquals(3832, fileWithSize.length());
  }

  @Test
  public void exists() throws IOException {
    MojiFile existentFile = getFile(newKey("exists"));
    assertTrue(existentFile.exists());
  }

  @Test
  public void notExists() throws IOException {
    MojiFile existentFile = getFile(newKey());
    assertFalse(existentFile.exists());
  }

  @Test
  public void notExistsAfterDelete() throws IOException {
    MojiFile existentFile = getFile(newKey("notExistsAfterDelete"));
    assertTrue(existentFile.exists());
    existentFile.delete();
    assertFalse(existentFile.exists());
  }

  @Test
  public void rename() throws IOException {
    String originalKey = newKey("rename");
    MojiFile fileToRename = getFile(originalKey);

    String newKey = newKey();
    fileToRename.rename(newKey);
    assertEquals(newKey, fileToRename.getKey());

    MojiFile renamed = getFile(newKey);
    assertTrue(renamed.exists());

    MojiFile oldName = getFile(originalKey);
    assertFalse(oldName.exists());
  }

  @Test(expected = UnknownKeyException.class)
  public void renameUnknownKey() throws IOException {
    MojiFile nonExistentFile = getFile(newKey());
    nonExistentFile.rename(newKey());
  }

  @Test(expected = KeyExistsAlreadyException.class)
  public void renameExistingKey() throws IOException {
    String alreadyHereKey = newKey("renameExistingKey1");
    String toRenameKey = newKey("renameExistingKey2");

    MojiFile newFile = getFile(toRenameKey);
    newFile.rename(alreadyHereKey);
  }

  @Test
  public void getAttributes() throws IOException {
    String originalKey = newKey("attributes");
    MojiFile forAttributes = getFile(originalKey);

    MojiFileAttributes attributes = forAttributes.getAttributes();
    assertEquals(storageClassA, attributes.getStorageClass());
    assertEquals(originalKey, attributes.getKey());
    assertEquals(forAttributes.getDomain(), attributes.getDomain());
    assertEquals(3832, attributes.getLength());
    assertEquals(1, attributes.getDeviceCount());
    assertTrue(attributes.getFid() > 0);
  }

  @Test
  public void updateStorageClass() throws IOException {
    String key = newKey("updateStorageClass");
    MojiFile fileToUpdate = getFile(key, storageClassA);
    MojiFileAttributes attributes = fileToUpdate.getAttributes();
    assertEquals(storageClassA, attributes.getStorageClass());

    fileToUpdate.modifyStorageClass(storageClassB);
    attributes = fileToUpdate.getAttributes();
    assertEquals(storageClassB, attributes.getStorageClass());

    MojiFile exists = getFile(key, storageClassB);
    assertTrue(exists.exists());
  }

  @Test(expected = UnknownStorageClassException.class)
  public void updateStorageClassToUnknown() throws IOException {
    MojiFile fileToUpdate = getFile(newKey("updateStorageClassToUnknown"));
    fileToUpdate.modifyStorageClass("madeup" + RandomStringUtils.randomAlphanumeric(8));
  }

  @Test(expected = UnknownKeyException.class)
  public void deleteNonExistent() throws IOException {
    MojiFile fileToDelete = getFile(newKey());
    fileToDelete.delete();
  }

  @Test
  public void copyToFile() throws IOException {
    MojiFile copyFile = getFile(newKey("mogileFileCopyToFile"));

    File file = testFolder.newFile(newKey() + ".dat");
    copyFile.copyToFile(file);

    byte[] actualData = FileUtils.readFileToByteArray(file);

    File original = new File("src/test/data/mogileFileCopyToFile.dat");
    byte[] expectedData = FileUtils.readFileToByteArray(original);
    assertArrayEquals(expectedData, actualData);
  }

  @Test(expected = UnknownKeyException.class)
  public void copyToFileUnknownKey() throws IOException {
    MojiFile copyFile = getFile(newKey());
    File file = testFolder.newFile(newKey() + ".dat");
    copyFile.copyToFile(file);
  }

  @Test
  public void getPaths() throws Exception {
    MojiFile file = getFile(newKey("getPaths"));
    List<URL> paths = file.getPaths();
    assertFalse(paths.isEmpty());
  }

}
