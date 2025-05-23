// redux/store.js
import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./authSlice";
import marketReducer from "./marketSlice";
import watchlistReducer from "./watchlistSlice";

import {
  persistStore,
  persistReducer,
  FLUSH,
  REHYDRATE,
  PAUSE,
  PERSIST,
  PURGE,
  REGISTER,
} from "redux-persist";
import storage from "redux-persist/lib/storage";

// Persistencia para autenticación
const authPersistConfig = {
  key: "auth",
  storage,
};

// Persistencia para mercado
const marketPersistConfig = {
  key: "market",
  storage,
};

// Persistencia para watchlist
const watchlistPersistConfig = {
  key: "watchlist",
  storage,
};

// Reducers persistidos
const persistedAuthReducer = persistReducer(authPersistConfig, authReducer);
const persistedMarketReducer = persistReducer(
  marketPersistConfig,
  marketReducer
);
const persistedWatchlistReducer = persistReducer(
  watchlistPersistConfig,
  watchlistReducer
);

// Configurar store
const store = configureStore({
  reducer: {
    auth: persistedAuthReducer,
    market: persistedMarketReducer,
    watchlist: persistedWatchlistReducer, // ⬅ aquí se agrega
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
      },
    }),
});

export const persistor = persistStore(store);
export default store;
