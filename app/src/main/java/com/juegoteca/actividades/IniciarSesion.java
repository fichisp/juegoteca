package com.juegoteca.actividades;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.juegoteca.util.JSONParser;
import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class IniciarSesion extends Activity {

	private static Utilidades utilidades;
	private String url_iniciar_sesion;
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
		"foo@example.com:hello", "bar@example.com:world" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
//	private EditText mEmailView;
	private AutoCompleteTextView mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		utilidades = new Utilidades(this);
		setContentView(R.layout.activity_iniciar_sesion);
		url_iniciar_sesion= this.getResources().getString(R.string.url_iniciar_sesion);

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		//mEmailView = (EditText) findViewById(R.id.email);
		mEmailView = (AutoCompleteTextView)findViewById(R.id.email);
		utilidades.cargarCuentasDispositivo(mEmailView);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
		.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		// Si el login dió error mostramos un mensaje
		if(getIntent().hasExtra("MENSAJE_ERROR")){
			Toast toast = Toast.makeText(getApplicationContext(), getIntent().getStringExtra("MENSAJE_ERROR"), Toast.LENGTH_SHORT);
			//			toast.setGravity(Gravity.CENTER|Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
			toast.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.iniciar_sesion, menu);
		return true;
	}

	/**
	 * Intento de inicio de sesión. Controla los errores en el formulario.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}
	/**
	 * Lanza la actividad para el registro de un nuevo usuario
	 * @param view
	 */
	public void registro(View view){
		Intent intent = new Intent(this, Registro.class);
		startActivity(intent);
	}

	/**
	 * Oculta el formulario de inicio se sesión y muestra un dialogo de progreso
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Clase para el inicio de sesión de forma asíncrona
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			Log.v("INICIO SESION", "Intento de inicio de sesion");
			String login = ((EditText)findViewById(R.id.email)).getText().toString();
			String password = utilidades.encriptar(((EditText)findViewById(R.id.password)).getText().toString());
			//			String password = ((EditText)findViewById(R.id.password)).getText().toString();
			Log.v("INICIO SESION", "Datos -> usuario: "+login+" - password: "+password);
			List<NameValuePair> paramsJS = new ArrayList<NameValuePair>();
			paramsJS.add((new BasicNameValuePair("login", login)));
			paramsJS.add((new BasicNameValuePair("password", password)));
			JSONParser jParser = new JSONParser();

			JSONObject jsInicioSesion = jParser.makeHttpRequest(url_iniciar_sesion, "POST", paramsJS);

			if(jsInicioSesion!=null){
				Log.v("INICIO SESION", "JSON:"+jsInicioSesion.toString());
				try {
					Intent intent;
					switch(Integer.valueOf(jsInicioSesion.get("success").toString())){
					case 0:
						intent=new Intent(getApplicationContext(),IniciarSesion.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("MENSAJE_ERROR", "Nombre de usuario o contraseña incorrectos.");
						startActivity(intent);
						break;
					case 1:
						SharedPreferences settings = getSharedPreferences("UserInfo", 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString("usuario",login);
						editor.commit();
						intent=new Intent(getApplicationContext(),Opciones.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						break;
					case 2:
						intent=new Intent(getApplicationContext(),IniciarSesion.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("MENSAJE_ERROR", "La cuenta no está activada. Revise su correo para localiza el mensaje con el en alce de activación.");
						startActivity(intent);
						break;
					case 3:
						intent=new Intent(getApplicationContext(),IniciarSesion.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("MENSAJE_ERROR", "Su cuenta ha sido baneada.");
						startActivity(intent);
						break;

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				Log.v("INICIO SESION", "JSON vacío");
				Intent intent=new Intent(getApplicationContext(),IniciarSesion.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("MENSAJE_ERROR", "No se ha podido conectar con el servidor.");
				startActivity(intent);
			}

			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(mEmail)) {
					// Account exists, return true if the password matches.
					return pieces[1].equals(mPassword);
				}
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
			} else {
				mPasswordView
				.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
