package construct;

import java.util.ArrayList;
import java.util.List;

import construct.exception.FieldError;
import construct.exception.SizeofError;
import construct.exception.ValueError;
import construct.lib.Container;

public class Core {

	/*
	 * #===============================================================================
	 * # abstract constructs
	 * #===============================================================================
	 */

/**
  The mother of all constructs.

  This object is generally not directly instantiated, and it does not
  directly implement parsing and building, so it is largely only of interest
  to subclass implementors.

  The external user API:

   * parse()
   * parse_stream()
   * build()
   * build_stream()
   * sizeof()

  Subclass authors should not override the external methods. Instead,
  another API is available:

   * _parse()
   * _build()
   * _sizeof()

  There is also a flag API:

   * _set_flag()
   * _clear_flag()
   * _inherit_flags()
   * _is_flag()

  And stateful copying:

   * __getstate__()
   * __setstate__()

  Attributes and Inheritance
  ==========================

  All constructs have a name and flags. The name is used for naming struct
  members and context dictionaries. Note that the name can either be a
  string, or None if the name is not needed. A single underscore ("_") is a
  reserved name, and so are names starting with a less-than character ("<").
  The name should be descriptive, short, and valid as a Python identifier,
  although these rules are not enforced.

  The flags specify additional behavioral information about this construct.
  Flags are used by enclosing constructs to determine a proper course of
  action. Flags are inherited by default, from inner subconstructs to outer
  constructs. The enclosing construct may set new flags or clear existing
  ones, as necessary.

  For example, if FLAG_COPY_CONTEXT is set, repeaters will pass a copy of
  the context for each iteration, which is necessary for OnDemand parsing.
*/
	static public abstract class Construct {
		byte[] conflags;
		public String name;

		public Construct(String name) {
			if (name.equals("_") || name.startsWith("<"))
				throw new FieldError("reserved name " + name); // raise
			// ValueError

			this.name = name;
		}

		public Construct(String name, byte[] flags) {
			if (name.equals("_") || name.startsWith("<"))
				throw new FieldError("reserved name " + name); // raise
			// ValueError

			this.name = name;
			this.conflags = flags;
		}

		public byte[] _read_stream(byte[] stream, int length) {
			if (length < 0)
				throw new FieldError("length must be >= 0 " + length);
			{
				int len = stream.length;
				if (len != length)
					throw new FieldError("expected " + length + " found " + len);
				return stream;
			}
		}

		static public int getDataLength( Object data ){
			if( data instanceof String)
				return ((String)data).length();
			else if( data instanceof Integer )
				return Integer.SIZE/8;
			else if( data instanceof byte[] )
				return ((byte[])data).length;
			else return -1;
		}

		static public void appendDataStream( StringBuilder stream, Object data ){
			if( data instanceof String)
				stream.append((String)data);
			else if( data instanceof Integer )
				stream.append((Integer)data);
			else if( data instanceof byte[] )
				stream.append( new String((byte[])data));
			else throw new ValueError( "Can't append data " + data);
		}
		
		public void _write_stream( StringBuilder stream, int length, Object data) {
			if (length < 0)
				throw new FieldError("length must be >= 0 " + length);

			int datalength = getDataLength( data );
			if ( length != datalength )
				throw new FieldError("expected " + length + " found " + datalength);

			appendDataStream( stream, data );
		};

		/**
		 * Parse an in-memory buffer.
		 * 
		 * Strings, buffers, memoryviews, and other complete buffers can be parsed with this method.
		 * 
		 * @param data
		 */
		public Object parse(byte[] data) {
			return parse_stream(data);
		}

		public Object parse(String text) {
			return parse_stream(text.getBytes());
		}

		/**
		 * Parse a stream.
		 * 
		 * Files, pipes, sockets, and other streaming sources of data are handled by this method.
		 */
		public Object parse_stream(byte[] stream) {
			return _parse(stream, new Container());
		}

		abstract public Object _parse(byte[] stream, Container context);

		/**
		 * Build an object in memory.
		 * 
		 * @param obj
		 * @return
		 */
		public byte[] build( Object obj) {
			StringBuilder stream = new StringBuilder();
			build_stream(obj, stream);

			return stream.toString().getBytes();
		}

		/**
		 * Build an object directly into a stream.
		 * 
		 * @param obj
		 * @param stream
		 */
		public void build_stream( Object obj, StringBuilder stream) {
			_build(obj, stream, new Container());
		}

		// abstract void _build( String obj, OutputStream stream, Container
		// context);
		protected abstract void _build( Object obj, StringBuilder stream, Container context);

		/**
		 * Calculate the size of this object, optionally using a context. Some constructs have no fixed size and can only know their size for a given hunk of data;
		 * these constructs will raise an error if they are not passed a context.
		 * 
		 * @param context
		 *          contextual data
		 * @return the length of this construct
		 */
		public int sizeof(Container context) {
			if (context == null) {
				context = new Container();
			}
			try {
				return _sizeof(context);
			} catch (Exception e) {
				throw new SizeofError(e);
			}
		}

		public int sizeof() {
			return sizeof(null);
		}

		/* abstract */public int _sizeof(Container context) {
			throw new SizeofError("Raw Constructs have no size!");
		}
	}

	/**
	 * """ Abstract subconstruct (wraps an inner construct, inheriting it's name and flags). """
	 * 
	 */
	public static abstract class Subconstruct extends Construct {

		protected Construct subcon;

		/**
		 * @param name
		 * @param subcon
		 *          the construct to wrap
		 */
		public Subconstruct(Construct subcon) {
			super(subcon.name, subcon.conflags);
			this.subcon = subcon;
		}

		@Override
		public Object _parse(byte[] stream, Container context) {
			return subcon._parse(stream, context);
		}

		@Override
		protected void _build( Object obj, StringBuilder stream, Container context) {
			subcon._build(obj, stream, context);
		}

		// def _sizeof(self, context):
		// return self.subcon._sizeof(context)
	}

	/**
	 * """ Abstract adapter: calls _decode for parsing and _encode for building. """
	 * 
	 */
	public static abstract class Adapter extends Subconstruct {
		/**
		 * @param name
		 * @param subcon
		 *          the construct to wrap
		 */
		public Adapter(Construct subcon) {
			super(subcon);
		}

		@Override
		public Object _parse(byte[] stream, Container context) {
			return _decode((byte[]) subcon._parse(stream, context), context);
		}

		public void _build(Object obj, StringBuilder stream, Container context) {
			subcon._build(_encode(obj, context), stream, context);
		}

		abstract public Object _decode(Object obj, Container context);
		abstract public Object _encode(Object obj, Container context);
	}

	/*
	 * ===============================================================================
	 * * Fields
	 * ===============================================================================
	 */

	/**
	 * A fixed-size byte field.
	 */
	public static class StaticField extends Construct {
		int length;

		/**
		 * @param name
		 *          field name
		 * @param length
		 *          number of bytes in the field
		 */
		public StaticField(String name, int length) {
			super(name);
			this.length = length;
		}

		@Override
		public Object _parse(byte[] stream, Container context) {
			return _read_stream(stream, length);
		}

		@Override
		protected void _build( Object obj, StringBuilder stream, Container context) {
			_write_stream(stream, length, obj);
		}
		
		/*
		  * public int _sizeof( Container context ){ return length; }
		  */
	}

	/**
	 * A field that uses ``struct`` to pack and unpack data.
	 * 
	 * See ``struct`` documentation for instructions on crafting format strings.
	 */
	public static class FormatField extends StaticField {
		int length;
		Packer packer;

		/**
		 * @param name
		 *          name of the field
		 * @param endianness
		 *          : format endianness string; one of "<", ">", or "="
		 * @param format
		 *          : a single format character
		 */
		public FormatField(String name, char endianity, char format) {
			super(name, 0);

			if (endianity != '>' && endianity != '<' && endianity != '=')
				throw new ValueError("endianity must be be '=', '<', or '>' " + endianity);

			packer = new Packer(endianity, format);

			this.name = name;
			this.length = packer.length();

		}

		@Override
		// public String _parse( InputStream stream, Container context )
		public Object _parse(byte[] stream, Container context) {
			try {
				// return packer.unpack( _read_stream(stream, length) )[0];
				return (packer.unpack(stream)[0]);
			} catch (Exception e) {
				throw new FieldError(e);
			}
		}

		@Override
		// void _build( String obj, OutputStream stream, Container context)
		public void _build( Object obj, StringBuilder stream, Container context) {
			_write_stream(stream, length, obj);
		}

		public byte[] build(Object... args) {
			return packer.pack(args);
		}

	}

}
