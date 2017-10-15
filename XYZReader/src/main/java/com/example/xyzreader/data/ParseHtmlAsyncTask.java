/*
 * Copyright (c) 2017 Andrew Chi Heng Lam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.xyzreader.data;

import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

/**
 * Created by Andrew Chi Heng Lam on 9/2/2017.
 * <p>
 * ParseHtmlAsyncTask
 * An implementation of the AsyncTask class to do html parsing to spanned body text IO on a separate thread,
 */

public class ParseHtmlAsyncTask extends AsyncTask<Void, Void, Spanned> {
    /* Debug Tag */
    private static final String TAG = ParseHtmlAsyncTask.class.getSimpleName();

    /* Listener to callback when the data is ready */
    private onTaskCompleteListener mListener;

    /* String of the html from the database */
    private String mRawHtmlBodyText;

    /* Starting position to output the text*/
    private int mStartingPos;
    private int mEndingPos;

    /* No-args default constructor */
    public ParseHtmlAsyncTask() {
    }

    /* Public Setters */
    public ParseHtmlAsyncTask setListener(onTaskCompleteListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public ParseHtmlAsyncTask setDataHtmlString (String rawHtmlBodyText) {
        this.mRawHtmlBodyText = rawHtmlBodyText;
        return this;
    }

    public ParseHtmlAsyncTask setSnippetRange(int startingPos, int numChar)
    {
        this.mStartingPos = startingPos;
        this.mEndingPos = startingPos + numChar;
        return this;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // Check for required parameter before doInBackground
        String msg = "";
        boolean hasError = false;

        if (mListener == null) {
            hasError = true;
            msg = msg.concat("Must set the mListener for this task." + "\n");
        }

        if (mRawHtmlBodyText == null) {
            hasError = true;
            msg = msg.concat("Must set the mRawHtmlBodyText for this task." + "\n");
        }

        if (hasError) {
            Log.e(TAG, msg);
            throw new IllegalArgumentException(msg);
        }

    }

    @Override
    protected Spanned doInBackground(Void... Void) {

        mRawHtmlBodyText = mRawHtmlBodyText.replaceAll("(\r\n|\n)", "<br />");
        if (mEndingPos >= mRawHtmlBodyText.length()) {
            mEndingPos = mRawHtmlBodyText.length();
            mRawHtmlBodyText = mRawHtmlBodyText.substring(mStartingPos, mEndingPos);
        }

        Spanned bodyText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            bodyText = Html.fromHtml(mRawHtmlBodyText,FROM_HTML_MODE_LEGACY);
        }else
        {
            bodyText = Html.fromHtml(mRawHtmlBodyText);
        }

        // return the formatted body text
        return bodyText;
    }

    @Override
    protected void onPostExecute(Spanned bodyText) {
        super.onPostExecute(bodyText);
        if (mListener != null) mListener.onBodyTextReady(bodyText, this.mEndingPos);
    }

    /**
     * Interface for callback to the listener at stages where UI change is required
     * in preExecute and postExecute.
     */
    public interface onTaskCompleteListener {
        void onBodyTextReady(Spanned bodyText, int endPos);
    }
}
