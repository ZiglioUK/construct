package construct.lib;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Various containers.
 */
public class Containers
{
	static public class Pair{
		public String s;
		public Object o;
		
		public Pair( String s, Object o){
			this.s = s;
			this.o = o;
		}
	}

	static public Pair p( final String s, final Object o ){
		return new Pair( s, o);
	}
	
	/**
    A generic container of attributes.

    Containers are the common way to express parsed data.
	 */
	static public class Container{
		
		Map<String, Object> dict = new HashMap<String, Object>();
		
		public Container( Pair... dict ){
			for( Pair p : dict ){
				this.dict.put( p.s, p.o);
			}
		}

		public Container( Map<String, Object> dict ){
			this.dict.putAll( dict );
		}

		public Object get( String name ){
			return dict.get(name);
		}
		
		public void del( String name ){
			dict.remove( name );
		}
		
		public void set( String name, Object value ){
			dict.put( name, value);
		}
		
		public Set<String> keys(){
			return dict.keySet();
		}
		
		public void update( Container other ){
			for( String s : other.keys() ){
					set( s, other.get(s));
			}
		}
		
		public boolean contains( String name ){
			return dict.containsKey(name);
		}
		
		public boolean equals( Container o ){
			return dict.equals(o.dict);
		}
    
		public Container clone(){
			return new Container( dict );
		}

	/*
    def iteritems(self):
        return self.__dict__.iteritems()

    # Rich comparisons.

    def __eq__(self, other):
        try:
            return self.__dict__ == other.__dict__
        except AttributeError:
            return False

    def __ne__(self, other):
        return not self == other

    # Copy interface.

    def copy(self):
        return self.__class__(**self.__dict__)

    __copy__ = copy

    # Iterator interface.

    def __iter__(self):
        return iter(self.__dict__)

    def __repr__(self):
        return "%s(%s)" % (self.__class__.__name__, repr(self.__dict__))

    def __str__(self):
        return "%s(%s)" % (self.__class__.__name__, str(self.__dict__))
*/
	}
}
