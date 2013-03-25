package com.test.oauthtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.test.oauthtest.R;

import oauth.signpost.commonshttp.*;
import oauth.signpost.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import oauth.signpost.basic.*;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.signature.SigningStrategy;

public class YahooScreen extends Activity {
	private static final String REQUEST_TOKEN_ENDPOINT_URL ="https://api.login.yahoo.com/oauth/v2/get_request_token";
    private static final String ACCESS_TOKEN_ENDPOINT_URL ="https://api.login.yahoo.com/oauth/v2/get_token";
    private static final String AUTHORIZE_WEBSITE_URL   ="https://api.login.yahoo.com/oauth/v2/request_auth";
    
    static final String YAHOO_CALLBACK_URL = "myyahoo://";
    static final String YAHOO_CONSUMER_KEY = "dj0yJmk9cmE4OWdUdTBNSEJoJmQ9WVdrOWNEVTFaR001TXpZbWNHbzlOelV6TXpneU16WXkmcz1jb25zdW1lcnNlY3JldCZ4PTAz";
    static final String YAHOO_CONSUMER_SECRET = "cd05d0e3adcb691d030bedd9d83fdfb954d50600";
    
    private String OAuthVerifier;
    
    private LinearLayout layout;
    private WebView webview;
    
    CommonsHttpOAuthConsumer mainConsumer;
    CommonsHttpOAuthProvider mainProvider;
    
	private Button button1;
	private OnClickListener button1_onclick = new OnClickListener()
	{
		 public void onClick(View v)
		 {
			 //layout.removeViewAt(1);
			 layout.addView(webview);
			 new OAuthRequestTokenTask(v.getContext(),mainConsumer,mainProvider).execute();
		 }
	};
	
	private Button button2;
	private OnClickListener button2_onclick = new OnClickListener()
	{
		 public void onClick(View v)
		 {
			 new OAuthGetAccessTokenTask().execute();
		 }
	};
	
	private Button button3;
	private OnClickListener button3_onclick = new OnClickListener()
	{
		 public void onClick(View v)
		 {
			 getGUID();
		 }
	};
	
	private Button button4;
	private OnClickListener button4_onclick = new OnClickListener()
	{
		 public void onClick(View v)
		 {
			 showToken();
		 }
	};
	
	private Button button5;
	private OnClickListener button5_onclick = new OnClickListener()
	{
		 public void onClick(View v)
		 {
			 getProfile();
		 }
	};
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.layout = (LinearLayout) this.findViewById(R.id.linearLayout1);
		this.webview = new WebView(this);
		webview.setWebViewClient(new MyWebViewClient(this));
		
		this.mainConsumer = new CommonsHttpOAuthConsumer(YAHOO_CONSUMER_KEY, YAHOO_CONSUMER_SECRET);
		this.mainProvider = new CommonsHttpOAuthProvider(REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL, AUTHORIZE_WEBSITE_URL);
		
		//this.mainConsumer.setSigningStrategy(new YahooAuthorizationHeaderSigningStrategy());
		
		// It turns out this was the missing thing to making standard Activity launch mode work
		//this.mainProvider.setOAuth10a(true);
		
		// get request
		button1 = (Button) this.findViewById(R.id.button1);
		button1.setOnClickListener(button1_onclick);
		
		// access token
		button2 = (Button) this.findViewById(R.id.button2);
		button2.setOnClickListener(button2_onclick);
		
		// guid
		button3 = (Button) this.findViewById(R.id.button3);
		button3.setOnClickListener(button3_onclick);
		
		// show token
		button4 = (Button) this.findViewById(R.id.button4);
		button4.setOnClickListener(button4_onclick);
		
		// Profile
		button5 = (Button) this.findViewById(R.id.button5);
		button5.setOnClickListener(button5_onclick);
	}
	
	class OAuthRequestTokenTask extends AsyncTask<Void, Void, String> {

		final String TAG = getClass().getName();
		private Context	context;
		private OAuthProvider provider;
		private OAuthConsumer consumer;

		public OAuthRequestTokenTask(Context context,OAuthConsumer consumer,OAuthProvider provider) {
			this.context = context;
			this.consumer = consumer;
			this.provider = provider;
		}

		@Override
		protected String doInBackground(Void... params) {

			try {
				Log.i(TAG, "Retrieving request token from Google servers");
				final String url = provider.retrieveRequestToken(consumer, YAHOO_CALLBACK_URL);
				Log.i(TAG, "Popping a browser with the authorize URL : " + url);
				//Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
				//context.startActivity(intent);
				
				return url;
			} catch (Exception e) {
				Log.e(TAG, "Error during OAUth retrieve request token", e);
			}

			return null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			webview.loadUrl(result);
		} 
	}
	
	public class OAuthGetAccessTokenTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				mainProvider.retrieveAccessToken(mainConsumer, OAuthVerifier);
			} catch (OAuthMessageSignerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			//super.onPostExecute(result);
			showToken();
		}
		
	}
	
	public void setVerifier(String verifier)
	{
		this.OAuthVerifier = verifier;
//		this.webview.loadData("verifier = " + this.OAuthVerifier + "<br>", "text/html", null);
		Log.d("setVerifier", verifier);
		
		this.showToken();
	}
	
	public void showToken()
	{
		//Log.d("SubPlurkV2", "Token = " + mainConsumer.getToken() + " and secret = " + mainConsumer.getTokenSecret());
		String str = 
				"verifier = " + this.OAuthVerifier + "<br>" + 
				"Token = " + mainConsumer.getToken() + "<br>" + 
				"secret = " + mainConsumer.getTokenSecret() + "<br>" + 
				"oauth_expires_in = " + mainProvider.getResponseParameters().getFirst("oauth_expires_in") + "<br>" +
				"oauth_session_handle = " + mainProvider.getResponseParameters().getFirst("oauth_session_handle") + "<br>" +
				"oauth_authorization_expires_in = " + mainProvider.getResponseParameters().getFirst("oauth_authorization_expires_in") + "<br>" + 
				"xoauth_yahoo_guid = " + mainProvider.getResponseParameters().getFirst("xoauth_yahoo_guid") + "<br>";
		
		this.webview.loadData(str, "text/html", null);		
	}
	
	private void doGet(String url) {
		OAuthConsumer consumer = this.mainConsumer;

	   	HttpGet request = new HttpGet(url);
	   	Log.i("doGet","Requesting URL : " + url);
	   	try {
			consumer.sign(request);
			new OAuthRequestTask(this.webview).execute(request);
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getGUID()
	{
		String GUID_URL="http://social.yahooapis.com/v1/me/guid?format=json"; 
		this.doGet(GUID_URL);
	}
	
	public void getProfile()
	{
		String guid = mainProvider.getResponseParameters().getFirst("xoauth_yahoo_guid");
		String url = "http://social.yahooapis.com/v1/user/" + guid + "/profile?format=json";
		this.doGet(url);
	}
}