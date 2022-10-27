package com.ortest.exceptions;

public class InvalidCommandeException  extends Exception
{
    public InvalidCommandeException (String str)
    {
        // calling the constructor of parent Exception
        super(str);
    }
}