package org.example;

public class ExecutableObject {
    private int step=1;
    private boolean ascending=true;
    private static final int LIMIT=10;
    public int result(){
        int current=step;
        if(ascending){
            step++;
            if (step > LIMIT-1) {
                ascending=false;
            }
        }else{
            step--;
            if(step<=1){
                ascending=true;
            }
        }
        return current;
    }
}
