import { useEffect, useState } from 'react';
import { Table, message, Typography } from 'antd';
import AppLayout from '../../components/AppLayout';

const { Title } = Typography;

const API_LOGS = 'http://localhost:8080/logs';


export default function Logs() {
  const [logs, setLogs] = useState([]);
  const accessToken = localStorage.getItem('accessToken');

  useEffect(() => { fetchLogs(); }, []);

  const fetchLogs = async () => {
    try {
      const res = await fetch(API_LOGS, {
        headers: { Authorization: `Bearer ${accessToken}` },
      });
      if (!res.ok) throw new Error('Błąd pobierania logów');
      const data = await res.json();
      setLogs(data);
    } catch (err) {
      console.error(err);
      message.error('Nie udało się pobrać logów');
    }
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id' },
    { title: 'User', dataIndex: 'username', key: 'username' },
    { title: 'Action', dataIndex: 'action', key: 'action' },
    { title: 'Details', dataIndex: 'details', key: 'details' },
    {
      title: 'Timestamp',
      dataIndex: 'timestamp',
      key: 'timestamp',
      render: ts => new Date(ts).toLocaleString(),
    },
  ];

  return (
    <AppLayout>
      <Title level={2}>Server Logs</Title>
      <Table
        rowKey="id"
        columns={columns}
        dataSource={logs}
        pagination={{ pageSize: 10 }}
      />
    </AppLayout>
  );
}
