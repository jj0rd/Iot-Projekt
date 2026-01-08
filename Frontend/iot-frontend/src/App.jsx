import { BrowserRouter, Routes, Route,Navigate } from 'react-router-dom';
import Login from './pages/Login/Login';
import Dashboard from './pages/Dashboard/Dashboard';
import Logi from './pages/Logi/Logi';
import DeviceTemperature from './pages/DeviceTemperature/DeviceTemperature';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />
         <Route path="/logs" element={<Logi />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/devices/:id" element={<DeviceTemperature />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
