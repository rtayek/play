package p;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import static p.DataPaths.*;
import static p.CSV.*;
public class CSVReader{}
class xCSVReader { // and writer
    //  get rid of all  of this
    public static StringWriter write(List<String[]> lines) throws IOException {
        StringWriter stringWriter=new StringWriter();
        StringBuffer sb=new StringBuffer();
        for(String[] line:lines) {
            for(int i=0;i<line.length;++i) {
                if(i>0) sb.append(',');
                sb.append(line[i]);
            }
            sb.append('\n');
            stringWriter.write(sb.toString());
            sb.setLength(0);
        }
        return stringWriter;
    }
    static ArrayList<String[]> read(String cvsSplitBy,BufferedReader br) {
        String line;
        ArrayList<String[]> rows=new ArrayList<>();
        try {
            while((line=br.readLine())!=null) {
                String[] words=line.split(cvsSplitBy);
                rows.add(words);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return rows;
    }
    static List<String[]> read(String cvsSplitBy,String csvFile) {
        ArrayList<String[]> rows=new ArrayList<>();
        try(BufferedReader br=new BufferedReader(new FileReader(csvFile))) {
            rows=read(cvsSplitBy,br);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return rows;
    }
    public static List<String[]> readAndFilter(Path csvFile, MyDate from, MyDate to) {
        List<String[]> data=read(",",csvFile.toString());
        removeExtraQuotes(data); // dates have quotes from r
        List<String[]> filtered=filter(data,from.date(),to.date());
        return filtered;
    }
    public static List<String[]> fromR(Path path,String filename,MyDate from,MyDate to) { // try to match the r code in getSymbols
        //String datasetFilename="aapl.us.txt";
        //Path csvFile=Path.of(MyDataset.kagglePath.toString(),datasetFilename);
        Path csvFile=Path.of(path.toString(),filename);
        List<String[]> filtered=readAndFilter(csvFile,from,to);
        return filtered;
    }
    public static Double[] getPricesFromR(Path path,String filename,MyDate from,MyDate to) {
        List<String[]> lines=fromR(path,filename,from,to); // quotes are removed.
        if(lines.size()==0) return null;
        Double[] prices=Plays.getClosingPrices(lines);
        return prices;
    }
    public static void main(String[] args) throws IOException {
        // r file has quotes around the dates
        String filename="apple.csv";
        // r file probably uses the same dates
        MyDate from=new MyDate("2016-01-01");
        MyDate to=new MyDate("2017-01-01");
        List<String[]> lines=fromR(rPath,filename,from,to); // quotes are removed.
        System.out.println(lines.size()+" filtered lines.");
        if(false) {
            StringWriter w=write(lines);
            // files are almost the same
            File file=new File("apple.csv"); // make our own
            FileWriter fw=new FileWriter(file);
            fw.write(w.toString());
            fw.close();
        }
        // get prices
        Double[] prices=Plays.getClosingPrices(lines);
        System.out.println(prices[0]);
        System.out.println("close:  "+Arrays.asList(prices));
        System.out.println(prices[prices.length-1]);
        // now use the prices here in play.
        // and use the same prices in get symbols (r code).
    }
}
