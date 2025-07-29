import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import MainLayout from './pages/MainLayout';
import MailPage from './pages/mailPage';
import Compose from './components/compose/Compose';
import CreateLabel from './components/createLabel/CreateLabel';
import Registration from './components/registration/registration';
import Login from './components/login/login';
import MailList from './components/MailList';
import ProtectedRoute from './services/ProtectedRoute';
import './App.css';


function App() {
  return (
    <Router>
      <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Registration />} />
          <Route path="/" element={
            <ProtectedRoute>
              <MainLayout />
            </ProtectedRoute>
          }>
            <Route index element={<MailList label="inbox" />} />
            <Route path=":boxType" element={<MailList />} />
            <Route path="label/:boxType" element={<MailList />} />
            <Route path=":boxType/:mailId" element={<MailPage />} />
            <Route path="label/:boxType/:mailId" element={<MailPage />} />
            <Route path="search" element={<MailList />} />
            <Route path="compose" element={<Compose />} />
            <Route path="create-label" element={<CreateLabel />} />
          </Route>
      </Routes>
    </Router>
  );
}

export default App;
