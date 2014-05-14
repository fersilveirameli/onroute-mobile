package br.com.fsilveira.onroute_mobile;

import java.util.ArrayList;
import java.util.List;

public class NavigationUtil {
	public static final List<String> MENU;
	public static final List<Integer> MENU_IMG;

	static {
		MENU = new ArrayList<String>();
		MENU.add("Mapa");
		MENU.add("Viagens");
		MENU.add("Configurações");

		MENU_IMG = new ArrayList<Integer>();
		MENU_IMG.add(R.drawable.maps);
		MENU_IMG.add(R.drawable.marker);
		MENU_IMG.add(R.drawable.settings);
	}

}
