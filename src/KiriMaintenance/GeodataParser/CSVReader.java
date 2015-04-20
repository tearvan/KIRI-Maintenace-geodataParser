package KiriMaintenance.GeodataParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Membaca CSV / .gz
 * @author PascalAlfadian
 *
 */
public class CSVReader {
	public CSVReader()
	{
	}
	/**
	 * Membaca sebuah file (bisa berupa .csv atau .csv.gz) untuk mendapatkan data yang terdapat di CSV.
	 * @param filename nama file yang ingin dibaca
	 * @return List<CSVRecord>.
	 */
	public static List<CSVRecord> readEntriesFromFile(String filename) throws IOException {
		//init reader (gz or csv)
		Reader reader;
		if (filename.endsWith(".gz")) {
			reader = new InputStreamReader(new GZIPInputStream(new FileInputStream(filename)));
		} else {
			reader = new BufferedReader(new FileReader(filename));
		}
		
		//init parser and record
		CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
		List<CSVRecord> data = parser.getRecords();
		parser.close();
		
		return data;
	}
}
