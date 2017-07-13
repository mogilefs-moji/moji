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

public class Base64FileNamingStrategyTest {

  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  private File folder;
  private Base64FileNamingStrategy namingStrategy;

  @Before
  public void setUp() {
    folder = testFolder.newFolder("local-moji");
    namingStrategy = new Base64FileNamingStrategy(folder);
  }

  @Test
  public void newFileName() {
    String name = namingStrategy.newFileName("lastfm", "8473848737", "mp3");
    assertThat(name, is("bGFzdGZt-ODQ3Mzg0ODczNw==-bXAz.dat"));
  }

  @Test
  public void keyForFile() {
    String key = namingStrategy.keyForFileName("bGFzdGZt-ODQ3Mzg0ODczNw==-bXAz.dat");
    assertThat(key, is("8473848737"));
  }

  @Test
  public void domainForFile() {
    String domain = namingStrategy.domainForFileName("bGFzdGZt-ODQ3Mzg0ODczNw==-bXAz.dat");
    assertThat(domain, is("lastfm"));
  }

  @Test
  public void storageClassForFile() {
    String domain = namingStrategy.storageClassForFileName("bGFzdGZt-ODQ3Mzg0ODczNw==-bXAz.dat");
    assertThat(domain, is("mp3"));
  }

  @Test
  public void fileNameFilter() {
    File anotherFolder = testFolder.newFolder("some-other-folder");
    FilenameFilter filter = namingStrategy.filterForPrefix("lastfm", "100");

    String allowed1 = namingStrategy.newFileName("lastfm", "1003848737", "mp3");
    String allowed2 = namingStrategy.newFileName("lastfm", "1003848737", "anotherClass");
    String disallowed1 = namingStrategy.newFileName("lastfm", "1013848737", "mp3");
    String disallowed2 = namingStrategy.newFileName("another", "1003848737", "mp3");

    assertTrue(filter.accept(folder, allowed1));
    assertTrue(filter.accept(folder, allowed2));
    assertFalse(filter.accept(folder, disallowed1));
    assertFalse(filter.accept(folder, disallowed2));
    assertFalse(filter.accept(folder, ".ssh"));
    assertFalse(filter.accept(folder, ".."));
    assertFalse(filter.accept(folder, "."));
    assertFalse(filter.accept(anotherFolder, allowed1));
  }

}
