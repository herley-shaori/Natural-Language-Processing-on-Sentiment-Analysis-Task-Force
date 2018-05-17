/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.preprocessing;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Random;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author herley
 */
public class ValidationSetToJSON {

    public static void main(String[] args) {
        try {
            Reader in = new FileReader("D:\\Netbeans Project\\NLP Preprocessing\\dua\\translate_review.csv");
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
            final ObjectMapper objectMapper = new ObjectMapper();
            String total = "";

            File fXmlFile = new File("D:\\Netbeans Project\\NLP Preprocessing\\dua\\training_set.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("review");

            int counter = 0;
            for (CSVRecord record : records) {
                String translate = record.get(1).replaceAll("[<>\\\\[\\\\]'`~!@#$%^&*+,.-]", "").replace("\"", "").replace("[", "").replace("]", "");
                Node nNode = nList.item(counter);
                Review review = new Review();

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String textContent = eElement.getElementsByTagName("text").item(0).getTextContent();
                    Node anotatorSatu = eElement.getElementsByTagName("aspects").item(0);
                    int food = 0, price = 0, ambience = 0, service = 0;
                    boolean presentFood = false, presentPrice = false, presentAmbience = false, presentService = false;

                    if (anotatorSatu != null) {
                        NodeList childNodes = anotatorSatu.getChildNodes();
                        for (int i = 0; i < childNodes.getLength(); i++) {
                            Node node = childNodes.item(i);
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element elem = (Element) node;
                                if (elem.getAttribute("category").equals("FOOD")) {
                                    if (elem.getAttribute("polarity").equals("POSITIVE")) {
                                        food++;
                                    } else {
                                        food--;
                                    }
                                    presentFood = true;
                                } else if (elem.getAttribute("category").equals("SERVICE")) {
                                    if (elem.getAttribute("polarity").equals("POSITIVE")) {
                                        service++;
                                    } else {
                                        service--;
                                    }
                                    presentService = true;
                                } else if (elem.getAttribute("category").equals("PRICE")) {
                                    if (elem.getAttribute("polarity").equals("POSITIVE")) {
                                        price++;
                                    } else {
                                        price--;
                                    }
                                    presentPrice = true;
                                } else if (elem.getAttribute("category").equals("AMBIENCE")) {
                                    if (elem.getAttribute("polarity").equals("POSITIVE")) {
                                        ambience++;
                                    } else {
                                        ambience--;
                                    }
                                    presentAmbience = true;
                                }
                            }
                        }
                    }

                    if (food == 0) {
                        if (presentFood) {
                            final Random random = new Random();
                            if (random.nextInt(2) == 0) {
                                review.setFoodPolarityInteger(Review.NEGATIVE);
                                review.setFoodPolarity(Review.negative);
                            } else {
                                review.setFoodPolarityInteger(Review.POSITIVE);
                                review.setFoodPolarity(Review.positive);
                            }
                        } else {
                            review.setFoodPolarityInteger(Review.UNKNOWN);
                            review.setFoodPolarity(Review.unknown);
                        }
                    } else if (food > 0) {
                        review.setFoodPolarity(Review.positive);
                        review.setFoodPolarityInteger(Review.POSITIVE);
                    } else {
                        review.setFoodPolarity(Review.negative);
                        review.setFoodPolarityInteger(Review.NEGATIVE);
                    }
                    
                    if (service == 0) {
                        if (presentService) {
                            final Random random = new Random();
                            if (random.nextInt(2) == 0) {
                                review.setServicePolarityInteger(Review.NEGATIVE);
                                review.setServicePolarity(Review.negative);
                            } else {
                                review.setServicePolarityInteger(Review.POSITIVE);
                                review.setServicePolarity(Review.positive);
                            }
                        } else {
                            review.setServicePolarityInteger(Review.UNKNOWN);
                            review.setServicePolarity(Review.unknown);
                        }
                    } else if (service > 0) {
                        review.setServicePolarity(Review.positive);
                        review.setServicePolarityInteger(Review.POSITIVE);
                    } else {
                        review.setServicePolarity(Review.negative);
                        review.setServicePolarityInteger(Review.NEGATIVE);
                    }
                    
                    if (price == 0) {
                        if (presentPrice) {
                            final Random random = new Random();
                            if (random.nextInt(2) == 0) {
                                review.setPricePolarityInteger(Review.NEGATIVE);
                                review.setPricePolarity(Review.negative);
                            } else {
                                review.setPricePolarityInteger(Review.POSITIVE);
                                review.setPricePolarity(Review.positive);
                            }
                        } else {
                            review.setPricePolarityInteger(Review.UNKNOWN);
                            review.setPricePolarity(Review.unknown);
                        }
                    } else if (price > 0) {
                        review.setPricePolarity(Review.positive);
                        review.setPricePolarityInteger(Review.POSITIVE);
                    } else {
                        review.setPricePolarity(Review.negative);
                        review.setPricePolarityInteger(Review.NEGATIVE);
                    }
                    
                    if (ambience == 0) {
                        if (presentAmbience) {
                            final Random random = new Random();
                            if (random.nextInt(2) == 0) {
                                review.setAmbiencePolarityInteger(Review.NEGATIVE);
                                review.setAmbiencePolarity(Review.negative);
                            } else {
                                review.setAmbiencePolarityInteger(Review.POSITIVE);
                                review.setAmbiencePolarity(Review.positive);
                            }
                        } else {
                            review.setAmbiencePolarityInteger(Review.UNKNOWN);
                            review.setAmbiencePolarity(Review.unknown);
                        }
                    } else if (ambience > 0) {
                        review.setAmbiencePolarity(Review.positive);
                        review.setAmbiencePolarityInteger(Review.POSITIVE);
                    } else {
                        review.setAmbiencePolarity(Review.negative);
                        review.setAmbiencePolarityInteger(Review.NEGATIVE);
                    }
                    review.setRawText(textContent);
                }

                review.setTranslatedText(translate);
                total += objectMapper.writeValueAsString(review) + "\n";
                counter++;
            }
            
            FileUtils.writeStringToFile(new File("training_food_dua.json"), total, Charset.defaultCharset());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
