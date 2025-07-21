import React, { Component } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './pages/MainLayout';
import MailPage from './pages/mailPage';
import Compose from './components/compose/Compose';
import CreateLabel from './components/createLabel/CreateLabel';
import Registration from './components/registration/registration';
import './App.css';
import MailList from './components/MailList';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchResults: null,
    };
  }
  render() {
    return (
      <Router>
        <Routes>
		  <Route path="/" element={<Navigate to="/register" replace />} />

		  <Route path="/register" element={<Registration />} />

          <Route path="/" element={<MainLayout />}> 
            <Route path="inbox" element={<MailList label="inbox" />} />
            <Route path="sent" element={<MailList label="sent" />} />
            <Route path="drafts" element={<MailList label="drafts" />} />
            <Route path="bin" element={<MailList label="bin" />} />
            <Route path="archive" element={<MailList label="archive" />} />
            <Route path="starred" element={<MailList label="starred" />} />
            <Route path="label/:labelName" element={<MailList />} />
            <Route path="/mail/:id" element={<MailPage />} />              
            <Route path="/compose=new" element={<Compose />} />
            <Route path="/create-label" element={<CreateLabel />} />
          </Route>
        </Routes>
      </Router>
    );
  }
}

export default App;
