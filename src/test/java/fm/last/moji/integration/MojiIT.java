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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fm.last.moji.MojiFile;

public class MojiIT extends AbstractMojiIT {

  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  @Test
  public void copyToMogile() throws IOException {
    File tempFile = testFolder.newFile(newKey() + ".txt");
    String data = newData();

    FileUtils.write(tempFile, data);
    MojiFile destination = getFile(newKey());

    moji.copyToMogile(tempFile, destination);

    assertEquals(data, downloadDataFromMogileFile(destination));
  }

  @Test
  public void list() throws IOException {
    List<MojiFile> list = moji.list(keyPrefix + "list");
    assertThat(list.size(), is(3));
    Set<String> keys = new HashSet<String>();
    for (MojiFile mojiFile : list) {
      keys.add(mojiFile.getKey());
    }
    assertTrue(keys.contains(keyPrefix + "list1"));
    assertTrue(keys.contains(keyPrefix + "list2"));
    assertTrue(keys.contains(keyPrefix + "list3"));
  }

  @Test
  public void listWithLimit() throws IOException {
    List<MojiFile> list = moji.list(keyPrefix + "list", 1);
    assertThat(list.size(), is(1));
  }

  @Test
  public void listNoMatches() throws IOException {
    List<MojiFile> list = moji.list("XXX" + keyPrefix);
    assertTrue(list.isEmpty());
  }

}
