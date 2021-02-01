package com.mrshiehx.mschatroom.xml;

import com.mrshiehx.mschatroom.xml.user_information.UserInformation;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class XMLContentHandler extends DefaultHandler {

    private List<UserInformation> userInformation = null;
    private UserInformation currentUserInformation;

    public List<UserInformation> getUserInformation() {
        return userInformation;
    }

    @Override
    public void startDocument() throws SAXException {
        userInformation = new ArrayList<>();
    }

    @Override
    public void startElement(String uri/*命名空间*/, String localName/*不带命名空间前缀的标签名*/, String qName/*带命名空间前缀的标签名*/, /*可以得到所有的属性名和对应的值*/Attributes attributes) throws SAXException {
        if (localName.equals("name")) {
            currentUserInformation = new UserInformation();
            currentUserInformation.setNameContent(attributes.getValue("content"));
        } else if (localName.equals("gender")) {
            currentUserInformation = new UserInformation();
            currentUserInformation.setGenderContent(attributes.getValue("content"));
        } else if (localName.equals("what_s_up")) {
            currentUserInformation = new UserInformation();
            currentUserInformation.setWhatsupContent(attributes.getValue("content"));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("name") || localName.equals("gender") || localName.equals("what_s_up")) {
            userInformation.add(currentUserInformation);
            currentUserInformation = null;
        }
    }


}