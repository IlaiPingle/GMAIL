import React, { Component } from 'react';
import { useParams } from 'react-router-dom';
import './mailPage.css';
import Client from '../services/Client';

// HOC to get params in class component
function withParams(Component) {
  return function ComponentWithParams(props) {
    const params = useParams();
    return <Component {...props} params={params} />;
  };
}

class MailPage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      mail: null,
      error: null
    };
  }

  fetchMail = async () => {
    const { id } = this.props.params;
    try {
      const response = await Client.getMailById(id);
      this.setState({ mail: response });
    } catch (err) {
      this.setState({ error: err.message });
    }
  };

  componentDidMount() {
    this.fetchMail();
  }

  componentDidUpdate(prevProps) {
    if (prevProps.params.id !== this.props.params.id) {
      this.fetchMail();
    }
  }

  render() {
    const { mail, error } = this.state;

    if (error) {
      return <div className="error">Error: {error}</div>;
    }

    if (!mail) {
      return <div className="loading">Loading...</div>;
    }

    return (
      <div className="mail-page">
        <h1>{mail.subject}</h1>
        <p><strong>From:</strong> {mail.sender}</p>
        <p><strong>To:</strong> {mail.receiver}</p>
        <p><strong>Date:</strong> {new Date(mail.dateCreated).toLocaleString()}</p>
        <div className="mail-body">
          {mail.body}
        </div>
      </div>
    );
  }
}

export default withParams(MailPage);