import { useEffect, useState, useMemo } from 'react';
import { CircularProgress } from '@mui/material';
import { StatusCircle } from '../assets/icons';
import Moment from 'react-moment';
import API from '../API';
import {
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Typography,
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';


export default function ToDoItems({ userId }) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [users, setUsers] = useState([]);

  // Fetch all tasks if no userId
  useEffect(() => {
    if (userId) return; // Skip if userId is present
    setLoading(true);
    fetch(API.TODOS, {
      credentials: 'include',
    })
      .then(response => {
        if (!response.ok) throw new Error('Something went wrong ...');
        return response.json();
      })
      .then(data => {
        setItems(data);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  }, [userId]);

  // Fetch users to display names in the table
  useEffect(() => {
    fetch(API.USERS, {
      credentials: 'include',
    })
      .then(response => {
        setLoading(true);
        if (!response.ok) throw new Error('Error cargando usuarios');
        return response.json();
      })
      .then(data => {
        setUsers(data);
        console.log('Fetched users:', data);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
      });
  }, []);


  // Get user name by userId for displaying in the table
  const getUserName = (userId) => {
    if (!userId) return 'Sin asignar';
    const user = users.find(u => u.userId == userId);
    console.log('User:', user);
    return user ? user.name : 'Sin asignar';
  };

  // Fetch tasks for specific userId when selecting a user in the filter
  useEffect(() => {
    if (!userId) return;

    const userIdInt = parseInt(userId, 10);
    setLoading(true);
    const fetchItems = () => {
      fetch(`${API.TODOS}/user/${userIdInt}`, {
        credentials: 'include',
      })
        .then(res => res.json())
        .then(data => {
          if (Array.isArray(data)) {
            setItems([...data]);
            console.log('Fetched tasks for user:', data);
            setLoading(false);
          } else {
            setItems([]);
            setLoading(false);
          }
        })
        .catch(err => {
          console.error('Error fetching tasks:', err);
          setItems([]);
          setLoading(false);
        });
    };

    fetchItems(); // Initial load
    const intervalId = setInterval(fetchItems, 5000); // Auto-refresh every 5s

    return () => clearInterval(intervalId); // Clean up on unmount
  }, [userId]);

  // Compute and split items into pending and done 
  const { pendingItems, doneItems } = useMemo(() => {
    const filtered = userId
      ? items.filter(item => item.user_id == userId)
      : [...items];

    const sorted = filtered.sort((a, b) => {
      const nameA = (users.find(u => u.userId === a.user_id)?.name || 'Sin asignar').toLowerCase();
      const nameB = (users.find(u => u.userId === b.user_id)?.name || 'Sin asignar').toLowerCase();
      return nameA.localeCompare(nameB);
    });

    return {
      pendingItems: sorted.filter(item => !item.done),
      doneItems: sorted.filter(item => item.done),
    };
  }, [items, userId, users]);


  // Render states
  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px' }}>
        <CircularProgress />
      </div>
    );
  }
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      {/* Accordion for Pending Tasks */}
      <Accordion sx={{ backgroundColor: 'transparent', boxShadow: 'none' }}>
        <AccordionSummary
          expandIcon={
            <ExpandMoreIcon sx={{ fontSize: 32 }} /> // default is ~24px
          }
        >
          <Typography variant="h6">Pending Tasks</Typography>
        </AccordionSummary>
        <AccordionDetails>
          {pendingItems.length === 0 ? (
            <p>No pending tasks found.</p>
          ) : (
            <table className="table">
              <thead>
                <tr>
                  <th>Task</th>
                  <th>Sprint</th>
                  <th>Status</th>
                  <th>Assigned member</th>
                  <th>Deadline</th>
                </tr>
              </thead>
              <tbody>
                {pendingItems.map(item => (
                  <tr key={item.id}>
                    <td>{item.description}</td>
                    <td>{item.sprint_id}</td>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center' }}>
                        <StatusCircle className="table-icon" status="pending" />
                        <span className="icon">Pending</span>
                      </div>
                    </td>
                    <td>{getUserName(item.user_id)}</td>
                    <td>
                      {item.deadline && (
                        <Moment format="MMM Do YYYY">{item.deadline}</Moment>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </AccordionDetails>
      </Accordion>
      {/* Accordion for Done Tasks */}
      <Accordion sx={{ backgroundColor: 'transparent', boxShadow: 'none' }}>
        <AccordionSummary
          expandIcon={
            <ExpandMoreIcon sx={{ fontSize: 32 }} /> // default is ~24px
          }
        >
          <Typography variant="h6">Done Tasks</Typography>
        </AccordionSummary>
        <AccordionDetails>
          {doneItems.length === 0 ? (
            <p>No completed tasks found.</p>
          ) : (
            <table className="table">
              <thead>
                <tr>
                  <th>Task</th>
                  <th>Sprint</th>
                  <th>Assigned member</th>
                  <th>Deadline</th>
                  <th>Completion Date</th>
                </tr>
              </thead>
              <tbody>
                {doneItems.map(item => (
                  <tr key={item.id}>
                    <td>{item.description}</td>
                    <td>{item.sprint_id}</td>
                    <td>{getUserName(item.user_id)}</td>
                    <td>
                      {item.deadline && (
                        <Moment format="MMM Do YYYY">{item.deadline}</Moment>
                      )}
                    </td>
                    <td>
                      {item.completion_date && (
                        <Moment format="MMM Do YYYY">{item.completion_date}</Moment>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </AccordionDetails>
      </Accordion>
    </div >
  );
}
