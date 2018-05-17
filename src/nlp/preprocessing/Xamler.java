/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.preprocessing;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringTokenizer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author herley
 */
public class Xamler {

    public static void main(String[] args) {
        try {
            if (new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\hasilnyaLSTM.txt").exists()) {
                File fXmlFile = new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\test.xml");
                List<String> hasilnyaLSTM = FileUtils.readLines(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\hasilnyaLSTM.txt"), Charset.defaultCharset());

                List<String> ref = new ArrayList();
                for (String string : hasilnyaLSTM) {
                    if (!string.contains("*")) {
                        ref.add(string);
                    }
                }
                hasilnyaLSTM = ref;
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("review");

                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                // root elements
                Document docu = docBuilder.newDocument();
                Element rootElement = docu.createElement("corpus");
                docu.appendChild(rootElement);

                for (int i = 0; i < nList.getLength(); i++) {
                    List<String> penilaian = new StringTokenizer(hasilnyaLSTM.get(i).replace("[", "").replace("]", ""), ",").getTokenList();
                    int food = Integer.parseInt((StringUtils.trim(penilaian.get(0))));
                    int price = Integer.parseInt((StringUtils.trim(penilaian.get(1))));
                    int service = Integer.parseInt((StringUtils.trim(penilaian.get(2))));
                    int ambience = Integer.parseInt((StringUtils.trim(penilaian.get(3))));
                    Node nNode = nList.item(i);
                    if (nNode != null && nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String textContent = eElement.getElementsByTagName("text").item(0).getTextContent();
//                        System.out.println(textContent);

                        Element review = docu.createElement("review");
                        review.setAttribute("rid", String.valueOf(i));
                        rootElement.appendChild(review);

                        Element text = docu.createElement("text");
                        text.appendChild(docu.createTextNode(textContent));
                        review.appendChild(text);

                        Element aspects = docu.createElement("aspects");

                        Element aspectFood = docu.createElement("aspect");
                        aspectFood.setAttribute("category", "FOOD");
                        if (food != 0) {
                            if (food == 1) {
                                aspectFood.setAttribute("polarity", "POSITIVE");
                            } else {
                                aspectFood.setAttribute("polarity", "NEGATIVE");
                            }
                            aspects.appendChild(aspectFood);
                        }

                        Element aspectPrice = docu.createElement("aspect");
                        aspectPrice.setAttribute("category", "PRICE");
                        if (price != 0) {
                            if (price == 1) {
                                aspectPrice.setAttribute("polarity", "POSITIVE");
                            } else {
                                aspectPrice.setAttribute("polarity", "NEGATIVE");
                            }
                            aspects.appendChild(aspectPrice);
                        }

                        Element aspectService = docu.createElement("aspect");
                        aspectService.setAttribute("category", "SERVICE");
                        if (service != 0) {
                            if (service == 1) {
                                aspectService.setAttribute("polarity", "POSITIVE");
                            } else {
                                aspectService.setAttribute("polarity", "NEGATIVE");
                            }
                            aspects.appendChild(aspectService);
                        }
                        Element aspectAmbience = docu.createElement("aspect");
                        aspectAmbience.setAttribute("category", "AMBIENCE");
                        if (ambience != 0) {
                            if (ambience == 1) {
                                aspectAmbience.setAttribute("polarity", "POSITIVE");
                            } else {
                                aspectAmbience.setAttribute("polarity", "NEGATIVE");
                            }
                            aspects.appendChild(aspectAmbience);
                        }
                        review.appendChild(aspects);
                    }
                }

//                / write the content into xml file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(docu);
                StreamResult result = new StreamResult(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\heroku\\herokuLSTM.xml"));
                transformer.transform(source, result);
                System.out.println("File saved!");
            }

            if (new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\hasilnyaDNN.txt").exists()) {
                File fXmlFile = new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\test.xml");
                List<String> hasilnyaLSTM = FileUtils.readLines(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\hasilnyaLSTM.txt"), Charset.defaultCharset());

                List<String> ref = new ArrayList();
                for (String string : hasilnyaLSTM) {
                    if (!string.contains("*")) {
                        ref.add(string);
                    }
                }
                hasilnyaLSTM = ref;
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("review");

                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                // root elements
                Document docu = docBuilder.newDocument();
                Element rootElement = docu.createElement("corpus");
                docu.appendChild(rootElement);

                for (int i = 0; i < nList.getLength(); i++) {
                    List<String> penilaian = new StringTokenizer(hasilnyaLSTM.get(i).replace("[", "").replace("]", ""), ",").getTokenList();
                    int food = Integer.parseInt((StringUtils.trim(penilaian.get(0))));
                    int price = Integer.parseInt((StringUtils.trim(penilaian.get(1))));
                    int service = Integer.parseInt((StringUtils.trim(penilaian.get(2))));
                    int ambience = Integer.parseInt((StringUtils.trim(penilaian.get(3))));
                    Node nNode = nList.item(i);
                    if (nNode != null && nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String textContent = eElement.getElementsByTagName("text").item(0).getTextContent();
//                        System.out.println(textContent);

                        Element review = docu.createElement("review");
                        review.setAttribute("rid", String.valueOf(i));
                        rootElement.appendChild(review);

                        Element text = docu.createElement("text");
                        text.appendChild(docu.createTextNode(textContent));
                        review.appendChild(text);

                        Element aspects = docu.createElement("aspects");

                        Element aspectFood = docu.createElement("aspect");
                        aspectFood.setAttribute("category", "FOOD");
                        if (food != 0) {
                            if (food == 1) {
                                aspectFood.setAttribute("polarity", "POSITIVE");
                            } else {
                                aspectFood.setAttribute("polarity", "NEGATIVE");
                            }
                            aspects.appendChild(aspectFood);
                        }

                        Element aspectPrice = docu.createElement("aspect");
                        aspectPrice.setAttribute("category", "PRICE");
                        if (price != 0) {
                            if (price == 1) {
                                aspectPrice.setAttribute("polarity", "POSITIVE");
                            } else {
                                aspectPrice.setAttribute("polarity", "NEGATIVE");
                            }
                            aspects.appendChild(aspectPrice);
                        }

                        Element aspectService = docu.createElement("aspect");
                        aspectService.setAttribute("category", "SERVICE");
                        if (service != 0) {
                            if (service == 1) {
                                aspectService.setAttribute("polarity", "POSITIVE");
                            } else {
                                aspectService.setAttribute("polarity", "NEGATIVE");
                            }
                            aspects.appendChild(aspectService);
                        }
                        Element aspectAmbience = docu.createElement("aspect");
                        aspectAmbience.setAttribute("category", "AMBIENCE");
                        if (ambience != 0) {
                            if (ambience == 1) {
                                aspectAmbience.setAttribute("polarity", "POSITIVE");
                            } else {
                                aspectAmbience.setAttribute("polarity", "NEGATIVE");
                            }
                            aspects.appendChild(aspectAmbience);
                        }
                        review.appendChild(aspects);
                    }
                }

//                / write the content into xml file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(docu);
                StreamResult result = new StreamResult(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\heroku\\herokuDNN.xml"));
                transformer.transform(source, result);
                System.out.println("File saved!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
