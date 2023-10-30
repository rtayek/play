package p;
import static p.Stock.*;
import static p.CSV.*;
import static p.Plays.Play.Result.*;
import static p.DataPaths.*;
import static p.Strategy.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import com.opencsv.exceptions.CsvException;
import com.tayek.util.Histogram;
public class Plays {
    class Play {
        public enum Result { win, tie, lose }
        public Play(String filename) {
            this.filename=filename;
            //this.prices=prices;
        }
        public static double profit(double boughtAt,double current) {
            double profit=(current-boughtAt)/boughtAt;
            return profit;
        }
        public void sell(double boughtAt,int index,double amountBet) { // add bought price, then recurse
            double current=prices[index];
            double change=current-boughtAt;
            Play.Result result=change>0?win:change==0?tie:lose;
            switch(result) {
                case win:
                    ++wins;
                    break;
                case tie:
                    ++ties;
                    break;
                case lose:
                    ++losses;
                    break;
            }
            double profit=(current-boughtAt)/boughtAt; // per share
            hProfit().add(profit);
            double delta=amountBet*(1+profit);
            double previous=bankroll;
            if(true) bankroll+=delta; // normally we adjust our bankroll
            else bankroll+=amountBet; // pretend no gain or loss.
            // this is just to keep the bankroll constant.
            if(verbosity>0) System.out.println("change: "+change+", delta br: "+delta);
            if(verbosity>1)
                System.out.format("profit %6.3f, br %7.3f, bought at %6.3f, current %6.3f, $ %6.3f, rake %7.3f\n",
                        profit,bankroll,boughtAt,current,current-boughtAt,rake);
            if(verbosity>1) System.out
                    .println(index+", buys: "+buys+", wins: "+wins+", ties "+ties+", losses "+losses+", br: "+bankroll);
        }
        double oneDay(BiPredicate<Integer,Double[]> strategy,int i,double boughtAt) { // one day
            if(boughtAt==0) { // new buy?
                if(strategy.test(i,prices)) {
                    boughtAt=prices[i-1];
                    double betAmount=bankroll*bet;
                    bankroll-=betAmount;
                    double rakeAmount=betAmount*rake;
                    totalRake+=rakeAmount;
                    betAmount-=rakeAmount;
                    if(verbosity>0) System.out
                            .println("bet amount: "+betAmount+", rake amount: "+rakeAmount+", totalrake: "+totalRake);
                    ++buys;
                    sell(boughtAt,i,betAmount);
                    boughtAt=0;
                } else {
                    //System.out.println("no buy");
                }
            } else { // we are holding some
                // this was never implemented
            }
            return boughtAt;
        }
        @Override public String toString() {
            return "Play [name()="+name()+", bankroll()="+bankroll()+", eProfit()="+eProfit()+", sdProfit()="+sdProfit()
                    +", pptd()="+pptd()+", winRate()="+winRate()+", buyRate()="+buyRate()+", days()="+days()
                    +", hProfit()="+hProfit()+"]";
        }
        public String toString2() {
            String s=String.format(
                    "symbol: %s, bankroll: %7.3f, eProfit: %5.2f, sdProfit: %5.2f, pptd: %7.3f, winRate: %5.2f, buyRate: %7.3f, days: %4d",
                    name(),bankroll(),eProfit(),sdProfit(), //
                    pptd(),winRate(),buyRate(),days());
            return s;
        }
        public String toCSVLine() {
            String s=String.format("%20s, %7.3f, %5.3f, %5.3f, %7.3f, %5.2f, %7.3f, %4d", //
                    name(),bankroll(),eProfit(),sdProfit(), //
                    pptd(),winRate(),buyRate(),days());
            if(false) s+=String.format(", \"%s\"",hProfit());
            return s;
        }
        public static String header() {
            return "exchange, name, bankroll, eProfit, sdProfit, pptd, winRate, buyRate, days, hProfit";
        }
        // name should be ticker symbol.
        public String name() { return filename; }
        public Double bankroll() { return bankroll; }
        public Double eProfit() { return hProfit().mean(); }
        public Double sdProfit() { return Math.sqrt(hProfit().variance()); }
        public Double pptd() { return hProfit().mean()*hProfit().n()/days(); }
        public Double winRate() {
            int n=wins+ties+losses;
            if(n!=buys) throw new RuntimeException("oops");
            return wins/(double)n;
        }
        public Double buyRate() {
            int n=wins+ties+losses;
            if(n!=buys) throw new RuntimeException("oops");
            return n/(double)prices.length;
        }
        public int days() { return prices.length; }
        public Histogram hProfit() { return hProfit; }
        void oneStock(BiPredicate<Integer,Double[]> strategy) {
            double boughtAt=0;
            if(verbosity>0) System.out.println("-----------------------");
            if(verbosity>0) System.out.println(prices.length+" prices.");
            for(int i=buffer;i<prices.length-forecast;++i) {
                if(verbosity>0) System.out.println("index: "+i+", bankroll: "+bankroll);
                if(bankroll<0) { System.out.println("broke!"); break; }
                boughtAt=oneDay(strategy,i,boughtAt);
                if(verbosity>0) System.out.println("-----------------------");
                if(false&&i>=buffer+1) { System.out.println("breaking out after "+i); break; }
            }
            if(hProfit.n()==0) {
                System.out.println("no activity for stock: "+filename);
                //throw new RuntimeException("no activity for stock: "+filename);
            }
        }
        void summary() {
            //System.out.println("summary:"); 
            System.out.println(this.toString2());
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
        //public static StringWriter toCSV(SortedMap<Comparable<?>,Play> map) throws IOException {
        // }
        public static StringWriter toCSV(SortedMap<Comparable<?>,Play> map) throws IOException {
            // maybe use values()?
            StringWriter w=new StringWriter();
            w.write(Play.header()+'\n');
            for(Object d:map.keySet()) {
                Play play=map.get(d);
                String name=play.filename;
                if(play.hProfit.n()>0) {
                    String[] words=name.split(" ");
                    name=words[0]; // first word of filename?
                    String target=".csv";
                    if(name.endsWith(target)) {
                        //System.out.println("before: "+name);
                        name=name.substring(0,name.length()-target.length());
                        //System.out.println("after: "+name);
                    }
                    Stock stock=stocks.get(name);
                    //System.out.println(stock);
                    if(true) { String s=String.format("%-10s",stock.exchange); w.write(s); w.write(", "); }
                    // get rid of the 10s above
                    w.write(play.toCSVLine());
                    w.write('\n');
                } else {
                    System.out.println("omitting: "+name); // never prints!
                    // because empties are not put into plays.map
                    throw new RuntimeException("omitting: "+name);
                }
            }
            //w.write(Play.header()+'\n');
            w.close();
            return w;
        }
        public static void toConsole(SortedMap<Comparable<?>,Play> map) {
            System.out.println(Play.header());
            for(Object d:map.keySet()) System.out.println(map.get(d));
        }
        public static void toCsv(SortedMap<Comparable<?>,Play> map) {
            System.out.println(Play.header());
            for(Object d:map.keySet()) System.out.println(map.get(d).toCSVLine());
        }
        public static List<String> getFilenames(String filename) throws IOException {
            File file=new File(filename);
            BufferedReader br=new BufferedReader(new FileReader(file));
            ArrayList<String> list=new ArrayList<>();
            for(String name=br.readLine();name!=null;name=br.readLine()) list.add(name);
            return list;
        }
        public void update() { // values now in outer class
            // this is fine where it it.
            hBankroll.add(bankroll());
            hExpectation.add(hProfit().mean());
            hWinRate.add(winRate());
            hBuyRate.add(buyRate());
            // hack key so it's unique
            long dt=System.nanoTime()-t0;
            double d=bankroll();
            double factor=dt/100_000_000.;
            //System.out.println("dt2: "+dt+", factor: "+factor);
            double key=-(bankroll()+factor);
            map.put(key,this);
        }
        public void prologue() {
            System.out.format("start at: %4d, min:  %4d, nax: %4d\n" //
                    +"br: %7.2f, bet: %5.2f, rake: %.2f, max files: %5d"+", buy: "+"\n", //
                    startAt,minSize,maxsize, //
                    bankroll,bet,rake,maxFiles //
            );
            //static int maxFiles=Integer.MAX_VALUE;
            //static int buffer=5,forecast=3;
            //static double initialBankroll=1;
        }
        public final long t0=System.nanoTime();
        public /*final*/ String filename; // so we can change it for different strategies.
        public transient int buys,wins,losses,ties;
        public transient double bankroll=initialBankroll;
        // make bankroll a list or array?
        public transient Histogram hProfit=new Histogram(10,0,.10);
        public transient double totalRake=0;
        public final double bet=initialBankroll; // amount of bankroll to bet.
        // won't be changing for a while. 10/29/23 maybe not.
        public transient /*final*/ Double[] prices;
        public double rake=.0;
        public int verbosity=0;
    } // end of class Play
    public static Double[] filter(int start,int stop,Double[] prices) {
        int n=stop-start; // use array copy?
        Double[] p=new Double[n];
        for(int i=start;i<stop;++i) p[i-start]=prices[i];
        return p;
    }
    static void toCsv(SortedMap<Comparable<?>,Play> map,String filename) throws IOException {
        if(filename!=null) {
            System.out.println("writing: "+map.size()+" entries to: "+filename);
            StringWriter w=Play.toCSV(map);
            File file=new File(filename);
            FileWriter fw=new FileWriter(file);
            fw.write(w.toString());
            fw.close();
        }
    }
    public void summary(SortedMap<Comparable<?>,Play> map,String filename) {
        System.out.println("summary for "+hBankroll.n()+" files");
        System.out.println("e: "+hExpectation);
        System.out.println("E(profit): "+toString(hExpectation));
        System.out.println("bankroll: "+hBankroll);
        System.out.println("bankroll: "+toString(hBankroll));
        System.out.println("win rate: "+hWinRate);
        System.out.println("buy rate: "+hBuyRate);
        //Play.toConsole(Play.map);
        try {
            toCsv(map,filename);
        } catch(IOException e) {
            e.printStackTrace();
        }
        System.out.println("end of summary for "+hBankroll.n()+" files");
    }
    public void filenames(String filename) throws IOException {
        StringWriter w=new StringWriter();
        for(Object d:map.keySet()) {
            Play play=map.get(d);
            w.write(play.filename);
            w.write('\n');
        }
        File file=new File(filename);
        FileWriter fw=new FileWriter(file);
        fw.write(w.toString());
        fw.close();
    }
    public Play one(String filename,Double[] prices,BiPredicate<Integer,Double[]> strategy) {
        // make this use complete path
        // make this independent of chart stuff
        Play play=new Play(filename);
        play.prices=prices;
        play.rake=.0;
        play.verbosity=1;
        play.oneStock(strategy);
        if(play.verbosity>0) play.summary();
        if(play.verbosity>1) System.out.println("profit: "+play.hProfit());
        if(play.verbosity>0) System.out.println(play);
        if(play.verbosity>0)
            System.out.println("wins: "+play.wins+", buys: "+play.buys+", total rake: "+play.totalRake);
        if(play.verbosity>0) System.out.println(play.toCSVLine());
        if(play.verbosity>0) System.out.println(play.toString2());
        return play;
    }
    public void some(Path path,List<String> filenames,MyDate from,MyDate to,BiPredicate<Integer,Double[]> strategy) {
        System.out.println("from: "+from);
        System.out.println("to: "+to);
        //for(int i=0;i<5;++i) System.out.println(filenames.get(i));
        for(int index=0;index<filenames.size();++index) {
            if(index>=maxFiles) break;
            String filename=filenames.get(index);
            Double[] prices=null;
            System.out.println("processing file: "+filename);
            List<String[]> rows=getCSV(path,filename);
            prices=getClosingPrices(rows);
            if(prices.length<minSize) { ++skippedFiles; continue; }
            int length=260; // same as min size for now. about one year
            if(prices.length<length) { System.out.println("too  small: "+filename); continue; }
            int start=prices.length-length;
            int stop=prices.length;
            prices=filter(start,stop,prices);
            if(prices.length==0) { System.out.println("no prices!"); continue; }
            //System.out.println(prices.length+" prices.");
            Play play=new Play(filename);
            play.prices=prices;
            //play.verbosity=1;
            if(index==0) play.prologue(); // before
            play.oneStock(strategy); // buy fails with array out of bounds
            //System.out.println("play: "+play.toString2());
            if(play.hProfit.n()>0) {
                if(play.verbosity>0) play.summary();
                if(play.verbosity>1) System.out.println("profit: "+play.hProfit());
                //System.out.println("dt: "+(System.nanoTime()-play.t0));
                // updates will co-mingle different buys.
                play.update();
                //update(hBankroll,hExpectation,map,play);
            }
            if(play!=null&&index==filenames.size()-1) play.prologue();
            if(index>0&&index%1000==0) System.out.println("index: "+index+", bankroll: "+hBankroll);
        }
        summary(map,"out.csv");
    }
    public static List<String> files(Path path) {
        List<String> files;
        System.out.println("files are in: "+path);
        File dir=path.toFile();
        files=Arrays.asList(dir.list());
        return files;
    }
    void run(BiPredicate<Integer,Double[]> strategy) {
        // name,bankroll,eProfit,sdProfit,pptd,winRate,buyRate,days,hProfit
        // qbac-ws-b.us.txt, 14.29,  0.01,  0.07,   0.013,  0.52,   0.977,  260, "-0.15215711<=0.012809268/254<=0.48584905 117,[24,22,17,14,10,9,6,3,5,6],21 NaNs: 0"
        // @SuppressWarnings("unused") List<String> forceInitialization=datasetFilenames;
        //Play.maxFiles=999;
        //Play.maxFiles=10;
        MyDate from=new MyDate("2000-01-01");
        MyDate to=new MyDate("2023-01-01");
        // the above dates do not seem to be used here.
        System.out.println("from: "+from+", to: "+to);
        Path path=Paths.get(rPath.toString(),"data","prices");
        List<String> files=files(path);
        System.out.println(files.size()+" files.");
        System.out.println("start of processing filenames.");
        System.out.println("<<<<<<<<<<<<<<<<<<");
        // this reads file with data made by r program.
        some(path,files,from,to,strategy);
        System.out.println(">>>>>>>>>>>>>>>>>>");
        System.out.println("map sze: "+map.size());
        System.out.println("end of processing filenames.");
        System.out.println(skippedFiles+" skipped files.");
        if(false) try {
            filenames("newfilenames.txt");
        } catch(IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // save filenames in order
          //System.out.println(getFilenames("filenames.txt"));
    }
    public static String toString(Histogram histogram) {
        return String.format("min: %5.2f, mean: %5.2f, max: %5.2f" //
                +", sd: %6.3f",histogram.min() //
                ,histogram.mean() //
                ,histogram.max() //
                ,Math.sqrt(histogram.variance()) //
        );
    }
    public static void main(String[] args) throws IOException {
        System.out.println("enter main()");
        /*
        System.out.println("make some stocks");
        SortedMap<String,Stock> some=stocks.entrySet().stream().limit(3).collect(TreeMap::new,
                (m,e)->m.put(e.getKey(),e.getValue()),Map::putAll);
        System.out.println("some stocks: "+some);
        */
        //Stock.printExchanges();
        Stock.sortExchangesByFrequency();
        //Stock.printSortedExchanges();
        //if(true) return;
        ArrayList<BiPredicate<Integer,Double[]>> buys=buys();
        int n=buys.size();
        Plays[] plays=new Plays[n];
        for(int i=0;i<n;++i) plays[i]=new Plays();
        // let's try to construct the play earlier so we can set stuff like verbosity, max files etc.
        for(int i=0;i<n;++i) {
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            plays[i].maxFiles=staticMaxFiles;
            //plays[i].maxFiles=10;
            plays[i].run(buys.get(i));
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println("elasped time: "+(System.currentTimeMillis()-plays[i].t0ms)+" ms.");
        }
        boolean writeBuys=true;
        if(writeBuys) for(int i=0;i<n;++i) {
            Plays plays_=plays[i];
            toCsv(plays_.map,"buy"+i+".csv");
        }
        boolean writeTickes=true;
        if(writeTickes) for(int i=0;i<n;++i) {
            Plays plays_=plays[i];
            //toCsv(plays_.map,"buy"+i+".csv");
        }
        for(int i=0;i<n;++i) { // mung the filenames
            for(Play play:plays[i].map.values()) { play.filename=play.filename+" "+i; }
        }
        // combine the different strategies.
        SortedMap<Comparable<?>,Play> map=new TreeMap<>();
        for(int i=0;i<n;++i) map.putAll(plays[i].map); // combine results from different strategies.
        //Play.toCsv(map);
        boolean writeBig=true;
        if(writeBig) toCsv(map,"newbuyall.csv");
    }
    int verbosity=0; // for the outer class
    // initializers
    long t0ms=System.currentTimeMillis();
    int startAt=0,minSize=260,maxsize=260;
    int maxFiles=Integer.MAX_VALUE;
    int buffer=5,forecast=3;
    double initialBankroll=1;
    // accumulators
    int skippedFiles=0;
    SortedMap<Comparable<?>,Play> map=new TreeMap<>();
    Histogram hBankroll=new Histogram(10,0,10);
    Histogram hExpectation=new Histogram(10,0,1);
    Histogram hWinRate=new Histogram(10,0,1);
    Histogram hBuyRate=new Histogram(10,0,1);
    static int staticMaxFiles=Integer.MAX_VALUE;
}
