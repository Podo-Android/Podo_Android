package com.meong.podoandroid.helper;

public class Sql {

  public static String createTableStoreSql
          ="create table if not exists store"
          + "(" +
          "    _id integer PRIMARY KEY autoincrement, " +
          "    name text UNIQUE, " +
          "    latitude float, " +
          "    longtitude float, " +
          "    address text" +
          ")";

  public static String insertStoreSql
          = "insert or replace into store(name, latitude, longtitude, address) "+
          "values(?, ?, ?, ?)";

  public static String selectStoreSql
          = "select name, latitude, longtitude, address from ";
}
