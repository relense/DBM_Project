package utils.transformations;

import metamodels.Attribute;
import metamodels.Class;
import metamodels.Model;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utils.builder.Builder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
/**
 * Created by Miguel on 20/04/2017.
 */

import MyModels.MyModels;

public class M2M {

    /**
     * Method to get the model from a XML file
     * @param filename the path to the file
     * @return a model
     */
    public static Model getModel(String filename) {
        Builder builder = new Builder();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new File(filename));

            // Get model node
            Node modelNode = document.getDocumentElement();
            String modelName = modelNode.getAttributes()
                    .getNamedItem("name").getNodeValue();

            NodeList classes = modelNode.getChildNodes();
            Model model = new Model(modelName);

            // Do M2M transformation
            for (int i = 0; i < classes.getLength(); i++) {

                if (classes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    String className = classes.item(i).getAttributes().getNamedItem("name").getNodeValue();
                    Class clazz = new Class(className, modelName);
                    NodeList attributes = classes.item(i).getChildNodes();

                    getClass(clazz, attributes);

                    model.addClass(clazz);
                }
            }

            model.setClasses(builder.makeRelations(model));

            return model;

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method to get a class from a list of attributes.
     * @param clazz Class we are building
     * @param attributes Nodes we get from and xml file. From them we get the Attributes for our Class
     */
    public static void getClass(Class clazz, NodeList attributes) {
        for (int j = 0; j < attributes.getLength(); j++) {
            if (attributes.item(j).getNodeType() == Node.ELEMENT_NODE) {

                switch (attributes.item(j).getNodeName()) {
                    case "attribute":
                        String name = attributes.item(j).getAttributes().getNamedItem("name").getNodeValue();
                        String type = attributes.item(j).getAttributes().getNamedItem("type").getNodeValue();
                        String required = attributes.item(j).getAttributes().getNamedItem("required").getNodeValue();

                        clazz.addAttribute(new Attribute(name, type, required.equalsIgnoreCase("true") ? true : false));
                        break;

                    case "relation":
                        String nameRelation = attributes.item(j).getAttributes().getNamedItem("name").getNodeValue();
                        String typeRelation = attributes.item(j).getAttributes().getNamedItem("type").getNodeValue();

                        clazz.addRelations(nameRelation, typeRelation);

                        break;
                }
            }
        }
    }

    /**
     * Method to get a model from a XMI file
     * @param filename the file path to the model
     * @return a model
     */
    public static Model getXMIModel(String filename) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new File(filename));
            // Get model node

            Node modelNode = document.getDocumentElement();
            String modelName = modelNode.getAttributes()
                    .getNamedItem("name").getNodeValue();
            NodeList classes = modelNode.getChildNodes();
            //instance model
            Model model = new Model(modelName);

            for (int i = 0; i < classes.getLength(); i++) {

                if (classes.item(i).getNodeType() == Node.ELEMENT_NODE) {

                    if (classes.item(i).getNodeName().equals("packagedElement")) {
                        Class c = new Class(classes.item(i).getAttributes().getNamedItem("name").getNodeValue(), modelName);
                        NodeList attributes = classes.item(i).getChildNodes();

                        for (int j = 0; j < attributes.getLength(); j++){
                            if(attributes.item(j).getNodeType() == Node.ELEMENT_NODE){
                                String name = attributes.item(j).getAttributes().getNamedItem("name").getNodeValue();
                                String type = attributes.item(j).getChildNodes().item(1).getAttributes().getNamedItem("href").getNodeValue().split("#")[1];
                                if(type.equalsIgnoreCase("INTEGER")){
                                    type = "int";
                                }
                                Attribute at = new Attribute(name, type, true);

                                c.addAttribute(at);
                            }
                        }
                        model.addClass(c);
                    }

                }
            }

            return model;

            // Do M2M transformation
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}



