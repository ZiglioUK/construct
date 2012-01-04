package construct;

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

		// public String _read_stream( InputStream stream, int length )
		public byte[] _read_stream(byte[] stream, int length) {
			if (length < 0)
				throw new FieldError("length must be >= 0 " + length);
			// try
			{
				// byte[] data = new byte[length];
				// int len = stream.read( data, 0, length );
				// byte[] data = stream.getBytes();
				int len = stream.length;
				if (len != length)
					throw new FieldError("expected " + length + " found " + len);
				return stream;
			}
			// catch( IOException e )
			// {
			// throw new FieldError( e.getMessage() );
			// }

			// len(data) != length:
			// raise FieldError("expected %d, found %d" % (length, len(data)))
		}

		// public void _write_stream( OutputStream stream, int length, byte[]
		// data)
		public void _write_stream(StringBuilder stream, int length, byte[] data) {
			if (length < 0)
				throw new FieldError("length must be >= 0 " + length);
			if (data.length != length)
				throw new FieldError("expected " + length + " found " + data.length);

			// try
			{
				// stream.write(data);
				stream.append(new String(data));
			}
			// catch( IOException e )
			// {
			// throw new FieldError( e.getMessage() );
			// }
		};

		/**
		 * Parse an in-memory buffer.
		 * 
		 * Strings, buffers, memoryviews, and other complete buffers can be parsed with this method.
		 * 
		 * @param data
		 */
		public Object parse(byte[] data) {
			// return parse_stream( new ByteArrayInputStream(data.getBytes()) );
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
		// public String parse_stream( InputStream stream )
		public Object parse_stream(byte[] stream) {
			return _parse(stream, new Container());
		}

		// abstract public String _parse( InputStream stream, Container context
		// );
		abstract public Object _parse(byte[] stream, Container context);

		public String build(String str) {
			return build(str.getBytes());
		}

		/**
		 * Build an object in memory.
		 * 
		 * @param obj
		 * @return
		 */
		public String build(byte[] obj) {
			// ByteArrayOutputStream stream = new ByteArrayOutputStream();
			StringBuilder stream = new StringBuilder();
			// build_stream( obj, stream );
			build_stream(obj, stream);

			// return new String( stream.toByteArray() );
			return stream.toString();
		}

		/**
		 * Build an object directly into a stream.
		 * 
		 * @param obj
		 * @param stream
		 */
		// public void build_stream( String obj, OutputStream stream)
		public void build_stream(byte[] obj, StringBuilder stream) {
			_build(obj, stream, new Container());
		}

		// abstract void _build( String obj, OutputStream stream, Container
		// context);
		protected abstract void _build(byte[] obj, StringBuilder stream, Container context);

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
		protected void _build(byte[] obj, StringBuilder stream, Container context) {
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

		public void _build(int obj, StringBuilder stream, Container context) {
			subcon._build(_encode(obj, context), stream, context);
		}

		abstract public int _decode(byte[] obj, Container context);

		abstract public byte[] _encode(int obj, Container context);
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
		protected void _build(byte[] obj, StringBuilder stream, Container context) {
			_write_stream(stream, length, obj);
		}/*
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
		public void _build(byte[] obj, StringBuilder stream, Container context) {
			_write_stream(stream, length, obj);
			// def _build(self, obj, stream, context):
			// try:
			// _write_stream(stream, self.length, self.packer.pack(obj))
			// except Exception, ex:
			// raise FieldError(ex)
		}

		public byte[] build(Object... args) {
			return packer.pack(args);
		}

	}

}
