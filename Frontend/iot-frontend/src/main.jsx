import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { ConfigProvider } from 'antd'
import plPL from 'antd/locale/pl_PL'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <ConfigProvider locale={plPL}>
      <App />
    </ConfigProvider>
  </StrictMode>,
)