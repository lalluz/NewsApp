package com.example.android.newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<List<Article>> {

    //Tag for log messages
    private static final String LOG_TAG = MainActivity.class.getName();
    // Messages for the user
    private static final String NO_INTERNET_CONNECTION = "No Internet Connection";
    private static final String NO_ARTICLES_FOUND = "Sorry, No Articles Found";

    // Url to make a request to the Guardian dataset and retrieve the JSONObject with:
    // news related to Europe, max 30 results, ordered by newest (so it will be always up to date),
    // all sections but: sport, football, business, money, politics, membership.
    private static String REQUEST_URL = "http://content.guardianapis.com/search?q=europe" +
            "&format=json&page=1&page-size=30&order-by=newest&api-key=test" +
            //"&section=music|books|opinion|stage|travel|culture|life%20and%20style";
            "&section=-football,-sport,-business,-money,-politics,-membership";

    // Url to test NO_ARTICLES_FOUND message
    // private static final String REQUEST_URL = "http://content.guardianapis.com/search?order-by=newest&q=floridacanada&api-key=test";

    // ConnectivityManager object to check state of network connectivity
    ConnectivityManager connMgr;
    // NetworkInfo object to store details on the currently active default data network
    NetworkInfo networkInfo;

    //Constant value for the earthquake loader ID. We can choose any integer.
    // This really only comes into play when using multiple loaders.
    private static final int ARTICLE_LOADER_ID = 1;

    private TextView messageTextView;
    private TextView messageTextViewNotCovering;
    private ProgressBar loadingSpinner;
    private RecyclerView recyclerView;

    // Create an array list of  custom Article objects and declare the adapter for the recyclerView
    private List<Article> articles = new ArrayList<>();
    private ArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Log.i(LOG_TAG, "TEST: MainActivity onCreate() called");

        // Find recycler view, message text view and progress bar in the layout.
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        messageTextView = (TextView) findViewById(R.id.message_text_view);
        messageTextViewNotCovering = (TextView) findViewById(R.id.message_text_view_not_covering);
        loadingSpinner = (ProgressBar) findViewById(R.id.progress_bar);

        // Custom method, defined in MainActivity.
        InitializeLoader();
    }

    // This method checks internet connection,
    // if there is connection initializes the Loader, if not shows a message.
    public void InitializeLoader() {
        // Check if there is a network connection with a custom method defined in MainActivity.
        if (isConnected()) {
            // Set message text views as gone
            messageTextView.setVisibility(View.GONE);
            messageTextViewNotCovering.setVisibility(View.GONE);
            // Set loading spinner as visible
            loadingSpinner.setVisibility(View.VISIBLE);

            // Get a reference to the LoaderManager to interact with the loader.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            // Log.i(LOG_TAG, "TEST: calling initLoader()");
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);

            // if there is no internet connection and the adapter is null display a message.
        } else if (!isConnected() && adapter == null) {
            messageTextView.setText(NO_INTERNET_CONNECTION);
            messageTextView.setVisibility(View.VISIBLE);
            // if there is no internet connection and the adapter is already populated display a message.
        } else if (!isConnected() && adapter != null) {
            messageTextViewNotCovering.setText(NO_INTERNET_CONNECTION);
            messageTextViewNotCovering.setVisibility(View.VISIBLE);
        }
    }

    // Set a refresh button icon in the actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    // This method is called when the refresh button is clicked.
    // It refresh the page by initializing the Loader.
    public void refreshPage(MenuItem menuItem) {
        InitializeLoader();
    }

    // Create the Loader.
    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        // Log.i(LOG_TAG, "TEST: onCreateLoader() called");
        // Create a new loader for the given URL
        return new ArticleLoader(this, REQUEST_URL);
    }

    // Once Loader has finished this method is called.
    @Override
    public void onLoadFinished(Loader<List<Article>> loader, final List<Article> articles) {
        // Log.i(LOG_TAG, "TEST: onLoadFinished() called");

        // Hide progress bar
        loadingSpinner.setVisibility(View.GONE);

        // If there is a valid list of {@link Article}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (articles != null && !articles.isEmpty()) {
            // Log.i(LOG_TAG, "TEST: articles is not null and not empty");
            adapter = new ArticleAdapter(articles);
            RecyclerView.LayoutManager LayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(LayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        } else {
            // Set message text view as visible and set a message
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(NO_ARTICLES_FOUND);
        }
    }

    // This method reset the Loader
    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        Log.i(LOG_TAG, "TEST: onLoadReset() called");
        // Loader reset, so we can clear out our existing data.
        articles.clear();
        adapter.notifyDataSetChanged();
    }

    //This method checks if there is internet connection.
        public boolean isConnected() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        networkInfo = connMgr.getActiveNetworkInfo();
        // Check if there is a network connection and display a message
        if (networkInfo == null || !networkInfo.isConnected()) {
            return false;
        } else {
            return true;
        }
    }
}