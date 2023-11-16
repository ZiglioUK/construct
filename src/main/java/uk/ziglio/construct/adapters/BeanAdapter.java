package uk.ziglio.construct.adapters;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import uk.ziglio.construct.Adapter;
import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.lib.Containers.Container;

public class BeanAdapter<V> extends Adapter<V, Container> {
	Class<V> clazz;
	  
	public BeanAdapter(Class<V> clazz, Construct subcon) {
      super( subcon );
      this.clazz = clazz;
	}
	
    @Override
    public Container encode(V obj, Container context) {
      Container c = new Container();

      for (Field f : clazz.getFields()) {
        String name = f.getName();
        try {
          c.set(name, clazz.getField(name).get(obj));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      return c;
    }

    @Override
    public V decode(Container c, Container context) {
      V t = newV( clazz );

      for (String name : c.<String> keys()) {
        try {
          Field f = clazz.getField((String) name);
          f.set(t, c.get(name));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      return t;
	 }

     static <V>V newV( Class<V> clazz ) {
      try {
        Constructor<V> c = clazz.getDeclaredConstructor();
        c.setAccessible(true);
        return c.newInstance();
      } catch (NoSuchMethodException ex) {
        try {
          return clazz.newInstance();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }