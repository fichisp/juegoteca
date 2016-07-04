package com.juegoteca.basedatos;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Almacena los datos de un juego
 * 
 * @author alvaro
 * 
 */
public class Juego {
	private long id;
	private String ean;
	private String titulo;
	private String compania;
	private int genero;
	private int plataforma;
	private String nombrePlataforma;
	private int clasificacion;
	private String nombreClasificacion;
	private int idioma;
	private String fechaLanzamiento;
	private String fechaCompra;
	private float precio;
	private String resumen;
	private int completado;// 0: false, 1: true
	private String caratula;
	private String fechaCreacion;
	private String fechaCompletado;
	private String comentario;
	private int puntuacion;
	private String formato;

	/**
	 * Constructor sin argumentos
	 */
	public Juego() {
		super();
	}

	/**
	 * Contructor parametrizado
	 * 
	 * @param id
	 * @param ean
	 * @param titulo
	 * @param compania
	 * @param genero
	 * @param plataforma
	 * @param clasificacion
	 * @param idioma
	 * @param fechaLanzamiento
	 * @param fechaCompra
	 * @param precio
	 * @param completado
	 * @param resumen
	 * @param caratula
	 */
	public Juego(int id, String ean, String titulo, String compania,
			int genero, int plataforma, int clasificacion, int idioma,
			String fecha_lanzamiento, String fecha_compra, float precio,
			int completado, String resumen, String caratula,
			String fechaCreacion, String fechaCompletado, String comentario,
			int puntuacion,String formato) {
		super();
		this.id = id;
		this.ean = ean;
		this.titulo = titulo;
		this.compania = compania;
		this.genero = genero;
		this.plataforma = plataforma;
		this.clasificacion = clasificacion;
		this.idioma = idioma;
		this.fechaLanzamiento = fecha_lanzamiento;
		this.fechaCompra = fecha_compra;
		this.precio = precio;
		this.resumen = resumen;
		this.completado = completado;
		this.caratula = caratula;
		this.fechaCreacion = fechaCreacion;
		this.fechaCompletado = fechaCompletado;
		this.comentario = comentario;
		this.puntuacion = puntuacion;
		this.formato=formato;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the ean
	 */
	public String getEan() {
		return ean;
	}

	/**
	 * @param ean
	 *            the ean to set
	 */
	public void setEan(String ean) {
		this.ean = ean;
	}

	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * @param titulo
	 *            the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * @return the compania
	 */
	public String getCompania() {
		return compania;
	}

	/**
	 * @param compania
	 *            the compania to set
	 */
	public void setCompania(String compania) {
		this.compania = compania;
	}

	/**
	 * @return the genero
	 */
	public int getGenero() {
		return genero;
	}

	/**
	 * @param genero
	 *            the genero to set
	 */
	public void setGenero(int genero) {
		this.genero = genero;
	}

	/**
	 * @return the plataforma
	 */
	public int getPlataforma() {
		return plataforma;
	}

	/**
	 * @param plataforma
	 *            the plataforma to set
	 */
	public void setPlataforma(int plataforma) {
		this.plataforma = plataforma;
	}

	/**
	 * @return the clasificacion
	 */
	public int getClasificacion() {
		return clasificacion;
	}

	/**
	 * @param clasificacion
	 *            the clasificacion to set
	 */
	public void setClasificacion(int clasificacion) {
		this.clasificacion = clasificacion;
	}

	/**
	 * @return the idioma
	 */
	public int getIdioma() {
		return idioma;
	}

	/**
	 * @param idioma
	 *            the idioma to set
	 */
	public void setIdioma(int idioma) {
		this.idioma = idioma;
	}

	/**
	 * @return the fechaLanzamiento
	 */
	public String getFechaLanzamiento() {
		return fechaLanzamiento;
	}

	/**
	 * @param fechaLanzamiento
	 *            the fechaLanzamiento to set
	 */
	public void setFechaLanzamiento(String fecha_lanzamiento) {
		this.fechaLanzamiento = fecha_lanzamiento;
	}

	/**
	 * @return the fechaCompra
	 */
	public String getFechaCompra() {
		return fechaCompra;
	}

	/**
	 * @param fechaCompra
	 *            the fechaCompra to set
	 */
	public void setFechaCompra(String fecha_compra) {
		this.fechaCompra = fecha_compra;
	}

	/**
	 * @return the precio
	 */
	public float getPrecio() {
		return precio;
	}

	/**
	 * @param precio
	 *            the precio to set
	 */
	public void setPrecio(float precio) {
		this.precio = precio;
	}

	/**
	 * @return the resumen
	 */
	public String getResumen() {
		return resumen;
	}

	/**
	 * @param resumen
	 *            the resumen to set
	 */
	public void setResumen(String resumen) {
		this.resumen = resumen;
	}

	/**
	 * @return the completado
	 */
	public int getCompletado() {
		return completado;
	}

	/**
	 * @param completado
	 *            the completado to set
	 */
	public void setCompletado(int completado) {
		this.completado = completado;
	}

	/**
	 * @return the caratula
	 */
	public String getCaratula() {
		return caratula;
	}

	/**
	 * @param caratula
	 *            the caratula to set
	 */
	public void setCaratula(String caratula) {
		this.caratula = caratula;
	}

	/**
	 * @return the nombrePlataforma
	 */
	public String getNombrePlataforma() {
		return nombrePlataforma;
	}

	/**
	 * @return the fechaCreacion
	 */
	public String getFechaCreacion() {
		return fechaCreacion;
	}

	/**
	 * @return the fechaCompletado
	 */
	public String getFechaCompletado() {
		return fechaCompletado;
	}

	/**
	 * @param fechaCreacion
	 *            the fechaCreacion to set
	 */
	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	/**
	 * @param fechaCompletado
	 *            the fechaCompletado to set
	 */
	public void setFechaCompletado(String fechaCompletado) {
		this.fechaCompletado = fechaCompletado;
	}

	/**
	 * @param nombrePlataforma
	 *            the nombrePlataforma to set
	 */
	public void setNombrePlataforma(String nombrePlataforma) {
		this.nombrePlataforma = nombrePlataforma;
	}

	/**
	 * @return the nombreClasificacion
	 */
	public String getNombreClasificacion() {
		return nombreClasificacion;
	}

	/**
	 * @param nombreClasificacion
	 *            the nombreClasificacion to set
	 */
	public void setNombreClasificacion(String nombreClasificacion) {
		this.nombreClasificacion = nombreClasificacion;
	}

	/**
	 * @return the comentario
	 */
	public String getComentario() {
		return comentario;
	}

	/**
	 * @param comentario
	 *            the comentario to set
	 */
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	/**
	 * @return the puntuacion
	 */
	public int getPuntuacion() {
		return puntuacion;
	}

	/**
	 * @param puntuacion
	 *            the puntuacion to set
	 */
	public void setPuntuacion(int puntuacion) {
		this.puntuacion = puntuacion;
	}

	/**
	 * @return the formato
	 */
	public String getFormato() {
		return formato;
	}

	/**
	 * @param formato the formato to set
	 */
	public void setFormato(String formato) {
		this.formato = formato;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Juego [id=" + id + ", ean=" + ean + ", titulo=" + titulo
				+ ", compania=" + compania + ", genero=" + genero
				+ ", plataforma=" + plataforma + ", nombrePlataforma="
				+ nombrePlataforma + ", clasificacion=" + clasificacion
				+ ", nombreClasificacion=" + nombreClasificacion + ", idioma="
				+ idioma + ", fechaLanzamiento=" + fechaLanzamiento
				+ ", fechaCompra=" + fechaCompra + ", precio=" + precio
				+ ", resumen=" + resumen + ", completado=" + completado
				+ ", caratula=" + caratula + ", fechaCreacion=" + fechaCreacion
				+ ", fechaCompletado=" + fechaCompletado + ", comentario="
				+ comentario + ", puntuacion=" + puntuacion + ", formato="
				+ formato + "]";
	}

	/**
	 * Convierte un JSON en un objeto Juego
	 * 
	 * @param juegoJSON
	 * @return
	 */
	public Juego convertirJSON(JSONObject juegoJSON) {
		Juego juego = new Juego();
		try {
			this.ean = juegoJSON.get("ean").toString();
			this.titulo = juegoJSON.get("titulo").toString();
			this.compania = juegoJSON.get("compania").toString();
			this.genero = Integer.valueOf(juegoJSON.get("genero").toString());
			this.plataforma = Integer.valueOf(juegoJSON.get("plataforma")
					.toString());
			this.clasificacion = Integer.valueOf(juegoJSON.get("clasificacion")
					.toString());
			this.idioma = Integer.valueOf(juegoJSON.get("idioma").toString());
			this.fechaLanzamiento = juegoJSON.get("fecha_lanzamiento")
					.toString();
			this.fechaCompra = juegoJSON.get("fecha_compra").toString();
			this.precio = Float.valueOf(juegoJSON.get("precio").toString());
			this.resumen = juegoJSON.get("resumen").toString();
			this.completado = Integer.valueOf(juegoJSON.get("completado")
					.toString());
			this.fechaCompletado = juegoJSON.get("fecha_completado").toString();
			this.caratula = juegoJSON.get("caratula").toString();
			this.comentario = juegoJSON.get("comentario").toString();
			try {
				this.puntuacion = Integer.valueOf(juegoJSON.get("puntuacion")
						.toString());
			} catch (NumberFormatException nfe) {
				this.puntuacion = 0;
			}
			this.formato = juegoJSON.get("formato").toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return juego;
	}
}
