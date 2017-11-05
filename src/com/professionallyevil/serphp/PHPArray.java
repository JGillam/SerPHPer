package com.professionallyevil.serphp;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An PHP Array, for example a:1:{s:3:"foo"; s:3:"bar";}
 */
public class PHPArray implements PHPDataType{

    private PHPDataType[] elements;
    private String jsonValue = "";
    private int length = 0;
    static Pattern pattern = Pattern.compile("\\{\\s*(((\"[\\p{Print}]+?\"):\\s*((\"[\\p{Print}]*?\")|([0-9]+))\\s*,?\\s*)*)}");
    private static Pattern pairPattern = Pattern.compile("\\s*(\"[\\p{Print}]*?\")|([0-9]+)\\s*[:,]?");

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

    PHPArray(String contents){
        contents = contents.substring(1,contents.length()-1).trim();  // strip out { }
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

}
