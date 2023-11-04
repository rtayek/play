package p;
import static p.CSV.getCSV;
import static p.DataPaths.newPrices;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import com.opencsv.exceptions.CsvException;
import com.tayek.util.Pair;
public class CSV { // utilities
    public String[] names() { return names; }
    @Override public String toString() { // can never work!
        if(true) throw new RuntimeException("ops");
        // needs data for the format strings
        StringBuffer stringBuffer=new StringBuffer();
        boolean once=false;
        for(Pair pair:pairs) {
            if(!once) once=true;
            else stringBuffer.append(", ");
            System.out.println("pair: "+pair);
            String s=String.format((String)pair.second,(String)pair.first);
            stringBuffer.append(s);
        }
        return stringBuffer.toString();
    }
    public static void removeExtraQuotes(List<String[]> rows) {
        int index=0;
        for(String[] row:rows) { stripQuotes(index,row); }
    }
    public static void stripQuotes(int index,String[] row) {
        if(row[index].startsWith("\"")) row[index]=row[index].substring(1);
        if(row[index].endsWith("\"")) row[index]=row[index].substring(0,row[index].length()-1);
    }
    public static ArrayList<Pair> timePeriods(final List<String[]> rows) {
        List<String> header=Arrays.asList(rows.get(0));
        System.out.println("header: "+header);
        //System.out.println("row 1: "+Arrays.asList(rows.get(1)));
        String f=rows.get(1)[0];
        String l=rows.get(rows.size()-1)[0];
        //System.out.println(f+" to "+l);
        MyDate first=new MyDate(f);
        MyDate last=new MyDate(l);
        //System.out.println(first+" to "+last);
        int year1=first.date().getYear()+1900;
        int yearn=last.date().getYear()+1900;
        //System.out.println(year1+" to "+yearn);
        // maybe generate a list of (from,to) pairs
        ArrayList<Pair> pairs=new ArrayList<>();
        for(int i=year1;i<yearn;++i) { //
            MyDate from=new MyDate(i+"-01-01");
            MyDate to=new MyDate((i+1)+"-01-01"); // maybe use 02-01?
            //System.out.println(from+" to "+to);
            Pair pair=new Pair(from,to);
            pairs.add(pair);
        }
        return pairs;
    }
    public static List<String[]> filter(List<String[]> lines,Date from,Date to) {
        ArrayList<String[]> l=new ArrayList<>();
        if(lines.size()>0) l.add(lines.get(0)); // add csv header
        for(int i=1;i<lines.size();++i) {
            String[] line=lines.get(i);
            MyDate myDate=new MyDate(line[0]);
            if(MyDate.inRange(from,myDate.date(),to)) {
                //System.out.println(i+" adding: "+myDate);
                l.add(line);
            }
        }
        //System.out.println("first line: "+Arrays.asList(l.get(0)));
        //System.out.println("second line: "+Arrays.asList(l.get(1)));
        //System.out.println("last line: "+Arrays.asList(l.get(l.size()-1)));
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
        // can this use toString somehow?
        StringBuffer s=new StringBuffer();
        for(int i=0;i<names.length;++i) {
            if(i>0) s.append(", ");
            s.append(names[i]).append(": ").append(objects[i]);
        }
        return s.toString();
    }
    public static String removeTarget(String name,String target) {
        if(name.endsWith(target)) {
            //System.out.println("before: "+name);
            name=name.substring(0,name.length()-target.length());
            //System.out.println("after: "+name);
        }
        return name;
    }
    public static boolean equals(List<String[]> x,List<String[]> y) {
        if(x==y) return true;
        if(x==null||y==null) return false;
        if(x.size()!=y.size()) return false;
        for(int i=0;i<x.size();++i) if(!Arrays.equals(x.get(i),y.get(i))) return false;
        return true;
    }
    public static Pair indicesToDates(List<String[]> lines,int start,int stop) {
        Date from=null;
        Date to=null;
        Pair pair=new Pair(from,to);
        return pair;
    }
    public static Pair datesToindices(List<String[]> lines,Date from,Date to) {
        Integer start=0;
        Integer stop=0;
        Pair pair=new Pair(start,stop);
        return pair;
    }
    public String header() {
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<pairs.size();++i) {
            if(i>0) stringBuffer.append(", ");
            String s=((String)pairs.get(i).first);
            stringBuffer.append(s);
        }
        return stringBuffer.toString();
    }
    public String toString(Object... arguments) {
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<Math.min(arguments.length,pairs.size());++i) {
            if(i>0) stringBuffer.append(", ");
            Pair pair=pairs.get(i);
            //System.out.println(pair.first+","+pair.second);
            String s=String.format((String)pairs.get(i).second,arguments[i]);
            stringBuffer.append(s);
        }
        return stringBuffer.toString();
    }
    public static void main(String[] args) {
        Plays plays=new Plays();
        Plays.Play play=plays.new Play("AAPL.csv");
        play.prices=new Double[] {1.,2.,3.};
        System.out.println(play);
        System.out.println("header: "+Plays.result.header());
        Object[] arguments=play.arguments();
        System.out.println(Arrays.asList(arguments));
        System.out.println("result: "+Plays.result.toString(arguments));
    }
    ArrayList<Pair> pairs=new ArrayList<>();
    String[] names=new String[pairs.size()];
    String[] formats=new String[pairs.size()];
}
