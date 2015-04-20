package KiriMaintenance.GeodataParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Mendefinisikan satu entri pada log KIRI
 * @author PascalAlfadian
 *
 */
public class CSVReader {
	public CSVReader()
	{
	}
	/**
	 * Membaca sebuah file (bisa berupa .csv atau .csv.gz) untuk mendapatkan
	 * daftar {@link CSVReader}nya. LogEntry yang tidak valid akan diabaikan.
	 * @param filename nama file yang ingin dibaca
	 * @return daftar log entry.
	 * banyak atribut sudah dipastikan 5: id, APIKey, date, Action, additionalData
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
