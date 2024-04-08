package com.example.deeproute.Service;

import com.example.deeproute.Dao.HistoryDao;
import com.example.deeproute.Model.History;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HistoryService {
    @Resource
    HistoryDao historyDao;

    public boolean insertHistory(History history) {
        return historyDao.insertHistory(history);
    }
    public List<History> getHistoryList(String location, String date, String name, String brand, String model, String version, String path , int currentPage){
        return  historyDao.getHistoryList(location,date,name,brand,model,version,path,currentPage);
    }
    public int getResultCnt(String location,String date,String name,String brand, String model,String version,String path ){
        return historyDao.getResultCnt(location,date,name,brand,model,version,path);
    }
    public History getHistoryById(int id){
        return historyDao.getHistoryById(id);
    }
    public boolean deleteHistory(int id){
        return historyDao.deleteHistory(id);
    }
}
