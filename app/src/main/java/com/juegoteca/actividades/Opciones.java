package com.juegoteca.actividades;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class Opciones extends PreferenceActivity {
    protected static final int PICKFILE_RESULT_CODE = 0;
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    private static final int FILE_SELECT_CODE = 0;
    private static Utilidades utilidades;
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference
                        .setSummary(index >= 0 ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };
    private ProgressDialog dialogoCopia;
    private ProgressDialog dialogoEmpaquetado;

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    @SuppressLint("InlinedApi")
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference
                .setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager.getDefaultSharedPreferences(
                        preference.getContext()).getString(preference.getKey(),
                        ""));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        utilidades = new Utilidades(this);
        setupSimplePreferencesScreen();

    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    @SuppressWarnings("deprecation")
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_general);
        // getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_general);

        // Add 'data and sync' preferences, and a corresponding header.
        PreferenceCategory fakeHeader2 = new PreferenceCategory(this);
        fakeHeader2.setTitle(R.string.pref_header_data);
        getPreferenceScreen().addPreference(fakeHeader2);
        addPreferencesFromResource(R.xml.pref_datos_seguridad);

        final SharedPreferences settings = getSharedPreferences("UserInfo",
                0);

        ListPreference preferenciaModeda = (ListPreference) findPreference("currencys");
        preferenciaModeda.
                setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    public boolean onPreferenceChange(Preference preference, Object newValue) {

                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("currency", newValue.toString());
                            editor.commit();

                        return true;
                    }
                });



        CheckBoxPreference preferenciaDetalle = (CheckBoxPreference) findPreference("detalle_imagen");
        preferenciaDetalle.
                setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("true")) {
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("detalle_imagen", true);
                            editor.commit();
                        } else {
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("detalle_imagen", false);
                            editor.commit();
                        }
                        return true;
                    }
                });

        // Establece las acciones al hacer click en las preferencias
        Preference preferenciaCopiaExportar = (Preference) findPreference("exportar_copia_seguridad");
        preferenciaCopiaExportar
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference preference) {
                        Toast t;
                        if (!utilidades.baseDatosEsVacia()) {
                            new CopiaSeguridadFichero().execute();
                        } else {
                            t = Toast.makeText(getApplicationContext(),
                                    getString(R.string.no_data_backup),
                                    Toast.LENGTH_SHORT);
                            t.show();
                        }
                        return true;
                    }
                });

        Preference preferenciaCopiaImportar = (Preference) findPreference("importar_restaurar_seguridad");

        preferenciaCopiaImportar
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference preference) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                Opciones.this);
                        builder.setMessage(R.string.alerta_restaurar_texto)
                                .setTitle(R.string.alerta_restaurar_titulo);
                        builder.setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        Intent intent = new Intent(
                                                Intent.ACTION_GET_CONTENT);
                                        intent.setType("application/zip");
                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                        startActivityForResult(
                                                Intent.createChooser(intent,
                                                        "Escoja el fichero de copia..."),
                                                FILE_SELECT_CODE);

                                    }
                                });

                        builder.setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // User cancelled the dialog
                                        return;
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        return true;
                    }
                });


        Preference preferenciaBorrar = (Preference) findPreference("borrar_todo");
        preferenciaBorrar
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference preference) {
                        utilidades.borrarTodosDatos(true);
                        return true;
                    }
                });

        // Add 'otros' preferences.
        PreferenceCategory fakeHeader3 = new PreferenceCategory(this);
        fakeHeader3.setTitle(R.string.pref_header_otros);
        getPreferenceScreen().addPreference(fakeHeader3);
        addPreferencesFromResource(R.xml.pref_otros);

        Resources res = getResources();
        // String versionName = res.getString(R.string.app_version);

        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(),
                    0).versionName;
        } catch (NameNotFoundException e) {
            // versionName = res.getString(R.string.app_version);
        }

        String version = res.getString(R.string.pref_title_version, versionName);
        Preference preferenciaInicioSesion = (Preference) findPreference("about");
        preferenciaInicioSesion.setTitle(version);

        Preference preferenciaLicencia = (Preference) findPreference("licenses");
        preferenciaLicencia
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(getApplicationContext(),
                                AcercaDe.class);
                        startActivity(intent);
                        return true;
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.buscador, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_home:
                intent = new Intent(this, Inicio.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    RestaurarCopiaFichero task = new RestaurarCopiaFichero();
                    task.data = data;
                    task.execute();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_datos_seguridad);
        }
    }

    /**
     * @author alvaro
     */
    private class CopiaSeguridad extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            dialogoCopia = ProgressDialog.show(Opciones.this, null,
                    "Realizando copia de seguridad...", true);
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialogoCopia.dismiss();
            Toast t;
            switch (result) {
                case 0:
                    t = Toast.makeText(getApplicationContext(),
                            "No se ha podido realizar la copia de seguridad.",
                            Toast.LENGTH_SHORT);
                    break;
                case 1:
                    t = Toast
                            .makeText(
                                    getApplicationContext(),
                                    "No se ha podido realizar la copia de seguridad. La cuenta de usuario no está activada",
                                    Toast.LENGTH_SHORT);
                    break;
                case 2:
                    t = Toast.makeText(getApplicationContext(),
                            "Copia de seguridad guardada correctamente.",
                            Toast.LENGTH_SHORT);
                    break;
                case 3:
                    t = Toast
                            .makeText(
                                    getApplicationContext(),
                                    "No se ha podido realizar la copia de seguridad. La cuenta de usuario está baneada",
                                    Toast.LENGTH_SHORT);
                    break;

                default:
                    t = Toast.makeText(getApplicationContext(),
                            "No se ha podido realizar la copia de seguridad.",
                            Toast.LENGTH_SHORT);
                    break;
            }
            t.show();
            return;
        }

        @Override
        protected Integer doInBackground(Void... arg0) {
            return utilidades.hacerCopiaSeguridadServidor();

        }
    }

    /**
     * @author alvaro
     */
    private class CopiaSeguridadFichero extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            dialogoEmpaquetado = ProgressDialog.show(Opciones.this, null,
                    getString(R.string.zip), true);

        }

        @Override
        protected void onPostExecute(String result) {
            dialogoEmpaquetado.dismiss();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM,
                    Uri.parse("file://" + result));
            shareIntent.setType("application/zip");
            startActivity(Intent.createChooser(shareIntent, "Enviar a..."));
            return;
        }

        @Override
        protected String doInBackground(Void... arg0) {
            String result = utilidades.hacerCopiaSeguridadFichero();

            return result;

        }
    }

    /**
     * @author alvaro
     */
    private class RestaurarCopia extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            dialogoCopia = ProgressDialog.show(Opciones.this, null,
                    "Restaurando copia de seguridad...", true);

        }

        @Override
        protected Integer doInBackground(Void... params) {
            return utilidades.restaurarCopiaSeguridadServidor();
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialogoCopia.dismiss();
            Toast t;
            switch (result) {
                case 1:
                    utilidades
                            .reiniciarApp(getString(R.string.copia_restaurada_ok));
                    break;
                case 2:
                    t = Toast.makeText(getApplicationContext(),
                            "No se hay ninguna copia de seguridad en el servidor.",
                            Toast.LENGTH_SHORT);
                    t.show();
                    break;
                case 3:
                    t = Toast
                            .makeText(
                                    getApplicationContext(),
                                    "No se ha podido restaurar la copia de seguridad porque se cuenta ha sido baneada.",
                                    Toast.LENGTH_SHORT);
                    t.show();
                    break;
            }
            return;
        }
    }

    /**
     * @author alvaro
     */
    private class RestaurarCopiaFichero extends AsyncTask<Void, Void, Integer> {

        Intent data;

        @Override
        protected void onPreExecute() {
            dialogoCopia = ProgressDialog.show(Opciones.this, null, getString(R.string.unzip), true);

        }

        @Override
        protected Integer doInBackground(Void... params) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            // Get the path
            String path = uri.getPath();
            if (utilidades.restaurarCopiaSeguridadFichero(uri)) {
                return 1;
            } else {
                return 2;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialogoCopia.dismiss();
            Toast t;
            switch (result) {
                case 1:
                    utilidades
                            .reiniciarApp(getString(R.string.copia_restaurada_ok));
                    break;
                case 2:
                    t = Toast.makeText(getApplicationContext(),
                            "Error.",
                            Toast.LENGTH_SHORT);
                    t.show();
                    break;
            }
            return;
        }
    }
}
