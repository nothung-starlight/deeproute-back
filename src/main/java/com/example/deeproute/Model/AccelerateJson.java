package com.example.deeproute.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class AccelerateJson {
    public ArrayList<Accelerate> accelerates=new ArrayList<>();
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Accelerate{
        String time;
        String longitudinal_acceleration;
        String lateral_acceleration;
    }
}
