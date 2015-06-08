package com.sirtrack.construct;

import com.sirtrack.construct.lib.Containers.Container;

public interface AdapterEncoder<T>{
  public Object encode(T obj, Container context);
}
