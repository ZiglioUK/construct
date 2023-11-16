package uk.ziglio.construct.adapters;

import uk.ziglio.construct.Adapter;
import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.errors.ValidationError;
import uk.ziglio.construct.interfaces.Validator;
import uk.ziglio.construct.lib.Containers.Container;

/*
   * #============================================================================
   * === # validators
   * #==========================================================
   * =====================
   */
  /**
   * validates a condition on the encoded/decoded object. Override
   * _validate(obj, context) in deriving classes.
   */
  public class ValidatorAdapter extends Adapter {
	Validator v;
	
    /**
     * @param subcon
     *          the subcon to validate
     */
    public ValidatorAdapter(Construct subcon, Validator v ) {
      super(subcon); 
      this.v = v;
    }

    @Override
    public Object decode(Object obj, Container context) {
      if (!v.validate(obj, context))
        throw new ValidationError("invalid object" + obj);
      return obj;
    }

    @Override
    public Object encode(Object obj, Container context) {
      return decode(obj, context);
    }
  }