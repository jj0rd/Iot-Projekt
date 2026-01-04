// src/components/AppLayout.jsx
import { Layout, Menu } from 'antd';
import { useNavigate } from 'react-router-dom';

const { Header, Content } = Layout;

export default function AppLayout({ children }) {
  const navigate = useNavigate();

  const handleLogout = () => {
    // tutaj tylko przekierowanie, prawdziwy logout zostaw w komponentach
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    navigate('/login');
  };

  return (
    <Layout className="dashboard-layout">
      <Header className="dashboard-header">
        <Menu
          theme="dark"
          mode="horizontal"
          onClick={({ key }) => {
            if (key === 'dashboard') navigate('/dashboard');
            if (key === 'logs') navigate('/logs');
            if (key === 'logout') handleLogout();
          }}
          items={[
            { key: 'dashboard', label: 'Dashboard' },
            { key: 'logs', label: 'Logs' },
            { key: 't1', label: 'T1', onClick: () => navigate('/devices/1') },
            { key: 't2', label: 'T2', onClick: () => navigate('/devices/2') },
            { key: 't3', label: 'T3', onClick: () => navigate('/devices/3') },
            { key: 'logout', label: 'Wyloguj się' },
          ]}
        />
      </Header>

      <Content className="dashboard-content" style={{ padding: '24px' }}>
        {children}
      </Content>
    </Layout>
  );
}
