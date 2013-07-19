package com.sirtrack.construct

import com.sirtrack.construct.Adapters._
import com.sirtrack.construct.Core._
import com.sirtrack.construct.Macros._
import com.sirtrack.construct.lib.Containers._
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals

import java.net.InetAddress
import java.net.UnknownHostException

import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import com.sirtrack.construct.Core.Container

object Snippet {
  
  @Test 
  def BeanAdapterTest() {
  	val p = new Pojo ( 100, 1 )
//  	val c1 = Container( "id", 100, "val", 1 )
  	
//  	val adapter = BeanAdapter( Pojo , Pass )
//  	assertEquals( p, adapter.decode( c1, null))
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

