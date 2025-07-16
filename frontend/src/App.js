import React, { Component } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import MainLayout from './pages/MainLayout';
import InboxPage from './pages/InboxPage';
import MailPage from './pages/mailPage';
import Compose from './components/compose/Compose';
import CreateLabel from './components/createLabel/CreateLabel';
import './App.css';

class App extends Component {
  render() {
    return (
      <Router>
        <div className="App">
          <MainLayout >
            <Routes>
              <Route path="/" element={<InboxPage />} />
              <Route path="/mail/:id" element={<MailPage />} />
              <Route path="/compose" element={<Compose />} />
              <Route path="/create-label" element={<CreateLabel />} />
            </Routes>
          </MainLayout>
        </div>
      </Router>
    );
  }
}

export default App;
