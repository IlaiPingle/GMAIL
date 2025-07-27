import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import './SearchBar.css';
import IconButton from '../common/IconButton';

function SearchBar({ onSearch }) {
    const [searchTerm, setSearchTerm] = useState('');
    const navigate = useNavigate();
    const location = useLocation();

    const handleSearch = async () => {
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

    const handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            handleSearch();
        }
    };

    const handleOptions = () => {
        console.log("Options clicked");
    };

    return (
        <div className="searchBar">
            <IconButton
                iconType='material' 
                onClick={handleSearch}>search</IconButton>
            <input
                type="text"
                placeholder="Search mail"
                className="searchInput"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                onKeyPress={handleKeyPress}
            />
            <IconButton iconType="material" onClick={handleOptions}>
                tune
            </IconButton>
        </div>
    );
}

export default SearchBar;
