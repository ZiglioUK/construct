package construct.lib;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import construct.Core.FieldError;
import construct.Core.FormatField;

public class Containers{
  
  public static class ContainerError extends RuntimeException {
    public ContainerError(String string) {
      super(string);
    }
  }
	
  static public class Container
  {
  	HashMap<Object, Object> dict = new HashMap<Object, Object>();
  	
  	public Container( Object... pairs ){
  		if(( pairs.length & 1 ) != 0 )
  			throw new ContainerError( "length of parameters is not and even number: " + pairs.length );
  		
  		for( int i=0; i<pairs.length;  ){
  			Object name = pairs[i++];
  			Object value = pairs[i++];
  			
  			this.dict.put( name, value);
  		}
  	}
  
  	public Container( Map<Object, Object> dict ){
  		this.dict.putAll( dict );
  	}
  
  	public <K,V>V get( K name ){
  		return (V)dict.get(name);
  	}

  	public <K,V>V get( K name, V defaultval ){
  		if( dict.containsKey(name))
  			return (V)dict.get(name);
  		else
  			return defaultval;
  	}
  	
  	public void del( Object name ){
  		dict.remove( name );
  	}
  	
  	public void set( Object name, Object value ){
  		dict.put( name, value);
  	}
  	
  	public Set<Object> keys(){
  		return dict.keySet();
  	}

  	public <T> T[] values( Class<T> clazz ){
  		return (T[]) dict.values().toArray((T[])Array.newInstance( clazz, 0));
  	}
  	
  	public void update( Container other ){
  		for( Object s : other.keys() ){
  				set( s, other.get(s));
  		}
  	}
  	
  	public boolean contains( Object name ){
  		return dict.containsKey(name);
  	}
  	
  	@Override
  	public boolean equals( Object o ){
  		if( o instanceof Container)
  			return dict.equals(((Container)o).dict);
  		else
  			return false;
  	}
    
  	public Container clone(){
  		return new Container( dict );
  	}
  
    public final String toString() {
      return dict.toString();
  }
  
    public Container reverse(){
    	Container reverse = new Container();
    	for( Object key : this.keys() ){
    		reverse.set( this.get(key ), key );
    	}
    	return reverse;
    }
  }
  
  static public List<Object> ListContainer( Object... args ){
  	ArrayList<Object> l = new ArrayList<Object>();
  	for( Object o: args )
  		l.add(o);
  	return l;
  }

}
