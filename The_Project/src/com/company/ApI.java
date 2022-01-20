package com.company;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ApI {
    public String Search_word(String word) throws ParserConfigurationException, IOException, SAXException {
        String head = new String("http://www.dictionaryapi.com/api/v1/references/collegiate/xml/");
        String word1 = word;
        String apiKey = new String("?key=82e84262-982c-4c82-9818-d579f1af204f"); //My API Key for Merriam webster
        String finalURL = head.trim() + word1.trim() + apiKey.trim();
        // ArrayList<String> api = new String[200];
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder b = f.newDocumentBuilder();
            Document doc = b.parse(finalURL);

            doc.getDocumentElement().normalize();

            NodeList items = doc.getElementsByTagName("entry");
            for (int i = 0; i < items.getLength(); i++) {
                Node n = items.item(i);

                if (n.getNodeType() != Node.ELEMENT_NODE)
                    continue;

                Element e = (Element) n;
                NodeList titleList = e.getElementsByTagName("dt");
                for (int j = 0; j < titleList.getLength(); j++) {
                    Node dt = titleList.item(0);
                    if (dt.getNodeType() != Node.ELEMENT_NODE)
                        continue;
                    Element titleElem = (Element) titleList.item(j);
                    Node titleNode = titleElem.getChildNodes().item(0);
                    if(titleNode.getNodeValue() != null) {
                        //System.out.println(word + " " + titleNode.getNodeValue());
                        String string = (String) titleNode.getNodeValue();
                        String string1 = word+" "+ string;
                        String []tokens = StringUtils.split(string," ");
                        if(tokens.length>2)
                        {
                            //System.out.println(string1);
                            return string1;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("No word exists\n");;
        }
        return "";

    }



    public static void main(String[] args) throws Exception {
        ApI apI = new ApI();
        String searched_word = apI.Search_word("fuck");
        if(searched_word.length()>0)
            System.out.println(searched_word);
        //else
        // System.out.println("No word exists");
    }
}