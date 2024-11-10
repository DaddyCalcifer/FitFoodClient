package com.fitfood.clientapp.models;

public enum ActivityType
{
    Inactive(0),
    Lite(1),
    Midi(2),
    High(3),
    Sport(4);
    final int lvl;
    ActivityType(int lvl)
    {
        this.lvl = lvl;
    }
    public int getLvl()
    {
        return this.lvl;
    }
}
