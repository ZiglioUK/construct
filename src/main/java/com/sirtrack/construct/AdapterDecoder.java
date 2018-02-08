package com.sirtrack.construct;
import com.sirtrack.construct.lib.Containers.Container;

public interface AdapterDecoder<T,V>{
  public V decode(T obj, Container context);
}
