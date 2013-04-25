package com.puntalcanario.scraperLZFV;

import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ScraperInter {
	private static final String ISLA = "InterinsularLF";
	private static final String CATEGORIA = "2";
	private static final String COMPETICION = "InterinsularLF";
	public static void main(String[] args) throws IOException, SQLException,
			ClassNotFoundException {
		// GrupoA
		getDatosInterInsuLZFV(new
				URL("http://tibiabin.es/2013/02/calendario-liga-interinsular-lucha-canaria/"),
				"A");
		// GrupoB
		// getDatosInterInsuLZFV(new
		// URL("http://tibiabin.es/2013/02/calendario-liga-interinsular-lucha-canaria/"),
		// "B");
		// Final
		// getDatosInterInsuLZFV(new
		// URL("http://tibiabin.es/2013/02/calendario-liga-interinsular-lucha-canaria/"),
		// "F");

	}

	private static void getDatosInterInsuLZFV(URL url, String grupo) throws IOException,
			ClassNotFoundException, SQLException {
		Document doc = Jsoup.connect(url.toString()).timeout(0).get();
		Elements tables = doc.select("table");
		if (grupo.matches("A")) {
			Element table = tables.get(0);
			System.out.println("table 1 => " + table.text());
			updateGrupo(table, grupo);
		} else if (grupo.matches("B")) {
			Element table = tables.get(1);
			System.out.println("table 2" + table.text());
			updateGrupo(table, grupo);
		} else if (grupo.matches("F")) {
			Element table = tables.get(2);
			System.out.println("table 3" + table.text());
			updateGrupo(table, grupo);
		}

	}
	private static void updateGrupo(Element table, String grupo) throws ClassNotFoundException,
			SQLException {
		Elements allBlocks = table.select("td");
		List<String> blocksList = new ArrayList<String>();
		String line = " ";
		for (Element e : allBlocks) {
			if (e.text().toString().matches(".*Jornada.*")) {
				blocksList.add(line);
				line = e.text().toString();
			} else if (!e.text().matches("")) {
				line = line + "|" + e.text().toString();
			}
		}
		blocksList.add(line);

		// for(String s : blocksList){
		// System.out.println("\n => " + s);
		// }
		// String line = "";
		int jornadaActual = getJornada(ISLA, CATEGORIA, COMPETICION);
		System.out.println("Jornada actual => " + jornadaActual);
		System.out.println("Estos son los datos que queremos!");
		System.out.println(blocksList.get(jornadaActual));
		List<String> datosJornada = new ArrayList<String>(Arrays.asList(blocksList.get(
				jornadaActual).split("\\|")));
		for (String s : datosJornada) {
			System.out.println("\n => " + s);
		}

		datosJornada.remove(0);
		while (!datosJornada.isEmpty()) {
			// borramos fecha
			System.out.println("Fecha => " + datosJornada.get(0));
			datosJornada.remove(0);
			// String casa = datosJornada.get(0).toString();
			String[] casaStr = datosJornada.get(0).toString().split("\t");
			String casa = casaStr[0];
			if (datosJornada.get(0).matches(".*Uni.*")) {
				System.out.println("UNIOOOOOOOONNN");
				casa = casaStr[0];
			}
			System.out.println("Casa => " + datosJornada.get(0));
			// // borramos casa
			datosJornada.remove(0);
			// System.out.println("visi => " + datosJornada.get(0));
			// if( datosJornada.get(0).startsWith("Uni")){
			// System.out.println("UNIOOOOOOOONNN");
			// }
			// borramos visitante
			datosJornada.remove(0);
			System.out.println("hora => " + datosJornada.get(0));
			// borramos hora
			datosJornada.remove(0);
			System.out.println("terrero => " + datosJornada.get(0));
			// borramos terrero
			datosJornada.remove(0);
			System.out.println("resultado => " + datosJornada.get(0));
			// borramos resultado
			datosJornada.remove(0);
			String id = getID(casa.replace("á", "a").replace("é", "e").replace("ó", "o"), CATEGORIA
					+ grupo, jornadaActual + "");
			System.out.println("ID ======================> " + id);

		}

	}

	private static String getID(String casa, String categoria, String jornada)
			throws ClassNotFoundException, SQLException {
		// Consulta a la bd
		Class.forName("org.sqlite.JDBC");
		java.sql.Connection conn = DriverManager
				.getConnection("jdbc:sqlite:production.sqlite");
		Statement stat = conn.createStatement();
		System.out.println("========> casa => " + casa + " | categoria => " + categoria
				+ " | jornada => " + jornada);
		ResultSet rs = stat
				.executeQuery("SELECT id FROM CALENDARIOS WHERE ( Lucha LIKE '%"
						+ casa
						+ "%' AND Categoria = '"
						+ categoria
						+ "' AND Jornada = '" + jornada + "')");

		while (rs.next()) {
			System.out.println("ID => " + rs.getString("id"));
			String salida = rs.getString("id");
			stat.close();
			conn.close();
			return salida;
		}
		stat.close();
		conn.close();
		return null;

	}
	private static int getJornada(String isla, String categoria, String competicion)
			throws ClassNotFoundException, SQLException {
		// Consulta a la bd
		Class.forName("org.sqlite.JDBC");
		java.sql.Connection conn = DriverManager
				.getConnection("jdbc:sqlite:production.sqlite");
		Statement stat = conn.createStatement();
		ResultSet rs = stat
				.executeQuery("SELECT jornada FROM 	JORNADA_ACTUALS WHERE ( Isla LIKE '%"
						+ isla
						+ "%' AND Categoria LIKE '"
						+ categoria
						+ "' AND Competicion LIKE '%" + competicion + "%')");

		while (rs.next()) {
			System.out.println("JORNADA => " + rs.getString("jornada"));
			int salida = Integer.parseInt(rs.getString("jornada"));
			stat.close();
			conn.close();
			return salida;
		}
		stat.close();
		conn.close();
		return 0;

	}
}
