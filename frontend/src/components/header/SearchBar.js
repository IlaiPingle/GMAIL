import React, { useState } from 'react';
import './SearchBar.css';
import IconButton from '../common/IconButton';
const SearchBar = ({onSearch}) => {
 return (
    <div className='searchBar'>
        <IconButton onClick={handleSearch}>
            bi bi-search
        </IconButton>
        <input 
            type='text' 
            placeholder='Search mail' 
            className='searchInput'
            onChange={(e) => onSearch(e.target.value)}/>
        <IconButton onClick={handleOptions}>
            bi bi-sliders
        </IconButton>
    </div>
    );


function handleOptions() {
    console.log("Options clicked");
}
    
function handleSearch(searchTerm) {
    console.log("Searching for:", searchTerm);
}
}
export default SearchBar;
