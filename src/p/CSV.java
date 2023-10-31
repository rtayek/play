package p;
import static p.CSV.getCSV;
import static p.DataPaths.newPrices;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.opencsv.exceptions.CsvException;
public class CSV { // utilities
    public static void removeExtraQuotes(List<String[]> rows) {
        int index=0;
        for(String[] row:rows) { stripQuotes(index,row); }
    }
    public static void stripQuotes(int index,String[] row) {
        if(row[index].startsWith("\"")) row[index]=row[index].substring(1);
        if(row[index].endsWith("\"")) row[index]=row[index].substring(0,row[index].length()-1);
    }
    public static List<String[]> filter(List<String[]> lines,Date from,Date to) {
        ArrayList<String[]> l=new ArrayList<>();
        if(lines.size()>0) l.add(lines.get(0));
        for(int i=1;i<lines.size();++i) {
            String[] line=lines.get(i);
            //System.out.println(Arrays.asList(line));
            MyDate myDate=new MyDate(line[0]);
            if(MyDate.inRange(from,myDate.date(),to)) l.add(line);
        }
        return l;
    }
    public static List<String[]> getCSV(Path path,String filename) {
        Path csvFile=Path.of(path.toString(),filename);
        Reader reader;
        List<String[]> rows=null;
        try {
            reader=new FileReader(csvFile.toString());
            com.opencsv.CSVReader r=new com.opencsv.CSVReader(reader);
            rows=r.readAll();
        } catch(IOException|CsvException e) {
            e.printStackTrace();
        }
        return rows;
    }
    public static List<String[]> getNewPrices(String ticker) {
        String filename=ticker+".csv";
        List<String[]> rows=getCSV(newPrices,filename);
        //System.out.println(ticker+" has: "+rows.size()+" rows");
        return rows;
    }
    public static Double[] getClosingPrices(List<String[]> lines) {
        if(lines==null||lines.size()==0) return new Double[0];
        int n=lines.size()-1;
        if(n==0) return new Double[0];
        Double[] prices=new Double[n];
        //System.out.println("first row: "+Arrays.asList(lines.get(0)));
        lines.remove(0);
        //System.out.println("second row: "+Arrays.asList(lines.get(0)));
        for(int i=0;i<n;++i) prices[i]=Double.valueOf(lines.get(i)[4]);
        return prices;
    }
    public static String toLine(String[] names,Object[] objects) {
        StringBuffer s=new StringBuffer();
        for(int i=0;i<names.length;++i) {
            if(i>0) s.append(", ");
            s.append(names[i]).append(": ").append(objects[i]);
        }
        return s.toString();
    }
    public static String toLine(Object... arguments) {
        StringBuffer s=new StringBuffer();
        for(int i=0;i<arguments.length;i+=2) {
            if(i>0) s.append(", ");
            if(i+1<arguments.length) s.append(": ").append(arguments[i+1]);
        }
        return s.toString();
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }
}
