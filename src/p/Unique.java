package p;
import static p.CSV.*;
import static p.DataPaths.rPath;
import static p.Stock.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import com.opencsv.exceptions.CsvException;
public class Unique {
    static Integer find(ArrayList<String[]> yahoo,String target) {
        Integer found=null;
        for(int i=0;i<yahoo.size();++i) {
            //if(i%10000==0) System.out.println(i);
            //System.out.println("'"+yahoo.get(i)[0]+"'");
            if(yahoo.get(i)[0].equals(target)) { found=i; break; }
        }
        return found;
    }
    static void makeUnbiqueFRomBuyAll() throws IOException, CsvException {
        // make a .csv like the yahoo.csv with unique stocks.
        Path path=Paths.get("");
        List<String[]> rows=getCSV(path,"newbuyall.csv");
        System.out.println(rows.size()+" rows.");
        LinkedHashSet<String> unique=new LinkedHashSet<>();
        for(int i=1;i<rows.size();++i) unique.add(rows.get(i)[1].trim());
        System.out.println(unique.size()+" unique.");
        ArrayList<String[]> stocks=new ArrayList<>();
        stocks.add(yahoo.get(0)); // copy yahoo header
        for(String target:unique) {
            Integer found=find(yahoo,target);
            if(found!=null) {
                ; //System.out.println("found: "+yahoo.get(found)[0]);
                stocks.add(yahoo.get(found));
            } else {
                System.out.println("can not find: "+target);
                throw new RuntimeException();
            }
        }
        if(stocks.size()>0) for(int i=0;i<stocks.size();++i) { System.out.println(toCSVLine(stocks.get(i))); }
        toCSV(stocks,"uniquefromnewbuyall.csv");
    }
    public static void main(String[] args) throws IOException, CsvException {
        //makeUnbiqueFRomBuyAll();
        //if(true) return;
        Path path=Paths.get("");
        List<String[]> rows=getCSV(path,"unusual.csv");
        // not sure what to do with this?
        System.out.println(rows.size()+" rows.");
    }
}
