/**
 * User Datagram Protocol (TCP/IP protocol stack)
*/
package uk.ziglio.construct.protocols.layer4;

import static uk.ziglio.construct.Adapters.ExprAdapter;
import static uk.ziglio.construct.Core.Struct;
import static uk.ziglio.construct.Core.Value;
import static uk.ziglio.construct.Macros.UBInt16;
import static uk.ziglio.construct.lib.Binary.byteArrayToHexString;
import static uk.ziglio.construct.lib.Binary.hexStringToByteArray;

import uk.ziglio.construct.Adapters.ExprAdapter;
import uk.ziglio.construct.Macros.BitField;
import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.core.Struct;
import uk.ziglio.construct.core.Value;
import uk.ziglio.construct.lib.Containers.Container;


public class udp2 {
  public static class udp_header extends Struct{ 
  	  public HeaderLength header_length;
	    public UBInt16 source;
  		public UBInt16 destination; 
  		public PayloadLength payload_length;
	    public UBInt16 checksum;
  }

  public static class HeaderLength extends Value<Integer> {
  	public HeaderLength( String name ) {
  		super( name, ctx->8 );
  	}
  }
  
	public static class PayloadLength extends ExprAdapter<Integer, Integer> {
		public PayloadLength( String name) { 
			super( new UBInt16(name),
  	    		(obj, context)-> obj + 8,
  	    		(obj, context)-> obj - 8
  	    		);
		}
	}
  
  public static void main(String[] args) {
  	String in = "0bcc003500280689";
  	byte[] cap = hexStringToByteArray(in); 
  	udp_header udph = new udp_header();
  	Container c = udph.parse(cap);
  	System.out.println(c);
  	byte[] ba = udph.build(c);
  	String out = byteArrayToHexString(ba);
  	System.out.println( out );
  	System.out.println(out.equals(in.toUpperCase())? "OK": "ERROR");
  }
  
}
