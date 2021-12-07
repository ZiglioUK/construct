package uk.ziglio.construct.interfaces;

import uk.ziglio.construct.lib.Containers.Container;

/**
 * a function that takes the context and return the computed value
 */
public interface ValueFunc<T> {
  T get(Container ctx);
}