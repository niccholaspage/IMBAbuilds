package com.nicholasnassar.imbabuilds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nicholasnassar.imbabuilds.adapter.Item;

import android.app.Activity;
import android.os.AsyncTask;

public class DataRetrieverTask extends AsyncTask<Void, Void, JSONObject> {
	private WeakReference<Activity> activity;

	private static final String DATA_URL = "http://74.91.113.69/imbabuilds/data.json";

	private static final String VERSION_URL = "http://74.91.113.69/imbabuilds/data.version";

	public DataRetrieverTask(Activity activity){
		this.activity = new WeakReference<Activity>(activity);
	}

	@Override
	protected JSONObject doInBackground(Void... args) {
		File dataFile = new File(activity.get().getCacheDir(), "data.json");

		File versionFile = new File(activity.get().getCacheDir(), "data.version");

		try {
			String data = "";

			if (dataFile.exists()){
				String currentVersion = getFileContents(versionFile);

				String newVersion = downloadFile(VERSION_URL);

				data = getFileContents(dataFile);

				if (newVersion != null && newVersion.length() > 0 && !currentVersion.equals(newVersion)) {
					data = downloadAndReplaceFile(dataFile, DATA_URL);

					downloadAndReplaceFile(versionFile, VERSION_URL);
				}
			}else {
				data = downloadAndReplaceFile(dataFile, DATA_URL);

				downloadAndReplaceFile(versionFile, VERSION_URL);
			}

			return new JSONObject(data);
		} catch (Exception e) {
			dataFile.delete();

			versionFile.delete();

			return null;
		}
	}

	@Override
	protected void onPostExecute(JSONObject result){
		try {
			if (result != null){
				JSONArray array = null;

				array = result.getJSONArray("latestbuilds");

				ArrayList<Item> items = ((MainApplication) activity.get().getApplication()).getLatestBuilds();

				items.clear();

				items.addAll(getLatestBuilds(array));

				for (Race race : Race.values()){
					for (Race opponent : Race.values()){
						String category = Race.getMatchup(race, opponent);

						array = result.getJSONArray(category);

						items = ((MainApplication) activity.get().getApplication()).getBuilds(category);

						items.clear();

						items.addAll(getMatchupItems(array, race));
					}
				}
			}else {
				throw new Exception();
			}
		} catch (Exception e){

		}

		((MainActivity) activity.get()).updateBuilds();
	}

	private String downloadAndReplaceFile(File file, String url) throws Exception {
		String data = downloadFile(url);

		if (data == null || data.equals("")){
			return data;
		}

		FileOutputStream outputStream = new FileOutputStream(file);

		outputStream.write(data.getBytes());

		outputStream.close();

		return data;
	}

	private String getFileContents(File file){
		StringBuilder builder = new StringBuilder();

		try {
			FileInputStream fileInputStream = new FileInputStream(file);

			BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

			String line = null;

			while ((line = reader.readLine()) != null) {
				builder.append(line).append("\n");
			}

			reader.close();
		}catch (Exception e){
			return null;
		}

		return builder.toString().trim();
	}

	private String downloadFile(String url){
		StringBuilder stringBuilder = new StringBuilder();

		HttpClient httpClient = new DefaultHttpClient();

		HttpGet httpGet = new HttpGet(url);

		try {
			HttpResponse response = httpClient.execute(httpGet);

			StatusLine statusLine = response.getStatusLine();

			int statusCode = statusLine.getStatusCode();

			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();

				InputStream inputStream = entity.getContent();

				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

				String line;

				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}

				inputStream.close();

				return stringBuilder.toString().trim();
			}else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public void setActivity(Activity activity){
		this.activity = new WeakReference<Activity>(activity);
	}

	public List<Item> getLatestBuilds(JSONArray array) throws JSONException {
		List<Item> items = new ArrayList<Item>();

		items.add(new Item("Latest Builds", null, -1, true));

		for (int i = 0; i < array.length(); i++){
			String item = array.getString(i);

			Race race = Race.getRaceFromFirstLetter(item.substring(0, 1));

			items.add(new Item(item, item.split(" ")[0], race.getColor(), false));
		}

		return items;
	}

	public List<Item> getMatchupItems(JSONArray array, Race race) throws JSONException {
		List<Item> items = new ArrayList<Item>();

		for (int i = 0; i < array.length(); i++){
			JSONObject raceCategory = array.getJSONObject(i);

			Iterator<?> raceCategoryIterator = raceCategory.keys();

			while (raceCategoryIterator.hasNext()){
				String key = raceCategoryIterator.next().toString();

				items.add(new Item(key, null, -1, true));

				JSONArray buildCategory = raceCategory.getJSONArray(key);

				for (int j = 0; j < buildCategory.length(); j++){
					String title = buildCategory.getJSONObject(j).getString("title");

					String text = buildCategory.getJSONObject(j).getString("text");

					Item item = new Item(title, text, race.getColor(), false);

					items.add(item);
				}
			}
		}

		return items;
	}
}