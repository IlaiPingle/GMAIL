import React, { Component } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import MainLayout from './pages/MainLayout';
import MailPage from './pages/mailPage';
import Compose from './components/compose/Compose';
import CreateLabel from './components/createLabel/CreateLabel';
import MailList from './components/MailList';
import './App.css';

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
          <Route path="/" element={<MainLayout />}> 
            <Route index element={<MailList label="inbox" />} />
            <Route path=":boxType" element={<MailList />} />
            <Route path=":boxType/:mailId" element={<MailPage />} />
            <Route path="/compose" element={<Compose />} />
            <Route path="/create-label" element={<CreateLabel />} />
          </Route>
        </Routes>
      </Router>
    );
  }
}

export default App;
