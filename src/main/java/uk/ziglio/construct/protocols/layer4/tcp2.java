/**
 * Transmission Control Protocol (TCP/IP protocol stack)
*/
package uk.ziglio.construct.protocols.layer4;

import static uk.ziglio.construct.Macros.Flag;
import static uk.ziglio.construct.Macros.Padding;
import static uk.ziglio.construct.Macros.UBInt16;
import static uk.ziglio.construct.Macros.UBInt32;
import static uk.ziglio.construct.lib.Binary.byteArrayToHexString;
import static uk.ziglio.construct.lib.Binary.hexStringToByteArray;

import uk.ziglio.construct.Adapters.ExprAdapter;
import uk.ziglio.construct.Macros.BitField;
import uk.ziglio.construct.Macros.BitStruct;
import uk.ziglio.construct.annotations.len;
import uk.ziglio.construct.core.Struct;
import uk.ziglio.construct.fields.MetaField;
import uk.ziglio.construct.lib.Containers.Container;


public class tcp2 {

	public static class tcp_header extends Struct{
		public UBInt16 source;
		public UBInt16 destination;
		public UBInt32 seq;
		public UBInt32 ack;
	
		public Header header;

		public UBInt16 window;
		public UBInt16 checksum;
		public UBInt16 urgent;
    
		public Options options;
	}
	
	public static class Header extends BitStruct {
//		public @len(4) BitField header_length;
		public HeaderLength header_length;
		public @len(3) Padding padding;
		public Flag ns;
		public Flag cwr;
		public Flag ece;
		public Flag urg;
		public Flag ack;
		public Flag psh;
		public Flag rst;
		public Flag syn;
		public Flag fin;
	}
	
	public static class Options extends MetaField {
		public Options(String name) {
			super(name, ctx -> (Integer)ctx.get("header_length") - 20);
		}
	}

	public static class HeaderLength extends ExprAdapter<Integer, Integer> {
		public HeaderLength( String name) { 
			super( new BitField(name, 4),
  	    		(obj, context)-> obj / 4,
  	    		(obj, context)-> obj * 4
  	    		);
		}
	}

  public static void main(String[] args) {
  	String in = "0db5005062303fb21836e9e650184470c9bc0000";
  	byte[] cap = hexStringToByteArray(in); 
  	tcp_header tcph = new tcp_header();
  	Container c = tcph.parse(cap);
  	System.out.println(c);
  	byte[] ba = tcph.build(c);
  	String out = "byteArrayToHexString(ba)";
  	System.out.println(out.equals(in.toUpperCase())? "OK": "ERROR");
  }
  
}
