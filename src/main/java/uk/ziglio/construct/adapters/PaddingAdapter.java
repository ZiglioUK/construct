package uk.ziglio.construct.adapters;

import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.errors.PaddingError;
import uk.ziglio.construct.lib.Containers.Container;

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
  public class PaddingAdapter extends Adapter {
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