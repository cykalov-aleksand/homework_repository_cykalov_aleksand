package org.example;


import com.google.common.base.CharMatcher;

import java.util.Random;

public class ArraySimulator {
    public Integer[]simulatorArrayInteger(int size,int min,int max){
        Integer[]integersArray=new Integer[size];
        for (int i=0;i<integersArray.length;i++){
            Random rand = new Random();
           integersArray[i]=rand.nextInt((max - min) + 1) + min;
        }
        return integersArray;
    }
    public String[] simulatorArrayString(String string){
        return CharMatcher.is('\n').replaceFrom(string, ' ').replaceAll("\\p{Punct}", "")
                .split(" ");
        }
}
