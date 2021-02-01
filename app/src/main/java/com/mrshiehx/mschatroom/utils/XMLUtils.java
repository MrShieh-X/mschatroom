package com.mrshiehx.mschatroom.utils;

import com.mrshiehx.mschatroom.MSCRApplication;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.xml.XMLContentHandler;
import com.mrshiehx.mschatroom.xml.user_information.UserInformation;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

//XML工具类
public class XMLUtils {
    public static List<UserInformation> readXmlBySAX(InputStream file) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser saxParser = spf.newSAXParser();
            XMLContentHandler handler = new XMLContentHandler();
            saxParser.parse(file, handler);
            file.close();
            return handler.getUserInformation();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.exceptionDialog(MSCRApplication.getContext(),e, MSCRApplication.getContext().getString(R.string.dialog_exception_parsing_xml_failed));
        }
        return null;
    }
}
