import { useNavigate } from 'react-router-dom'; // ✅ Add this
import { useState, useEffect } from 'react';
import { BrowserRouter, useLocation } from 'react-router-dom';
import Navbar from './components/layout/Navbar';
import AppRoutes from './routes';
import './index.css';

import API from './API';

function App() {
    return (
        <BrowserRouter>
            <MainLayout />
        </BrowserRouter>
    );
}

function MainLayout() {
    const location = useLocation();
    const navigate = useNavigate();

    const hideNavbarRoutes = ['/', '/signup'];
    const shouldHideNavbar = hideNavbarRoutes.includes(location.pathname);

    const [userOptions, setUserOptions] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!shouldHideNavbar) {
            // ✅ Check session
            fetch(API.SESSION, {
                credentials: 'include',
            })
                .then(res => {
                    if (!res.ok) throw new Error('Session invalid');
                    return res.json();
                })
                .then(() => {
                    return fetch(API.USERS, {
                        credentials: 'include',
                    });
                })
                .then(res => res.json())
                .then(data => {
                    setUserOptions(data);
                    setLoading(false);
                })
                .catch(err => {
                    console.warn("Not authenticated, redirecting to login:", err);
                    navigate('/'); // redirect if not authenticated
                });
        } else {
            // ✅ Public routes: set loading false directly
            setLoading(false);
        }
    }, [shouldHideNavbar, navigate]);

    if (loading) return <p>Loading app...</p>;

    return (
        <>
            {!shouldHideNavbar && <Navbar />}
            <div className='page-container'>
                <AppRoutes userOptions={userOptions} />
            </div>
        </>
    );
}

export default App;