package com.sirtrack.construct;

public class Pojo {
	public int id;
	public int val;
	
	public Pojo(){
		super();
	}
	
	public Pojo( int id, int val){
		this.id = id;
		this.val = val;
	}
	
	@Override
	public boolean equals(Object obj) {
		Pojo p2 = (Pojo)obj;
	  return p2.id == this.id && p2.val == this.val;
	}

}
