package com.nicholasnassar.imbabuilds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.nicholasnassar.imbabuilds.adapter.Item;
import com.nicholasnassar.imbabuilds.parser.GuidesParser;
import com.nicholasnassar.imbabuilds.parser.LatestBuildsParser;
import com.nicholasnassar.imbabuilds.parser.MatchupJSONParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

public class DataRetrieverTask extends AsyncTask<Void, Void, JSONObject> {
	private final WeakReference<Activity> activity;

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

				if (newVersion != null && !newVersion.isEmpty() && !currentVersion.equals(newVersion)) {
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

				ArrayList<Item> items = Race.getLatestBuilds();

				items.clear();

				items.addAll(new LatestBuildsParser().parse(array).getItems());

				for (Race race : Race.values()){
					for (Race opponent : Race.values()){
						String category = Race.getMatchup(race, opponent);

						array = result.getJSONArray(category);

						items = Race.getItems(race, opponent);

						items.clear();

						items.addAll(new MatchupJSONParser().parse(array, race).getItems());
					}
				}

				items = Race.getGuideBuilds();

				items.clear();

				items.addAll(new GuidesParser().parse(result.getJSONArray("guides")).getItems());

				((MainActivity) activity.get()).updateListViews();
			}else {
				throw new Exception();
			}
		} catch (Exception e){
			AlertDialog.Builder builder = new AlertDialog.Builder(activity.get());

			builder.setTitle("Error");

			builder.setMessage("Error retrieving builds");

			builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					new DataRetrieverTask(activity.get()).execute();
				} 
			});

			builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					activity.get().finish();
				} 
			});

			builder.setCancelable(false);

			AlertDialog alert = builder.create();

			alert.show();
		}
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
}