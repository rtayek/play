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







