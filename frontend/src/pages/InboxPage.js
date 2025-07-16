import React, { Component } from "react";
import { useLocation } from 'react-router-dom';
import MailItem from "../components/mailItem/MailItem";
import "./InboxPage.css";

class InboxPage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      mails: [],
      loading: true,
      currentSearchResults: null
    };
  }

  // Function to perform search
  performSearch = async (term) => {
    try {
      const response = await fetch(`http://localhost:8080/api/mails/search?q=${encodeURIComponent(term)}`, {
        headers: {
          'user-id': '2',
        },
      });

      if (!response.ok) {
        throw new Error('Search failed');
      }

      const searchResults = await response.json();
      
      const adaptedResults = searchResults.map(mail => ({
        id: mail.id,
        from: mail.sender,
        to: mail.receiver,
        subject: mail.subject,
        body: mail.body,
        date: mail.dateCreated,
        isRead: Math.random() > 0.5,
        isStarred: Math.random() > 0.7
      }));

      this.setState({ currentSearchResults: adaptedResults });
    } catch (err) {
      console.error('Search error:', err);
      this.setState({ currentSearchResults: [] });
    }
  };

  fetchMails = () => {
    console.log("Attempting to fetch from API...");
    fetch("http://localhost:8080/api/mails", {
      headers: {
        "Content-Type": "application/json",
        "user-id": "2",
      },
    })
      .then((res) => {
        console.log("API Response status:", res.status);
        console.log("API Response headers:", res.headers);
        if (!res.ok) {
          throw new Error(`API returned status ${res.status}: ${res.statusText}`);
        }
        return res.json();
      })
      .then((data) => {
        console.log("API data received:", data);
        // התאמת המבנה מה-API למבנה שהקומפוננט מצפה
        const adaptedMails = data.map(mail => ({
          id: mail.id,
          from: mail.sender,
          to: mail.receiver,
          subject: mail.subject,
          body: mail.body,
          date: mail.dateCreated,
          isRead: Math.random() > 0.5, // רנדומלי לדוגמה
          isStarred: Math.random() > 0.7 // רנדומלי לדוגמה
        }));
        this.setState({ mails: adaptedMails });
      })
      .catch((err) => {
        console.error("API Error:", err.message);
        console.log("Using dummy data instead");
        this.setState({ mails: "dummyMails" });
      })
      .finally(() => this.setState({ loading: false }));
  };

  componentDidMount() {
    this.fetchMails();
    
    // Handle search query from URL
    const searchQuery = this.getSearchQuery();
    if (searchQuery) {
      this.performSearch(searchQuery);
    }
  }

  componentDidUpdate(prevProps) {
    const prevSearchQuery = this.getSearchQuery(prevProps.location);
    const currentSearchQuery = this.getSearchQuery();
    
    if (prevSearchQuery !== currentSearchQuery) {
      if (currentSearchQuery) {
        this.performSearch(currentSearchQuery);
      } else {
        this.setState({ currentSearchResults: null });
      }
    }
  }

  getSearchQuery = (location = window.location) => {
    const query = new URLSearchParams(location.search);
    return query.get('search');
  };

  render() {
    const { searchResults } = this.props;
    const { mails, loading, currentSearchResults } = this.state;
    
    // Use search results if available, otherwise use fetched mails
    const displayMails = currentSearchResults || searchResults || mails;
    const searchQuery = this.getSearchQuery();
    
    console.log('InboxPage - searchQuery:', searchQuery);
    console.log('InboxPage - searchResults:', searchResults);
    console.log('InboxPage - displayMails:', displayMails);

    if (loading) {
      return <div className="inbox-page loading">Loading...</div>;
    }

    return (
      <div className="inbox-page">
        <div className="inbox-header">
          <h2>Inbox</h2>
        </div>
        <div className="inbox-content">
          {displayMails && displayMails.length === 0 ? (
            <p className="no-mails">No mails found.</p>
          ) : (
            displayMails && displayMails.map((mail) => <MailItem key={mail.id} mail={mail} />)
          )}
        </div>
      </div>
    );
  }
}

export default InboxPage;
