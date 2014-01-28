package com.sirtrack.construct.lib;
import com.sirtrack.construct.Core.ValueError;
import com.sirtrack.construct.lib.*;

/**
 * @author zigliolie
 *
 */
/**
 * @author zigliolie
 *
 */
public class Binary {

	public static final byte[][] _char_to_bin = new byte[256][8];
	static{
		for( int i = 0; i<256; i++)
		{
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
	
	
	/**
	 * @param bits
	 * @param bytesize
	 * @return it swaps two bytes in a bit array, max 2 byte long
	 */
	public static byte[] swap_bytes( byte[] bits, int bytesize ) {
	    final int L = bits.length;
	    byte[] output = new byte[L];
	    
      int j = L - bytesize;
	    for( int i = 0; i < L; i++, j++){
	      if( j == L ){
	        j = 0;
	      }
	      output[j] = bits[i];
	    }
	    return output;
	}

	public static Encoder BinaryEncoder(){
		return new Encoder(){
  		public byte[] encode(String data) {
  	    return decode_bin(data.getBytes());
      }
		};
	}
	public static Decoder BinaryDecoder(){
		return new Decoder(){
      public byte[] decode(byte[] data) {
	        return encode_bin( data );
      }
		};
	}
	public static byte[] encode_bin( byte[] data ) {
		byte[] out = new byte[8 * data.length ];
		for( int i = 0; i < data.length; i++ ){
			int ch = (int)data[i];
			if(ch<0)
				ch = 256 + ch;
			byte[] conv = _char_to_bin[ ch ];
			System.arraycopy(conv, 0, out, i*8, 8);
		}
		return out;
	}
	
	public static byte[] decode_bin( byte[] data ){
		if( (data.length & 7) != 0 )
			throw new ValueError("Data length must be a multiple of 8" );
		byte[] sb = new byte[data.length/8];
		
		for( int i = 0; i< data.length; i+=8 ){
			byte ch = 0;
			for( int j = 0; j<8; j++){
				ch = (byte)(ch<<1);
				ch |= data[i+j];
			}
			sb[i/8] = ch;
		}
		return sb;
	}

	public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                             + Character.digit(s.charAt(i+1), 16));
    }
    return data;
  }

  static final char[] HEXES = new char[]{ '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'}; 
	
	public static String byteArrayToHexString(byte[] raw, int linesize){
  	if ( raw == null ) 
  		return null; 
  	
  	final StringBuilder hex = new StringBuilder( 2 * raw.length ); 

		hex.append( '\n');
    int i = 0;
  	for ( final byte b : raw ) { 
  		hex.append(HEXES[(b >> 4) & 0xF]) 
  		   .append(HEXES[(b ) & 0xF]); 
  		i++;
			hex.append( ' ');
  		if( i == linesize ){
  			hex.append( '\n');
  			i = 0;
  		}
  	} 
		hex.append( '\n');
  	return hex.toString(); 
	}
  
	public static String byteArrayToHexString(byte[] raw )
	{
  	if ( raw == null ) 
  		return null; 
  	
  	final StringBuilder hex = new StringBuilder( 2 * raw.length ); 
  	
  	for ( final byte b : raw ) { 
  		hex.append(HEXES[(b >> 4) & 0xF]) 
  		   .append(HEXES[(b ) & 0xF]); 
  	} 
  	return hex.toString(); 
	}	
}
