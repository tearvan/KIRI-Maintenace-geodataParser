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
			for(int i = 0; i < record.size(); i++)
			{
				if(i == 0)
				{
					String[] temp = record.get(i).split(";");
					//musti 3, selain itu salah
					if(temp.length == 3)
					{
						String temp2 = temp[2].split("(")[1];
						result += temp[0] + " " + temp[1].split("\"")[1] + " " + temp2.split(" ")[0] + "," + temp2.split(" ")[1];
					}
					else
					{
						System.out.println("ERROR --> index 0 ga menghasilkan 3 String");
					}
				}
				else if(i == (record.size()-1))
				{
					String[] temp = record.get(i).split(")");
					temp = temp[0].split(" ");
					result += temp[0] + "," + temp[1];
					geodata.add(result);
				}
				else
				{
					String[] temp = record.get(i).split(" ");
					result += temp[0] + "," + temp[1];
				}
			}
		}
		
	}
}
