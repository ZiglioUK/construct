/**
 * Transmission Control Protocol (TCP/IP protocol stack)
*/
package uk.ziglio.construct.protocols.layer4;

import static uk.ziglio.construct.Macros.Flag;
import static uk.ziglio.construct.Macros.Padding;
import static uk.ziglio.construct.Macros.UBInt16;
import static uk.ziglio.construct.Macros.UBInt32;
import static uk.ziglio.construct.lib.Binary.hexStringToByteArray;

import uk.ziglio.construct.adapters.BitField;
import uk.ziglio.construct.adapters.ExprAdapter;
import uk.ziglio.construct.annotations.len;
import uk.ziglio.construct.core.Struct;
import uk.ziglio.construct.fields.MetaField;
import uk.ziglio.construct.lib.Containers.Container;
import uk.ziglio.construct.macros.BitStruct;
import uk.ziglio.construct.macros.Flag;
import uk.ziglio.construct.macros.Padding;
import uk.ziglio.construct.macros.UBInt16;
import uk.ziglio.construct.macros.UBInt32;


public class tcp2 {

	static class tcp_header extends Struct{

		/* error here if I remove static, not sure why */
		static class HeaderBits extends BitStruct {
			class HeaderLength extends ExprAdapter<Integer, Integer> {
				HeaderLength( String name) { 
					super( new BitField(name, 4),
		  	    		(obj, context)-> obj / 4,
		  	    		(obj, context)-> obj * 4
		  	    		);
				}
			}
			
			HeaderLength header_length;
			@len(3) Padding padding;
			Flag ns;
			Flag cwr;
			Flag ece;
			Flag urg;
			Flag ack;
			Flag psh;
			Flag rst;
			Flag syn;
			Flag fin;
		}
		
		class Options extends MetaField {
			Options(String name) {
				super(name, ctx -> (Integer)ctx.get("header_length") - 20);
			}
		}

		UBInt16 source;
		UBInt16 destination;
		UBInt32 seq;
		UBInt32 ack;
		HeaderBits  hbits;
		UBInt16 window;
		UBInt16 checksum;
		UBInt16 urgent;
    
		Options options;
	}
	
  public static void main(String[] args) {
  	String in = "0db5005062303fb21836e9e650184470c9bc0000";
  	byte[] cap = hexStringToByteArray(in); 
  	tcp_header tcph = new tcp_header();
  	Container c = tcph.parse(cap);
  	System.out.println(c);
//  	byte[] ba = tcph.build(c);
//  	String out = "byteArrayToHexString(ba)";
//  	System.out.println(out.equals(in.toUpperCase())? "OK": "ERROR");
  }
  
}
