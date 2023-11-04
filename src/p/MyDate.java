package p;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
class MyDate {
    @Override public int hashCode() { return Objects.hash(date); }
    @Override public boolean equals(Object obj) {
        if(this==obj) return true;
        if(obj==null) return false;
        if(getClass()!=obj.getClass()) return false;
        MyDate other=(MyDate)obj;
        return Objects.equals(date,other.date);
    }
    MyDate() { this(new Date()); }
    MyDate(Date date) { this.date=date; }
    MyDate(String string) { this(string,new SimpleDateFormat(defaultPattern)); }
    MyDate(String string,SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat=simpleDateFormat;
        date=date(string,simpleDateFormat);
    }
    public Date date() { return date; }
    @Override public String toString() { String string=simpleDateFormat.format(date); return string; }
    static Date date(String string,SimpleDateFormat simpleDateFormat) {
        Date out=null;
        try {
            out=simpleDateFormat.parse(string);
        } catch(ParseException e) {
            System.out.println("date() fails: "+string);
            e.printStackTrace();
        }
        return out;
    }
    String toShortDate() {
        SimpleDateFormat DATE_FORMAT=new SimpleDateFormat("iyyy-MM-dd");
        String s=DATE_FORMAT.format(date);
        return s;
    }
    public static boolean inRange(Date d1,Date d2,Date d3) { return d1.compareTo(d2)<=0&&d2.compareTo(d3)<=0; }
    public static boolean inRange(Integer d1,Integer d2,Integer d3) {
        return d1.compareTo(d2)<=0&&d2.compareTo(d3)<=0; // include the last.
    }
    public static void main(String[] args) {
        String in="2017-10-26";
        System.out.println(in);
        MyDate myDate=new MyDate(in);
        Date out=myDate.date();
        System.out.println(out);
        System.out.println(myDate);
    }
    private final Date date;
    static String defaultPattern="yyyy-MM-dd";
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat(defaultPattern);
}
