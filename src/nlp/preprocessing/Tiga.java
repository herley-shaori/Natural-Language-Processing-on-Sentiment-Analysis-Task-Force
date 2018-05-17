/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.preprocessing;

import com.darkprograms.speech.translator.GoogleTranslate;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringTokenizer;

/**
 *
 * @author herley
 */
public class Tiga {

    public static void main(String[] args) {
        try {
//<editor-fold defaultstate="collapsed" desc="Punctuation">
            List<String> tokenList = new StringTokenizer(FileUtils.readFileToString(new File("D:\\Netbeans Project\\NLP Preprocessing\\tiga\\training_normal_ind.txt"), "utf-8"), "***").getTokenList();
            List<String> punctFree = new ArrayList();
            for (String string : tokenList) {
                punctFree.add(string.replaceAll("[<>\\\\[\\\\]'`~:;{}!/@#$%^&*+,.-]", "").replace("\"", "").replace("[", "").replace("]", "").toLowerCase());
            }
//</editor-fold>

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

            final List<String> cleanSentence = new ArrayList();
//<editor-fold defaultstate="collapsed" desc="Clean Sentence">
            {
                for (String string : punctFree) {
                    Iterator<String> iterSatu = new StringTokenizer(string).getTokenList().iterator();
                    String kalimatBersih = "";
                    while (iterSatu.hasNext()) {
                        String rawWords = StringUtils.trim(iterSatu.next());
                        if (kamusKata.get(rawWords) != null) {
                            kalimatBersih += kamusKata.get(rawWords) + " ";
                        } else {
                            kalimatBersih += rawWords + " ";
                        }
                    }
                    kalimatBersih = StringUtils.trim(kalimatBersih.replaceAll("\\P{Print}", "").replaceAll("\\p{P}",""));
                    cleanSentence.add(kalimatBersih);
                }
                punctFree = null;
            }
//</editor-fold>

//            System.out.println("Ukuran: " + cleanSentence.size());
//            System.out.println(cleanSentence);
            {
                for(String string:cleanSentence){
                    System.out.println(Tiga.translate(string).get());
                }

//              for(String string:cleanSentence){
//                  System.out.println(GoogleTranslate.translate(string));
//              }

//                System.out.println(cleanSentence.get(6));

            }

            POOL.shutdown();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final ExecutorService POOL = Executors.newFixedThreadPool(10);

    private static Future<String> translate(final String kalimat) throws IOException {
        return POOL.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return GoogleTranslate.translate(kalimat);
            }
        });
    }
}
