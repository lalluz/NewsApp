package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * In this class there are useful methods to:
 * retrieve article data
 * create url
 * make http request
 * read from stream
 * extract items from json string
 */

public class Utils {

     // Tag for the log messages
    private static final String LOG_TAG = Utils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link Utils} object.
     */
    private Utils() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link Article} objects.
     */
    public static List<Article> fetchArticleData(String requestUrl) {

        Log.i(LOG_TAG, "TEST: fetchArticleData() called");

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link Article}s
        List<Article> articles = extractItemsFromJson(jsonResponse);

        // Return the list of {@link Article}s
        return articles;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Article> extractItemsFromJson(String articleJSON) {

        // Messages to display if no data has been found in a field
        final String NOT_AVAILABLE_MESSAGE = "Not Available";
        // Declare all keys as constants
        final String KEY_RESPONSE = "response";
        final String KEY_TOTAL = "total";
        final String KEY_RESULTS = "results";
        final String KEY_WEB_TITLE = "webTitle";
        final String KEY_SECTION_NAME = "sectionName";
        final String KEY_WEB_PUBLICATION_DATE = "webPublicationDate";
        final String KEY_WEB_URL = "webUrl";
        final String KEY_FIELDS = "fields";
        final String KEY_THUMBNAIL = "thumbnail";

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }

        // Create an empty ArrayList of Article objects
        List<Article> articles = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(articleJSON);

            // Extract the JSONObject with key "response"
            JSONObject responseJsonObject = baseJsonResponse.getJSONObject(KEY_RESPONSE);

            // Extract the int "total" which contains the number of articles retrieved.
            int total = responseJsonObject.getInt(KEY_TOTAL);
            // Log.v(LOG_TAG, "Total is: "  + total);
            // If there is articles to parse
            if (total > 0) {

                // Extract the JSONArray associated with the key called "results",
                // which represents a list of articles.
                JSONArray articleArray = responseJsonObject.getJSONArray(KEY_RESULTS);

                // Iterate on the Array to retrieve and parse all the Article objects
                for (int i = 0; i < articleArray.length(); i++) {

                    // Get a single article at position i
                    JSONObject currentArticle = articleArray.getJSONObject(i);

                    // Extract the value for the key "webTitle"
                    // I assume that the title is always present
                    String title = currentArticle.getString(KEY_WEB_TITLE);
                    // Log.v(LOG_TAG, "WebTitle is: "  + title);

                    // Extract the value for the key "webPublicationDate"
                    String publicationDate;
                    try {
                        publicationDate = currentArticle.getString(KEY_WEB_PUBLICATION_DATE);

                        String day = publicationDate.substring(8, 10);
                        String hour = publicationDate.substring(11, 16);
                        String month = publicationDate.substring(5, 7);
                        switch (month) {
                            case "01":  month = "Jan";
                                break;
                            case "02":  month = "Feb";
                                break;
                            case "03":  month = "Mar";
                                break;
                            case "04":  month = "Apr";
                                break;
                            case "05":  month = "May";
                                break;
                            case "06":  month = "Jun";
                                break;
                            case "07":  month = "Jul";
                                break;
                            case "08":  month = "Aug";
                                break;
                            case "09":  month = "Sep";
                                break;
                            case "10": month = "Oct";
                                break;
                            case "11": month = "Nov";
                                break;
                            case "12": month = "Dec";
                                break;
                            default: month = "Current month";
                                break;
                        }

                        publicationDate = month + " " + day + ", " + hour;

                        // Log.v(LOG_TAG, "Publication Date is: "  + publicationDate);
                        if (TextUtils.isEmpty(publicationDate)) {
                            publicationDate = NOT_AVAILABLE_MESSAGE;
                        }
                    } catch (JSONException e) {
                        publicationDate = NOT_AVAILABLE_MESSAGE;
                    }

                    // Extract the value for the key "sectionName"
                    String sectionName;
                    try {
                        sectionName = currentArticle.getString(KEY_SECTION_NAME);
                        // Log.v(LOG_TAG, "Section Name is: "  + sectionName);
                        if (TextUtils.isEmpty(sectionName)) {
                            sectionName = NOT_AVAILABLE_MESSAGE;
                        }
                    } catch (JSONException e) {
                        sectionName = NOT_AVAILABLE_MESSAGE;
                    }

                    // Extract the value for the key "webUrl"
                    String url = currentArticle.getString(KEY_WEB_URL);
                    // Log.v(LOG_TAG, "WebUrl is: "  + url);

                    // Create a new Article object with the title, sectionName,
                    // publicationDate, webUrl from the JSON response.
                    Article article = new Article(title, publicationDate, sectionName, url);

                    // Add the new {@link Article} to the list of articles
                    articles.add(article);
                }
            // If there are no articles to parse
            } else if(total == 0) {
                // Return the ArrayList of articles empty
                return articles;
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("Utils", "Problem parsing the JSON results", e);
        }

        // Return the list of articles
        return articles;
    }
}