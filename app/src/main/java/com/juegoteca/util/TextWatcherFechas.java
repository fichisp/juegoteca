package com.juegoteca.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mijuegoteca.R;

public class TextWatcherFechas implements TextWatcher {

	private View view;
	private Utilidades utilidades;

	/**
	 * 
	 * @param view
	 */
	public TextWatcherFechas(View view) {
		this.view = view;
		utilidades = new Utilidades(view.getContext());
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void onTextChanged(final CharSequence s, int start, int before,
			int count) {
		EditText fecha = (EditText) view.findViewById(view.getId());
		String texto = s.toString();
		if (s.length() == 2 || s.length() == 5) {
			if ((start == 2 || start == 5) && before == 1) {
				texto = texto.substring(0, texto.length() - 1);
				fecha.setText(texto);
				fecha.setSelection(texto.length());
			} else {
				texto += "/";
				fecha.setText(texto);
				fecha.setSelection(texto.length());
			}
		}
		if (s.length() == 10) {
			Toast avisoFecha;
			switch (utilidades.validaFecha(s.toString())) {
			case 1:
				avisoFecha = Toast.makeText(view.getContext(), view
						.getContext().getString(R.string.anio_invalido),
						Toast.LENGTH_SHORT);
				// avisoFecha.setGravity(Gravity.CENTER|Gravity.BOTTOM,0,0);
				avisoFecha.show();
				break;
			case 2:
				avisoFecha = Toast.makeText(view.getContext(), view
						.getContext().getString(R.string.mes_invalido),
						Toast.LENGTH_SHORT);
				// avisoFecha.setGravity(Gravity.CENTER|Gravity.BOTTOM,0,0);
				avisoFecha.show();
				break;
			case 3:
				avisoFecha = Toast.makeText(view.getContext(), view
						.getContext().getString(R.string.dia_invalido),
						Toast.LENGTH_SHORT);
				// avisoFecha.setGravity(Gravity.CENTER|Gravity.BOTTOM,0,0);
				avisoFecha.show();
				break;
			default:
				break;
			}
		}
	}

}
