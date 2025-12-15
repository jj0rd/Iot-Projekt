import { useState } from 'react';
import Login from "../src/pages/Login/Login"
import './App.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(
    () => !!localStorage.getItem('accessToken')
  );

  const handleLoginSuccess = () => {
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  };

  return (
    <div className="app">
      {!isAuthenticated ? (
        <Login onLoginSuccess={handleLoginSuccess} />
      ) : (
        <div className="dashboard-container">
          <div className="dashboard-card">
            <h1>Witaj w IoT Application! 🎉</h1>
            <p>Zostałeś pomyślnie zalogowany.</p>
            <button className="logout-button" onClick={handleLogout}>
              Wyloguj się
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;