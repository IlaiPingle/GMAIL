import React, { Component } from "react";
import "./SideBarOptions.css";

class SideBarOptions extends Component {
    handleClick = () => {
        const { onClick } = this.props;
        if (onClick) {
            onClick();
        }
    };

    render() {
        const { icon, text, id, isActive = false, iconType = "bootstrap" } = this.props;
        
        return (
            <button className={`sideBarOption ${isActive ? 'selected' : ''}`} onClick={this.handleClick} id={id}>
                <span className="material-symbols-outlined">{icon}</span>
                <span className="sideBarText">{text}</span>
            </button>
        );
    }
}

export default SideBarOptions;