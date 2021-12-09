/**
 * User Datagram Protocol (TCP/IP protocol stack)
*/
package uk.ziglio.construct.protocols.layer4;

import static uk.ziglio.construct.Macros.UBInt16;
import static uk.ziglio.construct.lib.Binary.byteArrayToHexString;
import static uk.ziglio.construct.lib.Binary.hexStringToByteArray;

import uk.ziglio.construct.Adapters.ExprAdapter;
import uk.ziglio.construct.core.Struct;
import uk.ziglio.construct.core.Value;
import uk.ziglio.construct.lib.Containers.Container;


public class udp2 {
	
  static class udp_header extends Struct{ 
	    class HeaderLength extends Value<Integer> {
	    	HeaderLength( String name ) {
	    		super( name, ctx->8 );
	    	}
	    }

	  	class PayloadLength extends ExprAdapter<Integer, Integer> {
	  		PayloadLength( String name) { 
	  			super( new UBInt16(name),
	    	    		(obj, context)-> obj + 8,
	    	    		(obj, context)-> obj - 8
	    	    		);
	  		}
	  	}
	    
	    HeaderLength header_length;
	    UBInt16 source;
  		UBInt16 destination; 
  		PayloadLength payload_length;
	    UBInt16 checksum;
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

//  	byte[] ba2 = udph.build();
//  	String out2 = byteArrayToHexString(ba);
//  	System.out.println( out2 );
//  	System.out.println(out2.equals(in.toUpperCase())? "OK": "ERROR");
  }
  
}
