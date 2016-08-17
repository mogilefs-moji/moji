package fm.last.moji.impl;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MojiFileAttributesImplTest {

  @Test
  public void typical() {
    Map<String, String> valueMap = new HashMap<String, String>();
    valueMap.put("domain", "domain2");
    valueMap.put("key", "key2");
    valueMap.put("class", "default");
    valueMap.put("length", "100");
    valueMap.put("devcount", "2");
    valueMap.put("fid", "5645");

    MojiFileAttributesImpl attributes = new MojiFileAttributesImpl(valueMap);
    assertEquals(attributes.getDomain(), "domain2");
    assertEquals(attributes.getKey(), "key2");
    assertEquals(attributes.getStorageClass(), "default");
    assertEquals(attributes.getLength(), 100L);
    assertEquals(attributes.getDeviceCount(), 2);
    assertEquals(attributes.getFid(), 5645L);
  }

}
