package KiriMaintenance.GeodataParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

public class Main {
	public static void main(String [] args) throws IOException
	{
		List<CSVRecord> data = CSVReader.readEntriesFromFile(""); 
		ArrayList<String> geodata = new ArrayList<String>();
		for (CSVRecord record: data) 
		{
			String result = "";
			for(int i = 0; i < data.size(); i++)
			{
				if(i == 0)
				{
					
				}
				else
				{
					
				}
			}
		}
		
	}
}
