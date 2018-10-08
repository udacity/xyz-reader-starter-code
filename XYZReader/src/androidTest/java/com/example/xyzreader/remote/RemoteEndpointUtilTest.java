package com.example.xyzreader.remote;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.example.xyzreader.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@RunWith(AndroidJUnit4.class)
public class RemoteEndpointUtilTest {

	@Test
	public void test_fetchJsonArray() throws JSONException {
		JSONArray jsonArray = RemoteEndpointUtil.fetchJsonArray();

		assert jsonArray != null;
		Assert.assertEquals(6, jsonArray.length());
		Assert.assertEquals(1L, jsonArray.getJSONObject(0).getLong("id"));
		Assert.assertEquals("Ebooks, Neither E, Nor Books", jsonArray.getJSONObject(0).getString("title"));
		Assert.assertEquals("Cory Doctorow", jsonArray.getJSONObject(0).getString("author"));
		Assert.assertTrue(jsonArray.getJSONObject(0).getString("body").contains("Ebooks aren't marketing"));
		Assert.assertEquals("2004-02-12T00:00:00.000", jsonArray.getJSONObject(0).getString("datePublished"));
	}

	@Test
	public void test_fetchPlainText() throws IOException {
		String plainText = RemoteEndpointUtil.fetchPlainText(Config.BASE_URL);

		assert plainText != null;
		Assert.assertTrue(plainText.contains("\"id\":"));
		Assert.assertTrue(plainText.contains("\"author\":"));
		Assert.assertTrue(plainText.contains("\"body\":"));

		InputStream rawResource = InstrumentationRegistry.getContext().getResources().openRawResource(R.raw.valid_remote_response);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(rawResource));
		String line = bufferedReader.readLine();

		Assert.assertSame(plainText, line);
	}
}