package ARSBot;

import javax.sound.sampled.Line;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Currency is an object with symbol, url, price and keyword
 */
public class Currency {
    private String Symbol; //Symbol used as reference
    private String Url; //Url of source of data/price
    private String KeywordPrice;   //keyword used to identify the data/price
    private String KeywordOpenPrice;
    private float Price; //price
    private float OpenPrice; //Open price

    //Connection Variables
    private URL urlhtml;
    private URLConnection urlConn;
    private InputStreamReader inStream;
    private BufferedReader buff;

    /**
     * To initilize it needs a symbol/emoji, url and 2 keywoards
     * @param symbol
     * @param url
     * @param keywoardP To get the price
     * @param keywoardOp To get the opening price
     */
    Currency(String symbol, String url, String keywoardP, String keywoardOp){
        Symbol = symbol;
        Url = url;
        KeywordPrice = keywoardP;
        KeywordOpenPrice = keywoardOp;
    }

    /**
     * Makes the connection to the website
     * @param urlwebsite Url of the website
     * @throws IOException
     */
    private void connectWebsite(String urlwebsite) throws IOException{
        urlhtml = new URL(urlwebsite);
        urlConn = urlhtml.openConnection();
        inStream = new InputStreamReader(urlConn.getInputStream());
        buff = new BufferedReader(inStream);
    }


    /**
     * Downloads the price of the currency from the website and returns true if it was successful
     * @throws IOException
     */
    public boolean downloadPrice() throws IOException{
        boolean FoundPrice = false;
        boolean FoundOpenPrice = false;
        boolean DownloadSuccess = true; // did it succeed in downloading data?

        connectWebsite(Url);
        String line = buff.readLine();

        while(line != null && (FoundPrice == false || FoundOpenPrice == false)){ //stops when there are no more lines or it already found all the data needed
            if(!FoundPrice) {
                if (line.contains(KeywordPrice)) {
                    Price = extractDataFromLine(line, KeywordPrice);
                    FoundPrice = true;
                }
            }

            if(!FoundOpenPrice) {
                if (line.contains(KeywordOpenPrice)) {
                    OpenPrice = extractDataFromLine(line, KeywordOpenPrice);
                    FoundOpenPrice = true;
                }
            }

            line = buff.readLine();
            if(line == null) DownloadSuccess = false; //could not download data
        }

        return DownloadSuccess;

    }

    /**
     *
     * @param context line where the data is
     * @param key keywoard for data extraction
     * @return
     */
    private float extractDataFromLine (String context, String key){
        float data;
        int start = context.indexOf(key) + key.length();
        try{
            data = Float.parseFloat(context.substring(start, start + 5));
        } catch (NumberFormatException n) {
            data = Float.parseFloat(context.substring(start, start + 4));
        }
        return data;
    }

    /**
     *
     * @return Price
     */
    public float getPrice(){
        return Price;
    }

    public String getSymbol(){
        return Symbol;
    }

    public float getOpenPrice(){
        return OpenPrice;
    }

    /**
     * Gets the variation (%) of the price in the last 24hrs
     * @return
     */
    public float getVariation(){
        float var = Price / OpenPrice;
        var = var - 1.0f;
        var = var * 100.0f;
        return var;
    }
}
