package com.sirtrack.construct.interfaces;

import com.sirtrack.construct.lib.Containers.Container;

/**
 * callable that takes a context and returns length as an int
 */
public interface CountFunc {
  abstract int count(Container context);
}