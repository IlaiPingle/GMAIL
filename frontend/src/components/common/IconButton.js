import React from "react";
import "./IconButton.css";
import "./Icon.css"; 
function IconButton({className, children, onClick }) {
    const renderIcon = () => {
        return <span className={`material-symbols-outlined ${className}`}>{children}</span>;
    };

    return (
        <button className="iconButton" onClick={onClick}>
            {renderIcon()}
        </button>
    );
}

export default IconButton;
