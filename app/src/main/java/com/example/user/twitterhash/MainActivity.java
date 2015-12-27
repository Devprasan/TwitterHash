package com.example.user.twitterhash;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetViewAdapter;
import com.twitter.sdk.android.tweetui.TweetViewFetchAdapter;

import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends ActionBarActivity {
    private TextView textView;

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "DiNiBoaobcukwENTZRNPFjVXO";
    private static final String TWITTER_SECRET = "l9uT3wCWRye4geXvTEV4GbsUSAjDgIpgMmSOoSdPHtbNZqFE0S";
    private TwitterLoginButton loginButton;
    private static SharedPreferences mSharedPreferences;
    private Boolean loggedin;
    private String username;
    private boolean flagLoading;
    private boolean endOfSearchResults;
    private TweetViewAdapter adapter;
    private static final String SEARCH_RESULT_TYPE = "recent";
    private static final int SEARCH_COUNT = 20;
    private static String SEARCH_QUERY = "#wingify";
    private long maxId;
    private long sinceid;
    ListView SearchList;
    final TweetViewFetchAdapter tweetadapter =
            new TweetViewFetchAdapter<CompactTweetView>(MainActivity.this);
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        tweetlisting();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Enter search");

        return super.onCreateOptionsMenu(menu);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.menu_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        gettingIntent(intent);

    }

    private void gettingIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            SEARCH_QUERY = intent.getStringExtra(SearchManager.QUERY);
        }
    }

    private void tweetlisting() {

        mSharedPreferences = getApplicationContext().getSharedPreferences("privatemode", Context.MODE_PRIVATE);
        Boolean isLoggedin = mSharedPreferences.getBoolean("loggedin", false);
        if (isLoggedin) {
            layoutgoneacivity();
            newactivity("");
        } else {

            loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
            loginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    username = result.data.getUserName();
                    loggedin = true;
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean("loggedin", loggedin);
                    editor.putString("username", username);
                    editor.commit();
                    layoutgoneacivity();
                    newactivity("");

                }

                @Override
                public void failure(TwitterException e) {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                }
            });

        }


    }

    private void newactivity(String query) {
        gettingIntent(getIntent());

        adapter = new TweetViewAdapter(MainActivity.this);
        SearchList = (ListView) findViewById(R.id.listView);
        SearchList.setAdapter(adapter);
        final SearchService searchService = Twitter.getApiClient().getSearchService();
        searchService.tweets(SEARCH_QUERY, null, null, null, SEARCH_RESULT_TYPE, SEARCH_COUNT, null, null,
                maxId, true, new Callback<Search>() {
                    @Override
                    public void success(Result<Search> result) {
                        final List<Tweet> tweets = result.data.tweets;
                        adapter.getTweets().addAll(tweets);
                        adapter.notifyDataSetChanged();

                        if (tweets.size() > 0) {
                            maxId = tweets.get(tweets.size() - 1).id - 1;
                            sinceid = tweets.get(0).id;
                        }
                    }

                    @Override
                    public void failure(TwitterException e) {
                    }
                });


    }


    private void layoutgoneacivity() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.first);
        relativeLayout.setVisibility(View.GONE);

        RelativeLayout relativeLayout1 = (RelativeLayout) findViewById(R.id.second);
        relativeLayout1.setVisibility(View.VISIBLE);


    }

}




