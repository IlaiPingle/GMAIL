import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/header/Header';
import SideBar from './components/sideBar/SideBar';
import InboxPage from './pages/InboxPage';
import MailPage from './pages/mailPage';
import Compose from './components/compose/Compose';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        <Header />
        <div className="app-body">
          <SideBar />
          <Routes>
            <Route path="/" element={<InboxPage />} />
            <Route path="/mail/:id" element={<MailPage />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
