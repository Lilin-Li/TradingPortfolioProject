1. How to run?  
   com.example.trading.Main.main()  

2. In com.example.trading.Main.main(), the main objects and structures are established, described as follows:  
   . DatabaseInitializer: Initialize H2 DB, create table and static data  
   . MarketDataProvider: Generate the latest stock price based on geometric Brownian motion and send it to the Queue.  
   . PortfolioService: Read user portfolios from CSV and listen for stock price updates on the Queue, rendering the calculated stocks/options profit and loss.  
   . Use BlockQueue to simulate real thread cooperation, and use ArrayBlockingQueue to avoid GC affecting performance  
  
3. Calculation formula  
   . geometric Brownian motion : com.example.trading.service.MarketDataProvider.run    
   . European call option’s price : com.example.trading.service.PricingService.calculateCallOptionPrice  
   . European put option’s price : com.example.trading.service.PricingService.calculatePutOptionPrice  
   . The function N(x) uses the approximation method of Abramowitz and Stegun: com.example.trading.service.PricingService.cnd  
   . The above formulas use double instead of BigDecimal for convenience in review, so there are precision errors  
