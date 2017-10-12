package com.example.gopal.assignmenthtml;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import com.example.gopal.assignmenthtml.databinding.ActivityMainBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends AppCompatActivity implements Constants, RecycleViewListener, SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Document> {


    private ArrayList<HtmlData> mProductList = new ArrayList<>();
    private ProductGridAdater mAdapter;
    private ActivityMainBinding mBinding;
    private static boolean orientationLand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.toolbar.setTitle(R.string.html_parsing);
        setSupportActionBar(mBinding.toolbar);

        mBinding.searchBar.setOnQueryTextListener(this);
        ((EditText) mBinding.searchBar.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                .setHintTextColor(ContextCompat.getColor(this, R.color.dark_gray));
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, orientationLand ? 3 : 2));

        mAdapter = new ProductGridAdater(this, mProductList);
        mBinding.recyclerView.setAdapter(mAdapter);
        if (!isNetworkAvailable())
            showSnackbar(getString(R.string.no_internet_msg), R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }, Snackbar.LENGTH_INDEFINITE);
        else
            getLoaderManager().initLoader(111, null, this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Update the Flag here
        orientationLand = (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? true : false);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        if (isNetworkAvailable()) {
            Bundle bundle = new Bundle();
            bundle.putString("query", query);
            getLoaderManager().restartLoader(111, bundle, this);
        } else {
            showSnackbar(getString(R.string.no_internet_msg), R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }, Snackbar.LENGTH_INDEFINITE);
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mBinding.searchBar.getWindowToken(), 0);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mProductList.get(position).getProductUrl()));
        startActivity(intent);
    }

    @Override
    public Loader<Document> onCreateLoader(int id, Bundle args) {
        mBinding.progressBar.setVisibility(View.VISIBLE);
        return new CustomLoader(getApplicationContext(), args == null ? "" : args.getString("query"));
    }

    @Override
    public void onLoadFinished(Loader<Document> loader, Document data) {
        ArrayList<HtmlData> result = parseHTML(data);
        if (result != null && !result.isEmpty()) {
            mProductList.clear();
            mProductList.addAll(result);
            mAdapter.notifyDataSetChanged();
        } else {
            //TODO: No data found for given key.
            showSnackbar(getString(R.string.no_data_found_for_query), R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBinding.searchBar.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mBinding.searchBar.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);
                }
            }, Snackbar.LENGTH_INDEFINITE);
        }
        mBinding.progressBar.setVisibility(View.GONE);


    }

    @Override
    public void onLoaderReset(Loader<Document> loader) {
        loader.reset();

    }

    private ArrayList<HtmlData> parseHTML(Document htmlDocument) {

        if (htmlDocument != null) {
            ArrayList<HtmlData> productList = new ArrayList<>();
            for (Element element : htmlDocument.getElementsByClass(CLASS_ALL_ITEMS)) {
                HtmlData htmlData = new HtmlData();
                htmlData.setProductUrl(element.parent().attr(ATTR_HREF));
                for (Element subElement : element.getAllElements()) {
                    if (subElement.className().contains(CLASS_DISCOUNT)) {
                        htmlData.setDiscount(element.text());
                        continue;
                    }
                    if (subElement.className().contains(CLASS_PROD_NAME)) {
                        htmlData.setProductName(subElement.text());
                        continue;
                    }
                    if (subElement.className().contains(CLASS_NEW_PRICE)) {
                        htmlData.setPriceWithDiscount(subElement.text());
                        continue;
                    }
                    if (subElement.className().contains(CLASS_OLD_PRICE)) {
                        htmlData.setPriceWithoutDiscount(subElement.text());
                        continue;
                    }
                    if (subElement.className().contains(CLASS_THUMBNAIL)) {
                        htmlData.setImageUrl(subElement.attr(ATTR_STYLE));
                    }

                }
                productList.add(htmlData);
            }
            return productList;
        }

        return null;
    }

    @Deprecated
    private class JsoupAsyncTask extends AsyncTask<String, Object, ArrayList<HtmlData>> {

        private Document htmlDocument;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBinding.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<HtmlData> doInBackground(String... params) {
            try {
                htmlDocument = Jsoup.connect(BASE_URL + "search?keyword=" + params[0]).get();
                htmlDocument.getAllElements();
                return parseHTML(htmlDocument);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(ArrayList<HtmlData> result) {
            if (result != null && !result.isEmpty()) {
                mProductList.clear();
                mProductList.addAll(result);
                mAdapter.notifyDataSetChanged();
            } else {
                //TODO: No data found for given key.
                showSnackbar(getString(R.string.no_data_found_for_query), R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBinding.searchBar.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mBinding.searchBar.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);
                    }
                }, Snackbar.LENGTH_INDEFINITE);
            }
            mBinding.progressBar.setVisibility(View.GONE);
        }
    }


    /**
     * isNetworkAvailable(Context) provides a network state status.
     */
    public boolean isNetworkAvailable() {
        boolean var = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            if (cm.getActiveNetworkInfo() != null) {
                var = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return var;
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final String mainTextStringId, final int actionStringId,
                              View.OnClickListener listener, int duraton) {
        Snackbar.make(
                findViewById(android.R.id.content),
                mainTextStringId,
                duraton)
                .setAction(getString(actionStringId), listener).show();
    }

}
