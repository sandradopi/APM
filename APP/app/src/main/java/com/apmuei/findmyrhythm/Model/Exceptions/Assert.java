package com.apmuei.findmyrhythm.Model.Exceptions;

public class Assert {

    public static void assertNotNull(Object object, String message) {
        if (object == null)
            throw new AssertionError(message);
    }
}
