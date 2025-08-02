import React, { useState } from "react";
import Header from "../components/header/Header";
import SideBar from "../components/sideBar/SideBar";
import "./MainLayout.css";
import { useLocation, Outlet } from 'react-router-dom';
import Compose from "../components/compose/Compose";
import CreateLabel from "../components/createLabel/CreateLabel";
function MainLayout() {
    const location = useLocation();
    const params = new URLSearchParams(location.search);
    const composeParam = params.get("compose");
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);
    const [showCreateLabel, setShowCreateLabel] = useState(false);
    const [labelCreated, setLabelCreated] = useState(false);

    const toggleSidebar = () => {
        setIsSidebarOpen(!isSidebarOpen);
    };
    
    return (
        <div className="main-layout">
            <Header onMenuClick={toggleSidebar}/>
            <div className="main-layout-body">
                <SideBar isOpen={isSidebarOpen}
                onOpenCreateLabel={() => setShowCreateLabel(true)} 
                onLabelCreated={labelCreated}
                resetLabelCreated={setLabelCreated}
                />
                <div className="content">
                    <Outlet/>
                </div>
                {composeParam && (<div className="compose-overlay">
                    <Compose draftId={composeParam !== "new" ? composeParam : null} />
                </div>)}
                {showCreateLabel && (
                    <div className="create-label-overlay">
                        <CreateLabel onClose={() => setShowCreateLabel(false)} onCreate={() => setLabelCreated(true)} />
                    </div>
                )}
            </div>
        </div>
    );
}

export default MainLayout;