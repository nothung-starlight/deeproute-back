package com.example.deeproute.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class LabelJson {
    ArrayList<Label> labels=new ArrayList<>();
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Label{
        public String time;
        public String labelInfo;
    }

}
