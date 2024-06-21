package com.example.deeproute.Service;

import com.example.deeproute.Dao.LabelDao;
import com.example.deeproute.Model.History;
import com.example.deeproute.Model.Label;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class LabelService {
    @Resource
    LabelDao labelDao;

    public boolean insertLabel(Label label) {
        return labelDao.insertLabel(label);
    }
    public Label getRecentLabel(Timestamp timestamp,int id){
        return labelDao.getRecentLabel(timestamp, id);
    }
    public List<Label> getAllLabel(int id){
        return  labelDao.getAllLabel(id);
    }
}
