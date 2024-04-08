package com.example.deeproute.Dao;

import com.example.deeproute.Model.Login;

public interface LoginDao {
    Login getLoginInfo();
    boolean update(String username, String password);
    void initialize();
}
