made on 10/7/23 from /d/ray/allEclipseProjects/_javamiscellaneous/src/compoundinterest
source of stock data: https://investexcel.net/all-yahoo-finance-stock-tickers/
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

maybe try to construct play earlier and set prices later?
made prices non final, so we can do this since 
made prices transient and remove prices from constructor.
is this what we wan to do to move the constructor calls up?

prices is only needed in when we call oneStock and afterwards.
so maybe set if before calling prices? - we are doing that. 
we don't need the prices until we call oneStock.
true, but maybe it  does not matter unless we really want to run more than one stock.
maybe just have some call One?

somehow got some nans from buy4 (i think). can not reproduce.

stopped adding run results if there were no buys.

what to do next. use the few hundred that are in buy3b?

got r code to make new price files. the are in DataPaths: Path newPrices=Path.of("e:\\data");
we only have 1538 new prices, but r is making the rest. so we can use these now.

we need to add a date to the data frame.
maybe include the date in the filename.
also maybe add the buy strategy to the data frame. 
also, add the change in value over the prices to the data frame.

consolidate these:
	System.out.println("to csv:  "+play.toCSVLine());
	System.out.println("toString2: "+play.toString2());
	System.out.println("toString; "+play.toString());
	System.out.println("csv result; "+result.toString(play.arguments()));

added ticker to Play, so we now have ticker and filename. 
added  toString to csv class.
got oCSVLine()) and result to be (almost) identical.
fixed bug in sort logic. added random with very small variance instead of elapsed time.

added change, date, and buy.

add number of buys to csv.
changed change() to be new/old as opposed to (new-old)/old.

curreny loop is:
	foreach strategy
		foreach file
			do one time period.
			
change to
	foreach file
		foreach time period
			foreach strategy
				do one stock
				
done, but inner most loop has only one stock run in it
how/ where to put the maps?
maybe in the code loops instead of the class?
not sure.

incorporate code from Time into Plays.

made deep equals for .csv data. working on time periods.
				
working on moving date code up in Plays.main();	
seems to work.
we have 106329stocks in the yahoo file.
we got prices data from 15730 of them
9168 of the stocks have some problem getting the price data.
about 500-600  of them return more than 1.2.

consider a loop like  readyahoo.R that starts with say 500.
and then loop through the time periods, add in the highest values.

probably should write some tests to see if this agrees with the r code.

maybe make dates start a the beginning of the year.

added  timePeriodIndices. seems to work with both with date and indices time periods!

made Unique class to get unique stocks from a buy all file.
maybe make a CSV instance for the yahoo.csv file format.

refactored some of the csv utilities. started a list of excluded stocks.

should we exclude stocks when building the map or require later code to avoid them?

let's just use nyq and nms exchanges.
experiment with other strategies. predict with a curve fit or finite differences.
require a certain amount of positive delta?

remove exchange from my output .csv files.

see how these work over time

maybe set this up so it takes a collection of strategies and time periods as well as the collection of files.
maybe add histogram  of high-low to indicate variance?

high variance over years for each stock.
maybe do time by quarter?
11/09/23
maybe extrapolate using finite differences.
made forward differences and a test case for it.

find a really good year and see  what happens when it goes bad.
hard to say each quarter can vary a lot.
so try to make better buy strategies.

maybe always do predictions?

12/02/23 moved to new machine. trying nms exchanges.






