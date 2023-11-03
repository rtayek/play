package p;
import static p.Stock.*;
import static p.CSV.*;
import static p.Plays.Play.What.*;
import static p.DataPaths.*;
import static p.Strategy.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import com.opencsv.exceptions.CsvException;
import com.tayek.util.Histogram;
import com.tayek.util.Pair;
public class Plays {
    class Play {
        public enum What { win, tie, lose }
        public static String getExchange(String filename) { // hack
            String ticker=removeTarget(filename,".csv");
            Stock stock=stocks.get(ticker);
            String exchange=stock!=null?stock.exchange:"";
            return exchange;
        }
        public Play(String filename) { this(filename,getExchange(filename)); }
        public Play(String filename,String exchange) {
            this.exchange=exchange;
            ticker=removeTarget(filename,".csv");
            // this is a filename i.e. AAPL.csv
            // and not AAPL which was why we could not find
            this.filename=filename;
            // this variable is not used as a filename!
            // we mung it later so  we can tell what buy was used.
            // we will add buy and date to the summary line.
            // so  add these and stop munging the filename.
            date=new MyDate(); // just so there is something there.
        }
        public static double profit(double boughtAt,double current) {
            double profit=(current-boughtAt)/boughtAt;
            return profit;
        }
        public void sell(double boughtAt,int index,double amountBet) { // add bought price, then recurse
            double current=prices[index];
            double change=current-boughtAt;
            Play.What whatHappened=change>0?win:change==0?tie:lose;
            switch(whatHappened) {
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
        double change() {
            if(prices==null) throw new RuntimeException("prices is null!");
            double old=prices[0];
            double change=(prices[prices.length-1]-old)/old;
            //return change;
            return prices[prices.length-1]/old;
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
        // we need to add a date to the data frame.
        // maybe include the date in the filename.
        // also maybe add the buy strategy to the data frame. 
        // also, add the change in value over the prices to the data frame.
        @Override public String toString() {
            return "Play [exchange()="+exchange()+", ticker()="+ticker()+", change()="+change()+", bankroll()="+bankroll()
                    +", buy="+strategyName()
                    +", eProfit()="+eProfit()+", sdProfit()="+sdProfit()+", pptd()="+pptd()+", winRate()="+winRate()
                    +", buyRate()="+buyRate()+", days()="+days()+", hProfit()="+hProfit()+"]";
        }
        Object[] arguments() { // maybe belongs in Plays?
            Object[] arguments=new Object[] {exchange(),ticker(),
                    date(),
                    change(),
                    // need  buy, date,
                    // no, we now have ticker and filename
                    // maybe just use ticker and add filename
                    bankroll(),strategyName(),eProfit(),sdProfit(),pptd(),winRate(),buyRate(),days(),};
            return arguments;
        }
        public String toCSVLine() {
            String s=result.toString(arguments());
            if(false) s+=String.format(", \"%s\"",hProfit());
            return s;
        }
        public static String xheader() {
            return "exchange, ticker, bankroll, eProfit, sdProfit, pptd, winRate, buyRate, days, hProfit";
        }
        // name should be ticker symbol.
        public String date() { return date.toString(); }
        public String strategyName() { return strategyName; }
        public String exchange() { return exchange; }
        public String filename() { return filename; }
        public String ticker() { return ticker; }
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
        public int days() { return prices.length; } // assumes we will always use them all!
        public Histogram hProfit() { return hProfit; }
        void oneStock(Strategy strategy) {
            double boughtAt=0;
            if(verbosity>0) System.out.println("-----------------------");
            if(verbosity>0) System.out.println(prices.length+" prices.");
            for(int i=buffer;i<prices.length-forecast;++i) {
                if(verbosity>0) System.out.println("index: "+i+", bankroll: "+bankroll);
                if(bankroll<0) { System.out.println("broke!"); break; }
                boughtAt=oneDay(strategy.buy,i,boughtAt);
                if(verbosity>0) System.out.println("-----------------------");
                if(false&&i>=buffer+1) { System.out.println("breaking out after "+i); break; }
            }
            if(hProfit.n()==0) {
                System.out.println("no activity for stock: "+ticker);
                //throw new RuntimeException("no activity for stock: "+name);
            }
        }
        //public static StringWriter toCSV(SortedMap<Comparable<?>,Play> map) throws IOException {
        // }
        public static StringWriter toCSV(SortedMap<Comparable<?>,Play> map) throws IOException {
            // maybe use values()? - just the Play[]?
            StringWriter w=new StringWriter();
            w.write(result.header()+'\n');
            for(Object d:map.keySet()) {
                Play play=map.get(d);
                if(play.hProfit.n()>0) {
                    // System.out.println(play); // seems to work ok
                    w.write(play.toCSVLine());
                    w.write('\n');
                } else {
                    System.out.println("omitting: "+play.ticker()+" add buy, add date"); // never prints!
                    // because empties are not put into plays.map
                    throw new RuntimeException("omitting: "+play.ticker());
                }
            }
            //w.write(Play.header()+'\n');
            w.close();
            return w;
        }
        public static void toConsole(SortedMap<Comparable<?>,Play> map) {
            System.out.println(result.header());
            for(Object d:map.keySet()) System.out.println(map.get(d));
        }
        public static void toCsv(SortedMap<Comparable<?>,Play> map) {
            System.out.println(result.header());
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
            double key=-(bankroll()+factor); // this breaks sort order!
            Random r=new Random();
            double x=r.nextGaussian(0,1e-7);
            key=-bankroll()+x;
            if(map.containsKey(key)) throw new RuntimeException("dup;licate key!");
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
        public MyDate date;
        public String exchange; // maybe can not be final
        public String strategyName;
        public final String ticker;
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
    static void toCsvFile(SortedMap<Comparable<?>,Play> map,String filename) throws IOException {
        if(filename!=null) {
            System.out.println("writing: "+map.size()+" entries to: "+filename);
            StringWriter w=Play.toCSV(map); // maybe pass values in?
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
            toCsvFile(map,filename);
        } catch(IOException e) {
            e.printStackTrace();
        }
        System.out.println("end of summary for "+hBankroll.n()+" files");
    }
    public void filenames(String filename) throws IOException {
        StringWriter w=new StringWriter();
        for(Object d:map.keySet()) {
            Play play=map.get(d);
            w.write(play.filename()); // may need to add buy and date!
            // no, this variable is not used as a filename!
            w.write('\n');
        }
        File file=new File(filename);
        FileWriter fw=new FileWriter(file);
        fw.write(w.toString());
        fw.close();
    }
    public void some(Path path,List<String> filenames,MyDate from,MyDate to,Strategy strategy) {
        System.out.println("from: "+from);
        System.out.println("to: "+to);
        //for(int i=0;i<5;++i) System.out.println(filenames.get(i));
        for(int index=0;index<filenames.size();++index) {
            if(index>=maxFiles) break;
            String filename=filenames.get(index);
            Double[] prices=null;
            List<String[]> rows=getCSV(path,filename);
            System.out.println("map size;  "+map.size()+", index: "+index+", file: "+filename+" has data from: "
                    +rows.get(1)[0]+" to: "+rows.get(rows.size()-1)[0]);
            prices=getClosingPrices(rows);
            if(prices.length<minSize) { ++skippedFiles; continue; }
            int length=260; // same as min size for now. about one year
            if(prices.length<length) { System.out.println("too  small: "+filename); continue; }
            int start=prices.length-length;
            int stop=prices.length;
            // try to get the date
            String[] row=rows.get(start);
            MyDate myDate=new MyDate(row[0]);
            prices=filter(start,stop,prices);
            if(prices.length==0) { System.out.println("no prices!"); continue; }
            //System.out.println(prices.length+" prices.");
            Play play=new Play(filename);
            play.prices=prices;
            play.strategyName=strategy.name; // this is just the name for csv.
            play.date=myDate;
            //play.verbosity=1;
            if(index==0) play.prologue(); // before
            play.oneStock(strategy); // buy fails with array out of bounds
            if(play.hProfit.n()>0) {
                if(play.verbosity>1) System.out.println("profit: "+play.hProfit());
                //System.out.println("dt: "+(System.nanoTime()-play.t0));
                // updates will co-mingle different buys.
                play.update();
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
    void run(Strategy strategy) {
        // name,bankroll,eProfit,sdProfit,pptd,winRate,buyRate,days,hProfit
        // qbac-ws-b.us.txt, 14.29,  0.01,  0.07,   0.013,  0.52,   0.977,  260, "-0.15215711<=0.012809268/254<=0.48584905 117,[24,22,17,14,10,9,6,3,5,6],21 NaNs: 0"
        // @SuppressWarnings("unused") List<String> forceInitialization=datasetFilenames;
        MyDate from=new MyDate("2000-01-01");
        MyDate to=new MyDate("2023-01-01");
        // the above dates do not seem to be used here.
        System.out.println("from: "+from+", to: "+to);
        Path path=Paths.get(rPath.toString(),"data","prices");
        path=newPrices;
        List<String> files=files(path);
        System.out.println(files.size()+" files.");
        System.out.println("start of processing filenames.");
        System.out.println("<<<<<<<<<<<<<<<<<<");
        // this reads file with data made by r program.
        some(path,files,from,to,strategy);
        System.out.println(">>>>>>>>>>>>>>>>>>");
        System.out.println("map sze: "+map.size());
        System.out.println("map: "+map);
        System.out.println("key set: "+map.keySet());
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
        ArrayList<Strategy> strategies=strategies();
        int n=strategies.size();
        // one(prices,strategies)
        // one(file,timePeriods,strategies)
        // some/run(files,timePeriods,strategies)

        Plays[] plays=new Plays[n];
        for(int i=0;i<n;++i) plays[i]=new Plays();
        // let's try to construct the play earlier so we can set stuff like verbosity, max files etc.
        for(int i=0;i<n;++i) {
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            plays[i].maxFiles=staticMaxFiles;
            plays[i].maxFiles=5;
            plays[i].run(strategies.get(i));
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
        boolean writeBuys=true;
        if(writeBuys) for(int i=0;i<n;++i) {
            Plays plays_=plays[i];
            toCsvFile(plays_.map,"buy"+i+".csv");
        }
        boolean writeTickes=true;
        if(writeTickes) for(int i=0;i<n;++i) {
            Plays plays_=plays[i];
            //toCsv(plays_.map,"buy"+i+".csv");
        }
        for(int i=0;i<n;++i) { // mung the filenames
            for(Play play:plays[i].map.values()) { play.filename=play.filename+" "+i; }
            // this adds the buy index to the filename
        }
        // combine the different strategies.
        SortedMap<Comparable<?>,Play> combinedMap=new TreeMap<>();
        for(int i=0;i<n;++i) combinedMap.putAll(plays[i].map); // combine results from different strategies.
        System.out.println("map:");
        //Play.toCsv(combinedMap); // to sysout
        boolean writeBig=true;
        if(writeBig) Plays.toCsvFile(combinedMap,"newbuyall.csv");
    }
    int verbosity=0; // for the outer class
    // initializers
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
    static final CSV result=new CSV() {
        {
            pairs.add(new Pair("exchange","%-3s"));
            pairs.add(new Pair("ticker","%-10s"));
            pairs.add(new Pair("date","%-10s"));
            pairs.add(new Pair("change","%7.2f"));
            pairs.add(new Pair("bankroll","%7.3f"));
            pairs.add(new Pair("buy","%-5s"));
            pairs.add(new Pair("eProfit","%6.3f"));
            pairs.add(new Pair("sdProfit","%6.3f"));
            pairs.add(new Pair("pptd","%6.3f"));
            pairs.add(new Pair("winRate","%5.2f"));
            pairs.add(new Pair("buyRate","%5.3f"));
            pairs.add(new Pair("days","%d"));
        }
    };
}
