package com.fitfood.clientapp.models;

public enum Gender
{
    Female(0),
    Male(1),
    None(2);

    final int sex;
    Gender(int sex)
    {
        this.sex = sex;
    }
    public int getSex()
    {
        return this.sex;
    }
}