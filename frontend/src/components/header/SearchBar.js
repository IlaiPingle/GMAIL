import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import IconButton from '../common/IconButton';
import './SearchBar.css';

function SearchBar({ onSearch }) {
    const [searchTerm, setSearchTerm] = useState('');
    const navigate = useNavigate();
    const location = useLocation();

    const handleSearch = async () => {
        if (!searchTerm.trim()) {
            // If search is empty, navigate to inbox without search param
            navigate('/inbox');
            return;
        }
        
        // Navigate to search results URL
        navigate(`/inbox?search=${encodeURIComponent(searchTerm)}`);
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
                className="icon-header"
                onClick={handleSearch}>search</IconButton>
            <input
                type="text"
                placeholder="Search mail"
                className="searchInput"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                onKeyDown={handleKeyPress}
            />
            <IconButton className="icon-header" onClick={handleOptions}>
                tune
            </IconButton>
        </div>
    );
}

export default SearchBar;
