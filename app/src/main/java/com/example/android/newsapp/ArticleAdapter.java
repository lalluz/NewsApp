package com.example.android.newsapp;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * This custom Adapter adapts the RecyclerView to hold an ArrayList of Article objects.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>{

    private List<Article> articles;

    public class ArticleViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView sectionName;
        private TextView publicationDate;
        private Uri articleUri;

        private ArticleViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            sectionName = (TextView) view.findViewById(R.id.sectionName);
            publicationDate = (TextView) view.findViewById(R.id.publicationDate);
            articleUri = null;
        }
    }

    public ArticleAdapter(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ArticleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.title.setText(article.getTitle());
        holder.sectionName.setText(article.getSectionName());
        holder.publicationDate.setText(article.getPublicationDate());
        holder.articleUri = Uri.parse(article.getUrl());

        // TODO implement OnClickListener
        // Convert the String retrieved with getUrl() into a URI object, then pass it to ClickListener
        // holder.itemView.setOnClickListener();
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

}
