package com.sirtrack.construct.lib;

public interface Encoder {
	/**
	 * a function that takes a string and returns an encoded
      string (used after building)
	 */
	byte[] encode( String data );
}
