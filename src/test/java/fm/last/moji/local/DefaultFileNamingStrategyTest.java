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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DefaultFileNamingStrategyTest {

  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  private File folder;
  private DefaultFileNamingStrategy namingStrategy;

  @Before
  public void setUp() {
    folder = testFolder.newFolder("local-moji");
    namingStrategy = new DefaultFileNamingStrategy(folder);
  }

  @Test
  public void keyForFile() {
    String key = namingStrategy.keyForFileName("lastfm-8473848737.dat");
    assertThat(key, is("8473848737"));
  }

  @Test
  public void domainForFile() {
    String domain = namingStrategy.domainForFileName("lastfm-8473848737.dat");
    assertThat(domain, is("lastfm"));
  }

  @Test
  public void fileNameFilter() {
    File anotherFolder = testFolder.newFolder("some-other-folder");
    FilenameFilter filter = namingStrategy.filterForPrefix("lastfm", "100");
    assertTrue(filter.accept(folder, "lastfm-1003848737.dat"));
    assertFalse(filter.accept(folder, "lastfm-1013848737.dat"));
    assertFalse(filter.accept(folder, "another-1003848737.dat"));
    assertFalse(filter.accept(folder, ".ssh"));
    assertFalse(filter.accept(folder, ".."));
    assertFalse(filter.accept(folder, "."));
    assertFalse(filter.accept(anotherFolder, "lastfm-1003848737.dat"));
  }

}
