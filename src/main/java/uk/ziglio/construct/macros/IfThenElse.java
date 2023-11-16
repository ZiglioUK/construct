package uk.ziglio.construct.macros;

import static uk.ziglio.construct.Core.Container;

import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.core.KeyFunc;
import uk.ziglio.construct.core.Switch;

public class IfThenElse extends Switch { 
    public IfThenElse( String name, KeyFunc keyfunc, Construct then_subcon, Construct else_subcon ){
      super( name, keyfunc, Container( true, then_subcon, false, else_subcon) );
    }
  }