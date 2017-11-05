package com.professionallyevil.serphp;

import java.util.regex.Pattern;

public class PHPInt implements PHPDataType {
    private String plainValue;
    static Pattern pattern = Pattern.compile("[0-9]+");

    PHPInt(String sphp, int start) {
        int end = sphp.indexOf(';', start);
        this.plainValue = sphp.substring(start, end);
    }

    PHPInt(String value) {
        this.plainValue = value;
    }

    @Override
    public String getPHPSerializedValue() {
        return "i:"+this.plainValue+";";
    }

    @Override
    public String getJSONValue() {
        return this.plainValue;
    }

    @Override
    public int getLength(){
        return plainValue.length();
    }
}
