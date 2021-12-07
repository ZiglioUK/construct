package uk.ziglio.construct.interfaces;

import uk.ziglio.construct.lib.Containers.Container;

public interface AdapterEncoder<V,T>{
  public T encode(V obj, Container context);
}
