package com.example.deeproute.Dao;

import com.example.deeproute.Model.History;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface HistoryDao {
    boolean insertHistory(History history);
    List<History> getHistoryList(String location,String date,String name,String brand, String model,String version,String path ,int currentPage);
    int getResultCnt(String location,String date,String name,String brand, String model,String version,String path);
    History getHistoryById(int id);
    boolean deleteHistory(int id);
}
