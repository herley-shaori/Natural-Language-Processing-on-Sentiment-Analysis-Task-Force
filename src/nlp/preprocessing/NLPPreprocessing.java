/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.preprocessing;

import com.darkprograms.speech.translator.GoogleTranslate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
public class NLPPreprocessing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            File fXmlFile = new File("training_set.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("review");
            System.out.println("Panjang nList: " + nList.getLength());

            final List<Review> reviews = new ArrayList();

            for (int temp = 0; temp < nList.getLength(); temp++) {
                final Review review = new Review();
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String textContent = eElement.getElementsByTagName("text").item(0).getTextContent();
                    Node anotatorSatu = eElement.getElementsByTagName("aspects").item(0);
                    Node anotatorDua = eElement.getElementsByTagName("aspects").item(1);

                    //<editor-fold defaultstate="collapsed" desc="Permintaan pendapat kedua Anotator">
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

                    if (anotatorDua != null) {
                        NodeList childNodes = anotatorDua.getChildNodes();
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
//</editor-fold>

                    //<editor-fold defaultstate="collapsed" desc="Reviews Anotator Construction">    
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
//</editor-fold>
                reviews.add(review);

            }

            final File plainFile = new File("plain.txt");
            String readFileToString = FileUtils.readFileToString(plainFile, Charset.defaultCharset());
            List<String> tokenList = new StringTokenizer(readFileToString, "************").getTokenList();
            List<String> kumpulanTeks = new ArrayList();
            for (String string : tokenList) {
                if (!StringUtils.trim(string).isEmpty()) {
                    kumpulanTeks.add(string);
                }
            }
            tokenList = null;

            ObjectMapper mapper = new ObjectMapper();
            System.out.println("Begin translation...");
            String total = "";
            for(int i=0; i<reviews.size(); i++){
                final Review review = reviews.get(i);
                final String kalimat = kumpulanTeks.get(i);
                Iterator<String> iterSatu = new StringTokenizer(kalimat,".").getTokenList().iterator();
                String totalKata = "";
                while (iterSatu.hasNext()) {
                    String kata = iterSatu.next();
                    totalKata+=GoogleTranslate.translate(kata)+" ";
                }
                review.setTranslatedText(GoogleTranslate.translate(StringUtils.trim(totalKata)));
                String hasil = mapper.writeValueAsString(review);
                total += hasil +"\n";
                if(i>0 && i%100==0){
                    System.out.println("Passing: "+i);
                }
            }
            System.out.println("Translation done.");
            System.out.println("Begin write into file...");
            FileUtils.writeStringToFile(new File("reviews.json"), total, "UTF-8");
            System.out.println("Writing done.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
