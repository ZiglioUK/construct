package com.sirtrack.construct;
import com.sirtrack.construct.lib.Containers.Container;

public interface AdapterDecoder<T>{
  public T decode(Object obj, Container context);
}
