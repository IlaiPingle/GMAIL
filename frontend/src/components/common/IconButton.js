import React from "react";
import "./IconButton.css";

function IconButton({ children, onClick }) {
    const renderIcon = () => {
        return <span className="material-symbols-outlined">{children}</span>;
    };

    return (
        <button className="iconButton" onClick={onClick}>
            {renderIcon()}
        </button>
    );
}

export default IconButton;
