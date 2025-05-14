import { useEffect, useState, useMemo } from 'react';
import { CircularProgress } from '@mui/material';
import { StatusCircle } from '../assets/icons';
import Moment from 'react-moment';
import API from '../API';

export default function ToDoItems({ userId }) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [users, setUsers] = useState([]);

  // Fetch all tasks if no userId
  useEffect(() => {
    if (userId) return; // Skip if userId is present
    setLoading(true);
    fetch(API.TODOS)
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
    fetch(API.USERS)
      .then(response => {
        if (!response.ok) throw new Error('Error cargando usuarios');
        return response.json();
      })
      .then(data => {
        setUsers(data);
      })
      .catch(err => {
        setError(err.message);
      });
  }, []);


    // Get user name by userId for displaying in the table
  const getUserName = (userId) => {
    if (!userId || !Array.isArray(users) || users.length === 0) return 'Sin asignar';
    const user = users.find(u => u.userId === userId);
    return user ? user.name : 'Sin asignar';
  };

  // Fetch tasks for specific userId when selecting a user in the filter
  useEffect(() => {
    if (!userId) return;

    const userIdInt = parseInt(userId, 10);

    const fetchItems = () => {
      fetch(`${API.TODOS}/user/${userIdInt}`)
        .then(res => res.json())
        .then(data => {
          if (Array.isArray(data)) {
            setItems([...data]);
            console.log('Fetched tasks for user:', data);
          } else {
            setItems([]);
          }
        })
        .catch(err => {
          console.error('Error fetching tasks:', err);
          setItems([]);
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

    const sorted = filtered.sort((a, b) => new Date(a.deadline) - new Date(b.deadline));

    return {
      pendingItems: sorted.filter(item => !item.done),
      doneItems: sorted.filter(item => item.done),
    };
  }, [items, userId]);

  // Render states
  if (loading) return <CircularProgress />;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      <h2>Pending Tasks</h2>
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
                <td>
                  <span className="icon">{item.sprint_id}</span>
                </td>
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

      <h2>Done Tasks</h2>
      {doneItems.length === 0 ? (
        <p>No completed tasks found.</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>Task</th>
              <th>Sprint</th>
              <th>Deadline</th>
              <th>Completion Date</th>
            </tr>
          </thead>
          <tbody>
            {doneItems.map(item => (
              <tr key={item.id}>
                <td>{item.description}</td>
                <td>{item.sprint_id}</td>
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
    </div>
  );
}
