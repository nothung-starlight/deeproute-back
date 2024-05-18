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
	public static String historyPath;
	public static void main(String[] args) throws FileNotFoundException {
//		// 实例化 PrintStream 对象，并重定向标准输出流到文件
//		String filePath="launch.log";
//		File file = new File(filePath);
//		PrintStream printStream = new PrintStream(new FileOutputStream(file));
//		System.setOut(printStream);

		try {
			runCommand("cmd /c python -m pip install whisper");
			// 读取文件内容并存储到historyPath变量中
			historyPath = new String(Files.readAllBytes(Paths.get("path.txt")))+ File.separator + "historyFiles" + File.separator;
			// 打印读取的文件内容
			System.out.println("读取的文件内容为: " + historyPath);
			File historyFolder = new File(historyPath);
			if (!historyFolder.exists()) {
				Files.createDirectories(Paths.get(historyPath));
			}
		} catch (IOException e) {
			// 处理文件读取过程中可能出现的异常
			e.printStackTrace();
		}
		SpringApplication.run(DeeprouteApplication.class, args);
	}
	public static void runCommand(String command) throws IOException {
		ProcessBuilder pb = new ProcessBuilder("cmd", "/c", command);
		pb.redirectErrorStream(true);
		Process process = pb.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}

	}
}
