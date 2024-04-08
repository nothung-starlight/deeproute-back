package com.example.deeproute.Dao;

import com.example.deeproute.Model.History;
import com.example.deeproute.Model.Label;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface LabelDao {
    boolean insertLabel(Label label);
    Label getRecentLabel(Timestamp timestamp,int id);
}
