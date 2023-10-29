package p;
import static p.DataPaths.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import com.opencsv.exceptions.CsvException;
import p.Plays.Play;
public class Bankrolls {
    public static void main(String[] args) throws IOException,CsvException {
        Path path=Paths.get(rPath.toString(),"data");
        File dir=path.toFile();
        String[] files=dir.list();
        Histogram histogram=new Histogram(10,0,10);
        int total=0;
        SortedMap<Comparable<?>,Integer> map=new TreeMap<>();
        double threshold=1.30;
        for(String filename:files) if(filename.startsWith("bankroll.")&&filename.endsWith(".csv")) {
            Path csvFile=Path.of(path.toString(),filename);
            System.out.println(csvFile);
            Reader reader=new FileReader(csvFile.toString());
            com.opencsv.CSVReader r=new com.opencsv.CSVReader(reader);
            List<String[]> rows=r.readAll();
            total+=rows.size();
            for(int i=1;i<rows.size();++i) {
                String[] row=rows.get(i);
                double d=Double.valueOf(row[1]);
                if(d<10) if(d>threshold) {
                    histogram.add(d);
                    System.out.println(row[0]+" "+d);
                }
            }
            System.out.println(rows.size()+" rows");
        }
        System.out.println(total+" stocks from bankroll files.");
        System.out.println(histogram.n()+" stocks with return >= "+threshold);
        System.out.println(histogram);
    }
}
