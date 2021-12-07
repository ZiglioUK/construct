package uk.ziglio.construct.core;

import java.io.ByteArrayOutputStream;
import java.util.List;

import uk.ziglio.construct.Core;
import uk.ziglio.construct.lib.ByteBufferWrapper;
import uk.ziglio.construct.lib.Containers.Container;

/**
 * A conditional branch. Switch will choose the case to follow based on the
 * return value of keyfunc. If no case is matched, and no default value is
 * given, SwitchError will be raised. See also Pass. Example: Struct("foo",
 * UBInt8("type"), Switch("value", lambda ctx: ctx.type, { 1 : UBInt8("spam"),
 * 2 : UBInt16("spam"), 3 : UBInt32("spam"), 4 : UBInt64("spam"), } ), )
 */
public class Switch extends Construct {
  /**
   * a function that takes the context and returns a key, which will ne used
   * to choose the relevant case.
   */
  public KeyFunc keyfunc;
  public Container cases;
  public Construct defaultval;
  public boolean include_key;

  /**
   * @param name
   *          the name of the construct
   * @param keyfunc
   *          a function that takes the context and returns a key, which will
   *          ne used to choose the relevant case.
   * @param cases
   *          a dictionary mapping keys to constructs. the keys can be any
   *          values that may be returned by keyfunc.
   * @param defaultval
   *          a default value to use when the key is not found in the cases.
   *          if not supplied, an exception will be raised when the key is not
   *          found. You can use the builtin construct Pass for 'do-nothing'.
   * @param include_key
   *          whether or not to include the key in the return value of
   *          parsing. defualt is False.
   */
  public Switch(String name, KeyFunc keyfunc, Container cases,
      Construct defaultval, boolean include_key) {
    super(name);
    this.keyfunc = keyfunc;
    this.cases = cases;
    this.defaultval = defaultval;
    this.include_key = include_key;
    Construct[] ca = cases.values(Construct.class);
    this._inherit_flags(ca);
    this._set_flag(FLAG_DYNAMIC);
  }

  public Switch(String name, KeyFunc keyfunc, Container cases) {
    this(name, keyfunc, cases, Core.NoDefault, false);
  }

  @Override
  public Object get() {
    return val;
  }

  @Override
  public void set(Object val) {
    // do nothing: prevent Structs from setting val to the parsed value
    // keep the Switch case construct as a value
    //this.val = val;
  }

  @Override
  public Switch clone() throws CloneNotSupportedException {
    Switch c = (Switch) super.clone();
    c.cases = cases.clone(); // TODO check deep copy
    c.defaultval = defaultval.clone();

    return c;
  }
  
  @Override
  public Object _parse(ByteBufferWrapper stream, Container context) {
    Object key = keyfunc.get(context);
    /* assign the case Construct as a value for Switch
     * users can then retrieve the case Construct with get()*/
    val = cases.get(key, defaultval);
    Object res = ((Construct)val)._parse(stream, context);
    if (include_key) 
      res = Core.Container(key, res);
   return res;
  }

  @Override
  public void _build(Object obj, ByteArrayOutputStream stream,
      Container context) {
    Object key;
    if (include_key) {
      List list = (List) obj;
      key = list.get(0);
      obj = list.get(1);
    } else {
      key = keyfunc.get(context);
    }

    Construct casestruct = cases.get(key, defaultval);
    casestruct._build(obj, stream, context);
    /*
     * if self.include_key: key, obj = obj else: key = self.keyfunc(context)
     * case = self.cases.get(key, self.default) case._build(obj, stream,
     * context)
     */
  }

  @Override
  public int _sizeof(Container context) {
    Construct casestruct = cases.get(keyfunc.get(context), defaultval);
    return casestruct._sizeof(context);
  }

}