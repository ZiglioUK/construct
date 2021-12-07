package uk.ziglio.construct.interfaces;

import uk.ziglio.construct.lib.Containers.Container;

public interface Validator {
    boolean validate(Object obj, Container context);
}
