package com.sirtrack.construct;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.sirtrack.construct.lib.Containers.Container;
import com.sirtrack.construct.lib.Containers.ContainerError;


public class ContainerTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void test_getattr() {
      Container c = new Container( "a",1 );
      assertEquals( 1, c.get("a") );
  }
  @Test
  public void test_setattr() {
      Container c = new Container();
      c.set("a", 1);
      assertEquals( 1, c.get("a") );
  }
  @Test
  public void test_delattr() {
      Container c = new Container( "a",1 );
      c.del("a");
      assertFalse( c.contains("a") );
  }
  @Test
  public void test_update() {
      Container c = new Container( "a",1 );
      Container d = new Container();
      d.update(c);
      assertEquals( 1, d.get("a") );
  }
  @Test
  public void test_eq_eq() {
      Container c = new Container( "a",1 );
      Container d = new Container( "a",1 );
      assertTrue( c.equals(d) );
  }
      /*
      def test_ne_wrong_type(self):
          c = Container(a=1)
          d = {"a": 1}
          self.assertNotEqual(c, d)
*/
  @Test
  public void test_ne_wrong_key() {
      Container c = new Container( "a",1 );
      Container d = new Container( "b",1 );
      assertFalse( c.equals(d) );
  }
  @Test
  public void test_ne_wrong_value() {
      Container c = new Container( "a",1 );
      Container d = new Container( "a",2 );
      assertFalse( c.equals(d) );
  }
  @Test
  public void test_copy() {
      Container c = new Container( "a",1 );
      Container d = c.clone();
      assertTrue( c.equals(d) );
      assertNotSame( c, d );
  }
  /*
      def test_bool_false(self):
          c = Container()
          self.assertFalse(c)

      def test_bool_true(self):
          c = Container(a=1)
          self.assertTrue(c)
*/
  @Test
  public void test_in() {
      Container c = new Container( "a",1 );
      assertTrue( c.contains("a") );
  }
  @Test
  public void test_not_in() {
      Container c = new Container();
      assertFalse( c.contains("a") );
  }

  /*
      def test_repr(self):
          c = Container(a=1, b=2)
          repr(c)

      def test_repr_recursive(self):
          c = Container(a=1, b=2)
          c.c = c
          repr(c)

      def test_str(self):
          c = Container(a=1, b=2)
          str(c)

      def test_str_recursive(self):
          c = Container(a=1, b=2)
          c.c = c
          str(c)

  class TestListContainer(unittest.TestCase):

      def test_str(self):
          l = ListContainer(range(5))
          str(l)
   */
  @Test
  public void testContainer(){
  	Container c, d;
  	
    c = new Container("name1",1,"name2",2);
    assertEquals( 1, c.get("name1"));
    assertEquals( 2, c.get("name2"));
    
    d = c.reverse();
    assertEquals( "name1", d.get(1));
    assertEquals( "name2", d.get(2));
   
    exception.expect( ContainerError.class );
    c = new Container(1,2,3);

  }
 

}

