package uk.ziglio.construct.core;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import uk.ziglio.construct.Core;
import uk.ziglio.construct.annotations.len;
import uk.ziglio.construct.errors.FieldError;
import uk.ziglio.construct.interfaces.LengthConstruct;
import uk.ziglio.construct.lib.ByteBufferWrapper;
import uk.ziglio.construct.lib.Containers.Container;

public class Struct extends Construct {
    public boolean nested = true;
    public Construct[] subcons;

    /**
     * @param name
     *          the name of the structure
     * @param subcons
     *          a sequence of subconstructs that make up this structure.
     */
    public Struct(String name, Construct... subcons) {
      super(name);
      this.subcons = subcons;
      _inherit_flags(subcons);
      _clear_flag(FLAG_EMBED);
    }

    @Override
    public Struct clone() throws CloneNotSupportedException {
      Struct clone = (Struct) super.clone();

      clone.subcons = new Construct[subcons.length];
      Field[] fields = getClass().getDeclaredFields();

      int i = 0;
      for( Field f : fields ){
        if (Construct.class.isAssignableFrom(f.getType()))
	        try{
	          f.setAccessible(true);
	          // clone field 
	          Construct fclone = ((Construct)f.get(this)).clone();
	          // set the field clone into the Struct clone 
	          f.set(clone, fclone);
	          // also add the field clone to the subcons array 
	          clone.subcons[i++] = fclone;
	        } catch( Exception e ){
	          throw new RuntimeException(e);
	        }
          
        // Clone elements in the subcons array
        // Because we cater for both static and runtime Struct definitions,
        // we need to make sure subcons don't end up twice in the subcons array
        // This case has to handle only the runtime (old) definition
        // So if we already have stuff in the subcons array, carry on
        else if ( /*f.getType() == Construct[].class &&*/ f.getName().equals("subcons") && clone.subcons[0] == null )
	        try{
	          i = 0;
	          for( Construct c : subcons ){
	            clone.subcons[i++] = c.clone();
	          }

	        } catch( Exception e ){
	          throw new RuntimeException(e);
	      }
        else
        	continue;
      }
      return clone;
    }

    /**
     * This is a special constructor for typesafe Structs.
     * Instead of passing an array of Subcons at runtime,
     * this constructor inspects the public fields of type Construct for this Struct
     * and invokes each field's constructor by passing the field name.
     * It's assumed that all declared fields have a public constructor: Construct( String name )
     * @param name
     */
    public Struct(String name) {
      super(name);
      Constructor fctor;
      Field field = null;
      String fname;
      try {
        Field[] fields = getClass().getFields();
        List<Construct> subconf = new ArrayList<Construct>();

        for( int i = 0; i < fields.length; i++ ) {
          field = fields[i];
          field.setAccessible(true);
          Class clazz = field.getType();
          
          if (!Construct.class.isAssignableFrom(clazz))
            continue;

          fname = field.getName();
          fctor = clazz.getConstructors()[0];
          fctor.setAccessible(true);
          Construct inst;
          Object enclosingInst;
          switch (fctor.getParameterTypes().length) {
            // TODO should check that the first instance is of the right type: enclosing type or String
            case 2: // inner classes
              try{
                // static class case
                enclosingInst = getClass().getDeclaredField("this$0").get(this);
              } catch( NoSuchFieldException nsfe ){
                // private nested class case
                enclosingInst = this;
              }
              inst = (Construct) fctor.newInstance(enclosingInst, fname);
              break;
            case 1:
              if( String.class.isAssignableFrom( fctor.getParameterTypes()[0] )){
                inst = (Construct) fctor.newInstance(fname);
              } else {
                // no arguments constructor
                try{
                  // static class case
                  enclosingInst = getClass().getDeclaredField("this$0").get(this);
                } catch( NoSuchFieldException nsfe ){
                  // private nested class case
                  enclosingInst = this;
                }
                inst = (Construct) fctor.newInstance(enclosingInst);
                
                // now call name setter with fname
                inst.setName(fname); 
              }
              break;
            case 0:
              inst = (Construct) fctor.newInstance();
              break;
            default:
              throw new Exception("No default case: " + fctor);
          
          }
          // process annotations
          if( field.isAnnotationPresent(len.class) ) {
            ((LengthConstruct)inst).setLength( field.getAnnotation(len.class).value() );
           }

          field.set(this, inst);
          subconf.add(inst);
        }
        subcons = new Construct[subconf.size()];
        subcons = subconf.toArray(subcons);
        _inherit_flags(subcons);
        _clear_flag(FLAG_EMBED);
      } catch (Exception e) {
        throw new RuntimeException("Error constructing field " + field + "\r\n" + e.toString(), e);
      }
    }

    public Struct() {
      this((String) null);
    }

    @Override
    public Object _parse(ByteBufferWrapper stream, Container context) {

      Container obj;
      if (context.contains("<obj>")) {
        obj = context.get("<obj>");
        context.del("<obj>");
      } else {
        obj = new Container();
        if (nested) {
          context = Core.Container("_", context);
        }
      }

      for (Construct sc : subcons) {
        if ((sc.conflags & FLAG_EMBED) != 0) {
          context.set("<obj>", obj);
          Object val = sc._parse(stream, context);
          sc.set( val );
        } else {
          Object val = sc._parse(stream, context);
          sc.set( val );
          if (sc.name != null) {
            obj.set(sc.name, val);
            context.set(sc.name, val);
//            System.out.println( " (" + sc.name + ") = " + val );
          }
        }
      }
      return obj;
    }

    @Override
    public void _build(Object obj, ByteArrayOutputStream stream, Container context) {
      if (context.contains("<unnested>")) {
        context.del("<unnested>");
      } else if (nested) {
        context = Core.Container("_", context);
      }
      for (Construct sc : subcons) {
        Object subobj;
        if ((sc.conflags & FLAG_EMBED) != 0) {
          context.set("<unnested>", true);
          subobj = obj;
        } else if (sc.name == null) {
          subobj = null;
        } else if (obj instanceof Container) {
          Container container = (Container) obj;
          subobj = container.get(sc.name);

          if (subobj == null)
            throw new FieldError("No field found: " + sc.name + " in " + subobj);

          context.set(sc.name, subobj);
        } else
          continue;

        sc._build(subobj, stream, context);
      }
    }

    @Override
    public int _sizeof(Container context) {
      int sum = 0;
      // if( nested )
      // context = Container( "_", context );

      for (Construct sc : subcons) {
        sum += sc._sizeof(context);
      }

      return sum;
    }
  }