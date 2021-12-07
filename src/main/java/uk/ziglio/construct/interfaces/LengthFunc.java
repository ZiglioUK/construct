package uk.ziglio.construct.interfaces;

import uk.ziglio.construct.lib.Containers.Container;

/**
 * callable that takes a context and returns length as an int
 */
public interface LengthFunc {
  int length(Container context);
}