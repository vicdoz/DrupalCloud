package com.insready.drupalcloud;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.util.JsonReader;

@SuppressLint("NewApi")
public class RESTServerClient {
	public HttpGet mSERVERGET;
	public HttpPost mSERVERPOST;
	public String mENDPOIN;
	public String mSERVER;
	private HttpClient mClient = new DefaultHttpClient();
	private List<NameValuePair> mPairs = new ArrayList<NameValuePair>(15);
	private String mSession = null;
	private String mToken = null;

	public RESTServerClient(String _server, String _endpoint) {
		mENDPOIN = _server + _endpoint;
		mSERVER = _server;
	}

	private String getSession() {
		return mSession;
	}

	public void setSession(String _session) {
		mSession = _session;
	}

	private String getToken() {
		return mToken;
	}
	
	public void setToken(String _token) {
		mToken = _token;
	}

	public InputStreamReader callPost(String url,
			BasicNameValuePair[] parameters)
			throws ServiceNotAvailableException, ClientProtocolException,
			IOException {
		mSERVERPOST = new HttpPost(url);
		mPairs.clear();
		// Call "X-CSRF-Token" token from service
		if (getToken() != null) {
			mSERVERPOST.setHeader("X-CSRF-Token", getToken());
		}
		if (getSession() != null) {
			mSERVERPOST.setHeader("Cookie", getSession());
		}
		for (int i = 0; i < parameters.length; i++) {
			mPairs.add(parameters[i]);
		}
		mSERVERPOST.setEntity(new UrlEncodedFormEntity(mPairs));
		HttpResponse response = mClient.execute(mSERVERPOST);
		InputStream is = response.getEntity().getContent();
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		return isr;
	}

	public InputStreamReader callGet(String url)
			throws ServiceNotAvailableException, ClientProtocolException,
			IOException {
		mSERVERGET = new HttpGet(url);

		mSERVERGET.setHeader("Cookie", getSession());
		HttpResponse response = mClient.execute(mSERVERGET);
		InputStream is = response.getEntity().getContent();
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		return isr;

	}

	/*
	 * private void systemConnect() throws ServiceNotAvailableException { //
	 * Cloud server hand shake String uri = mENDPOIN + "system/connect";
	 * mSERVERPOST = new HttpPost(uri); try { HttpResponse response =
	 * mClient.execute(mSERVERPOST); InputStream result =
	 * response.getEntity().getContent(); BufferedReader br = new
	 * BufferedReader( new InputStreamReader(result)); String tmp =
	 * br.readLine(); if (tmp == null) { // TODO throw new
	 * ServiceNotAvailableException(this, // "Invalid method"); } JSONObject jso
	 * = new JSONObject(tmp); // Save the sessionid to storage SharedPreferences
	 * auth = mCtx.getSharedPreferences(mPREFS_AUTH, 0);
	 * SharedPreferences.Editor editor = auth.edit();
	 * editor.putString("sessionid", jso.getString("sessid"));
	 * editor.putLong("sessionid_timestamp", new Date().getTime() / 100);
	 * editor.commit();
	 * 
	 * } catch (UnsupportedEncodingException e) { e.printStackTrace(); } catch
	 * (ClientProtocolException e) { e.printStackTrace(); } catch (IOException
	 * e) { e.printStackTrace(); } catch (JSONException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } }
	 */

	public JsonReader nodeGet(int nid, String fields)
			throws ServiceNotAvailableException, ClientProtocolException,
			IOException {
		String uri = mENDPOIN + "node/" + nid;
		JsonReader jsr = new JsonReader(callGet(uri));
		return jsr;
	}

	public JsonReader taxonomyVocabGetTree(int vid)
			throws ServiceNotAvailableException, ClientProtocolException,
			IOException {
		return taxonomyVocabGetTree(vid, 0, 0);
	}

	public JsonReader taxonomyVocabGetTree(int vid, int parent, int maxdepth)
			throws ServiceNotAvailableException, ClientProtocolException,
			IOException {
		String uri = mENDPOIN + "taxonomy_vocabulary/getTree";
		int size = 2;
		BasicNameValuePair[] parameters = null;
		if (maxdepth != 0) {
			size = 3;
			parameters = new BasicNameValuePair[size];
			parameters[2] = new BasicNameValuePair("maxdepth",
					String.valueOf(maxdepth));
		} else {
			parameters = new BasicNameValuePair[size];
		}

		parameters[0] = new BasicNameValuePair("vid", String.valueOf(vid));
		parameters[1] = new BasicNameValuePair("parent", String.valueOf(parent));

		JsonReader jsr = new JsonReader(callPost(uri, parameters));
		return jsr;
	}

	public int commentSave(String comment) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String commentLoadNodeComments(int nid, int count, int start) {
		// TODO Auto-generated method stub
		return null;
	}

	public String commentLoad(int cid) throws ServiceNotAvailableException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * public boolean flagFlag(String flagName, int contentId, int uid, boolean
	 * action, boolean skipPermissionCheck) throws ServiceNotAvailableException
	 * { String uri = mENDPOIN + "flag/flag"; BasicNameValuePair[] parameters =
	 * new BasicNameValuePair[5]; parameters[0] = new
	 * BasicNameValuePair("flag_name", flagName); parameters[1] = new
	 * BasicNameValuePair("content_id", String.valueOf(contentId));
	 * parameters[2] = new BasicNameValuePair("uid", String.valueOf(uid));
	 * String actionName = (action) ? "flag" : "unflag"; parameters[3] = new
	 * BasicNameValuePair("action", actionName); String skipPermissionCheckName
	 * = (skipPermissionCheck) ? "TRUE" : "FALSE"; parameters[4] = new
	 * BasicNameValuePair("skip_permission_check", skipPermissionCheckName);
	 * //String result = call(uri, parameters); JSONObject jso; try { jso = new
	 * JSONObject(result); boolean flag = jso.getBoolean("#data"); return flag;
	 * } catch (JSONException e) { e.printStackTrace(); } return false; }
	 */

	/*
	 * public boolean flagIsFlagged(String flagName, int contentId, int uid)
	 * throws ServiceNotAvailableException { String uri = mENDPOIN +
	 * "flag/flag_isflaged"; BasicNameValuePair[] parameters = new
	 * BasicNameValuePair[3]; parameters[0] = new
	 * BasicNameValuePair("flag_name", flagName); parameters[1] = new
	 * BasicNameValuePair("content_id", String.valueOf(contentId));
	 * parameters[2] = new BasicNameValuePair("uid", String.valueOf(uid));
	 * String result = call(uri, parameters);
	 * 
	 * JSONObject jso; try { jso = new JSONObject(result); boolean flag =
	 * jso.getBoolean(""); return flag; } catch (JSONException e) {
	 * e.printStackTrace(); } return false; }
	 */

	public JsonReader userLogin(String username, String password)
			throws ServiceNotAvailableException, IOException {
		String uri = mENDPOIN + "user/login";
		BasicNameValuePair[] parameters = new BasicNameValuePair[2];
		parameters[0] = new BasicNameValuePair("username", username);
		parameters[1] = new BasicNameValuePair("password", password);
		JsonReader jsr = new JsonReader(callPost(uri, parameters));

		return jsr;
	}

	/*
	 * public String userLogout(String sessionID) throws
	 * ServiceNotAvailableException { // TODO Auto-generated method stub
	 * BasicNameValuePair[] parameters = new BasicNameValuePair[0]; //
	 * parameters[0] = new BasicNameValuePair("sessid", sessionID); String uri =
	 * mENDPOIN + "user/logout"; return call(uri, parameters); }
	 */

	public JsonReader viewsGet(String view_name, String display_id,
			String args, int offset, int limit)
			throws ServiceNotAvailableException, ClientProtocolException,
			IOException {
		String uri = mENDPOIN + "views/" + view_name + "?" + "limit=" + limit
				+ "&offset=" + offset;

		if (args != null) {
			uri += "&args=" + args;
		}

		if (display_id != null) {
			uri += "&display_id=" + display_id;
		}

		JsonReader result = new JsonReader(callGet(uri));
		return result;
	}
}
