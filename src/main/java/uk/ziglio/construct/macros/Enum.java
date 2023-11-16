package uk.ziglio.construct.macros;

import static uk.ziglio.construct.Core.Container;

import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.lib.Containers.Container;

public class Enum extends SymmetricMapping {
  
  // we could do some static type checks, making sure that names are String
  // and that the size of values matches the size of subcon
  // Let's keep things simple for now
  // Also don't handle Pass, decided we should always return the same type
  public Enum( Construct subcon, Container map ){
    super( subcon, map, (String)map.get("_default_"));
  }
  
  public Enum( Construct subcon, Object... pairs ){
    this( subcon, Container(pairs) );
  }
}