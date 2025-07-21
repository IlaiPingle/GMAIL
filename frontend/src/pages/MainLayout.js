import React , { Component } from "react";
import Header from "../components/header/Header";
import SideBar from "../components/sideBar/SideBar";
import "./MainLayout.css";
import { Outlet } from 'react-router-dom';


class MainLayout extends Component {
    render() {
        return (
            <div className="main-layout">
                <Header/>
                <div className="main-layout-body">
                    <SideBar/>
                    <div className="content">
                        <Outlet/>
                    </div>
                </div>
            </div>
        );
    }
}

export default MainLayout;