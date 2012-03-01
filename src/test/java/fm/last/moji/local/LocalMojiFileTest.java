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
