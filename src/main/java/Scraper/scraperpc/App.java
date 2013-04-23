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
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) throws IOException, SQLException {
		
		GetDatosLigaTenerife(new URL("http://origencanario.com/primera-categoria-2/"));
		GetDatosLigaTenerife(new URL("http://origencanario.com/tercera-categoria-2012/"));
	
		


//		Consulta a la bd
//		ResultSet rs = stat.executeQuery("SELECT * FROM CALENDARIOS");
//		while (rs.next()) {
//            System.out.println(rs.getString("Categoria"));
//            
//        }


	}

	private static void GetDatosLigaTenerife(URL url) throws IOException {
		Connection.Response response;
		Document doc = Jsoup.connect(url.toString()).timeout(0).get();
		String title = doc.title();
		System.out.println("Origen Canario => " + title);
		String categoria = "1";
		System.out.println("Categoria = " + categoria);
		Element info = doc.select("p").first();
		String[] jornada = info.text().split(" ");
		System.out.println(" Jornada = " + jornada[1].replace("ª", ""));
		Elements allLuchas = doc.getElementsByTag("td");
		int numLuchas = 4;
		int numDatos = 4;
		
		for (int i = 0; i < (numLuchas * numDatos); i = i + 4) {
			allLuchas.get(i);
			System.out
					.println("\n elemento casa => " + allLuchas.get(i).text());
			System.out.println("elemento visitante => "
					+ allLuchas.get(i + 1).text());
			System.out.println("elemento resultado => "
					+ allLuchas.get(i + 2).text());
			System.out.println("elemento => fecha "
					+ allLuchas.get(i + 3).text());
			String casa = allLuchas.get(i).text();
			String lucha = allLuchas.get(i).text() +" - " + allLuchas.get(i+1).text();
			System.out.println("Lucha = " + lucha);
			String fecha = allLuchas.get(i + 3).text();
		}
		
	}

//	private static void GetDatosPrimeraTenerife() throws SQLException, IOException {
//		Connection.Response response;
//		URL url1 = new URL("http://origencanario.com/primera-categoria-2/");
//		Document doc = Jsoup.connect(url1.toString()).get();
//		String title = doc.title();
//		System.out.println("Origen Canario => " + title);
//		String categoria = "1";
//		System.out.println("Categoria = " + categoria);
//		Element info = doc.select("p").first();
//		String[] jornada = info.text().split(" ");
//		System.out.println(" Jornada = " + jornada[1].replace("ª", ""));
//		Elements allLuchas = doc.getElementsByTag("td");
//		int numLuchas = 4;
//		int numDatos = 4;
//		
//		for (int i = 0; i < (numLuchas * numDatos); i = i + 4) {
//			allLuchas.get(i);
//			System.out
//					.println("\n elemento casa => " + allLuchas.get(i).text());
//			System.out.println("elemento visitante => "
//					+ allLuchas.get(i + 1).text());
//			System.out.println("elemento resultado => "
//					+ allLuchas.get(i + 2).text());
//			System.out.println("elemento => fecha "
//					+ allLuchas.get(i + 3).text());
//			String casa = allLuchas.get(i).text();
//			String lucha = allLuchas.get(i).text() +" - " + allLuchas.get(i+1).text();
//			System.out.println("Lucha = " + lucha);
//			String fecha = allLuchas.get(i + 3).text();
//			try {
//				Class.forName("org.sqlite.JDBC");
//				java.sql.Connection conn = DriverManager
//						.getConnection("jdbc:sqlite:production.sqlite3");
//				Statement stat = conn.createStatement();
//				
//				ResultSet rs = stat.executeQuery("SELECT * FROM CALENDARIOS WHERE (Categoria = 1 AND Isla = \"Tenerife\" )");
////				stat.executeQuery("INSERT INTO CALENDARIOS ( Categoria, Competicion, Fecha, Hora, Isla, Lucha, Resultado, Jornada, created_at, updated_at])" +
////						" VALUES (\""+ categoria +"\", \"Liga\",\""+ fecha +"\", \""+ categoria +"\", \""+ categoria +"\", \""+ categoria +"\", \""+ categoria +"\" )");
////				
//				while (rs.next()) {
//	            System.out.println(" => " + rs.getString("Lucha"));
//				}
//				conn.close();
//
//			} catch (ClassNotFoundException e) {
//
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}
//		
//	}

}
