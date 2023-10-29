made on 10/7/23 from /d/ray/allEclipseProjects/_javamiscellaneous/src/compoundinterest
old notes:

8/23/23 notices that bankroll and mean profit do not seem to correlate that much.
adding 2% cost of buying really hurts.
how much does it cost to sell?
8/29/23 make list of the top and bottom files.
figure out how to get the date.
deleted d/data/archivefrom kagle
8/31/23 using bankroll as a map key is very bad.
9/2/23  - maybe just use OHLC classes
since we want the dates.
9/6/23 maybe make a java version of: https://www.r-bloggers.com/2021/05/retrieving-stock-price-using-r/
as a sanity check?
/d/data/spy/indice  has 9 indices.
also try new download /d/data/stock_market_data
data are in

9/19/23 (approximately)
made csv reader red and convert the r files to get prices.
so doing play in r and checking with the java  code here.
10/5/23 got rid of the ohlc dependencies in Play.

10/6/23 consider moving the java stuff
10/8 moved the java stuff here. refactored into using an inner class.

new notes:

try to see if we can re-initialize a play instance. (probably not a good idea.)

10/25/23 not going so well. still having problems with r reading prices.
and java has problems reading the yahoosymbols.csv file in /d/data.
maybe not much point in this until i can read csv data easily. 

so  let's try: https://sourceforge.net/projects/opencsv/
this seems to work. looks like it reads yahoosymbols.csv just fine.

10/28/23 old csv reader code is gone, just using open csv now.
expected value of profit seems very low for high bankroolls, e.g.
bankroll files in /d/ray/rapps/getSymbols/data/bankroll.*.csv are old.
probably made with buy1 or buy2: 

"symbol","bankroll","winRate","buyRate"
"AMTD","1.1996","0.30769","0.1554"

the buy* files that this java version makes have more info:

exchange, name, bankroll, eProfit, sdProfit, pptd, winRate, buyRate, days, hProfit
 Smith & Turner P.L.C.",      54GW.L.csv, 988380.045,  2.54, 15.68,   2.273,  0.03,   0.896,  260
TLV       ,     VISN.TA.csv, 12207.343,  4.33, 20.34,   3.401,  0.15,   0.785,  260
LSE       ,      BMTO.L.csv, 10030.237,  2.29, 14.92,   1.892,  0.04,   0.827,  260
TLV      

only 1670 price files out of 100k stocks. seems strange. investigate.











