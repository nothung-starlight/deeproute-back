package com.example.deeproute.Dao;

import com.example.deeproute.Model.Label;
import com.example.deeproute.Model.LabelInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelInfoDao {
    boolean insertLabelInfo(LabelInfo labelInfo);
}
