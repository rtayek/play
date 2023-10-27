package p;
import static org.junit.jupiter.api.Assertions.*;
import static p.DataPaths.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
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
        Path csvFile=Path.of(rPath.toString(),filename);
        Reader reader=new FileReader(csvFile.toString());
        com.opencsv.CSVReader r=new com.opencsv.CSVReader(reader);
        List<String[]> rows=r.readAll();
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

    @Test void testRead1() throws IOException,CsvException {
        Path path=Paths.get(rPath.toString(),"data","prices");
        String filename="001570.KS.csv";
        Path csvFile=Path.of(path.toString(),filename);
        Reader reader=new FileReader(csvFile.toString());
        com.opencsv.CSVReader r=new com.opencsv.CSVReader(reader);
        List<String[]> rows=r.readAll();
        //for(String[] row:rows) System.out.println(Arrays.asList(row));
        System.out.println(rows.size()+" rows");
    }
    @Test void testReadYahoo() throws IOException,CsvException {
        Path csvFile=yahooPath;
        Reader reader=new FileReader(csvFile.toString());
        com.opencsv.CSVReader r=new com.opencsv.CSVReader(reader);
        List<String[]> rows=r.readAll();
        //for(String[] row:rows) System.out.println(Arrays.asList(row));
        for(String[] row:rows) 
            if(row.length!=5)
                System.out.println(Arrays.asList(row));
        System.out.println(rows.size()+" rows");
    }
    static int max=10;
}
