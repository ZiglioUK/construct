package uk.ziglio.construct.protocols;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static uk.ziglio.construct.lib.Binary.hexStringToByteArray;

import org.junit.Test;

import uk.ziglio.construct.lib.Containers.Container;
import uk.ziglio.construct.protocols.layer3.ipv4;

public class ProtocolTests {

  @Test
  public void ipv4CRCTest() {
    byte[] cap = hexStringToByteArray("4500003ca0e3000080116185c0a80205d474a126");
    
    Container c = ipv4.ipv4_header.parse(cap);
    assertTrue((Boolean) c.get("checksum"));
    byte[] ba = ipv4.ipv4_header.build(c);
    assertArrayEquals(cap, ba);
  }

}
