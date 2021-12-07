package uk.ziglio.construct.core;

import static uk.ziglio.construct.lib.Containers.ListContainer;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.ListIterator;

import uk.ziglio.construct.Core;
import uk.ziglio.construct.lib.ByteBufferWrapper;
import uk.ziglio.construct.lib.Containers.Container;

public class Sequence extends Struct {
  public Sequence(String name, Construct... subcons) {
    super(name, subcons);
  }

  @Override
  public Object _parse(ByteBufferWrapper stream, Container context) {
    List<Object> obj;
    if (context.contains("<obj>")) {
      obj = context.get("<obj>");
      context.del("<obj>");
    } else {
      obj = ListContainer();
      if (nested) {
        context = Core.Container("_", context);
      }
    }
    for (Construct sc : subcons) {
      if ((sc.conflags & FLAG_EMBED) != 0) {
        context.set("<obj>", obj);
        sc._parse(stream, context);
      } else {
        Object subobj = sc._parse(stream, context);
        if (sc.name != null) {
          obj.add(subobj);
          context.set(sc.name, subobj);
        }
      }
    }
    return obj;
  }

  @Override
  public void _build(Object obj, ByteArrayOutputStream stream,
      Container context) {
    if (context.contains("<unnested>")) {
      context.del("<unnested>");
    } else if (nested) {
      context = Core.Container("_", context);
    }

    Object subobj;
    ListIterator objiter;
    if (obj instanceof List)
      objiter = ((List) obj).listIterator();
    else
      objiter = (ListIterator) obj;

    for (Construct sc : subcons) {
      if ((sc.conflags & FLAG_EMBED) != 0) {
        context.set("<unnested>", true);
        subobj = objiter;
      } else if (sc.name == null) {
        subobj = null;
      } else {
        subobj = objiter.next();
        context.set(sc.name, subobj);
      }
      sc._build(subobj, stream, context);
    }
  }
}