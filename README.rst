=========
Construct
=========

Construct is a powerful declarative parser for binary data.

It is based on the concept of defining data structures in a declarative
manner, rather than procedural code: Simple constructs can be combined
hierarchically to form increasingly complex data structures. It's the first
library that makes parsing fun, instead of the usual headache it is today.

Construct features bit and byte granularity, symmetrical operation (parsing
and building), component-oriented declarative design, easy debugging and
testing, an easy-to-extend subclass system, and lots of primitive
constructs to make your work easier:

 * Fields
 * Structs
 * Unions
 * Repeaters
 * Meta constructs
 * Switches
 * On-demand parsing
 * Pointers
 * And more!

Java Version
============

This Java version employs some syntactic sugar (i.e. static methods) to make the syntax as close as possible to the original Construct library in Python.

See for example: src/test/BitTest.java

    Construct struct = BitStruct("foo",
        BitField("a", 3),
        Flag("b"),
        Padding(3),
        Nibble("c"),
        Struct("bar",
            Nibble("d"),
            Bit("e")
        )
    );

A Java Construct can parse byte arrays and produces Objects like Containers. Viceversa, it can take Objects to produce byte arrays.
    public Object parse(byte[] data);
    public byte[] build( Object obj);

Parsing example:
    Container c1 = Container( P("a", 7), P("b", false), P("bar", Container( P("d", 15 ), P("e", 1))), P("c",8) );
    Container c2 = (Container)struct.parse(new byte[]{(byte)0xe1, 0x1f});
    assertTrue( c1.equals(c2) );
