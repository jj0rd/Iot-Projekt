import { useState } from 'react';
import { Input, Button, Card, Tabs, message, Space } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import './Login.css';

const API_URL = 'http://localhost:8080/api/auth';

function Login() {
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState('login');
  
  // Login state
  const [loginUsername, setLoginUsername] = useState('');
  const [loginPassword, setLoginPassword] = useState('');
  const [loginErrors, setLoginErrors] = useState({});
  
  // Register state
  const [regUsername, setRegUsername] = useState('');
  const [regPassword, setRegPassword] = useState('');
  const [regConfirmPassword, setRegConfirmPassword] = useState('');
  const [regErrors, setRegErrors] = useState({});

  const validateLogin = () => {
    const errors = {};
    if (!loginUsername) errors.username = 'Proszę wprowadzić nazwę użytkownika!';
    if (!loginPassword) errors.password = 'Proszę wprowadzić hasło!';
    setLoginErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const validateRegister = () => {
    const errors = {};
    if (!regUsername) errors.username = 'Proszę wprowadzić nazwę użytkownika!';
    else if (regUsername.length < 3) errors.username = 'Nazwa użytkownika musi mieć minimum 3 znaki!';
    
    if (!regPassword) errors.password = 'Proszę wprowadzić hasło!';
    else if (regPassword.length < 6) errors.password = 'Hasło musi mieć minimum 6 znaków!';
    
    if (!regConfirmPassword) errors.confirmPassword = 'Proszę potwierdzić hasło!';
    else if (regPassword !== regConfirmPassword) errors.confirmPassword = 'Hasła nie są identyczne!';
    
    setRegErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleLogin = async () => {
    if (!validateLogin()) return;
    
    setLoading(true);
    try {
      const response = await fetch(`${API_URL}/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: loginUsername,
          password: loginPassword,
        }),
      });

      if (response.ok) {
        const data = await response.json();
        localStorage.setItem('accessToken', data.accessToken);
        localStorage.setItem('refreshToken', data.refreshToken);
        message.success('Zalogowano pomyślnie!');
        setLoginUsername('');
        setLoginPassword('');
        setLoginErrors({});
      } else {
        const errorText = await response.text();
        message.error(errorText || 'Błąd logowania');
      }
    } catch (error) {
      message.error('Błąd połączenia z serwerem');
      console.error('Login error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async () => {
    if (!validateRegister()) return;
    
    setLoading(true);
    try {
      const response = await fetch(`${API_URL}/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: regUsername,
          password: regPassword,
        }),
      });

      if (response.ok) {
        message.success('Rejestracja zakończona sukcesem!');
        setRegUsername('');
        setRegPassword('');
        setRegConfirmPassword('');
        setRegErrors({});
        setActiveTab('login');
      } else {
        const errorText = await response.text();
        message.error(errorText || 'Błąd rejestracji');
      }
    } catch (error) {
      message.error('Błąd połączenia z serwerem');
      console.error('Register error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
  const accessToken = localStorage.getItem('accessToken');

  if (!accessToken) {
    message.warning('Nie jesteś zalogowany');
    return;
  }

  try {
    const response = await fetch(`${API_URL}/logout`, {
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

    // ⬇️ przekierowanie
    window.location.href = '/login';

  } catch (err) {
    console.error(err);
    message.error('Błąd wylogowania');
  }
};


  const loginTab = (
    <div>
      <div className="form-item">
        <Input
          prefix={<UserOutlined />}
          placeholder="Nazwa użytkownika"
          size="large"
          value={loginUsername}
          onChange={(e) => {
            setLoginUsername(e.target.value);
            setLoginErrors({...loginErrors, username: ''});
          }}
          status={loginErrors.username ? 'error' : ''}
        />
        {loginErrors.username && (
          <div className="error-message">
            {loginErrors.username}
          </div>
        )}
      </div>

      <div className="form-item">
        <Input.Password
          prefix={<LockOutlined />}
          placeholder="Hasło"
          size="large"
          value={loginPassword}
          onChange={(e) => {
            setLoginPassword(e.target.value);
            setLoginErrors({...loginErrors, password: ''});
          }}
          onPressEnter={handleLogin}
          status={loginErrors.password ? 'error' : ''}
        />
        {loginErrors.password && (
          <div className="error-message">
            {loginErrors.password}
          </div>
        )}
      </div>

      <Button
        type="primary"
        loading={loading}
        block
        size="large"
        onClick={handleLogin}
      >
        Zaloguj się
      </Button>
    </div>
  );

  const registerTab = (
    <div>
      <div className="form-item">
        <Input
          prefix={<UserOutlined />}
          placeholder="Nazwa użytkownika"
          size="large"
          value={regUsername}
          onChange={(e) => {
            setRegUsername(e.target.value);
            setRegErrors({...regErrors, username: ''});
          }}
          status={regErrors.username ? 'error' : ''}
        />
        {regErrors.username && (
          <div className="error-message">
            {regErrors.username}
          </div>
        )}
      </div>

      <div className="form-item">
        <Input.Password
          prefix={<LockOutlined />}
          placeholder="Hasło"
          size="large"
          value={regPassword}
          onChange={(e) => {
            setRegPassword(e.target.value);
            setRegErrors({...regErrors, password: ''});
          }}
          status={regErrors.password ? 'error' : ''}
        />
        {regErrors.password && (
          <div className="error-message">
            {regErrors.password}
          </div>
        )}
      </div>

      <div className="form-item">
        <Input.Password
          prefix={<LockOutlined />}
          placeholder="Potwierdź hasło"
          size="large"
          value={regConfirmPassword}
          onChange={(e) => {
            setRegConfirmPassword(e.target.value);
            setRegErrors({...regErrors, confirmPassword: ''});
          }}
          onPressEnter={handleRegister}
          status={regErrors.confirmPassword ? 'error' : ''}
        />
        {regErrors.confirmPassword && (
          <div className="error-message">
            {regErrors.confirmPassword}
          </div>
        )}
      </div>

      <Button
        type="primary"
        loading={loading}
        block
        size="large"
        onClick={handleRegister}
      >
        Zarejestruj się
      </Button>
    </div>
  );

  const tabItems = [
    {
      key: 'login',
      label: 'Logowanie',
      children: loginTab,
    },
    {
      key: 'register',
      label: 'Rejestracja',
      children: registerTab,
    },
  ];

  return (
    <div className="auth-container">
      <Card className="auth-card">
        <div className="auth-header">
          <h1 className="auth-title">IoT Application</h1>
          <p className="auth-subtitle">
            Zaloguj się lub utwórz nowe konto
          </p>
        </div>

        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          items={tabItems}
          centered
        />

        {localStorage.getItem('accessToken') && (
          <div className="logout-section">
            <Space>
              <Button onClick={handleLogout} danger>
                Wyloguj się
              </Button>
            </Space>
          </div>
        )}
      </Card>
    </div>
  );
}

export default Login;