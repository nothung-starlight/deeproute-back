package com.example.deeproute;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;



@SpringBootApplication
@MapperScan("com.example.deeproute.Dao")
public class DeeprouteApplication {


	public static void main(String[] args) throws FileNotFoundException {

		SpringApplication.run(DeeprouteApplication.class, args);
	}

}
