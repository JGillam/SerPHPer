package com.professionallyevil.serphp;

import burp.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;

public class SerPHP implements IBurpExtender, ITab{
    private JPanel mainPanel;
    private JTextField txtPHPSerializedInput;
    private JTextArea txtBase64EncodedInput;
    private JTextArea txtJsonInput;
    private JButton decodeButton;
    private JButton toJSONButton;
    private JButton encodeButton;
    private JButton toPHPButton;
    private IBurpExtenderCallbacks callbacks;
    private static String EXTENSION_NAME = "SerPHP";
    private static String VERSION = "0.1 (Alpha)";

    public SerPHP() {

        decodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBase64Input();
            }
        });
        toJSONButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePHPSerializedInput();
            }
        });
        encodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        toPHPButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePHPSerializedOutput();
            }
        });
    }

    private void updateBase64Input(){
        byte[] decodedBytes = callbacks.getHelpers().base64Decode(txtBase64EncodedInput.getText());
        String decodedString = callbacks.getHelpers().bytesToString(decodedBytes);
        txtPHPSerializedInput.setText(decodedString);
    }

    private void updatePHPSerializedInput(){
        String serializedPHP = txtPHPSerializedInput.getText();
        int i = 0;
        StringBuilder json = new StringBuilder();
        while (i<serializedPHP.length()) {
            Character c = serializedPHP.charAt(i);
            if(serializedPHP.charAt(i+1) == ':') {
                i+=2;
                PHPDataType data = null;
                switch (c){
                    case 's':
                        data = new PHPString(serializedPHP, i);
                        break;
                    case 'i':
                        data = new PHPInt(serializedPHP, i);
                        break;
                    case 'a':
                        data = new PHPArray(serializedPHP, i);
                        break;
                }

                if (data != null) {
                    i+= data.getLength();
                    json.append(data.getJSONValue());
                }
            }else {
                json.append("***Error: ").append(serializedPHP.substring(i));
                i += serializedPHP.substring(i).length();
            }
            json.append("\n");
            i+=1;
        }
        txtJsonInput.setText(json.toString());
    }

    private void updatePHPSerializedOutput(){
        String jsonText = txtJsonInput.getText().replace('\n', ' ');
        Matcher stringMatcher = PHPString.pattern.matcher(jsonText);
        Matcher intMatcher = PHPInt.pattern.matcher(jsonText);
        Matcher arrayMatcher = PHPArray.pattern.matcher(jsonText);
        PHPDataType data = null;
        if(stringMatcher.matches()) {
            data = new PHPString(jsonText.trim());
        }else if(intMatcher.matches()) {
            data = new PHPInt(jsonText.trim());
        }else if(arrayMatcher.find()){
            data = new PHPArray(arrayMatcher);
        }else{
            txtPHPSerializedInput.setText("***ERROR: Unrecognized format.");
        }
        if(data!=null) {
            txtPHPSerializedInput.setText(data.getPHPSerializedValue());
        }

    }

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        callbacks.addSuiteTab(this);
        callbacks.setExtensionName(EXTENSION_NAME);
        callbacks.printOutput("Started " + EXTENSION_NAME + " version " + VERSION);
    }

    @Override
    public String getTabCaption() {
        return EXTENSION_NAME;
    }

    @Override
    public Component getUiComponent() {
        return mainPanel;
    }

}

