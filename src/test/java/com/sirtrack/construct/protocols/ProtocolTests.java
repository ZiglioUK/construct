package com.sirtrack.construct.protocols;

import static com.sirtrack.construct.lib.Binary.hexStringToByteArray;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.sirtrack.construct.lib.Containers.Container;
import com.sirtrack.construct.protocols.layer3.ipv4;

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
