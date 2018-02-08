package com.sirtrack.construct;

import com.sirtrack.construct.lib.Containers.Container;

public interface AdapterEncoder<T,V>{
  public T encode(V obj, Container context);
}
