package com.sirtrack.construct;

public class CompositePojo {
	public int id;
	public Pojo Pojo;
	
	public CompositePojo(){
		super();
	}
	
	public CompositePojo( int id, Pojo val){
		this.id = id;
		this.Pojo = val;
	}
	
	@Override
	public boolean equals(Object obj) {
		CompositePojo p2 = (CompositePojo)obj;
	  return p2.id == this.id && p2.Pojo.equals(this.Pojo);
	}

}
