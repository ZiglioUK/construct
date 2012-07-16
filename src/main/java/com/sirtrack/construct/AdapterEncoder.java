package com.sirtrack.construct;

import com.sirtrack.construct.lib.Containers.Container;

public interface AdapterEncoder{
	public Object encode(Object obj, Container context);
}
