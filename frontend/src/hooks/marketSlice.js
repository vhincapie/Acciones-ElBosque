import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  stocks: [],
  stockHistory: {},
  marketStatus: null,
  lastUpdated: null,
};

const marketSlice = createSlice({
  name: "market",
  initialState,
  reducers: {
    setStocks(state, action) {
      const now = Date.now();
      state.stocks = action.payload;
      state.lastUpdated = new Date().toISOString();

      action.payload.forEach((stock) => {
        const dataPoint = { price: stock.currentPrice, timestamp: now };
        if (!state.stockHistory[stock.symbol]) {
          state.stockHistory[stock.symbol] = [];
        }
        state.stockHistory[stock.symbol].push(dataPoint);
      });
    },
    setMarketStatus(state, action) {
      state.marketStatus = action.payload;
    },
    clearStocks(state) {
      state.stocks = [];
      state.stockHistory = {};
      state.lastUpdated = null;
    },
  },
});

export const { setStocks, setMarketStatus, clearStocks } = marketSlice.actions;
export default marketSlice.reducer;
