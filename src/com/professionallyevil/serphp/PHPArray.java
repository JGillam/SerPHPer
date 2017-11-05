package com.professionallyevil.serphp;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PHPArray implements PHPDataType{

    private PHPDataType[] elements;
    private String jsonValue = "";
    private int length = 0;
    static Pattern pattern = Pattern.compile("\\{\\s*(((\"[\\p{Print}]+?\"):\\s*((\"[\\p{Print}]*?\")|([0-9]+))\\s*,?\\s*)*)}");
    static Pattern pairPattern = Pattern.compile("\\s*(\"[\\p{Print}]*?\")|([0-9]+)\\s*[:,]?");

    PHPArray(String sphp, int start) {
        int nextDelimiter = sphp.indexOf(':', start);
        String arraySizeString = sphp.substring(start, nextDelimiter);
        int arraySize = Integer.parseInt(arraySizeString);
        int openBracket = sphp.indexOf('{', nextDelimiter);
        this.elements = new PHPDataType[arraySize * 2];

        int i = openBracket + 1;
        for(int e=0; e < elements.length; e++) {
            if(sphp.charAt(i+1) == ':') {
                Character type = sphp.charAt(i);
                i += 2;
                PHPDataType data = null;
                switch (type) {
                    case 's':
                        data = new PHPString(sphp, i);
                        break;
                    case 'i':
                        data = new PHPInt(sphp, i);
                        break;
                }
                if (data != null) {
                    i += data.getLength();
                    elements[e] = data;
                }
            }
            i++;
        }
        int closeBracket = sphp.indexOf('}', i);
        this.length = closeBracket - openBracket + arraySizeString.length() + 1;
        System.out.println("String: "+sphp.substring(start, start+this.length));
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        for(int e=0; e<elements.length; e+=2) {
            buf.append("\n  ");
            buf.append(elements[e].getJSONValue());
            buf.append(':');
            buf.append(elements[e+1].getJSONValue());
            buf.append(',');
        }
        if (elements.length > 1) {
            buf.deleteCharAt(buf.length() - 1);
        }
        buf.append("\n}");
        this.jsonValue = buf.toString();
    }

    PHPArray(Matcher m){
        String contents = m.group(0).trim();
        contents = contents.substring(1,contents.length()-1).trim();
        System.out.println(contents);
        Matcher pairMatcher = pairPattern.matcher(contents);
        ArrayList<PHPDataType> elementList = new ArrayList<>();
        while(pairMatcher.find()){
            String value = pairMatcher.group().trim();
            if(PHPString.pattern.matcher(value).matches()) {
                elementList.add(new PHPString(value));
            }else if(PHPInt.pattern.matcher(value).matches()){
                elementList.add(new PHPInt(value));
            }else{
                // TODO: Error?
            }
        }
        this.elements = new PHPDataType[elementList.size()];
        this.elements = elementList.toArray(this.elements);
    }

    @Override
    public String getJSONValue() {
        return this.jsonValue;
    }

    @Override
    public String getPHPSerializedValue() {
        StringBuilder buf = new StringBuilder();
        buf.append("a:");
        buf.append(elements.length/2);
        buf.append(":{");
        for(PHPDataType element: elements) {
            buf.append(element.getPHPSerializedValue());
        }
        buf.append("}");
        return buf.toString();
    }

    @Override
    public int getLength() {
        return this.length;
    }

    public static void main(String[] args) {
        // PHPArray a = new PHPArray("a:3:{s:7:\"bgctype\";s:1:\"E\";s:6:\"eecode\";s:4:\"6891\";s:4:\"apid\";s:0:\"\";}", 2);
        // System.out.println(a.getJSONValue());
        String jsonString = "{ \"bgctype\":\"E\",  \"eecode\":\"68'-91\", \"apid\":12345 }";
        Matcher m = pattern.matcher(jsonString);
        if(m.matches()) {
            PHPArray array = new PHPArray(m);
            System.out.println(array.getPHPSerializedValue());
        }else {
            System.out.println("Does not match");
        }
//        System.out.println("Groups: "+m.groupCount());
//
//        for(int i=0; i< m.groupCount(); i ++){
//            System.out.println("Group "+i+": "+m.group(i));
//        }

    }
}
