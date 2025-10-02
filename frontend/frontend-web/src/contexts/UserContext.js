import { createContext,useContext,useState ,useEffect } from "react";
import Client from "../services/Client";
const UserContext = createContext();

export const useUser = () => useContext(UserContext);

export function UserProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const response = await Client.getCurrentUser();
                console.log("UserContext - Fetched user:", response);
                setUser(response);
            } catch (error) {
                console.error("Error fetching user:", error);

            } finally {
                setLoading(false);
            }
        }
        fetchUser();
    }, []);

    return (
        <UserContext.Provider value={{ user, setUser, loading}}>
            {children}
        </UserContext.Provider>
    );
}