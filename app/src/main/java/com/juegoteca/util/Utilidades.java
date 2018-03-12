package com.juegoteca.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.Process;
import android.provider.Settings.Secure;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnHoverListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.juegoteca.actividades.Splash;
import com.juegoteca.basedatos.JuegosSQLHelper;
import com.mijuegoteca.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

public class Utilidades {
    private JuegosSQLHelper juegosSQLH;
    private Context context;
    private String url_subir_copia, url_bajar_copia, url_comprobar_subir_copia,
            url_registrar_trial;
    private int serverResponseCode;
    static final String FTP_HOST = "**********";
    static final String FTP_USER = "**********";
    static final String FTP_PASS = "**********";
    private static final String MY_AD_UNIT_ID = "****************";
    public static final boolean ANUNCIOS = true;
    private InterstitialAd interstitial;

    public Utilidades(Context context) {
        this.context = context;
        juegosSQLH = new JuegosSQLHelper(context);
        url_subir_copia = context.getResources().getString(
                R.string.url_subir_copia);
        url_bajar_copia = context.getResources().getString(
                R.string.url_bajar_copia);
        url_comprobar_subir_copia = context.getResources().getString(
                R.string.url_comprobar_subir_copia);
        url_registrar_trial = context.getResources().getString(
                R.string.url_registrar_trial);
    }

    /**
     * Carga las generos en el spinner
     */
    @SuppressLint("NewApi")
    public void cargarGeneros(Spinner genero) {
        List<String> generos = new ArrayList<String>();

        String codigoIdioma = Locale.getDefault().getISO3Language();

        Cursor c = null;

        if (!"spa".equalsIgnoreCase(codigoIdioma)) {
            c = juegosSQLH.getGenerosEN();
        } else {
            c = juegosSQLH.getGeneros();
        }

        if (c != null && c.moveToFirst()) {
            do {
                generos.add(c.getString(1));
            } while (c.moveToNext());
        }
        c.close();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, generos);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genero.setAdapter(dataAdapter);

    }

    /**
     * Carga las generos en el spinner del buscador
     */
    @SuppressLint("NewApi")
    public void cargarGenerosBuscador(Spinner genero) {
        List<String> generos = new ArrayList<String>();
        // Detectar idioma

        String codigoIdioma = Locale.getDefault().getISO3Language();

        Cursor c = null;

        if (!"spa".equalsIgnoreCase(codigoIdioma)) {
            generos.add("ANY");
            c = juegosSQLH.getGenerosEN();
        } else {
            c = juegosSQLH.getGeneros();
            generos.add("CUALQUIERA");
        }

        if (c != null && c.moveToFirst()) {
            do {
                generos.add(c.getString(1));
            } while (c.moveToNext());
        }
        c.close();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, generos);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genero.setAdapter(dataAdapter);
    }

    /**
     * Carga las plataformas en el spinner
     */
    @SuppressLint("NewApi")
    public void cargarPlataformas(Spinner plataforma) {
        List<String> plataformas = new ArrayList<String>();

        Cursor c = juegosSQLH.getPlataformas();
        if (c != null && c.moveToFirst()) {
            do {
                plataformas.add(c.getString(1));
            } while (c.moveToNext());
        }
        c.close();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, plataformas);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plataforma.setAdapter(dataAdapter);

    }

    /**
     * Metodo especial para el buscador para contemplar el valor "todos"
     *
     * @param plataforma
     */
    public void cargarPlataformasBuscador(Spinner plataforma) {
        List<String> plataformas = new ArrayList<String>();

        String codigoIdioma = Locale.getDefault().getISO3Language();

        if (!"spa".equalsIgnoreCase(codigoIdioma)) {
            plataformas.add("ANY");
        } else {
            plataformas.add("CUALQUIERA");
        }

        Cursor c = juegosSQLH.getPlataformas();
        if (c != null && c.moveToFirst()) {
            do {
                plataformas.add(c.getString(1));
            } while (c.moveToNext());
        }
        c.close();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, plataformas);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plataforma.setAdapter(dataAdapter);
    }

    public void cargarFormatos(Spinner plataforma) {
        List<String> formatos = new ArrayList<String>();
        String codigoIdioma = Locale.getDefault().getISO3Language();

        if (!"spa".equalsIgnoreCase(codigoIdioma)) {
            formatos.add("Retail");
            formatos.add("Digital");
        } else {
            formatos.add("Fisico");
            formatos.add("Digital");
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, formatos);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plataforma.setAdapter(dataAdapter);
    }

    /**
     * @param plataforma
     */
    public void cargarFormatosBuscador(Spinner plataforma) {
        List<String> formatos = new ArrayList<String>();
        String codigoIdioma = Locale.getDefault().getISO3Language();

        if (!"spa".equalsIgnoreCase(codigoIdioma)) {
            formatos.add("ANY");
            formatos.add("Retail");
            formatos.add("Digital");
        } else {
            formatos.add("CUALQUIERA");
            formatos.add("Fisico");
            formatos.add("Digital");

        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, formatos);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plataforma.setAdapter(dataAdapter);
    }

    /**
     * Carga las clasificaciones en el spinner
     */
    @SuppressLint("NewApi")
    public void cargarClasificacion(Spinner clasificacion) {
        List<String> clasificaciones = new ArrayList<String>();

        Cursor c = juegosSQLH.getClasificaciones();
        if (c != null && c.moveToFirst()) {
            do {
                clasificaciones.add(c.getString(1));
            } while (c.moveToNext());
        }
        c.close();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, clasificaciones);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clasificacion.setAdapter(dataAdapter);
    }

    /**
     * Carga las clasificaciones en el spinner
     */
    @SuppressLint("NewApi")
    public void cargarIdioma(Spinner idioma) {
        List<String> idiomas = new ArrayList<String>();

        Cursor c = null;

        String codigoIdioma = Locale.getDefault().getISO3Language();

        if (!"spa".equalsIgnoreCase(codigoIdioma)) {
            c = juegosSQLH.getIdiomasEN();
        } else {
            c = juegosSQLH.getIdiomas();
        }

        if (c != null && c.moveToFirst()) {
            do {
                idiomas.add(c.getString(1));
            } while (c.moveToNext());
        }
        c.close();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, idiomas);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        idioma.setAdapter(dataAdapter);
    }

    /**
     * Carga las compañias para autocompletar
     */
    @SuppressLint("NewApi")
    public void cargarCompanias(AutoCompleteTextView compania) {
        List<String> companias = new ArrayList<String>();

        Cursor c = juegosSQLH.getCompanias();
        if (c != null && c.moveToFirst()) {
            do {
                companias.add(c.getString(1));
            } while (c.moveToNext());
        }
        c.close();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, companias) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(20);
                ((TextView) v).setPadding(15, 20, 15, 20);
                ((TextView) v).setOnHoverListener(new OnHoverListener() {

                    @Override
                    public boolean onHover(View v, MotionEvent event) {
                        v.setBackgroundColor(context.getResources().getColor(
                                R.color.naranja_cabeceras));
                        return true;
                    }
                });
                return v;
            }

        };
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        compania.setAdapter(dataAdapter);
    }

    /**
     * Carga un listado con los emails de las cuentas del dispositovo para el
     * autocompletar del campo
     *
     * @param email
     */
    public void cargarCuentasDispositivo(AutoCompleteTextView email) {
        // Autocompletar con los emails del dispositivo
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccounts();
        ArrayList<String> emails = new ArrayList<String>();
        int index = 0;
        Log.v("CUENTAS", "Hay " + accounts.length + "  cuentas");
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                Log.v("EMAIL:", possibleEmail);
                if (!emails.contains(possibleEmail)) {
                    emails.add(possibleEmail);
                }
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, emails) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(20);
                ((TextView) v).setPadding(15, 20, 15, 20);

                return v;
            }

        };
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        email.setAdapter(dataAdapter);

    }

    /**
     * Copia un fichero
     *
     * @param origen  Fichero de origen
     * @param destino Fichero de destino
     * @throws IOException
     */
    public void copiarFichero(File origen, File destino) throws IOException {
        InputStream in = new FileInputStream(origen);
        OutputStream out = new FileOutputStream(destino);
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    /**
     * Genera una imagen en la caché para la caratula de un juego
     *
     * @param caratulaOriginal
     * @return
     */
    public Uri caratulaTemporal(Uri caratulaOriginal) {
        Uri caratulaTemporal = null;
        try {
            InputStream is = context.getContentResolver().openInputStream(
                    caratulaOriginal);
            File outputDir = context.getCacheDir();
            Bitmap bmOriginal = BitmapFactory.decodeStream(is);
            Bitmap bmEscaldado = redimensionarImagen(bmOriginal, 600);
            File outputFile = File.createTempFile("temp", ".png", outputDir);
            bmEscaldado.compress(CompressFormat.JPEG, 90, new FileOutputStream(
                    outputFile));
            Log.v("Tamaño imagen original", "Ancho: " + bmOriginal.getWidth()
                    + " - Alto: " + bmOriginal.getHeight());
            Log.v("Temporales", "Directorio: " + outputDir.getAbsolutePath()
                    + " - Fichero: " + outputFile.getAbsolutePath());

            caratulaTemporal = Uri.parse(outputFile.getAbsolutePath());
            Log.v("CARGAR CARATULA",
                    "Carátula temporal: " + caratulaTemporal.toString());
        } catch (IOException e) {
            Toast toast = Toast.makeText(context.getApplicationContext(),
                    "Error al recuperar la imagen", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        return caratulaTemporal;
    }

    /**
     * Mueve la imagen de la caché al directorio de files
     *
     * @param caratulaTemporal
     * @param nombre
     * @return
     */
    public boolean caratulaDefinitiva(Uri caratulaTemporal, String nombre) {
        boolean caratulaDefinitiva = false;
        // String extensionFichero
        // =selectedImagePath.substring(selectedImagePath.lastIndexOf('.'),selectedImagePath.length());
        try {
            File origen = new File(caratulaTemporal.toString());
            File destino = new File(context.getFilesDir().getPath() + "/"
                    + nombre + ".png");
            Log.v("Copiando", "Desde" + origen.getAbsolutePath() + " a "
                    + destino.getAbsolutePath());
            Bitmap bmOriginal = BitmapFactory.decodeStream(new FileInputStream(
                    origen));
            Bitmap bmEscalado = redimensionarImagen(bmOriginal, 600);
            bmEscalado.compress(CompressFormat.JPEG, 90, new FileOutputStream(
                    destino));
            origen.delete();// Borramos el fichero de la caché
            caratulaDefinitiva = true;
        } catch (FileNotFoundException fnfe) {

        }
        return caratulaDefinitiva;
    }

    /**
     * Redimensiona una imagen
     *
     * @param imagen
     * @param tamanoMaximo
     * @param filter
     * @return
     */
    public Bitmap redimensionarImagen(Bitmap imagen, int ancho) {
        float proporcion = (float) imagen.getWidth()
                / (float) imagen.getHeight();
        int alto = Math.round(ancho / proporcion);
        Log.v("Dimensiones de la imagen original",
                "Ancho: " + imagen.getWidth() + " - Alto: "
                        + imagen.getHeight());
        Log.v("Proporción", String.valueOf(proporcion));
        Log.v("Dimensiones calculadas", "Ancho: " + ancho + " - Alto: " + alto);
        Bitmap newBitmap = Bitmap.createScaledBitmap(imagen, ancho, alto, true);
        return newBitmap;
    }

    /**
     * Valida la cadena suminstrada como fecha y devuelve un entero con el
     * resultado de la validación
     *
     * @param fecha Cadena de caracteres con la fecha suministrada (dd/mm/aaaa)
     * @return 0: la fecha es válida, 1: año incorrecto, 2:mes incorrecto, 3:
     * día incorrecto
     */
    public int validaFecha(String fecha) {
        int resultado = 0;
        try {
            int dia = Integer.parseInt(fecha.substring(0, 2));
            int mes = Integer.parseInt(fecha.substring(3, 5));
            int anio = Integer.parseInt(fecha.substring(6, 10));
            if (anio < 1950 || anio > Calendar.getInstance().get(Calendar.YEAR)) {
                return resultado = 1;
            } else {
                if ((mes < 1 || mes > 12)
                        || ((mes > Calendar.getInstance().get(Calendar.MONTH) + 1) && anio == Calendar
                        .getInstance().get(Calendar.YEAR))) {
                    return resultado = 2;
                } else {
                    if ((dia < 1 || dia > 31)
                            || ((mes == 4 || mes == 6 || mes == 9 || mes == 11) && (dia > 30))
                            || (mes == 2 && esBisiesto(anio) && dia > 29)
                            || (mes == 2 && !esBisiesto(anio) && dia > 28)) {
                        return resultado = 3;
                    }
                }
            }
        } catch (Exception e) {
            return resultado = 5;
        }
        return resultado;
    }

    /**
     * Calcula si un año es bisiesto
     *
     * @param anio Año para el cálculo
     * @return
     */
    public boolean esBisiesto(int anio) {
        if (anio % 400 == 0) {
            return true;
        } else {
            if (anio % 4 == 0 && anio % 100 != 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Convierte una fecha en formato dd/MM/YYYY en milisegundos
     *
     * @param fecha Fecha a convertir
     * @return Cadena de texto con la fecha convertida en ms
     */
    @SuppressLint("SimpleDateFormat")
    public String convertirFechaAMilisegundos(String fecha) {
        long time = 0;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date;
        try {
            date = dateFormat.parse(fecha);
            time = date.getTime() / 1000L; // UNIX Timestamp
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.v("Convertir fecha en milisegundos", "Fecha: " + fecha + " -  ts: "
                + String.valueOf(time));
        return String.valueOf(time);
    }

    /**
     * Convierte un valor en milisegundos en una fecha con el formato dd/MM/YYYY
     *
     * @param milisegundos Milisegundos a convertir
     * @return Fecha convertida
     */
    @SuppressLint("SimpleDateFormat")
    public String convertirMilisegundosAFecha(String milisegundos) {
        try {
            Date date = new Date(Long.parseLong(milisegundos) * 1000L);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String fecha = dateFormat.format(date);
            Log.v("Convertir milisegundos en fecha", "Milisegundos: "
                    + milisegundos + " -  Fecha: " + fecha);
            return fecha;
        } catch (NumberFormatException nfe) {
            return "";
        }
    }

    /**
     * Descarga una imagen desde un servidor y tranforma en un bitmap
     *
     * @param url Url donde está ubuicada la imagen
     * @return Bitmap resultante de la conversión
     */
    public Bitmap descargarImagen(String urlImagen) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(urlImagen);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            // connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
            input.close();
            connection.disconnect();
        } catch (IOException ioe) {

        }
        return bitmap;
    }

    /**
     * Sube un fichero de copia de seguridad al servidor
     *
     * @param rutaFichero Ruta del fichero a subir
     * @return int Código de respuesta del servidor en forma de
     */
    public int subirFichero(String rutaFichero, String ubicacion) {

        String fichero = rutaFichero;

        HttpURLConnection conexion = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(rutaFichero);

        try {
            // Abrir la conexión
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            SharedPreferences settings = context.getSharedPreferences(
                    "UserInfo", 0);
            URL url = new URL(ubicacion + "?usuario="
                    + settings.getString("usuario", ""));
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setDoInput(true); // Allow Inputs
            conexion.setDoOutput(true); // Allow Outputs
            conexion.setUseCaches(false); // Don't use a Cached Copy
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Connection", "Keep-Alive");
            conexion.setRequestProperty("ENCTYPE", "multipart/form-data");
            conexion.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            conexion.setRequestProperty("uploaded_file", fichero);

            dos = new DataOutputStream(conexion.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name='uploaded_file';filename='"
                    + fichero + "'" + lineEnd);

            dos.writeBytes(lineEnd);

            // Leer el fichero
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // Multipart form data necesaria tras subir los datos del fichero
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Respuesta del servidor
            serverResponseCode = conexion.getResponseCode();
            Log.v("Subir fichero - response", "Código: " + serverResponseCode);

            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (FileNotFoundException fnfe) {
            Log.v("SUBIR FICHERO", "error: " + fnfe.getMessage(), fnfe);
            return 0;
        } catch (MalformedURLException ex) {
            Log.v("SUBIR FICHERO", "error: " + ex.getMessage(), ex);
            return 0;
        } catch (Exception e) {
            Log.v("SUBIR FICHERO", "Exception : " + e.getMessage(), e);
            return 0;
        }
        return serverResponseCode;

    }

    /**
     * Realiza una copia de seguridad y la almacena en el servidor
     *
     * @return
     */
    public int hacerCopiaSeguridadServidor() {
        int copiaSubida = 0;
        switch (comprobarSubirCopia()) {
            case 0:// Usuario no encontrado
                break;
            case 1:// Usuario sin activar
                copiaSubida = 1;
                break;
            case 2: // Usuario válido
                String zip = generarFicheroCopiaSeguridad();
                // Subimos al servidor el fichero comprimido
                // if(subirFichero(zip, url_subir_copia) == 200){
                // copiaSubida = 2;
                // }
                copiaSubida = subirCopiaFTP(new File(zip));
                // Borrar el fichero temporal con la copia
                File fileZIP = new File(zip);
                fileZIP.delete();
                break;
            case 3:// Usuario baneado
                copiaSubida = 3;
                break;
        }
        return copiaSubida;
    }

    /**
     * Comprueba si el usuario actual puede subir una copia al servidor
     *
     * @return
     */
    public int comprobarSubirCopia() {
        int estado = 0;
        SharedPreferences settings = context
                .getSharedPreferences("UserInfo", 0);

        List<NameValuePair> paramsJS = new ArrayList<NameValuePair>();
        paramsJS.add((new BasicNameValuePair("usuario", settings.getString(
                "usuario", ""))));

        JSONParser jParser = new JSONParser();
        JSONObject jsComprobarCopia = jParser.makeHttpRequest(
                url_comprobar_subir_copia, "GET", paramsJS);
        if (jsComprobarCopia != null) {
            try {
                estado = Integer.valueOf(jsComprobarCopia.get("success")
                        .toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return estado;
    }

    /**
     * Realiza una copia de seguridad para que el usuario la almacene o la envíe
     *
     * @return string Ruta del fichero generado
     */
    public String hacerCopiaSeguridadFichero() {


        String zipOrigen = generarFicheroCopiaSeguridad();
        String dia = String.valueOf(Calendar.getInstance().get(
                Calendar.DAY_OF_MONTH));
        String mes = String
                .valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
        String ano = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

        Calendar calendar = Calendar.getInstance();

        String fechaFichero = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(calendar.getTime());

        String zipdestino = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + "/juegoteca_backup_"
                + fechaFichero
                + ".zip";
        // Mover a directorio
        try {
            File origen = new File(zipOrigen);
            File destino = new File(zipdestino);
            copiarFichero(origen, destino);
            origen.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return zipdestino;
    }

    /**
     * Genera un fichero de copia de seguridad
     *
     * @return
     */
    private String generarFicheroCopiaSeguridad() {
        // Comprimimos los ficheros necesarios para la copia de seguridad
        final String zip = context.getCacheDir().getPath()
                + "/"
                + encriptar(context.getSharedPreferences("UserInfo", 0)
                .getString("usuario", "")) + ".zip";
        ZipOutputStream osZIP;
        OutputStream ficheroZIP;
        try {
            ficheroZIP = new FileOutputStream(zip);
            osZIP = new ZipOutputStream(ficheroZIP);
            osZIP.setLevel(Deflater.BEST_COMPRESSION);
            Log.v("ZIP", zip);
            Log.v("Directorio files", context.getFilesDir().getPath());
            Log.v("Ruta de la base de datos",
                    context.getDatabasePath("JUEGOTECA").getAbsolutePath());
            // Directorio "files" que contiene las imágenes
            File directorioFiles = new File(context.getFilesDir().getPath());
            File[] ficherosFiles = directorioFiles.listFiles();
            for (int x = 0; x < ficherosFiles.length; x++) {
                Log.v("Fichero actual", ficherosFiles[x].getAbsolutePath());
                ZipEntry entrada = new ZipEntry("files/"
                        + ficherosFiles[x].getName());
                osZIP.putNextEntry(entrada);
                FileInputStream fis = new FileInputStream(
                        ficherosFiles[x].getAbsolutePath());
                byte[] buffer = new byte[1024];
                int leido = 0;
                while (0 < (leido = fis.read(buffer))) {
                    osZIP.write(buffer, 0, leido);
                }
                fis.close();
                osZIP.closeEntry();
            }
            // Base de datos
            ZipEntry entrada = new ZipEntry("databases/Juegoteca");
            osZIP.putNextEntry(entrada);
            InputStream fis = new FileInputStream(context.getDatabasePath(
                    "Juegoteca").getAbsolutePath());
            byte[] buffer = new byte[1024];
            int leido = 0;
            while (0 < (leido = fis.read(buffer))) {
                osZIP.write(buffer, 0, leido);
            }
            fis.close();
            osZIP.closeEntry();
            osZIP.close();
        } catch (FileNotFoundException e) {
            Log.v("HACER COPIA SEGURIDAD",
                    "Fichero no encontrado: " + e.getMessage());
            return null;
        } catch (IOException e) {
            Log.v("HACER COPIA SEGURIDAD",
                    "Error de entrada/salida: " + e.getMessage());
            return null;
        }
        return zip;
    }

    /**
     * Descarga un fichero de copia de seguridad del servidor (si existe),
     * eliminanando lo datos actuales de la aplicación sustituyéndolos por los
     * contenidos en el fichero de copia de seguridad
     *
     * @return int Valor para indicar el resultado de la operacion. Usado para
     * manejar estados de la cuenta de usuario.
     */
    public int restaurarCopiaSeguridadServidor() {
        int copiaRestaurada = 0;
        // Consultar en la BBDD online si ha una copia de segiridad para este
        // usuario
        String urlCopiaSeguridad = "";
        SharedPreferences settings = context
                .getSharedPreferences("UserInfo", 0);

        List<NameValuePair> paramsJS = new ArrayList<NameValuePair>();
        paramsJS.add((new BasicNameValuePair("usuario", settings.getString(
                "usuario", ""))));

        JSONParser jParser = new JSONParser();
        JSONObject jsBajarCopia = jParser.makeHttpRequest(url_bajar_copia,
                "GET", paramsJS);

        if (jsBajarCopia != null) {
            try {
                switch (Integer.valueOf(jsBajarCopia.get("success").toString())) {
                    case 1:
                        urlCopiaSeguridad = jsBajarCopia.get("ruta").toString();
                        final String rutaFicheroTemporal = context.getCacheDir()
                                .getPath() + "/" + "copia_seguridad.zip";
                        URL url;
                        // Descargar el fichero
                        try {
                            Log.v("Restaruar desde servidor", "Bajando:"
                                    + urlCopiaSeguridad);
                            url = new URL(urlCopiaSeguridad);
                            HttpURLConnection connection = (HttpURLConnection) url
                                    .openConnection();
                            InputStream input = connection.getInputStream();
                            OutputStream zipTMP = new FileOutputStream(new File(
                                    rutaFicheroTemporal));
                            byte[] array = new byte[1000];
                            int leido = input.read(array);
                            while (leido > 0) {
                                zipTMP.write(array, 0, leido);
                                leido = input.read(array);
                            }
                            input.close();
                            zipTMP.close();
                        } catch (MalformedURLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        // Crear el directorio "files" si no existe
                        File files = new File(context.getFilesDir().getPath());
                        // Descomprimir el fichero
                        try {
                            ZipInputStream zis = new ZipInputStream(
                                    new FileInputStream(rutaFicheroTemporal));
                            ZipEntry entrada;
                            // Borrar los ficheros de la carpeta "files" y
                            // "databases"
                            borrarTodosDatos(false);
                            while (null != (entrada = zis.getNextEntry())) {
                                // Extraer los ficheros en sus correspondientes
                                // directorios
                                Log.v("RESTAURAR COPIA",
                                        "Extrayendo: "
                                                + entrada.getName()
                                                + " en "
                                                + context.getApplicationInfo().dataDir
                                                + "/" + entrada.getName());
                                OutputStream fos = new FileOutputStream(
                                        context.getApplicationInfo().dataDir + "/"
                                                + entrada.getName());
                                int leido;
                                byte[] buffer = new byte[1024];
                                while (0 < (leido = zis.read(buffer))) {
                                    fos.write(buffer, 0, leido);
                                }
                                fos.close();
                                zis.closeEntry();
                            }
                            zis.close();
                            new File(rutaFicheroTemporal).delete();// Borrar la
                            // copia de
                            // seguridad
                            // descargada
                            copiaRestaurada = 1;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        copiaRestaurada = 2;
                        break;
                    case 3:
                        copiaRestaurada = 3;
                        break;
                }

            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {

        }
        return copiaRestaurada;
    }

    /**
     * Abre un fichero seleccionado por el usuario para sobreescribir los datos
     * de la aplicación
     *
     * @param path
     * @return boolean Resultado de la operación
     */
    public boolean restaurarCopiaSeguridadFichero(Uri path) {
        boolean copiaRestaurada = false;
        // Descomprimir el fichero
        try {
            File files = new File(context.getFilesDir().getPath());
            ZipInputStream zis = new ZipInputStream(context
                    .getContentResolver().openInputStream(path));
            if (zipValido(path)) {
                ZipEntry entrada;
                // TODO: Comprobar que el fichero es correcto
                // Borrar los ficheros de la carpeta "files" y "databases"
                borrarTodosDatos(false);
                while (null != (entrada = zis.getNextEntry())) {
                    // Extraer los ficheros en sus correspondientes directorios
                    Log.v("RESTAURAR COPIA", "Extrayendo: " + entrada.getName()
                            + " en " + context.getApplicationInfo().dataDir
                            + "/" + entrada.getName());
                    OutputStream fos = new FileOutputStream(
                            context.getApplicationInfo().dataDir + "/"
                                    + entrada.getName());
                    int leido;
                    byte[] buffer = new byte[1024];
                    while (0 < (leido = zis.read(buffer))) {
                        fos.write(buffer, 0, leido);
                    }
                    fos.close();
                    zis.closeEntry();
                }
                zis.close();
                // new File(path).delete();//Borrar la copia de seguridad
                // descargada
                // Reiniciar la aplicación
                // reiniciarApp("Copia restaurada. La aplicación se va a reiniciar");
                copiaRestaurada = true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return copiaRestaurada;
        } catch (IOException e) {
            e.printStackTrace();
            return copiaRestaurada;
        }
        return copiaRestaurada;
    }

    /**
     * Comprueba la validez de un fichero zip como copia de seguridad
     *
     * @param zis Ruta del fichero que se va a comprobar
     * @return boolean Resultado
     */
    private boolean zipValido(Uri path) {
        boolean zipValido = true;
        try {
            ZipInputStream zis = new ZipInputStream(context
                    .getContentResolver().openInputStream(path));
            ZipEntry entrada;
            while (null != (entrada = zis.getNextEntry())) {
                Log.v("COMPROBANDO COPIA", "Comprobando: files"
                        + entrada.getName().toString().contains("files"));
                if (entrada.getName().toString().contains("files") == false
                        && entrada.getName().toString().contains("databases") == false) {
                    zipValido = false;
                    zis.close();
                    break;
                }
            }
            zis.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return zipValido = false;
        }
        return zipValido;
    }

    /**
     * Elimina todos los datos de la aplicación
     *
     * @param mostrarAlerta Parámetro para indicar si queremos mostrar el cuadro de
     *                      advetencia de borrano
     */
    public void borrarTodosDatos(boolean mostrarAlerta) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.alerta_borrado_todo_texto).setTitle(
                R.string.alerta_borrado_todo_titulo);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        File directorioFiles = new File(context.getFilesDir()
                                .getPath());
                        File[] ficherosFiles = directorioFiles.listFiles();
                        for (int x = 0; x < ficherosFiles.length; x++) {
                            if (ficherosFiles[x].delete()) {
                                Log.v("BORRAR DATOS", "Borrado el fichero "
                                        + ficherosFiles[x].getAbsolutePath());
                            }
                        }
                        File fileBBDD = new File(context.getDatabasePath(
                                "Juegoteca").getAbsolutePath());

                        if (fileBBDD.delete()) {
                            Log.v("BORRAR DATOS", "Borrado el fichero "
                                    + context.getDatabasePath("Juegoteca")
                                    .getAbsolutePath());
                        }
                        reiniciarApp(context.getString(R.string.mensaje_datos_borrados_ok));
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        return;
                    }
                });

        // TODO Auto-generated method stub
        if (mostrarAlerta) {
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    /**
     * Reinicia la aplicación
     *
     * @param mensaje Mensaje a mostrar en el cuadro de diálogo
     */
    public void reiniciarApp(String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(mensaje).setTitle("");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Process.killProcess(Process.myPid());
                AlarmManager alm = (AlarmManager) context
                        .getSystemService(Context.ALARM_SERVICE);
                alm.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                        PendingIntent.getActivity(context, 0, new Intent(
                                context, Splash.class), 0));

            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    public void showBuildNotes(String versionName){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        String codigoIdioma = Locale.getDefault().getISO3Language();

        InputStream fraw;

        if (!"spa".equalsIgnoreCase(codigoIdioma)) {
            fraw = context.getResources().openRawResource(R.raw.relase_notes_en);
        } else {
            fraw = context.getResources().openRawResource(R.raw.relase_notes_es);
        }


        BufferedReader brin = new BufferedReader(new InputStreamReader(fraw));

        String texto = "";
        String linea;
        try {
            while ((linea = brin.readLine()) != null) {
                texto += linea;
            }
            fraw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        builder.setMessage(Html.fromHtml(texto)).setTitle(String.format(context.getResources().getString(
                R.string.pref_title_version, versionName)));

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final SharedPreferences settings = context.getSharedPreferences("UserInfo",
                        0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("releaseNotes", true);
                editor.commit();
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    /**
     * Comprueba si la base de datos está vacía
     *
     * @return
     */
    public boolean baseDatosEsVacia() {
        Cursor c = juegosSQLH.getJuegos();
        if (c == null || !c.moveToFirst()) {
            return true;
        }
        return false;
    }

    /**
     * Convierte una cadena de texto a MD5
     *
     * @param password Cadena de texto original
     * @return hashword Cadena de texto en MD5
     */
    public String encriptar(String password) {
        String hashword = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes());
            BigInteger hash = new BigInteger(1, md5.digest());
            hashword = hash.toString(16);
        } catch (NoSuchAlgorithmException nsae) {
            // ignore
        }
        return hashword;
    }

    /**
     * Redimensiona un control
     *
     * @param elemento
     * @param x
     * @param y
     */
    public void redimensionarElemento(View elemento) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        // Display display = context.getWindowManager().getDefaultDisplay();
        // Point size = new Point();
        // display.getSize(size);
        int width;
        int height;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;
        } else {
            width = display.getWidth(); // deprecated
            height = display.getHeight();
        }

        LayoutParams params = (LayoutParams) elemento.getLayoutParams();
        //params.width = Math.round(width / 4);
        params.height = Math.round(height / 4f);
        elemento.setLayoutParams(params);
    }

    // decodes image and scales it to reduce memory consumption

    /**
     * Decodifica una imagen y la escala para reducir el consumo de memoria
     *
     * @param f Fichero que se va a abrir
     * @return bitmap Bitmap resultante
     */
    public Bitmap decodeFile(File f) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = 2;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    /**
     * Elimina de memoria los drawables de una vista
     *
     * @param view
     */
    public void unbindDrawables(View view) {
        try {
            if (view != null) {
                if (view.getBackground() != null) {
                    view.getBackground().setCallback(null);
                }
                if (view instanceof ViewGroup) {
                    for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                        unbindDrawables(((ViewGroup) view).getChildAt(i));
                    }
                    ((ViewGroup) view).removeAllViews();
                }

            }
        } catch (Exception e) {
        }
    }

    /**
     * Registra un dispositivo en la base de datos online para la versión de
     * prueba
     *
     * @return
     * @throws JSONException
     */
    public boolean registrarDispositivo() throws JSONException {
        String android_id = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add((new BasicNameValuePair("android_id", android_id)));
        JSONParser jParser = new JSONParser();

        JSONObject resultadoRegistro = jParser.makeHttpRequest(
                url_registrar_trial, "GET", params);

        JSONArray jA = resultadoRegistro.getJSONArray("juego");
        ((JSONObject) jA.get(0)).get("id").toString();
        return false;
    }

    /**
     * Subida de un fichero por FTP
     *
     * @param fileName
     */
    public int subirCopiaFTP(File fileName) {
        FTPClient client = new FTPClient();

        try {
            client.connect(FTP_HOST, 21);
            client.login(FTP_USER, FTP_PASS);
            client.setType(FTPClient.TYPE_BINARY);
            client.changeDirectory("/public_html/uploads/copias_seguridad/");

            client.upload(fileName, new MyTransferListener());
            return 2;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                client.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * Carga un anuncio
     */
    public void cargarAnuncio() {
        if (ANUNCIOS) {
            interstitial = new InterstitialAd(context);
            interstitial.setAdUnitId(MY_AD_UNIT_ID);

            // Create ad request.
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// Emulador
                    .addTestDevice("358240057325789") // Nexus5
                    .build();

            // Begin loading your interstitial.
            interstitial.loadAd(adRequest);

            interstitial.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    displayInterstitial();
                }
            });
        }
    }

    /**
     * Muestra un anuncio
     */
    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }

    /**
     * Compartir imagen y texto
     * <p>
     * Ej:
     * <p>
     * <p>
     * <p>
     * try { File filePath = "/mnt/sdcard/vmphoto.jpg"; //This is imagefile path
     * in your change it acc. to your requirement.
     * share("twitter",filePath.toString(),"Hello"); <<<<<Hello is a text send
     * acc. to you req.
     * <p>
     * } catch(Exception e) { //exception occur might your app like gmail ,
     * facebook etc not installed or not working correctly. }
     *
     * @param nameApp
     * @param imagePath
     * @param text
     */

    public void share(String nameApp, String imagePath, String text) {
        try {
            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("image/jpeg");
            List<ResolveInfo> resInfo = this.context.getPackageManager()
                    .queryIntentActivities(share, 0);
            if (!resInfo.isEmpty()) {
                for (ResolveInfo info : resInfo) {
                    Intent targetedShare = new Intent(
                            android.content.Intent.ACTION_SEND);
                    targetedShare.setType("image/jpeg"); // put here your mime
                    // type
                    if (info.activityInfo.packageName.toLowerCase().contains(
                            nameApp)
                            || info.activityInfo.name.toLowerCase().contains(
                            nameApp)) {
                        targetedShare.putExtra(Intent.EXTRA_SUBJECT, text);
                        targetedShare.putExtra(Intent.EXTRA_TEXT, text);
                        targetedShare.putExtra(Intent.EXTRA_STREAM,
                                Uri.fromFile(new File(imagePath)));
                        targetedShare.setPackage(info.activityInfo.packageName);
                        targetedShareIntents.add(targetedShare);
                    }
                }
                Intent chooserIntent = Intent.createChooser(
                        targetedShareIntents.remove(0), "Select app to share");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                        targetedShareIntents.toArray(new Parcelable[]{}));
                this.context.startActivity(chooserIntent);
            }
        } catch (Exception e) {
        }
    }

    /**
     * @author alvaro
     */
    public class MyTransferListener implements FTPDataTransferListener {

        public void started() {
            // Transfer started
            // Toast.makeText(context, " Upload Started ...",
            // Toast.LENGTH_SHORT).show();
            System.out.println(" Upload Started ...");
        }

        public void transferred(int length) {

            // Yet other length bytes has been transferred since the last time
            // this
            // method was called
            // Toast.makeText(context, " transferred ..." + length,
            // Toast.LENGTH_SHORT).show();
            System.out.println(" transferred ..." + length);
        }

        public void completed() {
            // Transfer completed
            // Toast.makeText(context, " completed ...",
            // Toast.LENGTH_SHORT).show();
            System.out.println(" completed ...");
        }

        public void aborted() {
            // Transfer aborted
            // Toast.makeText(context," transfer aborted ,please try again...",
            // Toast.LENGTH_SHORT).show();
            System.out.println(" aborted ...");
        }

        public void failed() {
            // Transfer failed
            System.out.println(" failed ...");
        }

    }
}
