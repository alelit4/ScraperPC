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
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
	 // 1TF
	 GetDatosLigaTenerife(new URL("http://origencanario.com/primera-categoria-2/"));
	 // 3TF
	 GetDatosLigaTenerife(new URL("http://origencanario.com/tercera-categoria-2012/"));

    }

    private static void GetDatosLigaTenerife(URL url) throws IOException, ClassNotFoundException, SQLException {
	Document doc = Jsoup.connect(url.toString()).timeout(0).get();

	System.out.println("Origen Canario => " + doc.title());

	String categoria = getCategoria(doc.title());
	System.out.println("Categoria = " + categoria);

	String jornada = getJornada(doc.select("p").first());
	System.out.println("Jornada = " + jornada);

	Elements allLuchas = doc.getElementsByTag("td");

	int numLuchas = 4;
	int numDatos = 4;

	for (int i = 0; i < (numLuchas * numDatos); i = i + 4) {
	    allLuchas.get(i);
	    showInfo(allLuchas, i);
	    String resultado = getResultado(allLuchas.get(i + 2).text());
	    
	    String casa = allLuchas.get(i).text();
	    String lucha = allLuchas.get(i).text() + " - " + allLuchas.get(i + 1).text();
	    System.out.println("Lucha = " + lucha);
	    String fecha = allLuchas.get(i + 3).text();
	    String id = getID(casa, categoria, jornada);
	    updateLucha(id, resultado);
	

	}

    }

    private static String getResultado(String text) {
	if( text.matches(".*Aplazado.*")){
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
	    System.out.println("Entramos antes de update");
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
	System.out.println("\n elemento casa => "
		+ allLuchas.get(i).text().toLowerCase());
	System.out.println("elemento visitante => "
		+ allLuchas.get(i + 1).text());
	System.out.println("elemento resultado => "
		+ allLuchas.get(i + 2).text());
	System.out.println("elemento => fecha " + allLuchas.get(i + 3).text());
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

	} else {
	    return "3";

	}

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
	ResultSet rs = stat
		.executeQuery("SELECT id FROM CALENDARIOS WHERE ( Lucha LIKE '%"
			+ casa
			+ "%' AND Categoria = '"
			+ categoria
			+ "' AND Jornada = '" + jornada + "')");
	// stat.executeQuery("INSERT INTO CALENDARIOS ( Categoria, Competicion, Fecha, Hora, Isla, Lucha, Resultado, Jornada, created_at, updated_at])"
	// +
	// " VALUES (\""+ categoria +"\", \"Liga\",\""+ fecha +"\", \""+
	// categoria +"\", \""+ categoria +"\", \""+ categoria +"\", \""+
	// categoria +"\" )");
	//
	while (rs.next()) {
	    System.out.println(" => " + rs.getString("id"));
	}
	while (rs.next()) {
	    System.out.println(" => " + rs.getString("id"));
	    conn.close();
	    return rs.getString("id");
	}
	conn.close();
	return null;

    }

}
