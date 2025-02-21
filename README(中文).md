1. How to run ?
   -> com.example.trading.Main.main()

2. 在com.example.trading.Main.main()中，建立了主要物件及結構，描述如下:
    . DatabaseInitializer: 初始化 H2 DB，建立table 與靜態數據
    . MarketDataProvider: 根據 geometric Brownian motion 生成最新股價，發送至Queue
    . PortfolioService: 從CSV讀取用戶投資組合，並監聽Queue上的股價更新，渲染計算後的 stocks/ options損益
    . 使用BlockQueue模擬真實線程合作，並使用ArrayBlockingQueue，避免GC影響性能

3. 計算公式
   . geometric Brownian motion : com.example.trading.service.MarketDataProvider.run
   . European call option’s price : com.example.trading.service.PricingService.calculateCallOptionPrice
   . European put option’s price : com.example.trading.service.PricingService.calculatePutOptionPrice
   . 函数 𝑁(𝑥) 使用 Abramowitz 和 Stegun 的近似方法 : com.example.trading.service.PricingService.cnd
   . 以上公式為方便review，均使用 double 而非 BigDecimal，因此有精度誤差
