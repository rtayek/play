package p;
//     class Result { Result(String ticker) { this.ticker=ticker; } final String ticker; double br0,br1,br2,br3; }
public record Result(String ticker,double bankroll,double winRate,double buyRate) {
    public static void main(String[] args) {
        Result result=new Result("AAPL",1,.6,.4);
        System.out.println(result);
    }
}
