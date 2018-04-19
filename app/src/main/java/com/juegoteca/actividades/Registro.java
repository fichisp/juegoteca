package com.juegoteca.actividades;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
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

public class Registro extends Activity {

    private String url_registro_usuario;
    private Utilidades utilidades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        utilidades = new Utilidades(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.registro, menu);
        return true;
    }

    /**
     * Comprueba los datos del formulario e intenta realiza el registro del usuario en la
     * base de datos del servidor
     *
     * @param view
     */
    public void iniciarRegistro(View view) {
        url_registro_usuario = this.getResources().getString(R.string.url_registro_usuario);
        String usuario = ((EditText) findViewById(R.id.editTextUsuario)).getText().toString();
        String email = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
        String password = utilidades.encriptar(((EditText) findViewById(R.id.editTextContrasena)).getText().toString());
        String password2 = utilidades.encriptar(((EditText) findViewById(R.id.editTextRepiteContrasena)).getText().toString());
        // Comprobar que las constrasenas son iguales
        if (password.compareTo(password2) != 0) {
            Toast t = Toast.makeText(this, R.string.registro_contrasenas_no_coinciden, Toast.LENGTH_SHORT);
            t.show();
        } else {
            List<NameValuePair> paramsJS = new ArrayList<NameValuePair>();
            paramsJS.add((new BasicNameValuePair("usuario", usuario)));
            paramsJS.add((new BasicNameValuePair("email", email)));
            paramsJS.add((new BasicNameValuePair("password", password)));
            JSONParser jParser = new JSONParser();

            JSONObject jsInicioSesion = jParser.makeHttpRequest(url_registro_usuario, "POST", paramsJS);

            if (jsInicioSesion != null) {
                Toast t;
                try {
                    switch (Integer.valueOf(jsInicioSesion.get("success").toString())) {
                        // Registro correcto
                        case 1:
                            t = Toast.makeText(this, R.string.registro_correcto, Toast.LENGTH_SHORT);
                            t.show();
                            Intent intent = new Intent(getApplicationContext(), Opciones.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            break;
                        // El email ya existe
                        case 2:
                            t = Toast.makeText(this, R.string.registro_email_usado, Toast.LENGTH_SHORT);
                            t.show();
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    t = Toast.makeText(this, R.string.registro_correcto, Toast.LENGTH_SHORT);
                    t.show();
                    e.printStackTrace();
                }
            } else {
                Log.e("INICIO SESION", "JSON vacío");
            }
        }
    }

}
