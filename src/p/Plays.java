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
import p.Plays.Play;
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
            return "Play [exchange()="+exchange()+", ticker()="+ticker()+", date()="+date()+", change()="+change()
                    +", bankroll()="+bankroll()+", buy="+strategyName()+", eProfit()="+eProfit()+", sdProfit()="
                    +sdProfit()+", pptd()="+pptd()+", winRate()="+winRate()+", buyRate()="+buyRate()+", days()="+days()
                    +", hProfit()="+hProfit()+"]";
        }
        Object[] arguments() { // maybe belongs in Plays?
            Object[] arguments=new Object[] {exchange(),ticker(),date(),change(),
                    // need  buy, date,
                    // no, we now have ticker and filename
                    // maybe just use ticker and add filename
                    bankroll(),strategyName(),eProfit(),sdProfit(),pptd(),winRate(),buyRate(),days(),};
            return arguments;
        }
        public String toCSVLine() { // combine  with ?
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
                if(bankroll<0) { System.out.println("broke!"); break; }
                boughtAt=oneDay(strategy.buy,i,boughtAt);
            }
            if(hProfit.n()==0) {
                System.out.println("no activity for stock: "+ticker);
                //throw new RuntimeException("no activity for stock: "+name);
            }
        }
        public static StringWriter toCSV(SortedMap<Comparable<?>,Play> map) throws IOException {
            // maybe use values()? - just the Play[]?
            // maybe move to plays?
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
        public double jitter() { return random.nextGaussian(0,1e-7); }
        public void update() { // values now in outer class
            // this is fine where it it.
            hBankroll.add(bankroll());
            hExpectation.add(hProfit().mean());
            hWinRate.add(winRate());
            hBuyRate.add(buyRate());
            double key=-bankroll()+jitter(); // hack key so it's unique
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
        public MyDate date; // start date for price data.
        public String exchange; // maybe can not be final.
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
        Double[] some=new Double[n];
        for(int i=start;i<stop;++i) some[i-start]=prices[i];
        return some;
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
        // maybe pass in map and make static so other programs can use this?
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
    public static List<String> files(Path path) {
        List<String> files;
        System.out.println("files are in: "+path);
        File dir=path.toFile();
        files=Arrays.asList(dir.list());
        return files;
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
        // let's try to construct the play earlier so we can set stuff like verbosity, max files etc.
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        // @SuppressWarnings("unused") List<String> forceInitialization=datasetFilenames;
        Path path=Paths.get(rPath.toString(),"data","prices");
        path=newPrices;
        List<String> files=Plays.files(path);
        System.out.println(files.size()+" files.");
        System.out.println("start of processing filenames.");
        System.out.println("<<<<<<<<<<<<<<<<<<");
        staticMaxFiles=5;
        for(int index=0;index<files.size();++index) {
            if(index>=staticMaxFiles) break;
            String filename=files.get(index);
            Double[] prices=null;
            List<String[]> rows=getCSV(path,filename);
            System.out.println("index: "+index+", file: "+filename+" has data from: "+rows.get(1)[0]+" to: "
                    +rows.get(rows.size()-1)[0]);
            ArrayList<Pair> pairs=timePeriods(rows);
            System.out.println("number of time periods: "+pairs.size());
            Pair pair=pairs.get(0);
            System.out.println("first time period: "+pair.first+"  "+pair.second);            for(int periodIndexi1=0;periodIndexi1<1;++periodIndexi1) { // will be date ranges/ time periods
                Plays[] plays=new Plays[n];
                for(int strategyIndex=0;strategyIndex<n;++strategyIndex) {
                    plays[strategyIndex]=new Plays();
                    plays[strategyIndex].maxFiles=staticMaxFiles;
                    plays[strategyIndex].maxFiles=5;
                    Strategy strategy=strategies.get(strategyIndex);
                    Plays plays_=plays[strategyIndex];
                    System.out.println("map size; "+plays_.map.size());
                    // start of coe to move up.
                    prices=getClosingPrices(rows);
                    if(prices.length<plays_.minSize) { ++plays_.skippedFiles; continue; }
                    int length=260; // same as min size for now. about one year
                    if(prices.length<length) { System.out.println("too  small: "+filename); continue; }
                    int startIndex=prices.length-length;
                    int stopIndex=prices.length;
                    String[] row=rows.get(startIndex);
                    MyDate myDateStart=new MyDate(row[0]);
                    prices=Plays.filter(startIndex,stopIndex,prices);
                    // need to filter with dates. maybe write a test case.
                    // yes convert back and forth from  from/to to start/stop 
                    if(prices.length==0) { System.out.println("no prices!"); continue; }
                    // outer strategy look comes here.
                    Play play=plays_.new Play(filename);
                    play.prices=prices;
                    // end of code to move up.
                    play.strategyName=strategy.name; // this is just the name for csv.
                    play.date=myDateStart;
                    if(index==0) play.prologue(); // before
                    play.oneStock(strategy); // buy fails with array out of bounds
                    if(play.hProfit.n()>0) {
                        if(play.verbosity>1) System.out.println("profit: "+play.hProfit());
                        //System.out.println("dt: "+(System.nanoTime()-play.t0));
                        // updates will co-mingle different buys.
                        play.update();
                    }
                    combinedMap.putAll(plays_.map); // combine results from different strategies.
                } // end of for each strategy
                boolean writeBuys=false;
                if(writeBuys) for(int i=0;i<n;++i) {
                    Plays plays_=plays[i];
                    // these plays are for each strategy
                    // with a different filename for each strategy
                    // now we will have time period and strategy
                    // in the single cvs file for each file 
                    // or one big file!
                    Play.toCSV(plays_.map);
                    toCsvFile(plays_.map,"buy"+"."+filename+"."+periodIndexi1+".csv");
                }
                //r.summary(r.map,"out.csv");
                /*
                System.out.println(">>>>>>>>>>>>>>>>>>");
                System.out.println("map sze: "+r.map.size());
                System.out.println("map: "+r.map);
                System.out.println("key set: "+r.map.keySet());
                */
            } // end of for each time period.
              //if(index>0&&index%1000==0) System.out.println("index: "+index+", bankroll: "+r.hBankroll);
        } // end of for each file.
          // write a csv file here.
        System.out.println("end of processing filenames.");
        //System.out.println(r.skippedFiles+" skipped files.");
        /*
        if(false) try {
            r.filenames("newfilenames.txt"); // save filenames in order
        } catch(IOException e) {
            e.printStackTrace();
        }
        */
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
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
    Random random=new Random();
    TreeMap<Comparable<?>,Play> map=new TreeMap<>();
    Histogram hBankroll=new Histogram(10,0,10);
    Histogram hExpectation=new Histogram(10,0,1);
    Histogram hWinRate=new Histogram(10,0,1);
    Histogram hBuyRate=new Histogram(10,0,1);
    static int staticMaxFiles=Integer.MAX_VALUE;
    static TreeMap<Comparable<?>,Play> combinedMap=new TreeMap<>();
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
