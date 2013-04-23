package Scraper.scraperpc;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.parser.*;
import org.jsoup.nodes.*;
import org.jsoup.safety.*;
import org.jsoup.select.Elements;

import java.sql.*;

import org.sqlite.*;

import java.util.Iterator;
import java.util.Map;

import org.jsoup.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import javax.xml.crypto.Data;

/**
 * PC Scraper
 * 
 */
public class App {
	private static final int NUMLUCHASTF = 4;
	private static final int NUMDATOSTF = 4;

	public static void main(String[] args) throws IOException, SQLException,
			ClassNotFoundException {
		// 1TF
		GetDatosLigaTenerife(new URL(
				"http://origencanario.com/primera-categoria-2/"));
		// 3TF
		GetDatosLigaTenerife(new URL(
				"http://origencanario.com/tercera-categoria-2012/"));

	}

	private static void GetDatosLigaTenerife(URL url) throws IOException,
			ClassNotFoundException, SQLException {
		Document doc = Jsoup.connect(url.toString()).timeout(0).get();
		System.out.println("\n Origen Canario => " + doc.title());
		System.out.println("Categoria = " + getCategoria(doc.title()));
		Elements allLuchas = doc.getElementsByTag("td");
		for (int i = 0; i < (NUMLUCHASTF * NUMDATOSTF); i = i + 4) {
			showInfo(allLuchas, i);
			System.out.println("\nLucha => " + allLuchas.get(i).text() + " - "
					+ allLuchas.get(i + 1).text());
			// params => casa cat jor
			String id = getID(allLuchas.get(i).text(),
					getCategoria(doc.title()), getJornada(doc.select("p")
							.first()));
			updateLucha(id, getResultado(allLuchas.get(i + 2).text()));

		}

	}

	private static String getResultado(String text) {
		if (text.matches(".*Aplazado.*")) {
			System.out.println("oooooohh Aplazadoo");
			return "0-0";
		}
		return text;
	}

	private static boolean updateLucha(String id, String resultado) {

		try {
			Class.forName("org.sqlite.JDBC");
			java.sql.Connection conn = DriverManager
					.getConnection("jdbc:sqlite:production.sqlite3");
			Statement stat = conn.createStatement();
			// if every thing is OK rs should be 0
			int rs = stat.executeUpdate("UPDATE CALENDARIOS  SET Resultado = "
					+ resultado + " WHERE id=" + id + " ");
			conn.close();
			return true;

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	private static void showInfo(Elements allLuchas, int i) {
		System.out.println("Casa => " + allLuchas.get(i).text().toLowerCase());
		System.out.println("Visitante => " + allLuchas.get(i + 1).text());
		System.out.println("Resultado => " + allLuchas.get(i + 2).text());
		System.out.println("Fecha " + allLuchas.get(i + 3).text());
	}

	private static String getJornada(Element info) {
		String[] jornada = info.text().split(" ");
		System.out.println(" Jornada = " + jornada[1].replace("ª", ""));
		return jornada[1].replace("ª", "");
	}

	private static String getCategoria(String title) {
		if (title.matches(".*Primera.*")) {
			System.out.println("Primera");
			return "1";

		} else if (title.matches(".*Tercera.*")) {
			return "3";

		}
		return "-1";

	}

	private static String getID(String casa, String categoria, String jornada)
			throws ClassNotFoundException, SQLException {
		// Consulta a la bd
		Class.forName("org.sqlite.JDBC");
		java.sql.Connection conn = DriverManager
				.getConnection("jdbc:sqlite:production.sqlite3");
		Statement stat = conn.createStatement();
		if (casa.matches(".*UNIVERSIDAD-TIJARAFE.*")) {
			System.out
					.println("****************************************** UNI!! ");
			casa = "universidad";
		}
		if (casa.matches(".*GARA.*")) {
			System.out
					.println("****************************************** GARA!! ");
			casa = "igara";
		}
		ResultSet rs = stat
				.executeQuery("SELECT id FROM CALENDARIOS WHERE ( Lucha LIKE '%"
						+ casa
						+ "%' AND Categoria = '"
						+ categoria
						+ "' AND Jornada = '" + jornada + "')");

		while (rs.next()) {
			System.out.println("ID => " + rs.getString("id"));
			conn.close();
			return rs.getString("id");
		}
		conn.close();
		return null;

	}

}
