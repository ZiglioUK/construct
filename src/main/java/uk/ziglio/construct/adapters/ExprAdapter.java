package uk.ziglio.construct.adapters;

import uk.ziglio.construct.Adapter;
import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.interfaces.AdapterDecoder;
import uk.ziglio.construct.interfaces.AdapterEncoder;
import uk.ziglio.construct.lib.Containers.Container;

public class ExprAdapter<V, T> extends Adapter<V, T> {
    AdapterEncoder<V,T> encoder;
    AdapterDecoder<T,V> decoder;

    public ExprAdapter(Construct subcon, final AdapterEncoder<V,T> encoder, final AdapterDecoder<T,V> decoder) {
      super(subcon);
      this.encoder = encoder;
      this.decoder = decoder;
    }

    @Override
    public T encode(V obj, Container context) {
      return encoder.encode(obj, context);
    }

    @Override
    public V decode(T obj, Container context) {
      return decoder.decode(obj, context);
    }

  }