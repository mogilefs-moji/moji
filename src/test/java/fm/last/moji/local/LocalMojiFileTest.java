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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LocalMojiFileTest {

  private static final String DEFAULT_DOMAIN = "domain";
  private static final String DEFAULT_CLASS = "defaultClass";
  @Rule
  public final TemporaryFolder temporaryFolder = new TemporaryFolder();
  private LocalFileNamingStrategy namingStrategy;
  private File baseDir;

  @Before
  public void setUp() {
    namingStrategy = new Base64FileNamingStrategy(temporaryFolder.getRoot());
    baseDir = temporaryFolder.getRoot();
  }

  @Test
  public void rename() throws IOException {
    String originalKey = "original";
    String originalFileName = namingStrategy.newFileName(DEFAULT_DOMAIN, originalKey, DEFAULT_CLASS);
    String newKey = "renameTo";
    String newFileName = namingStrategy.newFileName(DEFAULT_DOMAIN, newKey, DEFAULT_CLASS);

    File originalFile = temporaryFolder.newFile(originalFileName);
    FileUtils.write(originalFile, "somedata");

    LocalMojiFile mojiFile = new LocalMojiFile(namingStrategy, baseDir, DEFAULT_DOMAIN, originalKey, DEFAULT_CLASS);

    File newFile = temporaryFolder.newFile(newFileName);
    mojiFile.rename(newKey);

    assertEquals(newFile, mojiFile.file);
  }

}
