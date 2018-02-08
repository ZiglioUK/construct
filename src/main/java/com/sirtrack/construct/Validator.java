package com.sirtrack.construct;

import com.sirtrack.construct.lib.Containers.Container;

public interface Validator {
    boolean validate(Object obj, Container context);
}
