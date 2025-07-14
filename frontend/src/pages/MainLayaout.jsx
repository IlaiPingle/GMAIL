
import { Outlet } from 'react-router-dom';
import './mainLayout.css';

function mainLayout(){
    return (
        <div className="main-layout">
            <div className="body">
                <div className="content">
                    <Outlet />
                </div>
            </div>
        </div>
    );
}
export default mainLayout;