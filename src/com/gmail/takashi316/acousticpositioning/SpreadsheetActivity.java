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
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SpreadsheetActivity extends MenuActivity {
	private EditText editTextAuthToken;
	private Button buttonGetAuthToken;
	private EditText editTextAccountType;
	private Button buttonInvalidate;
	private Button buttonRequestFeed;
	private EditText editTextAccountName;

	private String authToken;
	private String accountType;
	private String accountName;
	private AccountManager accountManager;
	private DocumentListFeed documentListFeed;

	private void getAuthToken() {
		accountManager = AccountManager.get(this);
		Account[] accounts = accountManager.getAccountsByType("com.google");
		if (accounts.length == 0) {
			Log.v(new Throwable(), "no accounts in AccountManager");
			return;
		}
		for (int i = 0; i < accounts.length; i++) {
			Account account = accounts[i];
			Log.v(new Throwable(), account.name);
		}// for

		accountType = accounts[0].type;
		accountName = accounts[0].name;
		accountManager.getAuthToken(accounts[0], // テストなので固定
				"writely", // ※1
				null, this, new AccountManagerCallback<Bundle>() {
					public void run(AccountManagerFuture<Bundle> arg0) {
						try {
							Bundle bundle = arg0.getResult();
							if (bundle.containsKey(AccountManager.KEY_INTENT)) {
								// 認証が必要な場合
								Intent intent = bundle
										.getParcelable(AccountManager.KEY_INTENT);
								int flags = intent.getFlags();
								flags &= ~Intent.FLAG_ACTIVITY_NEW_TASK;
								intent.setFlags(flags);
								startActivityForResult(intent, 0);
								// 本当はResultを受けとる必要があるけど割愛
								return;
							} else {
								// 認証用トークン取得
								authToken = bundle
										.getString(AccountManager.KEY_AUTHTOKEN);
							}// if
						} catch (OperationCanceledException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (AuthenticatorException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						runOnUiThread(new Runnable() {
							public void run() {
								editTextAuthToken.setText(authToken);
								editTextAccountType.setText(accountType);
								editTextAccountName.setText(accountName);
							}// run
						});

					}// run
				}, null);// AccountManagerCallback
	}// getAuthToken

	private void invalidateAuthToken() {
		if (this.authToken == null) {
			return;
		}// if
		accountManager.invalidateAuthToken(accountType, authToken);
		accountType = null;
		authToken = null;
		accountName = null;
	}// invalidateAuthToken

	private void requestFeed() {
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

		final MethodOverride method_override = new MethodOverride();

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
							}// intercept
						});// HttpExecuteIntercept
						request.addParser(new AtomParser(
								xml_name_space_dictionary));
						request.setUnsuccessfulResponseHandler(google_access_protected_resource);
					}// initialize
				});// HttpRequestInitializer

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
		try {
			this.documentListFeed = http_response
					.parseAs(DocumentListFeed.class);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		for (int i = 0; i < this.documentListFeed.totalResults; ++i) {
			Log.v(new Throwable(), this.documentListFeed.docs.get(i).title);
		}
	}// requestFeed

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spreadsheet);

		editTextAuthToken = (EditText) findViewById(R.id.editTextGetAuthToken);
		buttonGetAuthToken = (Button) findViewById(R.id.buttonGetAuthToken);
		editTextAccountType = (EditText) findViewById(R.id.editTextAccountType);
		buttonInvalidate = (Button) findViewById(R.id.buttonInvalidate);
		buttonRequestFeed = (Button) findViewById(R.id.buttonRequestFeed);
		editTextAccountName = (EditText) findViewById(R.id.editTextAccountName);

		buttonGetAuthToken.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				getAuthToken();
			}// onClick
		});// OnClickListener

		buttonInvalidate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				invalidateAuthToken();
				if (accountType == null) {
					editTextAccountType.setText("null");
				} else {
					editTextAccountType.setText(accountType);
				}// if
				if (authToken == null) {
					editTextAuthToken.setText("null");
				} else {
					editTextAuthToken.setText(authToken);
				}// if
				if (accountName == null) {
					editTextAccountName.setText("null");
				} else {
					editTextAccountName.setText(accountName);
				}// if
			}// onClick
		});// OnClickListener

		buttonRequestFeed.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				requestFeed();
			}// onClick
		});// OnClickListener
	}// onCreate

}// SpreadsheetActivity