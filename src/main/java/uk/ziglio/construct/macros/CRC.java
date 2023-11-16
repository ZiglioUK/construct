package uk.ziglio.construct.macros;

import java.io.ByteArrayOutputStream;

import uk.ziglio.construct.Macros.CRCFunc;
import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.core.KeyFunc;
import uk.ziglio.construct.core.Subconstruct;
import uk.ziglio.construct.fields.StaticField;
import uk.ziglio.construct.lib.ByteBufferWrapper;
import uk.ziglio.construct.lib.Containers.Container;

public class CRC extends Subconstruct {
	CRCFunc crcfunc;
	KeyFunc keyfunc;
	StaticField crcfield;

	public CRC(Construct subcon, StaticField crcfield, CRCFunc crcfunc) {
		super(subcon);
		this.crcfield = crcfield;
		this.crcfunc = crcfunc;
	}

	public CRC(Construct subcon, KeyFunc keyfunc, CRCFunc crcfunc) {
		super(subcon);
		this.keyfunc = keyfunc;
		this.crcfunc = crcfunc;
	}

	@Override
	public Object _parse(ByteBufferWrapper stream, Container context) {
		byte[] allData= new byte[stream.remaining()];
		stream.get(allData, 0, stream.remaining());
		Container c = (Container)(subcon._parse(new ByteBufferWrapper().wrap(allData), context));
		ByteBufferWrapper crcStream = new ByteBufferWrapper().wrap(allData);
		byte[] data = _read_stream(crcStream, subcon._sizeof(c));

		int crcval;
		String name;
		if( crcfield != null ){
			crcval = (Integer) crcfield._parse(crcStream, context);
			name = crcfield.name;
		}
		else{
			crcval = (Integer) this.keyfunc.get(c);
			name = keyfunc.key;
		}
		
		boolean crccheck = crcfunc.check(data, crcval);

		// set CRC value to true/false
		c.set(name, crccheck); 

	  // also return invalid data
		if(!crccheck) {
			c.set( name + "_data", data ); 	
		}
		return c;
	}
	
	@Override
	public void _build(Object obj, ByteArrayOutputStream stream, Container context) {
		if( crcfield != null )
			 _buildCrcField(obj, stream, context);
		else
			 _buildKeyFuncField(obj, stream, context);
	}
	
	protected void _buildCrcField(Object obj, ByteArrayOutputStream stream, Container context) {
//		 TODO needs testing
		 ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
		 subcon._build(obj, stream2, context);
		 byte[] data = stream2.toByteArray();
		 int size;
		 if (obj instanceof Container){
			 size = _sizeof((Container) obj) - crcfield.sizeof();
		 } else {
			 size = _sizeof(context);
		 }
		 if( data.length != size )
		 throw new RuntimeException( "Wrong data length: " + data.length );
		
		 int crcval = crcfunc.compute(data);
		 _write_stream(stream, size, data);
		 crcfield.build_stream(crcval, stream);
	}

	protected void _buildKeyFuncField(Object obj, ByteArrayOutputStream stream, Container context) {
  	 // set initial CRC to 0 
	   ((Container)obj).set( keyfunc.key, 0 );
		
		 ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
		 subcon._build(obj, stream2, context);
		 byte[] data = stream2.toByteArray();
		 int size = _sizeof(context);
		 if( data.length != size )
		   throw new RuntimeException( "Wrong data length: " + data.length );

		 // the compute function will compute the CRC on the byte array
		 // and will also set the CRC bytes into the array itself
		 int crcval = crcfunc.compute(data);
		 _write_stream(stream, size, data);
	}
	
	@Override
	public int _sizeof(Container context) {
	  int size = subcon.sizeof(context);
	  if( this.crcfield != null ){
	    size += this.crcfield.sizeof();
	  }
	    
		return size;
	}
}