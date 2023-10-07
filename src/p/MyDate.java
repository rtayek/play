package p;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
class MyDate {
    MyDate(String string) { this(string,new SimpleDateFormat(defaultPattern)); }
    MyDate(String string,SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat=simpleDateFormat;
        date=date(string,simpleDateFormat);
    }
    public Date date() { return date; }
    @Override public String toString() {
        String string=simpleDateFormat.format(date);
        return "MyDate [date="+string+"]";
    }
    static Date date(String string,SimpleDateFormat simpleDateFormat) {
        Date out=null;
        try {
            out=simpleDateFormat.parse(string);
        } catch(ParseException e) {
            System.out.println("fails: "+string);
            e.printStackTrace();
        }
        return out;
    }
    public String toString2() {
        String string=simpleDateFormat.format(date);
        return string;
    }
    public static boolean inRange(Date d1,Date d2,Date d3) {
        return d1.compareTo(d2)<=0&&d2.compareTo(d3)<=0;
     }
    public static boolean inRange(Integer d1,Integer d2,Integer d3) {
        return d1.compareTo(d2)<=0&&d2.compareTo(d3)<=0; // include the last.
     }
    public static void main(String[] args) {
        String in="2017-10-26";
        System.out.println(in);
        MyDate myDate=new MyDate(in);
        Date out=myDate.date();
        System.out.println(out);
        System.out.println(myDate.toString2());
    }
    SimpleDateFormat simpleDateFormat;
    private final Date date;
    static String defaultPattern="yyyy-MM-dd";
}
