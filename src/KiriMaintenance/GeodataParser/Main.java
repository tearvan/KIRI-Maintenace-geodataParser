package KiriMaintenance.GeodataParser;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import sun.net.util.URLUtil;

import com.sun.jndi.toolkit.url.UrlUtil;
import com.sun.media.sound.MidiUtils.TempoCache;
import com.sun.org.apache.xerces.internal.util.URI;

/**
 * 
 * @author Jovan Gunawan
 * Deskripsi:
 * Kelas yang digunakan untuk melakukan maintenance bagian pemindahan data dari DB KIRI ke angkot.web.id
 * 
 * Yang dilakukan oleh program:
 * Program akan menerima format csv yang merupakan eksport dari DB KIRI dari kelas Tirtayasa.tracks, kemudian dilakukan pemotongan data tertentu dan menghasilkan 
 * data dalam plain text yang berisi data yang siap dimasukan ke angkot.web.id (dengan cara Resend)
 * 
 * gunakan format yang dibawah ini untuk eksport CSV dari DB KIRI (contoh dibawah untuk mengambil data angkot di bandung):
 * SELECT trackTypeId, officialTrackNo, officialTrackName, ASTEXT( geodata )
 * FROM `tracks`
 * WHERE `trackTypeId` LIKE 'bdo_angkot'
 * AND `officialTrackNo` IS NOT NULL 
 */

public class Main {
	public static void main(String [] args) throws IOException
	{
		List<CSVRecord> data = CSVReader.readEntriesFromFile("tracks.csv"); 
		String result = "";
		for (CSVRecord record: data) 
		{
			// isi dari String array untuk [0]=Provinsi; [1]=City; [2]=Company; [3]=number; [4]=origin; [5]=destination; [6]=accept; [7]=geometry
			String [] tempDataResult = new String[8];
			tempDataResult[6] = "ContributorTerms";
			tempDataResult[7] = ""; //karena yang geometry akan menggunakan operasi += maka harus diinisialisasi terlebih dahulu
			for(int i = 0; i < 4; i++)
			{
				if(i == 0)
				{
					//data yang diakses adalah data trackTypeId
					String[] temp = record.get(i).split("_");
					tempDataResult[0] = Main.cekProvinsi(temp[0]);
					tempDataResult[1] = Main.cekKota(temp[0]);
					tempDataResult[2] = Main.cekCompany(temp[1]);
					if(tempDataResult[0].equals("FALSE") || tempDataResult[1].equals("FALSE") || tempDataResult[2].equals("FALSE"))
					{
						System.out.println("ERROR: kota " + temp[0] + " / Company " + temp[1] + " tidak ditemukan");
						break;
					}
				}
				else if(i == 1)
				{
					//data yang diakses adalah data officialTrackNo dan seharusnya sudah tidak ada nilai NULL karena sudah diseleksi di sql
					tempDataResult[3] = record.get(i);
				}
				else if(i == 2)
				{
					//data yang diakses adalah data officialTrackName
					String[] temp = record.get(i).split("-");
					
					tempDataResult[4] = temp[0].substring(0, temp[0].length()-2); //di akhir ada spasi, sebaiknya dibuang
					tempDataResult[5] = temp[1].substring(1); //di awal ada spasi, sebaiknya dibuang
				}
				else
				{
					//data yang diakses adalah data geometry
					String temp = record.get(i).substring(11, record.get(i).length()-2);
					String [] temp2 = temp.split(",");
					for(int j = 0; j < temp2.length; j++)
					{
						String [] geoData = temp2[j].split(" ");
						if(j != 0)
						{
							tempDataResult[7] += ",";
						}
						tempDataResult[7] += "[" + geoData[0] + "," + geoData[1] + "]";
					}
				}
			}
			//mulai susun data-data yang sudah diperoleh
			String temp = "{\"type\":\"Feature\",\"properties\":{\"province\":\"" + tempDataResult[0] + "\",\"city\":\"" + tempDataResult[1] + "\",\"company\":\"" + tempDataResult[2] + "\",\"number\":\"" + tempDataResult[3] + "\",\"origin\":\"" + tempDataResult[4] + "\",\"destination\":\"" + tempDataResult[5] + "\",\"accept\":[\"" + tempDataResult[6] + "\"]},\"geometry\":{\"type\":\"MultiLineString\",\"coordinates\":[[" + tempDataResult[7] + "]]}}";
			result += tempDataResult[0] + " " + tempDataResult[1] + " " + tempDataResult[2] + " " + tempDataResult[3] + ":\n" + URLEncoder.encode(temp) +"\n\n";

			//"type":"Feature","properties":{"province":"ID-JB","city":"Bandung","company":"Angkot","number":"35","origin":"Kalapa","destination":"Karang+Setra","accept":["ContributorTerms"]},"geometry":{"type":"MultiLineString","coordinates":
		}
		System.out.println(result);
	}
	
	public static String cekProvinsi(String kota)
	{
		if(kota.equals("bdo"))
		{
			return "ID-JB";
		}
		return "FALSE";
	}
	
	public static String cekKota(String kota)
	{
		if(kota.equals("bdo"))
		{
			return "Bandung";
		}
		return "FALSE";
	}
	
	public static String cekCompany(String kota)
	{
		if(kota.equals("angkot"))
		{
			return "Angkot";
		}
		return "FALSE";
	}
}
