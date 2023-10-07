package q;
import java.util.SortedMap;
import java.util.TreeMap;
import com.tayek.util.Histogram;
import p.Play;
class Plays {
    class Play { }
    void run(String[] filenames) {
        for(String filename:filenames) {
            Play x=new Play();
            update();
        }
    }
    public Double bankroll() { return bankroll; }
    double bankroll=initialBankroll;
    public void update() {
        hBankroll.add(bankroll());
        //hExpectation.add(hProfit().mean());
        //hWinRate.add(winRate());
        //hBuyRate.add(buyRate());
        // hack key so it's uniqie
        //long dt=System.nanoTime()-t0;
        //double d=bankroll();
        //double factor=dt/100000000.;
        //System.out.println("dt2: "+dt+", factor: "+factor);
        //double key=-(bankroll()+factor);
        //map.put(key,this);
        //map.put(filename,this);
    }
    int br;
    // initializers
    static long t0ms=System.currentTimeMillis();
    static int startAt=0,minSize=260,maxsize=260;
    static int maxFiles=Integer.MAX_VALUE;
    static int buffer=5,forecast=3;
    static double initialBankroll=1;
    // summary
    static int skippedFiles=0;
    static SortedMap<Comparable<?>,Play> map=new TreeMap<>();
    static Histogram hBankroll=new Histogram(10,0,10);
    static Histogram hExpectation=new Histogram(10,0,1);
    static Histogram hWinRate=new Histogram(10,0,1);
    static Histogram hBuyRate=new Histogram(10,0,1);
}
public class Main {
    public static void main(String[] args) {
        Plays xs=new Plays();
        String[] filenames=new String[] {"a","b","c"};
        xs.run(filenames);
        System.out.println(xs.hBankroll);
    }
}
