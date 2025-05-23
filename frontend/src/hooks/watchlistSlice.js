import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { watchlistApi } from "./watchlistApi";
import { fetchMarketStatus } from "./stocks";

const initialState = {
  watchlist: [],
  watchlistHistory: {},
  marketStatus: null,
  lastUpdated: null,
  loading: false,
  error: null,
};

// 🎯 Async thunk para cargar watchlist
export const fetchWatchlist = createAsyncThunk(
  "watchlist/fetchWatchlist",
  async (_, { rejectWithValue }) => {
    try {
      const response = await watchlistApi.getAll();
      return response.data;
    } catch (err) {
      return rejectWithValue("No se pudo cargar tu Watchlist.");
    }
  }
);

// 🎯 Async thunk para cargar estado del mercado
export const fetchMarketStatusThunk = createAsyncThunk(
  "watchlist/fetchMarketStatus",
  async (_, { rejectWithValue }) => {
    try {
      const estado = await fetchMarketStatus();
      return estado;
    } catch {
      return rejectWithValue("Error al obtener el estado del mercado.");
    }
  }
);

const watchlistSlice = createSlice({
  name: "watchlist",
  initialState,
  reducers: {
    clearWatchlist(state) {
      state.watchlist = [];
      state.watchlistHistory = {};
      state.lastUpdated = null;
      state.error = null;
      state.loading = false;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchWatchlist.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchWatchlist.fulfilled, (state, action) => {
        const now = Date.now();
        state.watchlist = action.payload;
        state.lastUpdated = new Date().toISOString();
        state.loading = false;
        state.error = null;

        action.payload.forEach((stock) => {
          const dataPoint = { price: stock.currentPrice, timestamp: now };
          if (!state.watchlistHistory[stock.symbol]) {
            state.watchlistHistory[stock.symbol] = [];
          }
          state.watchlistHistory[stock.symbol].push(dataPoint);
        });
      })
      .addCase(fetchWatchlist.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(fetchMarketStatusThunk.fulfilled, (state, action) => {
        state.marketStatus = action.payload;
      });
  },
});

export const { clearWatchlist } = watchlistSlice.actions;

export default watchlistSlice.reducer;
