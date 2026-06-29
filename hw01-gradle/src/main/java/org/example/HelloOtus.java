package org.example;

import com.google.common.base.Strings;

public class HelloOtus {
    private String element;

    public void setElement(String element) {
        this.element = element;
    }

    public String getElement() {
        return element;
    }

    public String demonstration(){
        if(Strings.isNullOrEmpty(element)){
return "Привет Otus!";
        }
        return element;
    }

}
