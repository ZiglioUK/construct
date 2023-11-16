package uk.ziglio.construct.adapters;

import static uk.ziglio.construct.Core.Pass;

import uk.ziglio.construct.Adapter;
import uk.ziglio.construct.adapters.Adapters.MappingError;
import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.lib.Containers.Container;

public class MappingAdapter extends Adapter<Object, Object> {

  Container decoding;
  Container encoding;
  Object decdefault;
  Object encdefault;

  public MappingAdapter(Construct subcon, Container decoding,
      Container encoding, Object decdefault, Object encdefault) {
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

  public Object decode(Object obj, Container context) {
    if (obj instanceof byte[])
      obj = ((byte[]) obj)[0];

    if (decoding.contains(obj))
      return decoding.get(obj);
    else {
      if (decdefault == null)
        throw new MappingError("no encoding mapping for " + obj);
      if (decdefault == Pass)
        return obj;
      return decdefault;
    }
  }

}