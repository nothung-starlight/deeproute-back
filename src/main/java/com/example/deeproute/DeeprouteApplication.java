package com.example.deeproute;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@SpringBootApplication
@MapperScan("com.example.deeproute.Dao")
public class DeeprouteApplication {

	public static void main(String[] args) {
		Timestamp time=new Timestamp(2100);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedTimestamp = sdf.format(time);
		System.out.println(formattedTimestamp);
		SpringApplication.run(DeeprouteApplication.class, args);
	}

}
