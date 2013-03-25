package com.test.oauthtest;

import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {

	private YahooScreen context;
	
	public MyWebViewClient(YahooScreen c)
	{
		super();
		init(c);
	}
	
	public void init(YahooScreen c)
	{
		this.context = c;
	}
	
	/* (non-Javadoc)
	 * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
	 */
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// TODO Auto-generated method stub
		Uri uri = Uri.parse(url);
		
		if(uri.getHost().equals("myyahoo"))
		{
			String verifier = uri.getQueryParameter("oauth_verifier");
			this.context.setVerifier(verifier);
			return true;
		}
		return super.shouldOverrideUrlLoading(view, url);	
	}

}
