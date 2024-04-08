package com.example.deeproute.Service;

import com.example.deeproute.Dao.LoginDao;
import com.example.deeproute.Model.Login;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class LoginService {
    @Resource
    LoginDao loginDao;

    public boolean check(String username, String password){
        try{
            Login login = loginDao.getLoginInfo();
            if (login == null) {
                loginDao.initialize();
                return username.equals("deeproute") && password.equals("deeproute888");
            } else {
                if (username.equals(login.getUsername()) && password.equals(login.getPassword())) {
                    return true;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean update(String username, String password){
        try{
            return loginDao.update(username,password);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
