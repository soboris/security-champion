package com.secchamp.chal.controller;

import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.springframework.http.ResponseEntity;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

@RestController
public class XMLController {

    @PostMapping("/pages/xmlreader")
    public ResponseEntity<String> xxeDemo(@RequestBody String xmlContent) {
        try {
            // Parse the uploaded XML content
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xmlContent));
            Document document = builder.parse(inputSource);

            // Extract and print the root element's content
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expression = xPath.compile("/*");
            Node root = (Node) expression.evaluate(document, XPathConstants.NODE);
            
            // Convert the content of the XML document to a string
            StringBuilder parsedXml = new StringBuilder();
            parsedXml.append("Root element: ").append(root.getNodeName()).append("\n");
            
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                parsedXml.append("Node: ").append(child.getNodeName()).append(" - ").append(child.getTextContent()).append("\n");
            }

            // Return the parsed XML content in the response
            return ResponseEntity.ok("XML parsed successfully! Content:\n" + parsedXml.toString());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to parse XML: " + e.getMessage());
        }
    }
}
