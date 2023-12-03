package p;
import static p.CSV.getCSV;
import static p.DataPaths.newPrices;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import com.opencsv.exceptions.CsvException;
import com.tayek.util.Pair;
import p.Plays.Play;
public class CSV { // utilities
    public String[] names() { return names; }
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
    public static void removeExtraQuotes(List<String[]> rows) {
        int index=0;
        for(String[] row:rows) { stripQuotes(index,row); }
    }
    public static void stripQuotes(int index,String[] row) {
        if(row[index].startsWith("\"")) row[index]=row[index].substring(1);
        if(row[index].endsWith("\"")) row[index]=row[index].substring(0,row[index].length()-1);
    }
    public static ArrayList<Pair> timePeriodIndices(final List<String[]> rows) {
        int minSize=260; // dup. fix this!!
        ArrayList<Pair> pairs=new ArrayList<>();
        for(int startIndex=rows.size()-minSize-1,
                stopIndex=rows.size()-1;startIndex>=0;startIndex-=minSize,stopIndex-=minSize) {
            pairs.add(new Pair(startIndex,stopIndex));
        }
        return pairs;
    }
    public static ArrayList<Pair> timePeriodDates(final List<String[]> rows,boolean byQuarter) {
        List<String> header=Arrays.asList(rows.get(0));
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
        for(int i=year1;i<=yearn;++i) { // make this <= so we get 2023.
            if(byQuarter) {
                MyDate from=new MyDate(i+"-01-01");
                MyDate to=new MyDate((i)+"-04-01"); // maybe use 02-01?
                //System.out.println(from+" to "+to);
                Pair pair=new Pair(from,to);
                pairs.add(pair);
                from=new MyDate(i+"-04-01");
                to=new MyDate((i)+"-07-01"); // maybe use 02-01?
                //System.out.println(from+" to "+to);
                pair=new Pair(from,to);
                pairs.add(pair);
                from=new MyDate(i+"-07-01");
                to=new MyDate((i)+"-10-01"); // maybe use 02-01?
                //System.out.println(from+" to "+to);
                pair=new Pair(from,to);
                pairs.add(pair);
                from=new MyDate(i+"-10-01");
                to=new MyDate((i+1)+"-01-01"); // maybe use 02-01?
                //System.out.println(from+" to "+to);
                pair=new Pair(from,to);
                pairs.add(pair);
            } else {
                MyDate from=new MyDate(i+"-01-01");
                MyDate to=new MyDate((i+1)+"-01-01"); // maybe use 02-01?
                //System.out.println(from+" to "+to);
                Pair pair=new Pair(from,to);
                pairs.add(pair);
            }
        }
        return pairs;
    }
    public static Double[] filterPrices(int start,int stop,Double[] prices) {
        // this works on prices
        // can we make it work on rows instead?
        int n=stop-start; // use array copy?
        Double[] some=new Double[n];
        for(int i=start;i<stop;++i) some[i-start]=prices[i];
        return some;
    }
    public static List<String[]> filter(List<String[]> lines,Date from,Date to) {
        ArrayList<String[]> l=new ArrayList<>();
        if(lines.size()>0) l.add(lines.get(0)); // add csv header
        for(int i=1;i<lines.size();++i) {
            String[] line=lines.get(i);
            MyDate myDate=new MyDate(line[0]);
            if(MyDate.inRange(from,myDate.date(),to)) l.add(line);
        }
        return l;
    }
    public static List<String[]> getCSV(Path path,String filename) throws IOException,CsvException {
        Path csvFile=Path.of(path.toString(),filename);
        Reader reader;
        List<String[]> rows=null;
        reader=new FileReader(csvFile.toString());
        com.opencsv.CSVReader r=new com.opencsv.CSVReader(reader);
        rows=r.readAll();
        return rows;
    }
    static void sample(final List<String[]> rows) {
        System.out.println(Arrays.asList(rows.get(0)));
        System.out.println(Arrays.asList(rows.get(1)));
        System.out.println(Arrays.asList(rows.get(rows.size()-1)));
    }

    public static List<String[]> getNewPrices(String ticker) {
        String filename=ticker+".csv";
        List<String[]> rows=null;
        try {
            rows=getCSV(newPrices,filename);
        } catch(IOException|CsvException e) {
            System.err.println("caught: "+e);
        }
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
    static String toCSVLine(String[] row) {
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<row.length;++i) {
            if(i>0) sb.append(", ");
            sb.append(row[i]);
        }
        return sb.toString();
    }
    public static StringWriter toCSV(List<String[]> rows) throws IOException {
        StringWriter w=new StringWriter();
        for(String[] row:rows) {
            w.write(toCSVLine(row));
            w.write('\n');
        }
        return w;
    }
    static void toCSV(List<String[]> rows,String filename) throws IOException {
        if(filename!=null) {
            StringWriter w=toCSV(rows);
            w.close();
            File file=new File(filename);
            FileWriter fw=new FileWriter(file);
            fw.write(w.toString());
            fw.close();
        }
    }
    public static String toLine(String[] names,Object[] objects) {
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
    public static Integer indexOf(List<String[]> lines,MyDate target) {
        // how to deal with missing?
        // find the last that is <= target
        // find the first that is >= tagret
        for(int i=1;i<lines.size();++i) { // starts at 1| assumes a header!
            MyDate myDate=new MyDate(lines.get(i)[0]);
            if(myDate.date().equals(target.date())) return i;
        }
        return null;
    }
    public static Pair indicesToDates(List<String[]> lines,int start,int stop) {
        String f=lines.get(start)[0];
        String t=lines.get(stop)[0];
        MyDate from=new MyDate(f);
        MyDate to=new MyDate(t);
        Pair pair=new Pair(from,to);
        return pair;
    }
    public static Pair datesToindices(List<String[]> lines,MyDate from,MyDate to) {
        // find index of the dates.
        Integer start=indexOf(lines,from);
        Integer stop=indexOf(lines,to);
        Pair pair=new Pair(start,stop);
        return pair;
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
