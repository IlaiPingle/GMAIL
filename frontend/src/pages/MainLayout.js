import React , { Component } from "react";
import Header from "../components/header/Header";
import SideBar from "../components/sideBar/SideBar";
import "./MainLayout.css";
import { useLocation ,Outlet } from 'react-router-dom';
import Compose from "../components/compose/Compose";

export function withLocation(Component) {
    return function WrappedComponent(props) {
        const location = useLocation();
        return <Component {...props} location={location} />;
    };
}

class MainLayout extends Component {
    render() {
        const { location } = this.props;
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
}

export default withLocation(MainLayout);