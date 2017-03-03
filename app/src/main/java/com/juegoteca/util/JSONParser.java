package com.juegoteca.util;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class JSONParser {

	private static InputStream inputStream = null;
	private static JSONObject objetoJSON = null;
	private static String stringJSON = "";
	/**
	 * Constructor
	 */
	public JSONParser() {}
	/**
	 * Obtiene un objeto JSON haciendo una petición mediante GET o POST
	 * @param url Dirección donde está el PHP que se encarga de la petición
	 * @param method Método por el que pasamos los parámetros (GET,POST)
	 * @param params Parámetros suminsitrados en la petición
	 * @return
	 */
	public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {
		try {
			if(method == "POST"){
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				inputStream = httpEntity.getContent();
			}
			else if(method == "GET"){
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				inputStream = httpEntity.getContent();
			}           

		} 
		catch (UnsupportedEncodingException e) {
			Log.e("UnsupportedEncodingException", e.toString());
			e.printStackTrace();
			return null;
		} 
		catch (ClientProtocolException e) {
			Log.e("ClientProtocolException", e.toString());
			e.printStackTrace();
			return null;
		} 
		catch (IOException e) {
			Log.e("IOException", e.toString());
			e.printStackTrace();
			return null;
		}
		// Tratamos de obtener el resultado de la petición
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			inputStream.close();
			stringJSON = sb.toString();
		} 
		catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
			return null;
		}
		// Pasamos el string a JSON
		try {
			objetoJSON = new JSONObject(stringJSON);
		} 
		catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
			return null;
		}

		return objetoJSON;
	}
}
