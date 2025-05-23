import { createSlice } from "@reduxjs/toolkit";

const username = sessionStorage.getItem("username");
const initialState = {
  username: username || null,
  isAuthenticated: !!username,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    login: (state, action) => {
      const { username } = action.payload;
      if (username) {
        state.username = username;
        state.isAuthenticated = true;
        sessionStorage.setItem("username", username);
      } else {
        console.error("Error de validación", action.payload);
      }
    },
    logout: (state) => {
      state.username = null;
      state.isAuthenticated = false;
      sessionStorage.clear();
    },
  },
});

export const { login, logout } = authSlice.actions;
export default authSlice.reducer;
