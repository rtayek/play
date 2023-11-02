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
import com.tayek.util.Pair;
public class CSV { // utilities
    public String[] names() { return names; }
    @Override public String toString() { // can never work!
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
        // can this use toString somehow?
        StringBuffer s=new StringBuffer();
        for(int i=0;i<names.length;++i) {
            if(i>0) s.append(", ");
            s.append(names[i]).append(": ").append(objects[i]);
        }
        return s.toString();
    }
    public static String toLine(Object... arguments) {
        // can this use toString somehow?
        StringBuffer s=new StringBuffer();
        for(int i=0;i<arguments.length;i+=2) {
            if(i>0) s.append(", ");
            if(i+1<arguments.length) s.append(": ").append(arguments[i+1]);
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
        System.out.println("header: "+result.header());
        Object[] arguments=new Object[] {play.exchange(),
                // need  buy, date,
                play.ticker(), // should be ticker
                // no, we now have ticker and filename
                // maybe just use ticker and add filename
                play.bankroll(),play.eProfit(),play.sdProfit(),play.pptd(),play.winRate(),play.buyRate(),play.days(),};
        System.out.println("result: "+result.toString(arguments));
        //System.out.println(result);
    }
    ArrayList<Pair> pairs=new ArrayList<>();
    String[] names=new String[pairs.size()];
    String[] formats=new String[pairs.size()];
    /*
        , winRate()="+winRate()+", buyRate()="+buyRate()+", days()="+days()
                +", hProfit()="+hProfit()+"]";
        eProfit: %5.2f, sdProfit: %5.2f, pptd: %7.3f, winRate: %5.2f, buyRate: %7.3f, days: %4d",
        String s=String.format("%15s, %7.3f, %5.3f, %5.3f, %7.3f, %5.2f, %7.3f, %4d", //
        if(false) s+=String.format(", \"%s\"",hProfit());
        return "exchange, ,hProfit";
    */
    static final CSV result=new CSV() {
        // this really needs a Play to print
        {
            pairs.add(new Pair("exchange","%3s"));
            pairs.add(new Pair("ticker","%10s"));
            pairs.add(new Pair("bankroll","%7.3f"));
            pairs.add(new Pair("eProfit","%6.3f"));
            pairs.add(new Pair("sdProfit","%6.3f"));
            pairs.add(new Pair("pptd","%6.3f"));
            pairs.add(new Pair("winRate","%5.2f"));
            pairs.add(new Pair("buyRate","%5.3f"));
            pairs.add(new Pair("days","%d"));
        }
    };
}
