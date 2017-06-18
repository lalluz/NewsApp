package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of articles by using an AsyncTask to perform the network request.
 */

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    // Tag for log messages
    private static final String LOG_TAG = ArticleLoader.class.getName();

    // Query URL
    private String mUrl;

    /**
     * Constructs a new {@link ArticleLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public ArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    // This starts the Loader.
    @Override
    protected void onStartLoading() {
        // Log.i(LOG_TAG, "TEST: onStartLoading() called");
        forceLoad();
    }

    // This is on a background thread.
    @Override
    public List<Article> loadInBackground() {

        // Log.i(LOG_TAG, "TEST: loadInBackground() called");

        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of articles.
        List<Article> articles = Utils.fetchArticleData(mUrl);
        return articles;
    }
}