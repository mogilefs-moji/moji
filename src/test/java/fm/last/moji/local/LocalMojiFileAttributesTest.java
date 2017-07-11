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
