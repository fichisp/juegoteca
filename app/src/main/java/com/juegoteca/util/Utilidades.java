package com.juegoteca.util;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnHoverListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.juegoteca.actividades.Splash;
import com.juegoteca.basedatos.Juego;
import com.juegoteca.basedatos.JuegosSQLHelper;
import com.mijuegoteca.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

import static com.twitter.sdk.android.core.Twitter.TAG;


/**
 * Clase de utilidades con métodos compartidos por varias partes de la aplicación.
 */
public class Utilidades {

    private final JuegosSQLHelper juegosSQLH;
    private final Context context;

    public Utilidades(Context context) {
        this.context = context;
        juegosSQLH = new JuegosSQLHelper(context);
    }

    /**
     * Carga las generos en el spinner
     */
    @SuppressLint("NewApi")
    public void cargarGeneros(Spinner genero) {
        List<String> generos = new ArrayList<>();

        String codigoIdioma = Locale.getDefault().getISO3Language();

        Cursor c;

        if (!"spa".equalsIgnoreCase(codigoIdioma)) {
            c = juegosSQLH.getGenerosEN();
        } else {
            c = juegosSQLH.getGeneros();
        }

        if (c != null && c.moveToFirst()) {
            do {
                generos.add(c.getString(1));
            } while (c.moveToNext());

            c.close();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,
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
        List<String> generos = new ArrayList<>();
        // Detectar idioma
        String codigoIdioma = Locale.getDefault().getISO3Language();

        Cursor c;

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
            c.close();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,
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
        List<String> plataformas = new ArrayList<>();

        Cursor c = juegosSQLH.getPlataformas();
        if (c != null && c.moveToFirst()) {
            do {
                plataformas.add(c.getString(1));
            } while (c.moveToNext());

            c.close();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,
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
        List<String> plataformas = new ArrayList<>();

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

            c.close();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, plataformas);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plataforma.setAdapter(dataAdapter);
    }

    public void cargarFormatos(Spinner plataforma) {
        List<String> formatos = new ArrayList<>();
        String codigoIdioma = Locale.getDefault().getISO3Language();

        if (!"spa".equalsIgnoreCase(codigoIdioma)) {
            formatos.add("Retail");
            formatos.add("Digital");
        } else {
            formatos.add("Fisico");
            formatos.add("Digital");
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, formatos);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plataforma.setAdapter(dataAdapter);
    }

    /**
     * @param plataforma
     */
    public void cargarFormatosBuscador(Spinner plataforma) {
        List<String> formatos = new ArrayList<>();
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

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,
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
        List<String> clasificaciones = new ArrayList<>();

        Cursor c = juegosSQLH.getClasificaciones();
        if (c != null && c.moveToFirst()) {
            do {
                clasificaciones.add(c.getString(1));
            } while (c.moveToNext());

            c.close();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,
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
        List<String> idiomas = new ArrayList<>();

        Cursor c;

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

            c.close();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,
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
        List<String> companias = new ArrayList<>();

        Cursor c = juegosSQLH.getCompanias();
        if (c != null && c.moveToFirst()) {
            do {
                companias.add(c.getString(1));
            } while (c.moveToNext());

            c.close();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, companias) {
            @NonNull
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(20);
                v.setPadding(15, 20, 15, 20);
                v.setOnHoverListener(new OnHoverListener() {

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
     * Copia un fichero
     *
     * @param origen  Fichero de origen
     * @param destino Fichero de destino
     * @throws IOException
     */
    private void copiarFichero(File origen, File destino) throws IOException {
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
            caratulaTemporal = Uri.parse(outputFile.getAbsolutePath());
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
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean caratulaDefinitiva(Uri caratulaTemporal, String nombre) {
        boolean caratulaDefinitiva;
        try {
            File origen = new File(caratulaTemporal.toString());
            File destino = new File(context.getFilesDir().getPath() + "/"
                    + nombre + ".png");
            Bitmap bmOriginal = BitmapFactory.decodeStream(new FileInputStream(
                    origen));
            Bitmap bmEscalado = redimensionarImagen(bmOriginal, 600);
            bmEscalado.compress(CompressFormat.JPEG, 90, new FileOutputStream(
                    destino));
            origen.delete();// Borramos el fichero de la caché
        } catch (FileNotFoundException fnfe) {
            Log.e("Error", "Error al cargar la cartula" + fnfe.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Redimensiona una imagen
     * @param imagen
     * @param ancho
     * @return
     */
    public Bitmap redimensionarImagen(Bitmap imagen, int ancho) {
        float proporcion = (float) imagen.getWidth()
                / (float) imagen.getHeight();
        int alto = Math.round(ancho / proporcion);
        return Bitmap.createScaledBitmap(imagen, ancho, alto, true);
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
                return 1;
            } else {
                if ((mes < 1 || mes > 12)
                        || ((mes > Calendar.getInstance().get(Calendar.MONTH) + 1) && anio == Calendar
                        .getInstance().get(Calendar.YEAR))) {
                    return 2;
                } else {
                    if ((dia < 1 || dia > 31)
                            || ((mes == 4 || mes == 6 || mes == 9 || mes == 11) && (dia > 30))
                            || (mes == 2 && esBisiesto(anio) && dia > 29)
                            || (mes == 2 && !esBisiesto(anio) && dia > 28)) {
                        return 3;
                    }
                }
            }
        } catch (Exception e) {
            return 5;
        }
        return resultado;
    }

    /**
     * Calcula si un año es bisiesto
     *
     * @param anio Año para el cálculo
     * @return
     */
    private boolean esBisiesto(int anio) {
        return anio % 400 == 0 || anio % 4 == 0 && anio % 100 != 0;
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
            return dateFormat.format(date);
        } catch (NumberFormatException nfe) {
            return "";
        }
    }


    /**
     * Realiza una copia de seguridad para que el usuario la almacene o la envíe
     *
     * @return string Ruta del fichero generado
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public String hacerCopiaSeguridadFichero() {


        String zipOrigen = generarFicheroCopiaSeguridad();

        Calendar calendar = Calendar.getInstance();

        String fechaFichero = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(calendar.getTime());

        String zipdestino = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + "/juegoteca_backup_"
                + fechaFichero
                + ".zip";
        // Mover a directorio
        if(zipOrigen!=null) {
            try {
                File origen = new File(zipOrigen);
                File destino = new File(zipdestino);
                copiarFichero(origen, destino);
                origen.delete();

            } catch (IOException e) {
                e.printStackTrace();
            }
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

            // Directorio "files" que contiene las imágenes
            File directorioFiles = new File(context.getFilesDir().getPath());
            File[] ficherosFiles = directorioFiles.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".png");
                }
            });
            for (File ficherosFile : ficherosFiles) {


                    ZipEntry entrada = new ZipEntry("files/"
                            + ficherosFile.getName());
                    osZIP.putNextEntry(entrada);
                    FileInputStream fis = new FileInputStream(
                            ficherosFile.getAbsolutePath());
                    byte[] buffer = new byte[1024];
                    int leido;
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
            int leido;
            while (0 < (leido = fis.read(buffer))) {
                osZIP.write(buffer, 0, leido);
            }
            fis.close();
            osZIP.closeEntry();
            osZIP.close();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return zip;
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
            ZipInputStream zis = new ZipInputStream(context
                    .getContentResolver().openInputStream(path));
            if (zipValido(path)) {
                ZipEntry entrada;
                // Comprobar que el fichero es correcto
                // Borrar los ficheros de la carpeta "files" y "databases"
                borrarTodosDatos(false);
                while (null != (entrada = zis.getNextEntry())) {
                    // Extraer los ficheros en sus correspondientes directorios
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
                copiaRestaurada = true;
            }
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return copiaRestaurada;
    }

    /**
     * Comprueba la validez de un fichero zip como copia de seguridad
     *
     * @return boolean Resultado
     */
    private boolean zipValido(Uri path) {
        boolean zipValido = true;
        try {
            ZipInputStream zis = new ZipInputStream(context
                    .getContentResolver().openInputStream(path));
            ZipEntry entrada;
            while (null != (entrada = zis.getNextEntry())) {
                if (!entrada.getName().contains("files")
                        && !entrada.getName().contains("databases")) {
                    zipValido = false;
                    zis.close();
                    break;
                }
            }
            zis.close();
        } catch (IOException e) {
            return false;
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
                    @SuppressWarnings("ResultOfMethodCallIgnored")
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        File directorioFiles = new File(context.getFilesDir()
                                .getPath());
                        //Borrar sólo las imágenes
                        File[] ficherosFiles = directorioFiles.listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.toLowerCase().endsWith(".png");
                            }
                        });
                        for (File ficherosFile : ficherosFiles) {
                            ficherosFile.delete();
                        }
                        File fileBBDD = new File(context.getDatabasePath(
                                "Juegoteca").getAbsolutePath());
                        fileBBDD.delete();
                        reiniciarApp(context.getString(R.string.mensaje_datos_borrados_ok));
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });


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
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
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

    public void showBuildNotes(final String versionName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        String codigoIdioma = Locale.getDefault().getISO3Language();

        InputStream fraw;

        if (!"spa".equalsIgnoreCase(codigoIdioma)) {
            fraw = context.getResources().openRawResource(R.raw.relase_notes_en);
        } else {
            fraw = context.getResources().openRawResource(R.raw.relase_notes_es);
        }


        BufferedReader brin = new BufferedReader(new InputStreamReader(fraw));

        StringBuilder texto = new StringBuilder();
        String linea;
        try {
            while ((linea = brin.readLine()) != null) {
                texto.append(linea);
            }
            fraw.close();
        } catch (IOException e) {
            return;
        }

        builder.setMessage(Html.fromHtml(texto.toString())).setTitle(R.string.novedades);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final SharedPreferences settings = context.getSharedPreferences("UserInfo",
                        0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("releaseNotes" + versionName, true);
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
        return c == null || !c.moveToFirst();
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
            Log.e("Error", "Error al decodificar el fichero " + e.getMessage());
            return null;
        }
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
            Log.e("Error", "Error al liberar drawables" + e.getMessage());
        }
    }


    public boolean isTwitterAuth(){

        SharedPreferences globalSettings = PreferenceManager.getDefaultSharedPreferences(context);

        return globalSettings.getBoolean("twitter", false);
    }

    /** Publica un tweet con la información del juego **/
    public void tweet(Juego juego, boolean marcadoCompletado){

        TwitterConfig config = new TwitterConfig.Builder(context)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("HxyV7qaJl2YoWkdbOBN0HPwmc", "p4EQ0Y6xNGnjLgdyR6hubYSlMfIHj5TMB6TM6FShCe6VqWMKXM"))
                .build();
        Twitter.initialize(config);


        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();

        //Recuperar la plataforma

        String nombrePlataforma = "";

        Cursor cursorPlataforma = juegosSQLH.buscarPlataformaID(String.valueOf(juego.getPlataforma()));
        if(cursorPlataforma!=null && cursorPlataforma.moveToFirst()){
            nombrePlataforma = cursorPlataforma.getString(1);
            cursorPlataforma.close();
        }

        Cursor cursorJuegos = juegosSQLH.getJuegos();
        int total = cursorJuegos.getCount();
        cursorJuegos.close();


        String status;

        if(!marcadoCompletado) {
            status = MessageFormat.format(context.getString(R.string.share_status_message), juego.getTitulo(), nombrePlataforma, total);
        } else {

            int completados = juegosSQLH.getCountJuegosCompletados();

            status = MessageFormat.format(context.getString(R.string.share_status_message_completado), juego.getTitulo(), nombrePlataforma,completados ,total);
        }


        if ((juego.getCaratula()).length() == 0) {

            Call<Tweet> update = statusesService.update(status, null, null, null, null, null, null, null, null);

            update.enqueue(new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    Log.e(TAG, "Tweet publicado" + result.data.toString());

                }

                @Override
                public void failure(TwitterException exception) {
                    Log.e(TAG, "Error al publicar el tweet" + exception.getMessage());
                }
            });

        } else {

            //TODO Añadir la caratula
            MediaType type = MediaType.parse("image/*");
            RequestBody body = RequestBody.create(type, new File(context.getFilesDir().getPath() + "/" + juego.getCaratula()));
            Call<Media> mediaCall = TwitterCore.getInstance().getApiClient().getMediaService().upload(body, null, null);

            mediaCall.enqueue(new Callback<Media>() {
                @Override
                public void failure(TwitterException exception) {
                    Log.e(TAG, "Error al publicar la imagen" + exception.getMessage());
                }

                @Override
                public void success(Result<Media> result) {


                    Call<Tweet> update = statusesService.update(status, null, null, null, null, null, null, null, result.data.mediaIdString);

                    update.enqueue(new Callback<Tweet>() {
                        @Override
                        public void success(Result<Tweet> result) {
                            Log.e(TAG, "Tweet publicado" + result.data.toString());

                        }

                        @Override
                        public void failure(TwitterException exception) {
                            Log.e(TAG, "Error al publicar el tweet" + exception.getMessage());
                        }
                    });
                }
            });
        }

    }


}
