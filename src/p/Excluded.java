package p;
import java.util.*;
public class Excluded {
    public static void main(String[] args) {
        Excluded excluded=new Excluded(); 
        System.out.println(excluded.excluded); }
    final TreeSet<String> excluded=new TreeSet<>();
    {
        excluded.add("BOTY");
        excluded.add("YZC.F"); // stopped trading o
    }
}
