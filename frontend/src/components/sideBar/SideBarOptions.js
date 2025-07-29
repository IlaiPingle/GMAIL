import React from "react";
import "./SideBarOptions.css";

function SideBarOptions({ text, icon, isActive, id, onClick }) {
    const handleClick = () => {
        if (onClick) {
            onClick();
        }
    };
    return (
        <button className={`sideBarOption ${isActive ? 'selected' : ''}`} onClick={handleClick} id={id}>
            <span className="material-symbols-outlined">{icon}</span>
            <span className="sideBarText">{text}</span>
        </button>
    );
}

export default SideBarOptions;