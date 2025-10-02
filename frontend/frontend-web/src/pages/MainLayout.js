import React, { useState } from "react";
import { useLocation, Outlet } from 'react-router-dom';
import Header from "../components/header/Header";
import SideBar from "../components/sideBar/SideBar";
import Compose from "../components/compose/Compose";
import "./MainLayout.css";
function MainLayout() {
    const location = useLocation();
    const params = new URLSearchParams(location.search);
    const composeParam = params.get("compose");
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);

    const toggleSidebar = () => {
        setIsSidebarOpen(!isSidebarOpen);
    };
    
    return (
        <div className="main-layout">
            <Header onMenuClick={toggleSidebar}/>
            <div className="main-layout-body">
                <SideBar isOpen={isSidebarOpen}/>
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