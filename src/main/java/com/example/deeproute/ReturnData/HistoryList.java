package com.example.deeproute.ReturnData;

import com.example.deeproute.Model.History;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@lombok.Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryList {
    private int resultCnt;
    private List<History> historyArrayList;
}
