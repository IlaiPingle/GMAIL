import React, { Component } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import './SearchBar.css';
import IconButton from '../common/IconButton';

// HOC for navigation hooks in class component
function withNavigation(Component) {
    return function ComponentWithNavigation(props) {
        const navigate = useNavigate();
        const location = useLocation();
        return <Component {...props} navigate={navigate} location={location} />;
    };
}

class SearchBar extends Component {
    state = {
        searchTerm: ''
    };

    handleSearch = async () => {
        const { searchTerm } = this.state;
        const { onSearch, navigate } = this.props;
        
        if (!searchTerm.trim()) {
            // If search is empty, navigate to inbox without search param
            navigate('/');
            onSearch(null);
            return;
        }
        
        // Navigate to search results URL
        navigate(`/?search=${encodeURIComponent(searchTerm)}`);
        
        try {
            const response = await fetch(`http://localhost:8080/api/mails/search?q=${encodeURIComponent(searchTerm)}`, {
                headers: {
                    'user-id': '2', // Replace with dynamic user ID
                },
            });

            if (!response.ok) {
                throw new Error('Search failed');
            }

            const searchResults = await response.json();
            
            // Adapt the results to match the expected format
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

            console.log('Search results:', adaptedResults);
            onSearch(adaptedResults);
        } catch (err) {
            console.error('Search error:', err);
            onSearch([]);
        }
    };

    handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            this.handleSearch();
        }
    };

    handleOptions = () => {
        console.log("Options clicked");
    };

    render() {
        const { searchTerm } = this.state;
        
        return (
            <div className="searchBar">
                <IconButton
                    iconType='material' 
                    onClick={this.handleSearch}>search</IconButton>
                <input
                    type="text"
                    placeholder="Search mail"
                    className="searchInput"
                    value={searchTerm}
                    onChange={(e) => this.setState({ searchTerm: e.target.value })}
                    onKeyPress={this.handleKeyPress}
                />
                <IconButton iconType="material" onClick={this.handleOptions}>
                    tune
                </IconButton>
            </div>
        );
    }
}

export default withNavigation(SearchBar);
