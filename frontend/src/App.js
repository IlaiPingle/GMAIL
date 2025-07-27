import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './pages/MainLayout';
import MailPage from './pages/mailPage';
import Compose from './components/compose/Compose';
import CreateLabel from './components/createLabel/CreateLabel';
import Registration from './components/registration/registration';
import Login from './components/login/login';
import './App.css';
import MailList from './components/MailList';
import './App.css';

function App() {
  return (
    <Router>
      <Routes>
          <Route path="/" element={<Navigate to="/login" replace />} /> 
          <Route path="/register" element={<Registration />} />
          <Route path="/login" element={<Login />} />
          <Route index element={<MailList label="inbox" />} />
          <Route path=":boxType" element={<MailList />} />
          <Route path="label/:boxType" element={<MailList />} />
          <Route path=":boxType/:mailId" element={<MailPage />} />
          <Route path="label/:boxType/:mailId" element={<MailPage />} />
          <Route path="/search" element={<MailList />} />
          <Route path="/compose" element={<Compose />} />
          <Route path="/create-label" element={<CreateLabel />} />
      </Routes>
    </Router>
  );
}

export default App;
