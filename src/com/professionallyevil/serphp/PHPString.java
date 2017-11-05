package com.professionallyevil.serphp;

import java.util.regex.Pattern;

public class PHPString implements PHPDataType{

    private String value;
    private int length;
    static Pattern pattern = Pattern.compile("^\\s*\"[\\p{Print}]*\"\\s*");

    PHPString(String phps, int start) {
        int firstDelim = phps.indexOf(':', start);
        String lengthString = phps.substring(start, firstDelim);
        int stringLength = Integer.parseInt(lengthString);
        this.value = phps.substring(firstDelim+1, firstDelim+3+stringLength);
        this.length = this.value.length() + lengthString.length() + 1;
        this.value = "\"" + this.value.substring(1,this.value.length()-1).replace("\"", "\\\"") + "\"";
    }
    PHPString(String value) {
        this.value = value;
    }

    @Override
    public String getJSONValue() {
        return value;
    }

    @Override
    public String getPHPSerializedValue() {
        return "s:" + (this.value.length()-2) + ":" + this.value + ";";
    }

    public int getLength() {
        return this.length;
    }
}
