package com.juegoteca.basedatos;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mijuegoteca.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Locale;

public class JuegosSQLHelper extends SQLiteOpenHelper {

    private static int DATABASE_VERSION = 15;
    private static String DATABASE_NAME = "Juegoteca";

    private Resources resources;

    private String sqlCreatePlataforma = "CREATE TABLE IF NOT EXISTS plataforma ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "nombre varchar(512) NOT NULL,"
            + "fabricante varchar(512) NULL,"
            + "fecha_lanzamiento varchar(512) NULL,"
            + "resumen varchar(512) NULL,"
            + "drawable varchar(512) NOT NULL,"
            + "imagen varchar(512) NULL)";

    private String sqlCreateJuego = "CREATE TABLE IF NOT EXISTS juego ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "ean varchar(256) UNIQUE NULL," + "titulo varchar(256) NOT NULL,"
            + "compania varchar(256) NOT NULL," + "genero int(2) NOT NULL,"
            + "plataforma int(2) NOT NULL," + "clasificacion int(2) NOT NULL,"
            + "idioma int(2)," + "fecha_lanzamiento bigint(64) DEFAULT NULL,"
            + "fecha_compra bigint(64) DEFAULT NULL,"
            + "precio float DEFAULT NULL," + "completado int(1) NOT NULL,"
            + "resumen varchar(512) NOT NULL,"
            + "caratula varchar(512) NOT NULL,"
            + "fecha_creacion bigint(64) NOT NULL,"
            + "fecha_completado bigint(64) NULL,"
            + "comentario varchar(512) NULL," + "puntuacion int(2) NULL,"
            + "formato char(2) NOT NULL,"
            + "FOREIGN KEY(plataforma) REFERENCES plataforma(id),"
            + "FOREIGN KEY(idioma) REFERENCES idioma(id),"
            + "FOREIGN KEY(genero) REFERENCES genero(id),"
            + "FOREIGN KEY(clasificacion) REFERENCES clasificacion(id));";

    private String sqlCreateClasificacion = "CREATE TABLE IF NOT EXISTS clasificacion ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, nombre VARCHAR(20), drawable VARCHAR(20));";

    private String sqlCreateIdiomas = "CREATE TABLE IF NOT EXISTS idioma ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, nombre VARCHAR(20), drawable VARCHAR(20));";

    private String sqlCreateIdiomasEN = "CREATE TABLE IF NOT EXISTS idioma_en ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, nombre VARCHAR(20), drawable VARCHAR(20));";

    private String sqlCreateGeneros = "CREATE TABLE IF NOT EXISTS genero ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, nombre VARCHAR(20));";

    private String sqlCreateGenerosEN = "CREATE TABLE IF NOT EXISTS genero_en ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, nombre VARCHAR(20));";

    private String sqlCreateFavoritos = "CREATE TABLE IF NOT EXISTS favorito ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, id_juego int(2) NOT NULL,"
            + "FOREIGN KEY(id_juego) REFERENCES juego(id));";

    /**
     * Constructor
     *
     * @param context
     * @param version
     */
    public JuegosSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        resources = context.getResources();
    }

    /**
     * Crea la base de datos inicial cuando no existe
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(sqlCreatePlataforma);
        database.execSQL(sqlCreateClasificacion);
        database.execSQL(sqlCreateIdiomas);
        database.execSQL(sqlCreateIdiomasEN);
        database.execSQL(sqlCreateGeneros);
        database.execSQL(sqlCreateGenerosEN);
        database.execSQL(sqlCreateJuego);
        database.execSQL(sqlCreateFavoritos);
        // Carga de los valores iniciales en las tablas
        try {
            InputStream fraw = resources.openRawResource(R.raw.db_create);
            BufferedReader brin = new BufferedReader(
                    new InputStreamReader(fraw));
            String linea;
            while ((linea = brin.readLine()) != null) {
                database.execSQL(linea);
            }
            fraw.close();
        } catch (IOException ioe) {
            Log.v("JuegosSQLHelper - onCreate", ioe.toString());
        }
    }

    /**
     * Actualización de la base de datos a una nueva versión
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int versionAnterior,
                          int versionActual) {
        Log.v("Actualizando la base de datos...", "Inicio");
        // Ejecuta las SQLs de actualizacion
        try {
            InputStream fraw = resources.openRawResource(R.raw.db_upgrade);
            BufferedReader brin = new BufferedReader(
                    new InputStreamReader(fraw));
            String linea;
            while ((linea = brin.readLine()) != null) {
                Log.v("Actualizando la base de datos...", linea);
                try {
                    database.execSQL(linea);
                } catch (SQLiteConstraintException sicve) {
                    Log.v("Omitiendo insesión repetida", linea);
                }
            }

            fraw.close();
        } catch (IOException ioe) {
            Log.v("JuegosSQLHelper - onCreate", ioe.toString());
        }
        Log.v("Actualizando la base de datos...", "Fin");
    }

    /**
     * Inserta un nuevo juego en la base de datos
     *
     * @param juego
     * @return int id del nuevo elemento creado
     */
    public long insertarJuego(Juego juego) {
        SQLiteDatabase db = this.getWritableDatabase();
        Calendar calendario = Calendar.getInstance();
        long id = -1;
        // Si hemos abierto correctamente la base de datos
        if (db != null) {
            // Insertamos los datos en la tabla Usuarios
            // Comprobamos que no exista el EAN
            try {
                Log.v("JuegosSQLHelper - insertarJuego", "Juego a insertar: "
                        + juego.toString());
                ContentValues valores = new ContentValues();
                valores.put("ean", juego.getEan());
                valores.put("titulo", juego.getTitulo());
                valores.put("compania", juego.getCompania());
                valores.put("genero", juego.getGenero());
                valores.put("formato", juego.getFormato());
                valores.put("plataforma", juego.getPlataforma());
                valores.put("clasificacion", juego.getClasificacion());
                valores.put("idioma", juego.getIdioma());
                valores.put("fecha_lanzamiento", juego.getFechaLanzamiento());
                valores.put("fecha_compra", juego.getFechaCompra());
                valores.put("precio", juego.getPrecio());
                valores.put("completado", juego.getCompletado());
                valores.put("resumen", juego.getResumen());
                valores.put("caratula", juego.getCaratula());
                valores.put("fecha_creacion",
                        String.valueOf(calendario.getTimeInMillis()));
                valores.put("comentario", juego.getComentario());
                valores.put("puntuacion", juego.getPuntuacion());
                if (juego.getCompletado() == 1) {
                    valores.put("fecha_completado", juego.getFechaCompletado());
                }
                id = db.insert("juego", null, valores);
            } catch (SQLiteConstraintException sqlce) {
                Log.v("JuegosSQLHelper - insertarJuego - Clave única violada",
                        sqlce.getMessage());
                return -5;
            } catch (SQLException sqle) {
                Log.v("JuegosSQLHelper - insertarJuego - Error desconocido",
                        sqle.getMessage());
                return id;
            }
            db.close();
            return (id != -1) ? id : -5;
        } else {
            return id;
        }
    }

    /**
     * Actualizar un juego existente en la base de datos
     *
     * @param juego
     * @return int id del nuevo elemento creado
     */
    public long actualizarJuego(Juego juego) {
        SQLiteDatabase db = this.getWritableDatabase();
        long filas = -1;
        // Si hemos abierto correctamente la base de datos
        if (db != null) {
            // Insertamos los datos en la tabla Usuarios
            try {
                Log.v("JuegosSQLHelper - actualizaJuego",
                        "Juego a actualizar: " + juego.toString());
                ContentValues valores = new ContentValues();
                valores.put("ean", juego.getEan());
                valores.put("titulo", juego.getTitulo());
                valores.put("compania", juego.getCompania());
                valores.put("genero", juego.getGenero());
                valores.put("formato", juego.getFormato());
                valores.put("plataforma", juego.getPlataforma());
                valores.put("clasificacion", juego.getClasificacion());
                valores.put("idioma", juego.getIdioma());
                valores.put("fecha_lanzamiento", juego.getFechaLanzamiento());
                valores.put("fecha_compra", juego.getFechaCompra());
                valores.put("precio", juego.getPrecio());
                valores.put("completado", juego.getCompletado());
                valores.put("resumen", juego.getResumen());
                valores.put("caratula", juego.getCaratula());
                valores.put("comentario", juego.getComentario());
                valores.put("puntuacion", juego.getPuntuacion());
                if (juego.getCompletado() == 1) {
                    valores.put("fecha_completado", juego.getFechaCompletado());
                }
                filas = db.update("juego", valores, "ID = " + juego.getId(),
                        null);
            } catch (SQLiteConstraintException sqlce) {
                Log.v("JuegosSQLHelper - actualizarJuego", sqlce.getMessage());
                return -5;
            } catch (SQLException sqle) {
                Log.v("JuegosSQLHelper - actualizarJuego", sqle.getMessage());
                return filas;
            }
            db.close();
            return filas;
        } else {
            return filas;
        }
    }

    /**
     * Devuelve un listado de todos los juegos
     *
     * @return Cursor con el resultado de la consulta
     */
    public Cursor getJuegos() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery("SELECT * FROM juego ORDER BY titulo ASC", null);
        } catch (SQLException sqle) {
            Log.v("JuegosSQLHelper - getJuegos", sqle.getMessage());
        }
        return c;
    }

    /**
     * Devuelve un cursor con los juegos que cumplan con los valores
     * suministrados
     *
     * @param args Valores de filtro de consulta
     * @return Cursor con los resultador o null si no hay resultados
     */
    public Cursor getJuegos(String[] args) {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery(
                    "SELECT * FROM juego WHERE ean LIKE '%'|| ? ||'%' AND titulo LIKE '%'|| ? ||'%' AND plataforma LIKE ? AND genero LIKE ? AND formato LIKE ? ORDER BY titulo ASC",
                    args);
        } catch (SQLException sqle) {
            Log.v("JuegosSQLHelper - getJuegos (args)", sqle.getMessage());
        }
        return c;
    }

    /**
     * Devuelve todas las compañías
     *
     * @return Cursor con el resultado de la consulta
     */
    public Cursor getCompanias() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery(
                    "SELECT id AS _id,compania FROM juego GROUP BY compania",
                    null);
        } catch (SQLException sqle) {
            Log.v("JuegosSQLHelper - getCompanias", sqle.getMessage());
        }
        return c;
    }

    /**
     * Devuelve todos los géneros
     *
     * @return Cursor con el resultado de la consulta
     */
    public Cursor getGeneros() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery("SELECT id AS _id,nombre FROM genero", null);
        } catch (SQLException sqle) {
            Log.v("JuegosSQLHelper - getGeneros", sqle.getMessage());
        }
        return c;
    }

    /**
     * Devuelve todos los géneros
     *
     * @return Cursor con el resultado de la consulta
     */
    public Cursor getGenerosEN() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery("SELECT id AS _id,nombre FROM genero_en", null);
        } catch (SQLException sqle) {
            Log.v("JuegosSQLHelper - getGenerosEN", sqle.getMessage());
        }
        return c;
    }

    /**
     * Devuelve un cursor con los géneros
     *
     * @return
     */
    public Cursor getGeneros(String[] args) {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery(
                    "SELECT id AS _id,nombre FROM genero WHERE nombre LIKE '%?%'",
                    args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve un cursor con las plataformas
     *
     * @return
     */
    public Cursor getPlataformas() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery("SELECT id AS _id,nombre FROM plataforma", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve un cursor con las clasificaciones
     *
     * @return
     */
    public Cursor getClasificaciones() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery("SELECT id AS _id,nombre FROM clasificacion", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve un cursor con los idiomas
     *
     * @return
     */
    public Cursor getIdiomas() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery("SELECT id AS _id,nombre FROM idioma", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve un cursor con los idiomas
     *
     * @return
     */
    public Cursor getIdiomasEN() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery("SELECT id AS _id,nombre FROM idioma_en", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve el cursor con el juego que coincida con el id suministrado
     *
     * @param id
     * @return
     */
    public Cursor buscarJuegoID(String id) {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery("SELECT * FROM juego WHERE id=" + id, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve el cursor con el genero que coincida con el id suministrado
     *
     * @param id
     * @return
     */
    public Cursor buscarGeneroID(String id) {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            String codigoIdioma = Locale.getDefault().getISO3Language();

            if (!"spa".equalsIgnoreCase(codigoIdioma)) {
                c = db.rawQuery("SELECT * FROM genero_en WHERE id=" + id, null);
            } else {
                c = db.rawQuery("SELECT * FROM genero WHERE id=" + id, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve el cursor con la plataforma que coincida con el id suministrado
     *
     * @param id
     * @return
     */
    public Cursor buscarPlataformaID(String id) {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery("SELECT * FROM plataforma WHERE id=" + id, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve el cursor con la clasificación que coincida con el id
     * suministrado
     *
     * @param id
     * @return
     */
    public Cursor buscarClasificacionID(String id) {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery("SELECT * FROM clasificacion WHERE id=" + id, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve el cursor con el idioma que coincida con el id suministrado
     *
     * @param id
     * @return
     */
    public Cursor buscarIdiomaID(String id) {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            String codigoIdioma = Locale.getDefault().getISO3Language();

            if (!"spa".equalsIgnoreCase(codigoIdioma)) {
                c = db.rawQuery("SELECT * FROM idioma_en WHERE id=" + id, null);
            } else {
                c = db.rawQuery("SELECT * FROM idioma WHERE id=" + id, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Borra un juego de la base de datos
     *
     * @param id
     * @return
     */
    public int borrarJuego(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int borrado = 0;
        try {
            borrado = db.delete("juego", "ID = " + id, null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return borrado;
    }

    /**
     * Devuelve un cursor con los últimos añadidos
     *
     * @return
     */
    public Cursor getUltimosJuegosAnadidos() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery("SELECT * FROM juego ORDER BY fecha_creacion DESC",
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve un cursor con los últimos añadidos
     *
     * @return
     */
    public Cursor getUltimosJuegosCompletados() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery(
                    "SELECT * FROM juego WHERE completado=1 ORDER BY fecha_completado DESC",
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve un cursor con la cantidad total de juegos por plataforma
     *
     * @return
     */
    public Cursor getJuegosPorPlataforma() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery(
                    "SELECT count(*) AS NUM,p.nombre FROM juego AS j LEFT JOIN plataforma AS p ON p.id=j.plataforma GROUP BY p.nombre ORDER BY NUM DESC",
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve un cursor con la cantidad total de juegos por plataforma
     *
     * @return
     */
    public Cursor getJuegosCompletados() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery(
                    "SELECT count(*),completado FROM juego GROUP BY completado",
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve un cursor con los juegos por formato
     *
     * @return
     */
    public Cursor getJuegosPorFormato() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery(
                    "SELECT count(*) NUM ,formato FROM juego GROUP BY formato ORDER BY NUM DESC",
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve un cursor con los juegos
     *
     * @return
     */
    public Cursor getJuegosGenero() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery(
                    "SELECT count(*) NUM,g.nombre FROM juego AS j LEFT JOIN genero AS g ON g.id=j.genero GROUP BY g.nombre ORDER BY NUM DESC",
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve un cursor con los juegos
     *
     * @return
     */
    public Cursor getJuegosGeneroEN() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery(
                    "SELECT count(*) NUM,g.nombre FROM juego AS j LEFT JOIN genero_en AS g ON g.id=j.genero GROUP BY g.nombre ORDER BY NUM DESC",
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Comprueba si un juego está marcado como favorito
     *
     * @param idJuego
     * @return
     */
    public boolean esFavorito(String idJuego) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM favorito WHERE id_juego="
                + idJuego, null);
        // Si ya está en favoritos, devolvemos el valor -5 para indicarlo
        if (c != null && c.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Devuelve los juegos marcados como favoritos
     *
     * @return
     */
    public Cursor getFavoritos() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery(
                    "SELECT * FROM juego WHERE id IN (SELECT id_juego FROM favorito) ORDER BY titulo ASC",
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve los juegos marcados como favoritos
     *
     * @return
     */
    public Cursor getFavoritosPlataforma(int plataforma) {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery(
                    "SELECT * FROM juego WHERE id IN (SELECT id_juego FROM favorito) AND plataforma LIKE '"
                            + plataforma + "' ORDER BY titulo ASC", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Inserta un juego en la base de datos
     *
     * @param id_juego
     * @return
     */
    public long insertarFavorito(long id_juego) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = -1; // id de la inserción
        // Si hemos abierto correctamente la base de datos
        if (db != null) {
            // Insertamos los datos en la tabla Usuarios
            // Comprobamos que no exista el EAN
            try {
                Cursor c = db.rawQuery("SELECT * FROM favorito WHERE id_juego="
                        + id_juego, null);
                // Si ya está en favoritos, devolvemos el valor -5 para
                // indicarlo
                if (c != null && c.moveToFirst()) {
                    return -5;
                } else {
                    Log.v("JuegosSQLHelper - insertarFavorito",
                            "Juego a insertar (ID): " + id_juego);
                    ContentValues valores = new ContentValues();
                    valores.put("id_juego", id_juego);
                    id = db.insert("favorito", null, valores);
                }

            } catch (SQLException sqle) {
                Log.v("JuegosSQLHelper - insertarJuego", sqle.getMessage());
                return id;
            }
            db.close();
            return id;
        } else {
            return id;
        }
    }

    /**
     * Elimina un juego de la tabla de favoritos
     *
     * @param id
     * @return
     */
    public int eliminarFavorito(String idJuego) {
        SQLiteDatabase db = this.getWritableDatabase();
        int borrado = 0;
        try {
            borrado = db.delete("favorito", "ID_JUEGO = " + idJuego, null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return borrado;
    }

    /**
     * Devuelve un cursor con los juegos pendientes
     *
     * @return
     */
    public Cursor getPendientes() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery(
                    "SELECT * FROM juego WHERE completado = 0 ORDER BY titulo ASC",
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Devuelve un cursor con los juegos pendientes de la plataforma indicada en
     * el parámetro
     *
     * @param plataforma
     * @return
     */
    public Cursor getPendientesPlataforma(int plataforma) {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            c = db.rawQuery(
                    "SELECT * FROM juego WHERE completado = 0 AND plataforma LIKE '"
                            + plataforma + "' ORDER BY titulo ASC", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Retorna el sumatorio de los precios
     *
     * @return
     */
    public Float getValorColeccion() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            c = db.rawQuery(
                    "SELECT SUM(precio) FROM juego", null);

            if (c.moveToFirst()) {
                return c.getFloat(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;

    }

    /**
     * Retorna el sumatorio de los precios
     *
     * @return
     */
    public Integer getCountJuegosConPrecio() {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            c = db.rawQuery(
                    "SELECT count(*) FROM juego where precio <> 0", null);

            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;

    }


}
