import React from "react";
import IconButton from "../common/IconButton";
import SearchBar from "./SearchBar";
import UserActions from "./userActions";
import {useUser} from "../../contexts/UserContext";
import { useTheme } from "../../contexts/ThemeContext";
import "./Header.css";

function Header({ onSearch, onMenuClick }) {
  const { user } = useUser();
  const {darkMode} = useTheme();
  const handleMenuClick = () => {
    onMenuClick();
  };

  const handleSearch = (searchResults) => {
    console.log("Search results:", searchResults);
    if (onSearch) {
      onSearch(searchResults);
    }
  };

  return (
    <div className="header">
        {/* Left Side - Menu + Logo */}
      <div className="header-left">
        <IconButton iconType="material" onClick={handleMenuClick}>
          menu
        </IconButton>
          <picture className="gmail-logo">
            <img
              src={darkMode ? "https://ssl.gstatic.com/ui/v1/icons/mail/rfr/logo_gmail_lockup_dark_2x_r5.png" : "https://ssl.gstatic.com/ui/v1/icons/mail/rfr/logo_gmail_lockup_default_2x_r5.png"}
              alt="Gmail"
              style={{ height: "40px" }}
            />
          </picture>
      </div>
        <SearchBar className="header-center" onSearch={handleSearch} />
          {/* Right Side - User Actions */}
        <UserActions
          userName={user.username}
          userImage={user?.picture || "https://upload.wikimedia.org/wikipedia/commons/a/ac/Default_pfp.jpg?20200418092106"}
        />
    </div>
  );
}

export default Header;
