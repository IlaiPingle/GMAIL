import React from "react";
import Header from "../components/header/Header";
import SideBar from "../components/sideBar/SideBar";
import "./MainLayout.css";
import { useLocation, Outlet } from 'react-router-dom';
import Compose from "../components/compose/Compose";

function MainLayout() {
    const location = useLocation();
    const params = new URLSearchParams(location.search);
    const composeParam = params.get("compose");
    
    return (
        <div className="main-layout">
            <Header/>
            <div className="main-layout-body">
                <SideBar/>
                <div className="content">
                    <Outlet/>
                </div>
                {composeParam && (<div className="compose-overlay">
                    <Compose draftId={composeParam !== "new" ? composeParam : null} />
                </div>)}
            </div>
        </div>
    );
}

export default MainLayout;