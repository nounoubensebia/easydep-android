package com.example.nouno.easydep.exceptions;

/**
 * Created by nouno on 01/03/2017.
 */




// exception des erreur de connection
public class ConnectionProblemException extends Exception {
    public ConnectionProblemException (String s)
    {
        super(s);
    }
}
