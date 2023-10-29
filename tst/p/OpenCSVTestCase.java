package p;
import static org.junit.jupiter.api.Assertions.*;
import static p.DataPaths.*;
import static p.Plays.*;
import static p.CSV.getCSV;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
class OpenCSVTestCase {
    @BeforeEach void setUp() throws Exception {}
    @AfterEach void tearDown() throws Exception {}
    @Test void testReadApple2016() throws IOException,CsvException {
        String filename="apple.csv";
        List<String[]> rows=getCSV(rPath,filename);
        //for(String[] row:rows) System.out.println(Arrays.asList(row));
        System.out.println(rows.size()+" rows");
    }
    // data/prices/001570.KS.csv
    // data/prices/002410.KS.csv
    // data/prices/003075.KS.csv
    // data/prices/003610.KS.csv
    // data/prices/0037.HK.csv
    // data/prices/0038.KL.csv
    // data/prices/005257.KS.csv
    // data/prices/0093.KL.csv
    // data/prices/009310.KS.csv
    ///data/prices/0099.HK.csv
    @Test void testReadBankroll() throws IOException,CsvException {
        Path path=Paths.get(rPath.toString(),"data");
        String filename="bankroll.000.csv";
        List<String[]> rows=getCSV(path,filename);
        //for(String[] row:rows) System.out.println(Arrays.asList(row));
        System.out.println(rows.size()+" rows");
    }
    @Test void testReadPrices() throws IOException,CsvException {
        Path path=Paths.get(rPath.toString(),"data","prices");
        String filename="001570.KS.csv";
        List<String[]> rows=getCSV(path,filename);
        //for(String[] row:rows) System.out.println(Arrays.asList(row));
        System.out.println(rows.size()+" rows");
    }
    @Test void testReadYahoo() throws IOException,CsvException {
        Path csvFile=yahooPath;
        Reader reader=new FileReader(csvFile.toString());
        com.opencsv.CSVReader r=new com.opencsv.CSVReader(reader);
        List<String[]> rows=r.readAll();
        //for(String[] row:rows) System.out.println(Arrays.asList(row));
        for(String[] row:rows) if(row.length!=5) System.out.println(Arrays.asList(row));
        System.out.println(rows.size()+" rows");
    }
    @Test void testReadFromString() throws IOException,CsvException {
        String string=com.tayek.util.StringUtilities.toString(Arrays.asList(aapl).iterator(),"\n",true);
        BufferedReader br=new BufferedReader(new StringReader(string));
        Reader reader=new StringReader(string);
        com.opencsv.CSVReader r=new com.opencsv.CSVReader(reader);
        List<String[]> rows=r.readAll();
        String[] headers=rows.remove(0);
        for(String[] words:rows) {
            String expected=words[0];
            String actual=new MyDate(expected).toString2();
            assertEquals(expected,actual);
        }
    }
    // @formatter:off
    static String[] aapl =new String[] { 
    "Date,Open,High,Low,Close,Volume,OpenInt",
    "1984-09-07,0.42388,0.42902,0.41874,0.42388,23220030,0",
    "1984-09-10,0.42388,0.42516,0.41366,0.42134,18022532,0",
    "1984-09-11,0.42516,0.43668,0.42516,0.42902,42498199,0",
    "1984-09-12,0.42902,0.43157,0.41618,0.41618,37125801,0",
    "1984-09-13,0.43927,0.44052,0.43927,0.43927,57822062,0",
    "1984-09-14,0.44052,0.45589,0.44052,0.44566,68847968,0",
    "1984-09-17,0.45718,0.46357,0.45718,0.45718,53755262,0",
    "1984-09-18,0.45718,0.46103,0.44052,0.44052,27136886,0",
    "1984-09-19,0.44052,0.44566,0.43157,0.43157,29641922,0",
    };
    // @formatter:on
}
