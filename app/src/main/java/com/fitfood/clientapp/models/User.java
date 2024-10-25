package com.fitfood.clientapp.models;


import androidx.annotation.NonNull;

public class User {
    public String Login = "";
    public String Password = "";

    @NonNull
    @Override
    public String toString()
    {
        return "Login: " + Login + ", password = " + Password;
    }
}
