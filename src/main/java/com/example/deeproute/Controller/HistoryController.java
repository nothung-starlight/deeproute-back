package com.example.deeproute.Controller;

import com.example.deeproute.Model.*;
import com.example.deeproute.ReturnData.HistoryList;
import com.example.deeproute.ReturnData.Result;
import com.example.deeproute.Service.HistoryService;
import com.example.deeproute.Service.LabelInfoService;
import com.example.deeproute.Service.LabelService;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.example.deeproute.config.WebConfig.historyPath;


@RestController
@RequestMapping(value = "/history")
public class HistoryController {
    private static int currentHistoryId=1;

    @Resource
    HistoryService historyService;
    @Resource
    LabelService labelService;
    @Resource
    LabelInfoService labelInfoService;
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = "application/json;charset=utf-8", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> upload(@RequestParam("location") String location,
                                 @RequestParam("name") String name,
                                 @RequestParam("brand") String brand,
                                 @RequestParam("model") String model,
                                 @RequestParam("version") String version,
                                 @RequestParam("path") String path,
                                 @RequestParam("info") String info,
                                 @RequestParam("file") MultipartFile file,
                                 @CookieValue(value = "login-cookie", defaultValue = "") String cookie)  {
        String filename = UUID.randomUUID().toString();
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(cookie))) {
                redisTemplate.expire(cookie, 1, TimeUnit.HOURS);
                History history = new History();
                history.setLocation(location);
                history.setName(name);
                history.setBrand(brand);
                history.setModel(model);
                history.setVersion(version);
                history.setPath(path);
                history.setInfo(info);

                // 将MultipartFile转换为File
                Path tempDir = Files.createTempDirectory("tempDir");
                File tempFile = tempDir.resolve(filename + ".zip").toFile();
                file.transferTo(tempFile);

                // 解压ZIP文件
                String destDirectory = historyPath + filename;
                File destFile = new File(destDirectory);
                destFile.mkdirs();
                filename = filename + File.separator + unzip(tempFile, destFile);
                // 删除临时文件
                tempFile.delete();

                history.setSource(filename);

                File accelerateJson = new File(historyPath + filename + "accelerate.txt");
                File labelJson = new File(historyPath + filename + "label.txt");

                BufferedReader reader1 = new BufferedReader(new FileReader(accelerateJson));
                StringBuilder stringBuilder1 = new StringBuilder();
                String line1;
                while ((line1 = reader1.readLine()) != null) {
                    stringBuilder1.append(line1);
                }
                reader1.close();

                // 将 JSON 字符串反序列化为对象
                Gson gson = new Gson();
                AccelerateJson accelerates = gson.fromJson(stringBuilder1.toString(), AccelerateJson.class);
                String format = "yyyy-MM-dd-HH-mm-ss"; // 日期时间字符串的格式
                SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                Date parsedDate = dateFormat.parse(accelerates.accelerates.get(0).getTime());
                Timestamp timestamp1 = new Timestamp(parsedDate.getTime());
                history.setTime(timestamp1);

                BufferedReader reader2 = new BufferedReader(new FileReader(labelJson));
                StringBuilder stringBuilder2 = new StringBuilder();
                String line2;
                while ((line2 = reader2.readLine()) != null) {
                    stringBuilder2.append(line2);
                }
                reader2.close();

                LabelJson labels = gson.fromJson(stringBuilder2.toString(), LabelJson.class);

                historyService.insertHistory(history);
                int history_id = history.getId();

                for(int i=0;i<labels.getLabels().size();i++){
                    Timestamp timestamp2 = new Timestamp(dateFormat.parse(labels.getLabels().get(i).getTime()).getTime());
                    Label label=new Label();
                    label.setHistory_id(history_id);
                    label.setTime(timestamp2);
                    label.setName(labels.getLabels().get(i).getLabelInfo());
                    labelService.insertLabel(label);
                }

                File labelAudio = new File(historyPath + filename + "labelAudio");
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                        if (labelAudio.exists() && labelAudio.isDirectory()) {
                            // 获取文件夹中的所有文件
                            File[] files = labelAudio.listFiles();
                            if(files!=null)
                            {// 遍历文件夹中的每个文件
                                for (File file : files) {
                                    // 检查文件是否是 MP3 文件
                                    if (file.isFile() && file.getName().toLowerCase().endsWith(".mp3")) {
                                        LabelInfo labelInfo=new LabelInfo();
                                        labelInfo.setSource(file.getName());
                                        Date parsedDate = dateFormat.parse(file.getName());
                                        Timestamp timestamp = new Timestamp(parsedDate.getTime());
                                        labelInfo.setLabel_id(labelService.getRecentLabel(timestamp,history_id).getId());
                                        try{
                                            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "python", "audio.py", file.getPath());
                                            pb.redirectErrorStream(true);
                                            Process process = pb.start();
                                            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                                            String line;
                                            while ((line = reader.readLine()) != null) {
                                                labelInfo.setInfo(line);
                                                System.out.println(line);
                                            }
                                        }
                                        catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        labelInfoService.insertLabelInfo(labelInfo);
                                    }
                                }
                            }
                        }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                return Result.ok(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        deleteDirectory(Path.of(historyPath + filename));
        return Result.error(-1, "");
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @RequestMapping(value = "/getHistoryList", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public Result<HistoryList> getHistoryList(@RequestParam(value = "location",defaultValue = "") String location,
                                          @RequestParam(value = "date",defaultValue = "") String date,
                                          @RequestParam(value = "name",defaultValue = "") String name,
                                          @RequestParam(value = "brand",defaultValue = "") String brand,
                                          @RequestParam(value = "model",defaultValue = "") String model,
                                          @RequestParam(value = "version",defaultValue = "") String version,
                                          @RequestParam(value = "path",defaultValue = "") String path,
                                          @RequestParam(value = "currentPage",defaultValue = "1") int currentPage,
                                          @CookieValue(value = "login-cookie", defaultValue = "") String cookie)  {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(cookie))) {
                redisTemplate.expire(cookie, 1, TimeUnit.HOURS);
                HistoryList historyList=new HistoryList(historyService.getResultCnt(location,date,name,brand,model,version,path),
                        historyService.getHistoryList(location,date,name,brand,model,version,path,currentPage));
                return Result.ok(historyList);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return Result.error(-1, "");
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @RequestMapping(value = "/setCurrentHistoryId", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public Result<String> setCurrentHistoryId(@RequestParam(value = "id",defaultValue = "1") int id,
                                        @CookieValue(value = "login-cookie", defaultValue = "") String cookie)  {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(cookie))) {
                redisTemplate.expire(cookie, 1, TimeUnit.HOURS);
                currentHistoryId=id;
                System.out.println(currentHistoryId);
                return Result.ok(null);

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return Result.error(-1, "");
    }
    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @RequestMapping(value = "/deleteHistory", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public Result<String> deleteHistory(@RequestParam(value = "id",defaultValue = "-1") int id,
                                        @CookieValue(value = "login-cookie", defaultValue = "") String cookie)  {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(cookie))) {
                redisTemplate.expire(cookie, 1, TimeUnit.HOURS);
                History history;
                if ((history=historyService.getHistoryById(id))!=null){
                    if (historyService.deleteHistory(id)){
                        deleteDirectory(Path.of(historyPath + history.getSource()));
                        return Result.ok(null);
                    }
                }


            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return Result.error(-1, "");
    }
    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @RequestMapping(value = "/initialize", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public Result<History> initialize(@CookieValue(value = "login-cookie", defaultValue = "") String cookie)  {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(cookie))) {
                redisTemplate.expire(cookie, 1, TimeUnit.HOURS);
                return Result.ok(historyService.getHistoryById(currentHistoryId));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return Result.error(-1, "");
    }

    public static String unzip(File zipFile, File destDir) {
        String name = "";
        boolean first = true;
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile), Charset.forName("GBK"))) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                if (first) {
                    if (!entry.getName().equals("cam110/")) {
                        name = entry.getName().replace("/", "") + File.separator;
                    }
                    first = false;
                }
                File file = new File(destDir, entry.getName());

                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                entry = zis.getNextEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static void deleteDirectory(Path directory) {
        // 如果目录不存在，则直接返回
        if (!Files.exists(directory)) {
            return;
        }
        try
        {// 遍历目录中的所有文件和子目录
            Files.walk(directory)
                    .sorted((p1, p2) -> -p1.compareTo(p2)) // 逆序遍历，确保子目录先被删除
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path); // 删除文件或空目录
                        } catch (IOException e) {
                            e.printStackTrace(); // 处理删除失败的情况
                        }
                    });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @RequestMapping(value = "/getAccelerate", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public Result<AccelerateJson> getAccelerate(@CookieValue(value = "login-cookie", defaultValue = "") String cookie)  {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(cookie))) {
                Gson gson = new Gson();
                History history=historyService.getHistoryById(currentHistoryId);

                File file = new File(historyPath+history.getSource()+"accelerate.txt"); // 替换为你的JSON文件路径
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder jsonContent = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    jsonContent.append(line);
                }

                reader.close();
                AccelerateJson accelerates = gson.fromJson(jsonContent.toString(), AccelerateJson.class);
                return Result.ok(accelerates);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return Result.error(-1, "");
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @RequestMapping(value = "/getLane", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public Result<Lanes> getLane(@CookieValue(value = "login-cookie", defaultValue = "") String cookie)  {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(cookie))) {
                Gson gson = new Gson();
                History history=historyService.getHistoryById(currentHistoryId);

                File file = new File(historyPath+history.getSource()+"lane.txt"); // 替换为你的JSON文件路径
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder jsonContent = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    jsonContent.append(line);
                }

                reader.close();
                Lanes lanes = gson.fromJson(jsonContent.toString(), Lanes.class);
                return Result.ok(lanes);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return Result.error(-1, "");
    }
    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @RequestMapping(value = "/getHistory", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public Result<History> getHistory(@CookieValue(value = "login-cookie", defaultValue = "") String cookie)  {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(cookie))) {
                History history=historyService.getHistoryById(currentHistoryId);
                return Result.ok(history);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return Result.error(-1, "");
    }
    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @RequestMapping(value = "/getLabel", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public Result<Label> getLabel(@RequestParam(value = "duration",defaultValue = "") int duration,
            @CookieValue(value = "login-cookie", defaultValue = "") String cookie)  {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(cookie))) {
                History history=historyService.getHistoryById(currentHistoryId);
                // Assuming 'duration' is in milliseconds
                long durationInMillis = history.getTime().getTime() + duration;

                Timestamp timestamp = new Timestamp(durationInMillis);

                Label label = labelService.getRecentLabel(timestamp, currentHistoryId);
                return Result.ok(label);

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return Result.error(-1, "");
    }
    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @RequestMapping(value = "/getAllLabel", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public Result<List<Label>> getAllLabel(@CookieValue(value = "login-cookie", defaultValue = "") String cookie)  {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(cookie))) {
                return Result.ok(labelService.getAllLabel(currentHistoryId));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return Result.error(-1, "");
    }

}
