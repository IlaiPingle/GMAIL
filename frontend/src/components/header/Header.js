import React from "react";
import IconButton from "../common/IconButton";
import GmailLogo from "./GmailLogo";
import SearchBar from "./SearchBar";
import UserActions from "./userActions";
import "./Header.css";

function Header({ onSearch }) {
  const handleMenuClick = () => {
    console.log("Menu clicked");
    // כאן אפשר להוסيף לוגיקה לפתיחת sidebar
  };

  const handleSearch = (searchResults) => {
    console.log("Search results:", searchResults);
    if (onSearch) {
      onSearch(searchResults);
    }
  };

  return (
    <nav
      className="navbar navbar-expand-lg bg-body-tertiary"
      style={{ backgroundColor: "#f1f3f4" }}
    >
      <div className="container-fluid">
        {/* Left Side - Menu + Logo */}
        <div className="header-left">
          <IconButton 
            iconType="material"
          onClick={handleMenuClick}>
            menu
          </IconButton>
          <GmailLogo />
        </div>
        <div className="header-center">
        {/* Center - Search */}
        <SearchBar onSearch={handleSearch} />
        </div>
        <div className="header-right">
        {/* Right Side - User Actions */}
        <UserActions
          userName="John Doe"
          userImage="https://upload.wikimedia.org/wikipedia/commons/a/ac/Default_pfp.jpg?20200418092106"
        />
        </div>
      </div>
    </nav>
  );
}

export default Header;
