import "./App.css";
import Footer from "./layout/Footer";
import Nav from "./layout/Nav";
import Main from "./layout/Main";
import useStockUpdater from "./hooks/useStockUpdater";
import useWatchlistUpdater from "./hooks/useWatchlistUpdater";

function App() {
  useStockUpdater();
  useWatchlistUpdater();
  return (
    <>
      <Nav />
      <Main className="min-h-[100vh]" />
      <Footer />
    </>
  );
}

export default App;
