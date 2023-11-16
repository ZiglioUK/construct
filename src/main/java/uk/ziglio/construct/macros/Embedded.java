package uk.ziglio.construct.macros;

import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.core.Reconfig;

/**
 * embeds a struct into the enclosing struct.
 */
public class Embedded<T extends Construct> extends Reconfig<T> {
  /*
  * @param subcon the struct to embed
  * @return
  */
  public Embedded( T subcon ){
    super( subcon.name, subcon, Construct.FLAG_EMBED, 0 );
  }
}