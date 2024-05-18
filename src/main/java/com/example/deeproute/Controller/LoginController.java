package com.example.deeproute.Controller;

import com.alibaba.fastjson2.JSON;

import com.example.deeproute.ReturnData.Result;
import com.example.deeproute.Service.HistoryService;
import com.example.deeproute.Service.LoginService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/login")
public class LoginController {
    @Resource
    LoginService loginService;
    @Resource
    RedisTemplate<String,Object> redisTemplate;

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @RequestMapping(value = "/check", method = RequestMethod.POST, produces = "application/json;charset=utf-8",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> check(@RequestParam("username") String username,
                                @RequestParam("password") String password, HttpServletResponse response) {

        if (loginService.check(username,password)){
            Cookie cookie = new Cookie("login-cookie", UUID.randomUUID().toString());
            cookie.setPath("/"); // 设置 Cookie 的路径
            cookie.setAttribute("SameSite","None");
            cookie.setSecure(true);
            response.addCookie(cookie); // 将 Cookie 添加到响应中
            redisTemplate.opsForValue().set(cookie.getValue(), "", 1, TimeUnit.HOURS);
            return Result.ok(null);
        }
        else {
            return Result.error(-1,"");
        }
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json;charset=utf-8",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> update(@RequestParam("username") String username,
                                 @RequestParam("password") String password,
                                 @CookieValue(value = "login-cookie" , defaultValue = "") String cookie) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cookie))){
            redisTemplate.expire(cookie,1, TimeUnit.HOURS);
            if(loginService.update(username,password)){
                return Result.ok(null);
            }

        }
        return Result.error(-1,"");

    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @RequestMapping(value = "/checkCookie",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public Result<String> checkCookie(@CookieValue(value = "login-cookie" , defaultValue = "") String cookie) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cookie))){
            return Result.ok(null);
        }
        else {
            return Result.error(-1,"");
        }

    }
}
