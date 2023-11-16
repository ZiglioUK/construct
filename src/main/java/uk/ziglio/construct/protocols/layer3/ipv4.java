package uk.ziglio.construct.protocols.layer3;

import static uk.ziglio.construct.Core.*;
import static uk.ziglio.construct.adapters.Adapters.*;
import static uk.ziglio.construct.lib.Binary.*;
import static uk.ziglio.construct.lib.Checksum.calculateChecksum;
import static uk.ziglio.construct.macros.Macros.*;

import uk.ziglio.construct.*;
import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.lib.Containers.*;

/**
 * Ethernet (TCP/IP protocol stack)
 */
public class ipv4 {

  static Adapter ProtocolEnum( Construct subcon ) {
    return Enum( subcon, 
                 "ICMP", 1, 
                 "TCP", 6, 
                 "UDP", 17
                );
  };

  static CRCFunc CRC16 = new CRCFunc() {
    public boolean check(byte[] bytes, int crcval) {
      int cs = compute(bytes);
      return crcval == cs;
    }

    @Override
    public int compute(byte[] bytes) {
      // zero the checksum bytes
      bytes[10] = 0;
      bytes[11] = 0;

      int cs = (int) calculateChecksum(bytes);

      // set the checksum bytes when building
      bytes[10] = (byte) (cs >> 8);
      bytes[11] = (byte) (cs & 0xFF);

      return cs;
    }
  };

  public static Construct ipv4_header = Struct( "ip_header",
   
    CRC( EmbeddedStruct(  
        EmbeddedBitStruct( 
          Const( Nibble("version"), 4 ),
        
          ExprAdapter( 
            Nibble("header_length"), 
            ( obj, context ) -> (int) obj / 4,
            ( obj, context ) -> (int) obj * 4 )
        ),
  
        BitStruct( "tos", 
          Bits("precedence", 3), 
          Flag("minimize_delay"),
          Flag("high_throuput"), 
          Flag("high_reliability"),
          Flag("minimize_cost"), 
          Padding(1)
        ),
        
        UBInt16("total_length"),
  
        Value("payload_length", ctx -> (Integer) ctx.get("total_length") - (Integer) ctx.get("header_length") ),
  
        UBInt16("identification"),
          
        EmbeddedBitStruct(
          Struct( "flags", 
            Padding(1), 
            Flag("dont_fragment"),
            Flag("more_fragments")
          ), 
          Bits("frame_offset", 13)
        ),
  
        UBInt8("ttl"), 
        ProtocolEnum( UBInt8("protocol") ),
        UBInt16("checksum"), 
        IpAddress("source"),
        IpAddress("destination")
      ), 
    
      KeyVal("checksum"), 
      
      CRC16
    ),

    Field("options", context -> (Integer) context.get("header_length") - 20)
  );

  static byte[] cap = hexStringToByteArray("4500003ca0e3000080116185c0a80205d474a126");

  public static void main(String[] args) {
    Container c = ipv4_header.parse(cap);
    System.out.println(c);
    byte[] ba = ipv4_header.build(c);
    System.out.println(byteArrayToHexString(ba));
  }

}
