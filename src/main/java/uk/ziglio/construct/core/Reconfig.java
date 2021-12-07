package uk.ziglio.construct.core;

/**
   * Reconfigures a subconstruct. Reconfig can be used to change the name and
   * set and clear flags of the inner subcon. Example: Reconfig("foo",
   * UBInt8("bar"))
   */
  public class Reconfig<T extends Construct> extends Subconstruct {

    /**
     * @param name
     *          the new name
     * @param subcon
     *          the subcon to reconfigure
     * @param setflags
     *          the flags to set (default is 0)
     * @param clearflags
     *          the flags to clear (default is 0)
     */
    public Reconfig(String name, T subcon, int setflags, int clearflags) {
      super(name, subcon);
      _set_flag(setflags);
      _clear_flag(clearflags);
    }

    public Reconfig(String name, T subcon) {
      this(name, subcon, 0, 0);
    }

    @Override
    public Construct get(){
      return subcon;
    }
//
//    @Override
//    public void set( Object val ){
//      subcon.set(val);
//    }
    
  }