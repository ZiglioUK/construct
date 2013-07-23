package com.sirtrack.construct.scala

import java.io.ByteArrayOutputStream
import com.sirtrack.construct.Core._
import com.sirtrack.construct.Core.Construct._
import com.sirtrack.construct.lib._
import com.sirtrack.construct.lib.Containers.Container
import com.sirtrack.construct.lib.Containers._
import com.sirtrack.construct.Core._


/*
* #===============================================================================
* # structures and sequences
* #===============================================================================
*/
  /**
   * @param name the name of the structure
   * @param subcons a sequence of subconstructs that make up this structure.
   * @param nested: a keyword-only argument that indicates whether this struct
      creates a nested context. The default is True. This parameter is
      considered "advanced usage", and may be removed in the future.
   * @return A sequence of unnamed constructs. The elements are parsed and built in the
    order they are defined.
    See also Embedded.
    Example:
    Sequence("foo",
        UBInt8("first_element"),
        UBInt16("second_element"),
        Padding(2),
        UBInt8("third_element"),
    )
    
     * @param name the name of the structure
     * @param subcons a sequence of subconstructs that make up this structure.
   */
  class SStruct( name: String, subcons: Construct* ) extends Construct(name) {
    var nested = true

      _inherit_flags(subcons.toArray : _*)
      _clear_flag(FLAG_EMBED)

    override def _parse( stream: ByteBufferWrapper, context: Container ): Object = {
      
      var obj: Container = null
      
      if( context.contains("<obj>")){
        obj = context.get("<obj>")
        context.del("<obj>")
      } else{
        obj = new Container()
        if( nested ){
          var parent = context.clone
          context.clear
          context.set( "_", parent )
        }
      }

      for( sc <- subcons ){
        if( (sc.conflags & FLAG_EMBED) != 0 ){
          context.set("<obj>", obj)
          sc._parse(stream, context)
        } else {
          var subobj = sc._parse(stream, context)
          if( sc.name != null ){
            obj.set( sc.name, subobj )
            context.set( sc.name, subobj )
          }
        }
      }
      return obj
    }

    override protected def _build( obj: Object, stream: ByteArrayOutputStream, context: Container  ) {
      if( context.contains("<unnested>")){
        context.del("<unnested>")
      } 
      else if( nested ){
          var parent = context.clone
          context.clear
          context.set( "_", parent )
      }
      for( sc <- subcons){
        var subobj : Object = null
        
        if( (sc.conflags & FLAG_EMBED) != 0 ){
          context.set( "<unnested>", true )
          subobj = obj
          sc._build(subobj, stream, context)
        } 
        else if( sc.name == null ){
          subobj = null
          sc._build(subobj, stream, context)
        } 
        else if( obj.isInstanceOf[Container] ){
          var container = obj.asInstanceOf[Container]
          subobj = container.get( sc.name )
          
          if( subobj == null )
            throw new FieldError( "No field found: " + sc.name + " in " + subobj )
          
          context.set(sc.name, subobj)
          sc._build(subobj, stream, context)
        } 
        // else continue
      }
    }

    override def _sizeof(context: Container ) = {
        var sum = 0
//        if( nested )
//            context = Container( "_", context )
        
        for( sc <- subcons ){
          sum += sc._sizeof(context)
        }
        
        sum
    }
  } 

object Core {
}
