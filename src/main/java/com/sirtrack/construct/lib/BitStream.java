package com.sirtrack.construct.lib;

import static com.sirtrack.construct.Core._read_stream;
import static com.sirtrack.construct.lib.Binary.encode_bin;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import com.sirtrack.construct.Core.ValueError;

public class BitStream {

	public static class BitStreamReader extends ByteBufferWrapper{

		ByteBufferWrapper substream;
		int total_size = 0;

		public BitStreamReader(){
			super();
		}
		
		public ByteBufferWrapper init( ByteBufferWrapper substream ){
			this.substream = substream;
			this.total_size = 0;
			return this;
		}

		public void close(){
      if( total_size % 8 != 0 ){
        throw new ValueError( "total size of read data must be a multiple of 8: " + total_size );
      }
		}
		
		@Override
	  public int remaining() {
			int r = substream.remaining() * 8;
			if( bb != null && bb.remaining()>0 )
				r += bb.remaining();
		  return r;
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

      if( length == 0 )
      	return this;
      
    if( bb == null ){
      int bytes = length / 8;
      if(( length  & 7 ) != 0 ){
          bytes += 1;
      }
    	bb = ByteBuffer.wrap(encode_bin(_read_stream( substream, bytes )));
      bb.get( dst, 0, length );  
      total_size += length;
    }
    else {
      int l = bb.remaining();
    	
      if( length <= l ){
      	bb.get( dst, 0, length );  
        total_size += length;
      }
      else {
        bb.get( dst, 0, l );  
        total_size += l;
        length -= l;
        int bytes = length / 8;
        if(( length  & 7 ) != 0 ){
            bytes += 1;
        }
      	bb = ByteBuffer.wrap(encode_bin(_read_stream( substream, bytes )));
        bb.get( dst, l, length );  
        total_size += length;
      }
    }

    if( bb.remaining() == 0 )
    	bb = null;
    
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
