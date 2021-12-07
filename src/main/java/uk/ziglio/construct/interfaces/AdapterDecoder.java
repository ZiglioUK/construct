package uk.ziglio.construct.interfaces;
import uk.ziglio.construct.lib.Containers.Container;

public interface AdapterDecoder<T,V>{
  public V decode(T obj, Container context);
}
