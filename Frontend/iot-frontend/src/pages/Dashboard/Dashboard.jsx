import { useEffect, useState } from 'react';
import { Card, Row, Col, Typography, message } from 'antd';
import AppLayout from '../../components/AppLayout'; // import wspólnego layoutu
import './Dashboard.css';

const { Title, Text } = Typography;

const API_URL = 'http://localhost:8080/devices';
const API_LOGOUT = 'http://localhost:8080/api/auth';

export default function Dashboard() {
  const [devices, setDevices] = useState([]);
  const [temperatures, setTemperatures] = useState({});
  const accessToken = localStorage.getItem('accessToken');

  useEffect(() => { fetchDevices(); }, []);

  useEffect(() => {
    if (devices.length === 0) return;

    const interval = setInterval(() => {
      devices.forEach(device => fetchTemperature(device.id));
    }, 5000);

    return () => clearInterval(interval);
  }, [devices]);

  const handleLogout = async () => {
    if (!accessToken) {
      message.warning('Nie jesteś zalogowany');
      return;
    }

    try {
      const response = await fetch(`${API_LOGOUT}/logout`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${accessToken}` },
      });

      if (!response.ok) {
        throw new Error(await response.text());
      }

      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      message.success('Wylogowano pomyślnie');
      window.location.href = '/login';
    } catch (err) {
      console.error(err);
      message.error('Błąd wylogowania');
    }
  };

  const fetchDevices = async () => {
    try {
      const res = await fetch(`${API_URL}/getAllDevices`, {
        headers: { Authorization: `Bearer ${accessToken}` },
      });
      const data = await res.json();
      setDevices(data.slice(0, 4));
      data.slice(0, 4).forEach(d => fetchTemperature(d.id));
    } catch {
      message.error('Błąd pobierania urządzeń');
    }
  };

  const fetchTemperature = async (deviceId) => {
    try {
      const res = await fetch(`${API_URL}/${deviceId}/temperatures`, {
        headers: { Authorization: `Bearer ${accessToken}` },
      });
      const data = await res.json();
      if (data.length > 0) {
        setTemperatures(prev => ({ ...prev, [deviceId]: data[0] }));
      }
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <AppLayout onLogout={handleLogout}>
      <Row gutter={[24, 24]}>
        {devices.map((device, index) => (
          <Col xs={24} md={12} key={device.id}>
            <Card title={`L${index + 1} – ${device.name}`} className="sensor-card">
              <Title level={1} className="sensor-value">
                {temperatures[device.id]?.value?.toFixed(1) ?? '--'}°C
              </Title>
              <Text type="secondary">{device.location}</Text>
            </Card>
          </Col>
        ))}
      </Row>
    </AppLayout>
  );
}
