package com.gmail.takashi316.acousticpositioning;

import java.io.IOException;
import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleUrl;

import com.google.api.client.googleapis.MethodOverride;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.xml.atom.AtomParser;
import com.google.api.client.xml.XmlNamespaceDictionary;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SpreadsheetActivity extends MenuActivity {
	final static private int START_ACTIVITY_FOR_RESULT_REQUEST_CODE = 0;
	final static private int DIALOG_ACCOUNTS = 0;
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

	private final HttpTransport httpTransport = AndroidHttp
			.newCompatibleTransport();

	private void getAuthToken() {
		accountManager = AccountManager.get(this);
		Account[] accounts = accountManager.getAccountsByType("com.google");
		if (accounts.length == 0) {
			Log.v("no accounts in AccountManager");
			return;
		}
		for (int i = 0; i < accounts.length; i++) {
			Account account = accounts[i];
			Log.v(account.name);
		}// for

		accountType = accounts[0].type;
		accountName = accounts[0].name;
		accountManager.getAuthToken(accounts[0], // ƒeƒXƒg‚È‚Ì‚ÅŒÅ’è
				"writely", // ¦1
				null, this, new AccountManagerCallback<Bundle>() {
					public void run(AccountManagerFuture<Bundle> arg0) {
						try {
							Bundle bundle = arg0.getResult();
							if (bundle.containsKey(AccountManager.KEY_INTENT)) {
								// needs to invoke another activity to grant
								// account.
								Intent intent = bundle
										.getParcelable(AccountManager.KEY_INTENT);
								int flags = intent.getFlags();
								flags &= ~Intent.FLAG_ACTIVITY_NEW_TASK;
								intent.setFlags(flags);
								startActivityForResult(intent,
										START_ACTIVITY_FOR_RESULT_REQUEST_CODE);
								return;
							} else {
								authToken = bundle
										.getString(AccountManager.KEY_AUTHTOKEN);
							}// if
						} catch (OperationCanceledException e) {
							e.printStackTrace();
						} catch (AuthenticatorException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}// try
						runOnUiThread(new Runnable() {
							public void run() {
								editTextAuthToken.setText(authToken);
								editTextAccountType.setText(accountType);
								editTextAccountName.setText(accountName);
							}// run
						});// runOnUiThread

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

	// user defined http request initializer
	HttpRequestInitializer httpRequestInitializer = new HttpRequestInitializer() {
		final GoogleAccessProtectedResource google_access_protected_resource = new GoogleAccessProtectedResource(
				authToken);

		public void initialize(HttpRequest request) throws IOException {
			GoogleHeaders google_headers = new GoogleHeaders();
			google_headers.setApplicationName("Google-DocsSample/1.0");
			google_headers.setGoogleLogin(authToken);
			google_headers.gdataVersion = "3";

			request.setHeaders(google_headers);
			request.setInterceptor(new HttpExecuteInterceptor() {
				public void intercept(HttpRequest request) throws IOException {
					method_override.intercept(request);
					google_access_protected_resource.intercept(request);
				}// intercept
			});// HttpExecuteIntercept
			request.addParser(new AtomParser(xml_name_space_dictionary));
			request.setUnsuccessfulResponseHandler(google_access_protected_resource);
		}// initialize
	};

	private void requestFeed() {
		GoogleUrl google_url = new GoogleUrl("https://docs.google.com/feeds");
		google_url.getPathParts().add("default");
		google_url.getPathParts().add("private");
		google_url.getPathParts().add("full");

		// it works but use AndroidHttp.newCompatibleTransport instead.
		// NetHttpTransport net_http_transport = new NetHttpTransport();

		HttpRequestFactory http_request_factory = httpTransport
				.createRequestFactory(httpRequestInitializer);

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
		Log.d("HTTP response status code = " + http_response.getStatusCode());
		Log.d("HTTP response content type = " + http_response.getContentType());

		try {
			this.documentListFeed = http_response
					.parseAs(DocumentListFeed.class);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		for (int i = 0; i < this.documentListFeed.docs.size(); ++i) {
			Log.d(this.documentListFeed.docs.get(i).title);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case START_ACTIVITY_FOR_RESULT_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Log.v("account granted");
			} else {
				showDialog(DIALOG_ACCOUNTS);
			}// if
			break;
		}// switch
	}// onActivityResult

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ACCOUNTS:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Select a Google account");
			final AccountManager manager = AccountManager.get(this);
			final Account[] accounts = manager.getAccountsByType("com.google");
			final int size = accounts.length;
			String[] names = new String[size];
			for (int i = 0; i < size; i++) {
				names[i] = accounts[i].name;
			}
			builder.setItems(names, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Log.v("account " + which + " was selected.");
				}// onClick
			});
			return builder.create();
		}// switch
		return null;
	}// onCreateDialog
}// SpreadsheetActivity
