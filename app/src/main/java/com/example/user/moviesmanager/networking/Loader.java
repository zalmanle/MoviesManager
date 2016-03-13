package com.example.user.moviesmanager.networking;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by User on 14/12/2015.
 */
public abstract class Loader {


    //region CONSTANTS
    private static final String USER_AGENT = "Mozilla/5.0";

    protected static final String SERVICE_URL = "http://www.omdbapi.com/";

    protected static final String SEPARATOR = "%20";

    protected static final String WHITESPACE = " ";
    //endregion

    //region INSTANCE VARIABLES
    protected String searchPhrase;

    protected String url;

    protected String resultStr;
    //endregion

    //region ABSTRACT FUNCTIONS TO OVERRIDE
    protected abstract void buildSearchQuery();

    protected abstract void parseData() throws JSONException;
    //endregion

    //region SEND HTTP REQUEST FUNCTION
    protected final String sendHTTPRequest()
            throws Exception {

        HttpURLConnection con = null;

        try {

            URL urlObject = new URL(url);
            con = (HttpURLConnection) urlObject.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);


            int responseCode = con.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();

            }
            else {

                throw new Exception(String.valueOf(responseCode));
            }

        }
        catch(IOException e) {

            throw e;
        }
        finally {

            if(con != null) {
                con.disconnect();
            }
        }

    }
    //endregion


}
