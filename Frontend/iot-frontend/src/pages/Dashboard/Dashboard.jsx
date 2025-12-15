import { useEffect, useState } from 'react';
import { Layout, Menu, Card, Row, Col, Typography, message } from 'antd';
import './Dashboard.css';

const { Header, Content } = Layout;
const { Title, Text } = Typography;

const API_URL = 'http://localhost:8080/devices';
const API_LOGOUT = 'http://localhost:8080/api/auth';


export default function Dashboard() {
  const [devices, setDevices] = useState([]);
  const [temperatures, setTemperatures] = useState({});

  const accessToken = localStorage.getItem('accessToken');

  useEffect(() => {
    fetchDevices();
  }, []);
  useEffect(() => {
  if (devices.length === 0) return;

  const interval = setInterval(() => {
    devices.forEach(device => {
      fetchTemperature(device.id);
    });
  }, 5000); // 5 sekund

  return () => clearInterval(interval);
}, [devices]);

const handleLogout = async () => {
  const accessToken = localStorage.getItem('accessToken');

  if (!accessToken) {
    message.warning('Nie jesteś zalogowany');
    return;
  }

  try {
    const response = await fetch(`${API_LOGOUT}/logout`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      throw new Error(await response.text());
    }

    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');

    message.success('Wylogowano pomyślnie');

    // przekierowanie
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
    } catch (e) {
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
        setTemperatures(prev => ({
          ...prev,
          [deviceId]: data[0],
        }));
      }
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <Layout className="dashboard-layout">
      <Header className="dashboard-header">
            <Menu
                theme="dark"
                mode="horizontal"
                onClick={({ key }) => {
                if (key === 'logout') {
                    handleLogout();
                }
                }}
                items={[
                { key: 'menu', label: 'Menu' },
                { key: 'l1', label: 'L1' },
                { key: 'l2', label: 'L2' },
                { key: 'l3', label: 'L3' },
                { key: 'l4', label: 'L4' },
                { key: 'logout', label: 'Wyloguj się' }, // 🔥
                ]}
            />
            </Header>

      <Content className="dashboard-content">
        <Row gutter={[24, 24]}>
          {devices.map((device, index) => (
            <Col xs={24} md={12} key={device.id}>
              <Card className="sensor-card" title={`L${index + 1} – ${device.name}`}>
                <Title level={1} className="sensor-value">
                  {temperatures[device.id]?.value?.toFixed(1) ?? '--'}°C
                </Title>
                <Text type="secondary">{device.location}</Text>
              </Card>
            </Col>
          ))}
        </Row>
      </Content>
    </Layout>
  );
}
