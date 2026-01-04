import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Typography, message, Card, Table } from 'antd';
import AppLayout from '../../components/AppLayout';

const { Title } = Typography;
const API_URL = 'http://localhost:8080/devices';

export default function DeviceTemperature() {
  const { id } = useParams(); // ID urządzenia z URL
  const [temperatures, setTemperatures] = useState([]);
  const accessToken = localStorage.getItem('accessToken');

  useEffect(() => {
    fetchTemperatureHistory();
  }, [id]);

  const fetchTemperatureHistory = async () => {
    try {
      const res = await fetch(`${API_URL}/${id}/temperature-history`, {
        headers: { Authorization: `Bearer ${accessToken}` },
      });
      if (!res.ok) throw new Error('Błąd pobierania historii temperatury');
      const data = await res.json();
      setTemperatures(data);
    } catch (err) {
      console.error(err);
      message.error('Nie udało się pobrać historii temperatury');
    }
  };

  const columns = [
    { title: 'Data i czas', dataIndex: 'timestamp', key: 'timestamp', 
      render: ts => new Date(ts).toLocaleString() 
    },
    { title: 'Temperatura (°C)', dataIndex: 'value', key: 'value' }
  ];

  return (
    <AppLayout>
      <Card style={{ maxWidth: 800, margin: '0 auto' }}>
        <Title level={2}>Historia temperatury – Urządzenie {id}</Title>
        <Table
          style={{ marginTop: 20 }}
          rowKey={(record, index) => index}
          columns={columns}
          dataSource={temperatures}
          pagination={{ pageSize: 10 }}
        />
      </Card>
    </AppLayout>
  );
}
