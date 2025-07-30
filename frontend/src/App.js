import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import MainLayout from './pages/MainLayout';
import MailPage from './pages/mailPage';
import Compose from './components/compose/Compose';
import CreateLabel from './components/createLabel/CreateLabel';
import Registration from './components/auth/registration/registration';
import Login from './components/auth/login/login';
import MailList from './pages/MailList';
import ProtectedRoute from './services/ProtectedRoute';
import './App.css';
import { UserProvider } from './contexts/UserContext'; // Assuming you have a UserContext for user state management
import { ThemeProvider } from './contexts/ThemeContext';

function App() {
  return (
    <ThemeProvider>
      <UserProvider>
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
    </UserProvider>
  </ThemeProvider>
  );
}

export default App;
