package com.pillowapps.liqear.helpers;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = LBDatabase.NAME, version = LBDatabase.VERSION)
public class LBDatabase {
    public static final String NAME = "LiquidBear";
    public static final int VERSION = 3;
}
