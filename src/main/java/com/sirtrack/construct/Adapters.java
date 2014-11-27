package com.sirtrack.construct;

import com.sirtrack.construct.lib.*;
import com.sirtrack.construct.lib.Containers.*;

import static com.sirtrack.construct.Core.*;
import static com.sirtrack.construct.Macros.Field;
import static com.sirtrack.construct.lib.Binary.*;

import com.sirtrack.construct.Core.Construct;
import com.sirtrack.construct.Core.Subconstruct;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Adapters {
  public static class BitIntegerError extends RuntimeException {
    public BitIntegerError(String string) {
      super(string);
    }
  }

  public static class PaddingError extends RuntimeException {
    public PaddingError(String string) {
      super(string);
    }
  }

  public static class MappingError extends RuntimeException {
    public MappingError(String string) {
      super(string);
    }
  }

  public static class ConstError extends RuntimeException {
    public ConstError(String string) {
      super(string);
    }
  }

  public static class ValidationError extends RuntimeException {
    public ValidationError(String string) {
      super(string);
    }
  }

  /**
   * """ Adapter for bit-integers (converts bitstrings to integers, and vice
   * versa). See BitField.
   * 
   * Parameters: subcon - the subcon to adapt width - the size of the subcon, in
   * bits swapped - whether to swap byte order (little endian/big endian).
   * default is False (big endian) signed - whether the value is signed (two's
   * complement). the default is False (unsigned) bytesize - number of bits per
   * byte, used for byte-swapping (if swapped). default is 8. """
   * 
   */
  static public Adapter BitIntegerAdapter(Construct subcon, final int width) {
    return new BitIntegerAdapter(subcon, width, false, false, 8);
  }

  static public Adapter BitIntegerAdapter(Construct subcon, final int width,
      final boolean swapped, final boolean signed) {
    return new BitIntegerAdapter(subcon, width, swapped, signed, 8);
  }

  static public Adapter BitIntegerAdapter(Construct subcon, final int width,
      final boolean swapped, final boolean signed, final int bytesize) {
    return new BitIntegerAdapter(subcon, width, swapped, signed, bytesize);
  }

  static public class BitIntegerAdapter extends Adapter {
    final int width;
    final boolean swapped;
    final boolean signed;
    final int bytesize;

    public BitIntegerAdapter(Construct subcon, final int width,
        final boolean swapped, final boolean signed, final int bytesize) {
      super(subcon);
      this.width = width;
      this.swapped = swapped;
      this.signed = signed;
      this.bytesize = bytesize;
    }

    public Object encode(Object obj, Container context) {
      int intobj = (Integer) obj;
      if (intobj < 0 && !signed) {
        throw new BitIntegerError(
            "object is negative, but field is not signed " + intobj);
      }
      byte[] obj2 = int_to_bin(intobj, width);
      if (swapped) {
        obj2 = swap_bytes(obj2, bytesize);
      }
      return obj2;
    }

    public Object decode(Object obj, Container context) {
      byte[] ba = (byte[]) obj;
      if (swapped) {
        ba = swap_bytes(ba, bytesize);
      }
      return bin_to_int(ba, signed);
    }

  }

  /**
   * @param subcon
   *          the subcon to map
   * @param decoding
   *          the decoding (parsing) mapping (a dict)
   * @param encoding
   *          the encoding (building) mapping (a dict)
   * @param decdefault
   *          the default return value when the object is not found in the
   *          decoding mapping. if no object is given, an exception is raised.
   *          if `Pass` is used, the unmapped object will be passed as-is
   * @param encdefault
   *          the default return value when the object is not found in the
   *          encoding mapping. if no object is given, an exception is raised.
   *          if `Pass` is used, the unmapped object will be passed as-is
   * @return Adapter that maps objects to other objects. See SymmetricMapping
   *         and Enum.
   */
  static public <T> MappingAdapter<T> MappingAdapter(Construct subcon,
      Container decoding, Container encoding, T decdefault,
      T encdefault) {
    return new MappingAdapter<T>(subcon, decoding, encoding, decdefault,
        encdefault);
  }

  // public static int getLength( Object obj ){
  // if( obj instanceof String)
  // return ((String)obj).length();
  // else if( obj instanceof Arrays )
  //
  // }
  /**
   * @param subcon
   *          the subcon returning a length-value pair
   * @return Adapter for length-value pairs. It extracts only the value from the
   *         pair, and calculates the length based on the value. See
   *         PrefixedArray and PascalString.
   */
  public static Adapter LengthValueAdapter(Construct subcon) {
    return new Adapter(subcon) {
      public Object encode(Object obj, Container context) {
        List l = new ArrayList();
        l.add(getDataLength(obj));
        l.add(obj);
        return l;
      }

      public Object decode(Object obj, Container context) {
        List l = (List) obj;
        return l.get(1);
      }
    };
  }

  /**
   * @param subcon
   *          the subcon to validate
   * @param value
   *          the expected value
   * @return Adapter for enforcing a constant value ("magic numbers"). When
   *         decoding, the return value is checked; when building, the value is
   *         substituted in. Example: Const(Field("signature", 2), "MZ")
   */
  static public Adapter Const(Construct subcon, final Object value) {
    return ConstAdapter(subcon, value);
  }

  /**
   * Adapter for hex-dumping strings. It returns a HexString, which is a string
   */
  static public Adapter HexDumpAdapter(Construct subcon) {
    return HexDumpAdapter(subcon, 16);
  }

  static public Adapter HexDumpAdapter(Construct subcon, final int linesize) {
    return new Adapter(subcon) {
      public Object encode(Object obj, Container context) {
        String str = (String) obj;
        str = str.replaceAll("[\n ]", "");
        return hexStringToByteArray(str);
      }

      public Object decode(Object obj, Container context) {
        return byteArrayToHexString((byte[]) obj, 16);
      }
    };
  }

  /**
   * @param subcon
   *          the subcon to validate
   * @param value
   *          the expected value
   * @return Adapter for enforcing a constant value ("magic numbers"). When
   *         decoding, the return value is checked; when building, the value is
   *         substituted in. Example: Const(Field("signature", 2), "MZ")
   */
  static public Adapter ConstAdapter(Construct subcon, final Object value) {
    return new Adapter(subcon) {
      public Object encode(Object obj, Container context) {
        if (obj == null || obj.equals(value))
          return value;
        else
          throw new ConstError("expected " + value + " found " + obj);
      }

      public Object decode(Object obj, Container context) {
        if (!obj.equals(value))
          throw new ConstError("expected " + value + " found " + obj);
        return obj;
      }
    };
  }

  /*
   * class MappingAdapter(Adapter): def _encode(self, obj, context): def
   * _decode(self, obj, context):
   */
  static public PaddingAdapter PaddingAdapter(Construct subcon) {
    return new PaddingAdapter(subcon);
  }
  static public PaddingAdapter PaddingAdapter(Construct subcon, byte pattern, boolean strict) {
    return new PaddingAdapter(subcon, pattern, strict);
  }
  /**
   * @param subcon
   *          the subcon to pad
   * @param pattern
   *          the padding pattern (character). default is "\x00"
   * @param strict
   *          whether or not to verify, during parsing, that the given padding
   *          matches the padding pattern. default is False (unstrict)
   * @return Adapter for padding.
   */
  static public class PaddingAdapter extends Adapter {
    byte pattern;
    boolean strict;

    public PaddingAdapter(Construct subcon, byte pattern, boolean strict) {
      super(subcon);
      this.pattern = pattern;
      this.strict = strict;
    }

    public PaddingAdapter(Construct subcon){
      this(subcon, (byte) 0x00, false);
    }
    
    public Object encode(Object obj, Container context) {
      byte[] out = new byte[_sizeof(context)];
      for (int i = 0; i < out.length; i++)
        out[i] = pattern;
      return out;
    }

    public Object decode(Object obj, Container context) {
      if (strict) {
        byte[] expected = new byte[_sizeof(context)];
        for (int i = 0; i < expected.length; i++)
          expected[i] = pattern;

        if (!obj.equals(expected))
          throw new PaddingError("Expected " + expected + " found " + obj);
      }
      return obj;
    }
  }

  /**
   * A generic adapter that accepts 'encoder' and 'decoder' as parameters. You
   * can use ExprAdapter instead of writing a full-blown class when only a
   * simple expression is needed. Example: ExprAdapter(UBInt8("foo"), encoder =
   * lambda obj, ctx: obj / 4, decoder = lambda obj, ctx: obj * 4, )
   * 
   * @param subcon
   *          the subcon to adapt
   * @param encoder
   *          a function that takes (obj, context) and returns an encoded
   *          version of obj
   * @param decoder
   *          a function that takes (obj, context) and returns an decoded
   *          version of obj
   */
  public static <T extends Construct,V> ExprAdapter<T,V> ExprAdapter(T subcon, AdapterEncoder<V> encoder, AdapterDecoder<V> decoder) {
    return new ExprAdapter<T,V>(subcon, encoder, decoder);
  };

  public static class ExprAdapter<T extends com.sirtrack.construct.Core.Construct, V> extends Adapter<T, V> {
    AdapterEncoder<V> encoder;
    AdapterDecoder<V> decoder;

    public ExprAdapter(T subcon, final AdapterEncoder<V> encoder, final AdapterDecoder<V> decoder) {
      super(subcon);
      this.encoder = encoder;
      this.decoder = decoder;
    }

    @Override
    public V decode(Object obj, Container context) {
      return decoder.decode(obj, context);
    }

    @Override
    public Object encode(V obj, Container context) {
      return encoder.encode(obj, context);
    }
  };

  /*
   * #============================================================================
   * === # validators
   * #==========================================================
   * =====================
   */
  /**
   * validates a condition on the encoded/decoded object. Override
   * _validate(obj, context) in deriving classes.
   */
  public static abstract class Validator extends Adapter {
    /**
     * @param subcon
     *          the subcon to validate
     */
    public Validator(Construct subcon) {
      super(subcon);
    }

    @Override
    public Object decode(Object obj, Container context) {
      if (!validate(obj, context))
        throw new ValidationError("invalid object" + obj);
      return obj;
    }

    @Override
    public Object encode(Object obj, Container context) {
      return decode(obj, context);
    }

    abstract public boolean validate(Object obj, Container context);

  }

  /**
   * >>> OneOf(UBInt8("foo"), [4,5,6,7]).parse("\\x05") 5 >>>
   * OneOf(UBInt8("foo"), [4,5,6,7]).parse("\\x08") Traceback (most recent call
   * last): ... construct.core.ValidationError: ('invalid object', 8) >>> >>>
   * OneOf(UBInt8("foo"), [4,5,6,7]).build(5) '\\x05' >>> OneOf(UBInt8("foo"),
   * [4,5,6,7]).build(9) Traceback (most recent call last): ...
   * construct.core.ValidationError: ('invalid object', 9) Validates that the
   * object is one of the listed values.
   * 
   * @param subcon
   *          object to validate
   * @param valids
   *          a set of valid values
   */
  public static Validator OneOf(Construct subcon, final List valids) {
    return new Validator(subcon) {
      @Override
      public boolean validate(Object obj, Container context) {
        return valids.contains(obj);
      }
    };
  }

  /***************************************************************
   * Miscellaneous Adapters
   ****************************************************************/

  /**
   * @param name
   * @return an IPv4 Address Adapter
   */
  public static Adapter IpAddress(String name) {
    return IpAddressAdapter(Field(name, 4));
  }

  /**
   * @param name
   * @return an IPv6 Address Adapter
   */
  public static Adapter Ipv6Address(String name) {
    return IpAddressAdapter(Field(name, 16));
  }

  public static Adapter IpAddressAdapter(Construct field) {
    return new Adapter(field) {
      public Object encode(Object obj, Container context) {
        return ((InetAddress) obj).getAddress();
      }

      public Object decode(Object obj, Container context) {
        try {
          return InetAddress.getByAddress((byte[]) obj);
        } catch (UnknownHostException e) {
          throw new RuntimeException(e);
        }
      }
    };
  };

  public static <T> BeanAdapter BeanAdapter(Class<T> clazz, Construct subcon) {
    return new BeanAdapter(clazz, subcon);
  }

  public static class BeanAdapter extends Adapter {
    Class clazz;

    public BeanAdapter(Class clazz, Construct subcon) {
      super(subcon);
      this.clazz = clazz;
    }

    <T> T newT() {
      try {
        Constructor<T> c = clazz.getDeclaredConstructor();
        c.setAccessible(true);
        return c.newInstance();
      } catch (NoSuchMethodException ex) {
        try {
          return (T) clazz.newInstance();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public Object decode(Object obj, Container context) {
      Container c = (Container) obj;
      Object t = newT();

      for (String name : c.<String> keys()) {
        try {
          Field f = clazz.getField((String) name);
          f.set(t, c.get(name));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      return t;
    }

    @Override
    public Object encode(Object obj, Container context) {
      Container c = new Container();

      for (Field f : clazz.getFields()) {
        String name = f.getName();
        try {
          c.set(name, clazz.getField(name).get(obj));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      return c;
    }

  }
}