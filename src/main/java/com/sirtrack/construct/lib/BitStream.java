package com.sirtrack.construct.lib;

import java.io.ByteArrayOutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.sirtrack.construct.Core.ValueError;
import static com.sirtrack.construct.Core._read_stream;
import static com.sirtrack.construct.lib.Binary.encode_bin;

public class BitStream {

	public static class BitStreamReader extends ByteBufferWrapper{

		ByteBufferWrapper substream;

		int total_size = 0;
		byte[] buffer;
		
		public BitStreamReader(){
			super();
		}
		
		public ByteBufferWrapper init( ByteBufferWrapper substream ){
			this.substream = substream;
			this.buffer = new byte[ 8*substream.remaining() ];
			this.total_size = 0;
			bb = ByteBuffer.wrap(buffer);
			
			return this;
		}

		public void close(){
      if( total_size % 8 != 0 ){
        throw new ValueError( "total size of read data must be a multiple of 8: " + total_size );
      }
		}
		
		@Override
	  public int remaining() {
		  return bb.remaining();
    }

		@Override
		public int position() {
      return substream.position();
	  }

		@Override
		public void position(int pos) {
//      self.buffer = ""
       total_size = 0;
//      self.substream.seek(pos, whence)
       substream.position(pos);
	  }

		@Override
		public byte get(){
			byte[] d = new byte[1];
			get( d, 0, 1);
			return d[0];
		}

		@Override
	  public ByteBufferWrapper get( byte[] dst, int offset, int length ) {
      if( length < 0 )
        throw new ValueError("length cannot be negative");

    int l = bb.position();
    int bytes;
      
    if( length == 0 ){
//        dst = "";
    }
    else if( length <= l ){
        bb.get( dst, 0, length );  
    }
    else {
        length -= l;
        bytes = length / 8;
        if(( length & 7 ) != 0 ){
            bytes += 1;
        }
        
        byte[] buf = encode_bin(_read_stream( substream, bytes ));
        
        bb.position(0);
        bb.put(buf);
        bb.position(0);
        bb.get( dst, 0, length );  

        total_size += buf.length;
    }
//    total_size += len(dst);
    
    return this;
	  }
		
	}
	
	public static class BitStreamWriter{

		public ByteArrayOutputStream init(ByteArrayOutputStream stream) {
	    // TODO Auto-generated method stub
	    return null;
    }

		public void close() {
	    // TODO Auto-generated method stub
	    
    }
	}
	/*
class BitStreamReader(object):

class BitStreamWriter(object):

    __slots__ = ["substream", "buffer", "pos"]

    def __init__(self, substream):
        self.substream = substream
        self.buffer = []
        self.pos = 0

    def close(self):
        self.flush()

    def flush(self):
        bytes = decode_bin("".join(self.buffer))
        self.substream.write(bytes)
        self.buffer = []
        self.pos = 0

    def tell(self):
        return self.substream.tell() + self.pos // 8

    def seek(self, pos, whence = 0):
        self.flush()
        self.substream.seek(pos, whence)

    def write(self, data):
        if not data:
            return
        if type(data) is not str:
            raise TypeError("data must be a string, not %r" % (type(data),))
        self.buffer.append(data)
	 */
}
