package com.gmail.takashi316.acousticpositioning;

import java.io.IOException;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.googleapis.MethodOverride;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.xml.atom.AtomParser;
import com.google.api.client.xml.XmlNamespaceDictionary;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SpreadsheetActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// AccountManager を通じてGoogleアカウントを取得
		AccountManager manager = AccountManager.get(this);
		Account[] accounts = manager.getAccountsByType("com.google");
		Bundle bundle = null;
		try {
			bundle = manager.getAuthToken(accounts[0], // テストなので固定
					"writely", // ※1
					null, this, null, null).getResult();
		} catch (OperationCanceledException e) {
			Log.e("", e);
			return;
		} catch (AuthenticatorException e) {
			Log.e("", e);
			return;
		} catch (IOException e) {
			Log.e("", e);
			return;
		}

		String authToken = "";
		if (bundle.containsKey(AccountManager.KEY_INTENT)) {
			// 認証が必要な場合
			Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
			int flags = intent.getFlags();
			flags &= ~Intent.FLAG_ACTIVITY_NEW_TASK;
			intent.setFlags(flags);
			startActivityForResult(intent, 0);
			// 本当はResultを受けとる必要があるけど割愛
			return;
		} else {
			// 認証用トークン取得
			authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
		}

		final GoogleAccessProtectedResource google_access_protected_resource = new GoogleAccessProtectedResource(
				authToken);
		GoogleUrl google_url = new GoogleUrl("https://docs.google.com/feeds");
		google_url.getPathParts().add("default");
		google_url.getPathParts().add("private");
		google_url.getPathParts().add("full");

		final XmlNamespaceDictionary xml_name_space_dictionary = new XmlNamespaceDictionary()
				.set("", "http://www.w3.org/2005/Atom")
				.set("app", "http://www.w3.org/2007/app")
				.set("batch", "http://schemas.google.com/gdata/batch")
				.set("docs", "http://schemas.google.com/docs/2007")
				.set("gAcl", "http://schemas.google.com/acl/2007")
				.set("gd", "http://schemas.google.com/g/2005")
				.set("openSearch", "http://a9.com/-/spec/opensearch/1.1/")
				.set("xml", "http://www.w3.org/XML/1998/namespace");

		final MethodOverride method_override = new MethodOverride(); // needed
																		// for
																		// PATCH
		NetHttpTransport net_http_transport = new NetHttpTransport();
		HttpRequestFactory http_request_factory = net_http_transport
				.createRequestFactory(new HttpRequestInitializer() {
					public void initialize(HttpRequest request)
							throws IOException {
						GoogleHeaders headers = new GoogleHeaders();
						headers.setApplicationName("Google-DocsSample/1.0");
						headers.gdataVersion = "3";
						request.setHeaders(headers);
						request.setInterceptor(new HttpExecuteInterceptor() {

							public void intercept(HttpRequest request)
									throws IOException {
								method_override.intercept(request);
								google_access_protected_resource
										.intercept(request);
							}
						});
						request.addParser(new AtomParser(
								xml_name_space_dictionary));
						request.setUnsuccessfulResponseHandler(google_access_protected_resource);
					}
				});

		HttpRequest http_request;
		try {
			http_request = http_request_factory.buildGetRequest(google_url);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		HttpResponse http_response;
		try {
			http_response = http_request.execute();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		DocumentListFeed document_list_feed;
		try {
			document_list_feed = http_response.parseAs(DocumentListFeed.class);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		for (int i = 0; i < document_list_feed.totalResults; ++i) {
			Log.v(new Throwable(), document_list_feed.docs.get(i).title);
		}
	}// onCreate
}// SpreadsheetActivity