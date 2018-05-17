/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.preprocessing;

import com.darkprograms.speech.translator.GoogleTranslate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
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
public class SecondTrainingSet {

    public static void main(String[] args) {
        final List<Review> listReview = new ArrayList();
        String total = "";
        try {
            final HashMap kamusKata = new HashMap();
//<editor-fold defaultstate="collapsed" desc="Dictionary">
            {
                Reader in = new FileReader("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\fix_kamus_normalisasi.csv");
                Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
                for (CSVRecord record : records) {
                    String satu = record.get(0);
                    String dua = record.get(1);
                    if (dua.isEmpty() || dua.equals(" ")) {
                        dua = satu;
                    }
                    kamusKata.put(satu, dua);
                }
            }
//</editor-fold>

//            Reader in = new FileReader("training_translate.csv");
            Reader in = new FileReader("D:\\Netbeans Project\\NLP Preprocessing\\dua\\translate_review.csv");
//Reader in = new FileReader("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\translate_review.csv");
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
            final ObjectMapper objectMapper = new ObjectMapper();

//            File fXmlFile = new File("training_set.xml");
//            File fXmlFile = new File("D:\\Netbeans Project\\NLP Preprocessing\\dua\\training_set.xml");
            File fXmlFile = new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\train_test.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("review");

            int counter = 0;
            for (int xk = 0; xk < 10000; xk++) {
//                CSVRecord record = records
                String translate = "";
                Node nNode = nList.item(xk);
                if (nNode != null) {
                    Review review = new Review();
                    if (nNode != null && nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String textContent = eElement.getElementsByTagName("text").item(0).getTextContent();

                        translate = textContent.replaceAll("[<>\\\\[\\\\]'`~:;{}!/@#$%^&*+,.-]", "").replace("\"", "").replace("[", "").replace("]", "").toLowerCase();
                        String kalimatBersih = "";
                        Iterator<String> iterSatu = new StringTokenizer(translate).getTokenList().iterator();
                        while (iterSatu.hasNext()) {
                            String rawWords = StringUtils.trim(iterSatu.next());
                            if (kamusKata.get(rawWords) != null) {
                                kalimatBersih += kamusKata.get(rawWords) + " ";
                            } else {
                                kalimatBersih += rawWords + " ";
                            }
                        }
                        kalimatBersih = StringUtils.trim(kalimatBersih.replaceAll("\\P{Print}", "").replaceAll("\\p{P}", ""));
                        translate = kalimatBersih;

                        Node anotatorSatu = eElement.getElementsByTagName("aspects").item(0);
                        Node anotatorDua = eElement.getElementsByTagName("aspects").item(1);

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

                        if (anotatorSatu != null || anotatorDua != null) {
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
                        }
                        review.setRawText(textContent);
                    }

                    if (nNode != null) {
//                    review.setTranslatedText(translate);
                        review.setTranslatedText(GoogleTranslate.translate(translate));
                        total += objectMapper.writeValueAsString(review) + "\n";
                    }
                    counter++;
                    if (counter % 500 == 0) {

                    }
                    System.out.println("Counter Valid: " + counter);
                    listReview.add(review);
                } else {
                    break;
                }
            }

//            FileUtils.writeStringToFile(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\data.json"), total, "utf-8");
//            System.out.println("Data JSON sudah ditulis");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SecondTrainingSet.appendReview(listReview,total);
    }

    /**
     *
     * @param review
     */
    private static void appendReview(List<Review> reviews,String total) {
        try {

            final HashMap kamusKata = new HashMap();
//<editor-fold defaultstate="collapsed" desc="Dictionary">
            {
                Reader in = new FileReader("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\fix_kamus_normalisasi.csv");
                Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
                for (CSVRecord record : records) {
                    String satu = record.get(0);
                    String dua = record.get(1);
                    if (dua.isEmpty() || dua.equals(" ")) {
                        dua = satu;
                    }
                    kamusKata.put(satu, dua);
                }
            }
//</editor-fold>

            System.out.println("Appending Review on Data Size: " + reviews.size());
            Reader in = new FileReader("D:\\Netbeans Project\\NLP Preprocessing\\dua\\valid_translate.csv");
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
            final ObjectMapper objectMapper = new ObjectMapper();
//            String total = "";

            File fXmlFile = new File("validation_set.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("review");

            int counter = 0;
            for (CSVRecord record : records) {
                String translate = record.get(1).replaceAll("[<>\\\\[\\\\]'`~!/@#$%^&*+,.-]", "").replace("\"", "").replace("[", "").replace("]", "");
                Node nNode = nList.item(counter);
                Review review = new Review();

                if (nNode != null && nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String textContent = eElement.getElementsByTagName("text").item(0).getTextContent();

                    translate = textContent.replaceAll("[<>\\\\[\\\\]'`~:;{}!/@#$%^&*+,.-]", "").replace("\"", "").replace("[", "").replace("]", "").toLowerCase();
                    String kalimatBersih = "";
                    Iterator<String> iterSatu = new StringTokenizer(translate).getTokenList().iterator();
                    while (iterSatu.hasNext()) {
                        String rawWords = StringUtils.trim(iterSatu.next());
                        if (kamusKata.get(rawWords) != null) {
                            kalimatBersih += kamusKata.get(rawWords) + " ";
                        } else {
                            kalimatBersih += rawWords + " ";
                        }
                    }
                    kalimatBersih = StringUtils.trim(kalimatBersih.replaceAll("\\P{Print}", "").replaceAll("\\p{P}", ""));
                    translate = kalimatBersih;

                    Node anotatorSatu = eElement.getElementsByTagName("aspects").item(0);
                    Node anotatorDua = eElement.getElementsByTagName("aspects").item(1);

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

                if (nNode != null) {
                    review.setTranslatedText(GoogleTranslate.translate(translate));
                    total += objectMapper.writeValueAsString(review) + "\n";
                }
                counter++;
                System.out.println("Validation Counter: "+counter);
                reviews.add(review);
            }
            FileUtils.writeStringToFile(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\datadanvalidasi.json"), total, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Penulisan data dan validasi selesai");
        System.out.println("Appending Review Completed, final data size: " + reviews.size());
        SecondTrainingSet.aspectSeparator(reviews);
    }

    private static void aspectSeparator(List<Review> reviews) {
        final List<String> foodPos = new ArrayList();
        final List<String> foodNeg = new ArrayList();
        final List<String> foodUnk = new ArrayList();

        final List<String> pricePos = new ArrayList();
        final List<String> priceNeg = new ArrayList();
        final List<String> priceUnk = new ArrayList();

        final List<String> servicePos = new ArrayList();
        final List<String> serviceNeg = new ArrayList();
        final List<String> serviceUnk = new ArrayList();

        final List<String> ambiencePos = new ArrayList();
        final List<String> ambienceNeg = new ArrayList();
        final List<String> ambienceUnk = new ArrayList();

        final List<String> gambit = new ArrayList();

        for (Review review : reviews) {

            if (review.getFoodPolarityInteger() != null && review.getPricePolarityInteger() != null && review.getServicePolarityInteger() != null && review.getAmbiencePolarityInteger() != null) {
                if (review.getFoodPolarityInteger() == 1) {
                    foodPos.add(review.getTranslatedText());
                } else if (review.getFoodPolarityInteger() == -1) {
                    foodNeg.add(review.getTranslatedText());
                } else {
                    foodUnk.add(review.getTranslatedText());
                }

                if (review.getPricePolarityInteger() == 1) {
                    pricePos.add(review.getTranslatedText());
                } else if (review.getPricePolarityInteger() == -1) {
                    priceNeg.add(review.getTranslatedText());
                } else {
                    priceUnk.add(review.getTranslatedText());
                }

                if (review.getServicePolarityInteger() == 1) {
                    servicePos.add(review.getTranslatedText());
                } else if (review.getServicePolarityInteger() == -1) {
                    serviceNeg.add(review.getTranslatedText());
                } else {
                    serviceUnk.add(review.getTranslatedText());
                }

                if (review.getAmbiencePolarityInteger() == 1) {
                    ambiencePos.add(review.getTranslatedText());
                } else if (review.getAmbiencePolarityInteger() == -1) {
                    ambienceNeg.add(review.getTranslatedText());
                } else {
                    ambienceUnk.add(review.getTranslatedText());
                }
            }
            gambit.add(review.getTranslatedText());
        }

        System.out.println("Begin Writing Files...");

        try {
            FileUtils.writeLines(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\gambit.txt"), gambit);

            FileUtils.writeLines(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\food-POS.txt"), foodPos);
            FileUtils.writeLines(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\food-NEG.txt"), foodNeg);
            FileUtils.writeLines(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\food-UNK.txt"), foodUnk);

            FileUtils.writeLines(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\price-POS.txt"), pricePos);
            FileUtils.writeLines(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\price-NEG.txt"), priceNeg);
            FileUtils.writeLines(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\price-UNK.txt"), priceUnk);

            FileUtils.writeLines(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\service-POS.txt"), servicePos);
            FileUtils.writeLines(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\service-NEG.txt"), serviceNeg);
            FileUtils.writeLines(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\service-UNK.txt"), serviceUnk);

            FileUtils.writeLines(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\ambience-POS.txt"), ambiencePos);
            FileUtils.writeLines(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\ambience-NEG.txt"), ambienceNeg);
            FileUtils.writeLines(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\ambience-UNK.txt"), ambienceUnk);
            System.out.println("Saving Document Done...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final ExecutorService POOL = Executors.newFixedThreadPool(4000);

    private static Future<String> translate(final String kalimat) throws IOException {
        return POOL.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return GoogleTranslate.translate(kalimat);
            }
        });
    }
}
