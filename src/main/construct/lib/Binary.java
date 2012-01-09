package construct.lib;

import java.io.UnsupportedEncodingException;

import construct.exception.ValueError;

public class Binary {

	static byte[][] _char_to_bin = new byte[256][8];
	static{
		for( int i = 0; i<256; i++)
		{
			char ch = (char)i;
			byte[] bin = int_to_bin(i, 8);
		    _char_to_bin[i] = bin;
		}
	}
	
	public static byte[] int_to_bin( int number ){
		return int_to_bin( number, 32 );
	}
	
	public static byte[] int_to_bin( int number, int width ){
	    if( number < 0 ){
	        number += (1 << width);
	    }
	    int i = width - 1;
	    byte[] bits = new byte[width];//["\x00"] * width;
	    while( i >= 0 ){
	        bits[i] = (byte)(number & 1);
	        number >>= 1;
	        i -= 1;
	    }
	    return bits;
	}
	
	public static int bin_to_int( byte[] bits ){
		return bin_to_int( bits, false );
	}
	
	public static int bin_to_int( byte[] bits, boolean signed ){
	    int number = 0;
	    int bias = 0;
	    int i = 0;
	    
	    if( bits.length == 0 )
	    	return 0;
	    
	    if( signed && bits[0] == (byte)1 ){
	    	i++;
	    	bias = 1 << (bits.length - 1);
	    }
	        
	    for( int j = i; j < bits.length; j++ )
	    {
	    	number <<= 1;
	        number |= bits[j];
	    }
	    
	    return number - bias;
	}
	
	public static byte[] swap_bytes( byte[] bits) {
		return swap_bytes( bits, 8 );
	}
	
	public static byte[] swap_bytes( byte[] bits, int bytesize ) {
	    int i = 0;
	    int l = bits.length;
	    byte[] output = new byte[l];
	    int j = output.length - 1;
	    while( i < l ){
	    	output[j] = bits[i];
	    	i++;
	        j--;
	    }
	    return output;
	}

	public static Encoder BinaryEncoder(){
		return new Encoder(){
  		public byte[] encode(String data) {
  	    return decode_bin(data.getBytes()).getBytes();
      }
		};
	}
	public static Decoder BinaryDecoder(){
		return new Decoder(){
      public String decode(byte[] data) {
  	    byte[] out;
        try {
	        out = encode_bin(new String(data, "ISO-8859-1"));
	  	    return new String(out, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
	        throw new RuntimeException( "UnsupportedEncodingException: " + e.getMessage() );
        }
      }
		};
	}
	public static byte[] encode_bin( String data ) {
		byte[] out = new byte[8 * data.length() ];
		for( int i = 0; i < data.length(); i++ ){
			char ch = data.charAt(i);
			byte[] conv = _char_to_bin[ ch ]; 
			System.arraycopy(conv, 0, out, i*8, 8);
		}
		return out;
	}
	
	public static String decode_bin( byte[] data ){
		if( (data.length & 7) != 0 )
			throw new ValueError("Data length must be a multiple of 8" );
		StringBuilder sb = new StringBuilder();
		
		for( int i = 0; i< data.length; i+=8 ){
			char ch = 0;
			for( int j = 0; j<8; j++){
				ch = (char)(ch<<1);
				ch |= data[i+j];
			}
			sb.append(ch);
		}
		return sb.toString();
	}

}
