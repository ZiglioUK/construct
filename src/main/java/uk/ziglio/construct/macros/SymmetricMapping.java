package uk.ziglio.construct.macros;

import uk.ziglio.construct.adapters.MappingAdapter;
import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.lib.Containers.Container;

public class SymmetricMapping extends MappingAdapter{
  public SymmetricMapping( Construct subcon, final Container mapping, Object mappingdefault ){
    super( subcon, mapping.reverse(), mapping, mappingdefault, mappingdefault );
  }
}