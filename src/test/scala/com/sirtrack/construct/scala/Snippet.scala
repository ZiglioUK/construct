package com.sirtrack.construct.scala

import com.sirtrack.construct.Adapters._
import com.sirtrack.construct.Core._
import com.sirtrack.construct.Macros._
import com.sirtrack.construct.lib.Containers._
import org.junit.Assert.assertEquals
import com.sirtrack.construct.Pojo
import org.junit.Test
import com.sirtrack.construct.Core.Container

class Snippet {
  
  @Test 
  def BeanAdapterTest() {
  	val p = new Pojo( 100, 1 )
  	val c1 = Container( "id", 100.asInstanceOf[Integer], "val", 1.asInstanceOf[Integer] )
  	
  	val adapter = BeanAdapter( classOf[Pojo], Pass )
  	assertEquals( p, adapter.decode( c1, null))
//  	
//  	val s1 =  Struct( "Pojo", UBInt8("id"), UBInt8( "val") )
//  	assertEquals( c1, s1.parse(ByteArray( 100, 1 )) )
//  	assertArrayEquals( ByteArray( 100, 1 ), s1.build( c1) )
//  	
//  	val s2 =  BeanAdapter( Pojo.class, Struct( "Pojo", UBInt8("id"), UBInt8( "val") ))
//  	assertEquals( p, s2.parse(ByteArray( 100, 1 )))
//  	assertArrayEquals( ByteArray( 100, 1 ), s2.build( p ) )
//  
//  	CompositePojo cp = new CompositePojo( 50, p )
//  	Construct cs =  BeanAdapter( CompositePojo.class, Struct( "CompositePojo", UBInt8("id"), s2 ))
//  	assertEquals( cp, cs.parse(ByteArray( 50, 100, 1 )))
//  	assertArrayEquals( ByteArray( 50, 100, 1 ), cs.build( cp ) )
  
  }
  
}

