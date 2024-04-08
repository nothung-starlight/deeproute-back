package com.example.deeproute.Service;

import com.example.deeproute.Dao.LabelInfoDao;
import com.example.deeproute.Model.LabelInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabelInfoService {
    @Resource
    LabelInfoDao labelInfoDao;

    public boolean insertLabelInfo(LabelInfo labelInfo) {
        return labelInfoDao.insertLabelInfo(labelInfo);
    }
}
