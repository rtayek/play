package p;
import java.util.Arrays;
class ForwardDifferences {
    //calculating u mentioned in the formula
    static double u_cal(double u,int n) { double temp=u; for(int i=1;i<n;i++) temp=temp*(u-i); return temp; }
    //calculating factorial of given number n
    static int fact(int n) { int f=1; for(int i=2;i<=n;i++) f*=i; return f; }
    static void print(int n,double[] x,double[][] y) {
        for(int i=0;i<n;i++) {
            System.out.print(x[i]+"\t");
            for(int j=0;j<n-i;j++) System.out.print(y[i][j]+"\t");
            System.out.println();
        }
    }
    static double[][] buildDifferences(int n,Double[] x,Double[] y) {
        double dy[][]=new double[n][n];
        for(int i=0;i<n;i++) dy[i][0]=y[i];
        for(int i=1;i<n;i++) { for(int j=0;j<n-i;j++) dy[j][i]=dy[j+1][i-1]-dy[j][i-1]; }
        // Displaying the forward difference table
        //print(n,x,y);
        return dy;
    }
    static double extrapolate(double value,int n,Double[] x,Double[] y) {
        double[][] yy=buildDifferences(n,x,y);
        double sum=yy[0][0];
        double u=(value-x[0])/(x[1]-x[0]);
        for(int i=1;i<n;i++) { sum=sum+(u_cal(u,i)*yy[0][i])/fact(i); }
        return sum;
    }
    static double cubic(double x) { double rc=(x*x*x+3*x*x-6*x-8)/4; return rc; }
    static double d1(double x) {
        {
            double rc=(3*x*x+6*x-6)/4;
            return rc;
        }
    }
    static double d2(double x) {
        {
            double rc=(3*x+6)/4;
            return rc;
        }
    }
    public static void main(String[] args) {
        System.out.println(cubic(-4));
        System.out.println(cubic(-1));
        System.out.println(cubic(2));
        System.out.println("-----------");
        double r1=-1+Math.sqrt(3);
        double r2=-1-Math.sqrt(3);
        System.out.println(d1(r1));
        System.out.println(d1(r2));
        System.out.println("-----------");
        double r=-2;
        System.out.println(d2(r));
        System.out.println("-----------");
        int n=5; // makes dx's 1/2
        Double[] x=new Double[n];
        for(int i=0;i<n;++i) x[i]=0+i*2./(n-1); // 0-2
        for(int i=0;i<n;++i) System.out.println(x[i]);
        Double[] y=new Double[n];
        for(int i=0;i<n;++i) y[i]=cubic(x[i]);
        for(int i=0;i<n;++i) System.out.println(y[i]);
        System.out.println("-----------");
        System.out.println("x:"+Arrays.asList(x));
        System.out.println("y:"+Arrays.asList(y));
        double value=x[n-1]+2./(n-1); // one time step into the future
        double z=extrapolate(value,n,x,y);
        System.out.println("extrapolated value: y("+value+")="+z);
        System.out.println("cubic value: "+cubic(value));
        System.out.println("-----------");
        value=x[n-2]; // one time step into the past
        z=extrapolate(value,n,x,y);
        System.out.println("extrapolated value: y("+value+")="+z);
        System.out.println("cubic value: "+cubic(value));
    }
}
