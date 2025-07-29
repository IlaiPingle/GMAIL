import { Navigate } from "react-router-dom";
import { useEffect, useState } from "react";

import Client from "./Client";

function ProtectedRoute({ children }) {
  const [authChecked, setAuthChecked] = useState(false);
  const [isAuthorized, setIsAuthorized] = useState(false);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const response = await Client.checkLoginStatus();
        setIsAuthorized(response);
      } catch (error) {
        console.error("Error checking authentication status:", error);
        setIsAuthorized(false);
      } finally {
        setAuthChecked(true);
      }
    };
    checkAuth();
  }, []);

  if (!authChecked) return <div>טוען...</div>;

  return isAuthorized ? children : <Navigate to="/login" />;
}

export default ProtectedRoute;
