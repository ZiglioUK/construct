package uk.ziglio.construct.core;

import uk.ziglio.construct.lib.Containers.Container;

/**
 * a function that takes the context and returns a key
 */
public abstract class KeyFunc {
  public final String key;

  public KeyFunc(String key) {
    this.key = key;
  }

  public KeyFunc() {
    this.key = null;
  }

  public String key() {
    return key;
  }

  public abstract Object get(Container context);
}