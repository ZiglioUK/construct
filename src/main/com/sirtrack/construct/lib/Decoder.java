package com.sirtrack.construct.lib;

public interface Decoder {
	/**
	 * a function that takes a string and returns a decoded
      string (used before parsing)
	 */
	byte[] decode( byte[] data );
}
