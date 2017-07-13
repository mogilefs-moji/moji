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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LocalMojiFileAttributesTest {

  @Mock
  private LocalMojiFile mockMojiFile;

  @Test
  public void testGetStorageClass() throws IOException {
    when(mockMojiFile.getStorageClass()).thenReturn("storage-class");
    LocalMojiFileAttributes attributes = new LocalMojiFileAttributes(mockMojiFile);
    assertThat(attributes.getStorageClass(), is("storage-class"));
  }

  @Test
  public void testGetDeviceCount() throws IOException {
    List<URL> paths = new ArrayList<URL>();
    paths.add(new URL("http://foo.fid"));
    when(mockMojiFile.getPaths()).thenReturn(paths);
    LocalMojiFileAttributes attributes = new LocalMojiFileAttributes(mockMojiFile);
    assertThat(attributes.getDeviceCount(), is(paths.size()));
  }

  @Test
  public void testGetLength() throws IOException {
    when(mockMojiFile.length()).thenReturn(333L);
    LocalMojiFileAttributes attributes = new LocalMojiFileAttributes(mockMojiFile);
    assertThat(attributes.getLength(), is(333L));
  }

  @Test
  public void testGetFid() throws IOException {
    LocalMojiFileAttributes attributes = new LocalMojiFileAttributes(mockMojiFile);
    assertThat(attributes.getFid(), is(0L));
  }

  @Test
  public void testGetKey() throws IOException {
    when(mockMojiFile.getKey()).thenReturn("key");
    LocalMojiFileAttributes attributes = new LocalMojiFileAttributes(mockMojiFile);
    assertThat(attributes.getKey(), is("key"));
  }

  @Test
  public void testGetChecksum() throws IOException {
    InputStream stream = new ByteArrayInputStream("foo".getBytes(StandardCharsets.UTF_8));
    when(mockMojiFile.getInputStream()).thenReturn(stream);
    LocalMojiFileAttributes attributes = new LocalMojiFileAttributes(mockMojiFile);
    assertThat(attributes.getChecksum(), is("MD5:acbd18db4cc2f85cedef654fccc4a4d8"));
  }

}
