import React , { Component } from "react";
import Header from "../components/header/Header";
import SideBar from "../components/sideBar/SideBar";
import "./MainLayout.css";

class MainLayout extends Component {
    constructor(props) {
        super(props);
        this.state = {
            searchResults: null
        };
    }

    handleSearch = (results) => {
        console.log("MainLayout.js - Received search results:", results);
        this.setState({ searchResults: results });
    };

    render() {
        const { children } = this.props;
        return (
            <div className="main-layout">
                <Header onSearch={this.handleSearch} />
                <div className="main-layout-body">
                    <SideBar />
                    <div className="main-content">
                        {children}  
                    </div>
                </div>
            </div>
        );
    }
}

export default MainLayout;