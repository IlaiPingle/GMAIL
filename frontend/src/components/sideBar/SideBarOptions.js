import React from "react";
import "./SideBarOptions.css";

function SideBarOptions({icon, text, onClick, id}) {
    return (
        <button className="sideBarOption" onClick={onClick} id={id}>
            <i className={icon}></i>
            <span className="sideBarText">{text}</span>
        </button>
    );
}
export default SideBarOptions;