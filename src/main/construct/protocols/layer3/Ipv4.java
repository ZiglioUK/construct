package construct.protocols.layer3;

import static construct.Core.*;
import static construct.Macros.*;
import static construct.Adapters.*;
import static construct.lib.Containers.*;
import static construct.lib.Binary.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import construct.Core.AdapterDecoder;
import construct.Core.AdapterEncoder;
import construct.Core.ValueFunc;
import construct.lib.Containers.Container;

/**
 * Ethernet (TCP/IP protocol stack) 
*/
public class Ipv4 {

    public static Adapter IpAddress( String name ) {
    	return IpAddressAdapter( Field( name, 4));
    }
  
    public static Adapter IpAddressAdapter( Construct field ) {
  	return new Adapter( field ){
			@Override
      public Object encode(Object obj, Container context) {
				return ((InetAddress)obj).getAddress(); 
      }
      public Object decode( Object obj, Container context) {
      	try {
      		return InetAddress.getByAddress((byte[])obj);
        } catch (UnknownHostException e) {
	        throw new RuntimeException(e);
        }
      }
  	};
  };
  
  static Adapter ProtocolEnum( Construct subcon ){
  	return Enum( subcon,
  							"ICMP", 1,
  							"TCP", 6,
  							"UDP", 17 );
  };
  
  static Construct ipv4_header = 
  		Struct( "ip_header",
  						EmbeddedBitStruct(
    						Const(Nibble("version"), 4 ),
    						ExprAdapter( Nibble("header_length"),
  									 new AdapterEncoder() {
  										public Object encode(Object obj, Container context) {
  											return (Integer)obj / 4;
  										}
  									},
  									new AdapterDecoder() {
  										public Object decode(Object obj, Container context) {
  											return (Integer)obj * 4;
  										}
  									}
  							)
  						),
							BitStruct("tos",
					        Bits("precedence", 3),
					        Flag("minimize_delay"),
					        Flag("high_throuput"),
					        Flag("high_reliability"),
					        Flag("minimize_cost"),
					        Padding(1)
					    ),
					    UBInt16("total_length"),
					    Value("payload_length", new ValueFunc(){public Object get(Container ctx) {
					    	return (Integer)ctx.get("total_length") - (Integer)ctx.get("header_length");
					    }}),
					    UBInt16("identification"),
					    EmbeddedBitStruct(
					        Struct("flags",
					            Padding(1),
					            Flag("dont_fragment"),
					            Flag("more_fragments")
					        ),
					        Bits("frame_offset", 13)
					    ),
					    UBInt8("ttl"),
					    ProtocolEnum(UBInt8("protocol")),
					    UBInt16("checksum"),
					    IpAddress("source"),
					    IpAddress("destination"),
					    Field("options", new LengthFunc(){
                public int length(Container context) {
	                return (Integer)context.get("header_length") - 20;
                }
					    })
  					);

  public static void main(String[] args) {
  	byte[] cap = hexStringToByteArray("4500003ca0e3000080116185c0a80205d474a126"); 
  	Container c = ipv4_header.parse(cap);
  	System.out.println(c);
  	byte[] ba = ipv4_header.build(c);
  	System.out.println( byteArrayToHexString(ba) );
  }
  
}
