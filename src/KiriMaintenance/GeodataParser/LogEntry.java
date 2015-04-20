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
public class LogEntry {
	
	/**
	 * Tipe log KIRI, dapat ditambahkan sesuai kebutuhan
	 * @author PascalAlfadian
	 *
	 */
	public enum LogType {SEARCHPLACE, FINDROUTE, PAGELOAD}
	private LogType type;
	private int logId;
	private String apiKey;
	private Calendar timestamp;
	private LatLon startPoint, finishPoint;
	/**
	 * constructor LogEntry
	 * parameter: array String dengan isi 5 nilai, yaitu: id, apiKey, date, action, additionalData
	 */
	public LogEntry(String[] data)
	{
		try {
			type = LogType.valueOf(data[3]);
			logId = Integer.parseInt(data[0]);
			apiKey = data[1];
			String[] time = data[2].split(" ");
			String[] date = time[0].split("-");
			time = time[1].split(":");
			timestamp = Calendar.getInstance();
			timestamp.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
			
			// Parse additional data
			switch (type) {
			case FINDROUTE:
				try
				{
					String[] loc = data[4].split("/");
					String[] loc1 = loc[0].split(",");
					String[] loc2 = loc[1].split(",");
				
					startPoint = new LatLon(Float.parseFloat(loc1[0]), Float.parseFloat(loc1[1]));
					finishPoint = new LatLon(Float.parseFloat(loc2[0]), Float.parseFloat(loc2[1]));
				}
				catch(Exception e)
				{
					System.out.println("ERROR: split additional data FINDROUTE pada logEntry gagal: " + data[0] + " " + data[4]);
				}
				break;
			default:
				break;
			}
		} catch (IllegalArgumentException iae) {
			// void
		}
	}

	/**
	 * Mendapatkan log id
	 * @return log id
	 */
	public int getLogId() {
		return logId;
	}
	
	/**
	 * Mendapatkan API key 
	 * @return API key
	 */
	public String getAPIKey() {
		return apiKey;	
	}
	
	/**
	 * Mendapatkan timestamp
	 * @return timestamp
	 */
	public Calendar getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Mendapatkan tipe log
	 * @return tipe log
	 */
	public LogType getLogType() {
		return type;	
	}
	
	/**
	 * Mendapatkan koordinat start
	 * @return koordinat start
	 * @throws InvalidParameterException jika {@link #getLogType()} != {@link LogType#FINDROUTE}.
	 */
	public LatLon getStartPoint() throws InvalidParameterException {
		if(getLogType() == LogType.FINDROUTE)
		{
			return startPoint;
		}
		else
		{
			throw new InvalidParameterException();
		}
	}
	
	/**
	 * Mendapatkan koordinat finish
	 * @return koordinat finish
	 * @throws InvalidParameterException jika {@link #getLogType()} != {@link LogType#FINDROUTE}.
	 */
	public LatLon getFinishPoint() throws InvalidParameterException {
		if(getLogType() == LogType.FINDROUTE)
		{
			return finishPoint;
		}
		else
		{
			throw new InvalidParameterException();
		}
	}
	
	/**
	 * Membaca sebuah file (bisa berupa .csv atau .csv.gz) untuk mendapatkan
	 * daftar {@link LogEntry}nya. LogEntry yang tidak valid akan diabaikan.
	 * @param filename nama file yang ingin dibaca
	 * @return daftar log entry.
	 * banyak atribut sudah dipastikan 5: id, APIKey, date, Action, additionalData
	 */
	public static List<LogEntry> readEntriesFromFile(String filename) throws IOException {

		ArrayList<LogEntry> result = new ArrayList<LogEntry>();
		int countAttribute = 5;

		Reader reader;
		if (filename.endsWith(".gz")) {
			reader = new InputStreamReader(new GZIPInputStream(new FileInputStream(filename)));
		} else {
			reader = new BufferedReader(new FileReader(filename));
		}
		CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
		List<CSVRecord> data = parser.getRecords();
		for (CSVRecord record: data) {
			String[] tempData = new String[countAttribute];
			if(record.get(3).equals("FINDROUTE") || record.get(3).equals("SEARCHPLACE") || record.get(3).equals("PAGELOAD"))
			{
				for(int i = 0; i < countAttribute; i++)
				{
					tempData[i] = record.get(i);
				}
				if(tempData[3].equals("FINDROUTE"))
				{
					//mengecek apakah ada kesalahan data atau tidak, seharusnya akan ada 3 data jika dilakukan split ,
					if(tempData[4].split(",").length == 3)
					{
						result.add(new LogEntry(tempData));
					}
					else
					{
						// errors will be ignored
					}
				}
				else
				{
					result.add(new LogEntry(tempData));
				}
			}
		}
		parser.close();
		return result;
	}
	
	public static void main(String [] args) throws IOException
	{
		String dataPath = "statistics-2014-";
		for(int i = 1; i <= 12; i++)
		{
			String dataPath1 = "";
			String dataPath2 = "";
			if(i < 10)
			{
				dataPath1 = dataPath + "0" + i + ".csv.gz";
				dataPath2 = dataPath + "0" + i + ".csv";
			}
			else
			{
				dataPath1 = dataPath + i + ".csv.gz";
				dataPath2 = dataPath + i + ".csv";
			}
			
			System.out.println("\nRead File " + i + ".1 .gz");
			List<LogEntry> data1 = readEntriesFromFile(dataPath1);
			System.out.println("Read File " + i + ".2 .csv");
			List<LogEntry> data2 = readEntriesFromFile(dataPath2);

		}
	}
}
