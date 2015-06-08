package com.sirtrack.construct;

import static com.sirtrack.construct.Core.Pass;

import com.sirtrack.construct.Adapters.MappingError;
import com.sirtrack.construct.Core.Construct;
import com.sirtrack.construct.lib.Containers.Container;

public class MappingAdapter<T> extends Adapter<Construct, T> {

  Container decoding;
  Container encoding;
  T decdefault;
  Object encdefault;

  public MappingAdapter(Construct subcon, Container decoding,
      Container encoding, T decdefault, Object encdefault) {
    super(subcon);
    this.decoding = decoding;
    this.encoding = encoding;
    this.decdefault = decdefault;
    this.encdefault = encdefault;

  }

  public Object encode(Object obj, Container context) {
    if (encoding.contains(obj))
      return encoding.get(obj);
    else {
      if (encdefault == null)
        throw new MappingError("no encoding mapping for " + obj);
      if (encdefault == Pass)
        return obj;
      return encdefault;
    }
  }

  public T decode(Object obj, Container context) {
    if (obj instanceof byte[])
      obj = ((byte[]) obj)[0];

    if (decoding.contains(obj))
      return decoding.get(obj);
    else {
      if (decdefault == null)
        throw new MappingError("no encoding mapping for " + obj);
      if (decdefault == Pass)
        return (T) obj;
      return decdefault;
    }
  }

}