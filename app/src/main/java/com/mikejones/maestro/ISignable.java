package com.mikejones.maestro;

public interface ISignable {

    void signIn();
    void failedSignIn(String message);
}
