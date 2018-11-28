package ARSBot;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;


import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, TwitterException {
        //Links to where the prices is extracted
        String yahooUsd = "https://finance.yahoo.com/quote/ARS=X?p=ARS=X&.tsrc=fin-srch";
        String yahooEur = "https://finance.yahoo.com/quote/EURARS=X?p=EURARS=X&.tsrc=fin-srch";
        String yahooBRL = "https://finance.yahoo.com/quote/BRLARS=X?p=BRLARS=X&.tsrc=fin-srch";
        String yahooCAD = "https://finance.yahoo.com/quote/CADARS=X?p=CADARS=X&.tsrc=fin-srch";
        String yahooGBP = "https://finance.yahoo.com/quote/GBPARS=X?p=GBPARS=X&.tsrc=fin-srch";
        String hashTagCurrency = "#dolar #argentina #usd #ars #inversion #market #BCRA #dolarargentina #dolarpeso #euro #Real";
        //Array of currencies
        Currency[] foreignCurrency = new Currency[5];
        foreignCurrency[0] = new Currency("\uD83C\uDDFA\uD83C\uDDF8 USD: ", yahooUsd, "\"bid\":{\"raw\":", "PreviousClose\":{\"raw\":");
        foreignCurrency[1] = new Currency("\uD83C\uDDEA\uD83C\uDDFA EURO: ", yahooEur, "\"bid\":{\"raw\":", "PreviousClose\":{\"raw\":");
        foreignCurrency[2] = new Currency("\uD83C\uDDEC\uD83C\uDDE7 GBP: ", yahooGBP, "\"bid\":{\"raw\":", "PreviousClose\":{\"raw\":");
        foreignCurrency[3] = new Currency("\uD83C\uDDE7\uD83C\uDDF7 REAL: ", yahooBRL, "\"bid\":{\"raw\":", "PreviousClose\":{\"raw\":");
        foreignCurrency[4] = new Currency("\uD83C\uDDE8\uD83C\uDDE6 CAD: ", yahooCAD, "\"bid\":{\"raw\":", "PreviousClose\":{\"raw\":");
        if(pullPrices(foreignCurrency)){ //pulls the prices form the website and returns true if it was successful
            try{
                tweetCurrency(foreignCurrency, hashTagCurrency);
            } catch (TwitterException t){
                sendTweeterMessageError("Could not tweet price!");
            }
            //sendTweeterMessageError("Success");
        }else {
            System.out.printf("Could not get prices");
            sendTweeterMessageError("Could not get prices");
            //If it fails, sends a message with an error
        }
        //tweetCurrency(foreignCurrency, hashTagCurrency);
    }

    /**
     * tweetCurrency tweets out the price of the currency with hashtags
     * @param a array of currencies
     * @param hashTag hashtags to tweet
     * @throws TwitterException
     * @throws IOException
     */
    private static void tweetCurrency(Currency[] a, String hashTag) throws TwitterException, IOException {
        String tweetMessage = "";
        for(Currency x : a){
            tweetMessage += x.getSymbol() + "  $" + x.getPrice() +"  (" + String.format("%.2f",x.getVariation()) +"%)" + "\n";
        }
        tweetMessage+= "\n" + hashTag;
        Twitter twitter = TwitterFactory.getSingleton();
        Status status = twitter.updateStatus(tweetMessage);
    }

    /**
     * Downloads all the prices of an array of currencies
     * @param c stands for currency
     * @return a bool if it was successful or not
     * @throws IOException
     */
    private static boolean pullPrices(Currency[] c) throws IOException{
        boolean success = true;
        for(Currency x : c){
            if(x.downloadPrice() == false) success = false; //downloads price, function returns true if it was successful
        }
        return success;
    }

    /**
     * Sends an error message through dm to the owner of the bot
     * @param message
     * @throws TwitterException
     */
    private static void sendTweeterMessageError(String message) throws TwitterException{
        Twitter sender = TwitterFactory.getSingleton();
        DirectMessage dm = sender.sendDirectMessage(277231425, message);
    }
}



