package p;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }
}
