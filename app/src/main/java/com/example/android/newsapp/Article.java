package com.example.android.newsapp;

/**
 * This Article object describes an article retrieved from the Guardian dataset.
 * It contains: title, publication date, section name, and url.
 */

public class Article {

    private String mTitle;

    private String mPublicationDate;

    private String mSectionName;
    // When the user clicks on the article this url opens the guardian website.
    private String mUrl;

    /**
     * Constructor whit all params
     * @param title
     * @param publicationDate
     * @param sectionName
     * @param url
     */
    public Article (String title, String publicationDate,
                    String sectionName, String url){
        mTitle = title;
        mPublicationDate = publicationDate;
        mSectionName = sectionName;
        mUrl = url;
    }

    /** Getter methods */
    public String getTitle(){
        return mTitle;
    }

    public String getPublicationDate(){ return mPublicationDate; }

    public String getSectionName() { return mSectionName; }

    public String getUrl() { return mUrl; }
}
