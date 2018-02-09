package com.sirtrack.construct;

import com.sirtrack.construct.lib.Containers.Container;

public interface AdapterEncoder<V,T>{
  public T encode(V obj, Container context);
}
