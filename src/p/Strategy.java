package p;
import java.util.ArrayList;
import java.util.function.BiPredicate;
public class Strategy {
    static public ArrayList<BiPredicate<Integer,Double[]>> buys() {
        ArrayList<BiPredicate<Integer,Double[]>> buys=new ArrayList<>();
        buys.add(buy0);
        buys.add(buy1);
        buys.add(buy2);
        return buys;
    }
    public static void main(String[] args) {}
    public static final BiPredicate<Integer,Double[]> buy0=(index,prices)-> { return true; };
    public static final BiPredicate<Integer,Double[]> buy1=(index,prices)-> {
        if(index<2) throw new RuntimeException("not enought prices for buy!");
        double p1=prices[index-2];
        double p2=prices[index-1];
        return p1<=p2;
    };
    public static final BiPredicate<Integer,Double[]> buy2=(index,prices)-> {
        if(index<3) throw new RuntimeException("not enought prices for buy!");
        double p1=prices[index-3];
        double p2=prices[index-2];
        double p3=prices[index-1];
        // add a check for some mimimum increase? 
        return p1<=p2&&p2<=p3;
    };
    public static final BiPredicate<Integer,Double[]> buy3=(index,prices)-> {
        if(index<4) throw new RuntimeException("not enought prices for buy!");
        double p1=prices[index-4];
        double p2=prices[index-3];
        double p3=prices[index-2];
        double p4=prices[index-1];
        return p1<=p2&&p2<=p3&&p3<=p4;
    };
    public static final BiPredicate<Integer,Double[]> buy4=(index,prices)-> {
        if(index<4) throw new RuntimeException("not enought prices for buy!");
        double p1=prices[index-4];
        double p2=prices[index-3];
        double p3=prices[index-2];
        double p4=prices[index-1];
        double dy2=p2-p1;
        double dy3=p3-p2;
        double dy4=p4-p3;
        double d2y3=dy3-dy2;
        double d2y4=dy4-dy3;
        double d3y4=d2y4-d2y3;
        return dy4>0&&d2y4>0; // maybe needs more constraints?
    };
}