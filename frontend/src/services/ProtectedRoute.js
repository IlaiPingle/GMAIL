import { Navigate } from "react-router-dom";
import { useUser } from "../contexts/UserContext";

function ProtectedRoute({ children }) {
  const {user , loading} = useUser();

  if (loading) return <div>טוען...</div>;

  if (!user) {
    return <Navigate to="/login" />;
  }
  return children;
}

export default ProtectedRoute;
