package com.example.gopal.assignmenthtml;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.SystemClock;
import android.text.Html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

class CustomLoader extends AsyncTaskLoader<Document> {
    private final String query;
    private Document htmlDocument;

    public CustomLoader(Context context, String query) {
        super(context);
        this.query = query;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (htmlDocument != null)
            deliverResult(htmlDocument);
        else
            forceLoad();
    }

    @Override
    public Document loadInBackground() {
        try {
            return htmlDocument = Jsoup.connect(Constants.BASE_URL + "search?keyword=" + query).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (htmlDocument != null) {
            htmlDocument = null;
        }
    }
}