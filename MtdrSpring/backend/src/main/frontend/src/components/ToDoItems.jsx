import { useEffect, useState } from 'react';
import { CircularProgress } from '@mui/material';
import { StatusCircle } from '../assets/icons';
import Moment from 'react-moment';
import API from '../API';

export default function ToDoItems() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [users, setUsers] = useState([]);

  useEffect(() => {
    setLoading(true);
    fetch(API.TODOS)
      .then(response => {
        if (!response.ok) throw new Error('Something went wrong ...');
        return response.json();
      })
      .then(data => {
        setItems(data);
        console.log(data);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  }, []);


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

  const getUserName = (userId) => {
    const user = users.find(u => u.userId == userId);
    // console.log(userId, user);
    // console.log(users);
    return user ? user.name : 'Sin asignar';
  };

  if (loading) return <CircularProgress />;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      <h2>Pending Tasks</h2>
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
          {items.filter(item => !item.done).map(item => (
            <tr key={item.id}>
              <td>{item.description}</td>
              <td>
                <span className="icon">
                  {/* <span className="table-icon">{sprintTagIcon}</span> */}
                  {item.sprint_id}
                </span>
              </td>
              <td>
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <StatusCircle className="table-icon" status={item.done ? 'done' : 'pending'} />
                  <span className="icon">{item.done ? 'Done' : 'Pending'}</span>
                </div>
              </td>
              <td>{getUserName(item.user_id)}
              </td>
              <td>
                {item.deadline && (
                  <Moment format="MMM Do YYYY">{item.deadline}</Moment>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <h2>Done Tasks</h2>
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
          {items.filter(item => item.done).map(item => (
            <tr key={item.id}>
              <td>{item.description}</td>
              <td>{item.sprint_id}</td>
              <td>
                {item.deadline && (
                  <Moment format="MMM Do YYYY">{item.deadline}</Moment>
                )}
              </td>
              <td> {item.completion_date && (
                  <Moment format="MMM Do YYYY">{item.completion_date}</Moment>
                )}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
