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
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';



export default function ToDoItems({ userId }) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [users, setUsers] = useState([]);

  // Fetch all tasks if no userId
  useEffect(() => {
    if (userId) return;

    setLoading(true);

    fetch(API.TODOS, { credentials: 'include' })
      .then(async (response) => {
        if (response.status === 401 || response.status === 403) {
          throw new Error('Session expired');
        }
        if (!response.ok) {
          const text = await response.text();
          throw new Error(`Fetch failed: ${text}`);
        }
        return response.json();
      })
      .then(data => {
        setItems(data);
        setLoading(false);
      })
      .catch(err => {
        console.error('Fetch error:', err);
        if (err.message === 'Session expired') {
          window.location.href = '/'; // Redirect to login
        } else {
          setError(err.message);
        }
        setLoading(false);
      });
  }, [userId]);


  // Fetch users to display names in the table
  useEffect(() => {
    setLoading(true);

    fetch(API.USERS, {
      credentials: 'include',
    })
      .then(async (response) => {
        if (response.status === 401 || response.status === 403) {
          throw new Error('Session expired');
        }
        if (!response.ok) {
          const text = await response.text();
          throw new Error(`Error cargando usuarios: ${text}`);
        }
        return response.json();
      })
      .then(data => {
        setUsers(data);
        console.log('Fetched users:', data);
        setLoading(false);
      })
      .catch(err => {
        console.error('Error fetching users:', err);
        if (err.message === 'Session expired') {
          window.location.href = '/'; // or navigate('/')
        } else {
          setError(err.message);
          setLoading(false);
        }
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
        .then(async res => {
          if (res.status === 401 || res.status === 403) {
            throw new Error('Session expired');
          }
          if (!res.ok) {
            const text = await res.text();
            throw new Error(`Fetch error: ${text}`);
          }
          return res.json();
        })
        .then(data => {
          if (Array.isArray(data)) {
            setItems([...data]);
            setLoading(false);
          } else {
            setItems([]);
            setLoading(false);
          }
        })
        .catch(err => {
          console.error('Error fetching tasks:', err);
          if (err.message === 'Session expired') {
            window.location.href = '/'; // or use `navigate('/')` if available
          } else {
            setItems([]);
            setLoading(false);
          }
        });
    };

    fetchItems(); // initial fetch
    const intervalId = setInterval(fetchItems, 5000); // poll every 5s

    return () => clearInterval(intervalId); // cleanup
  }, [userId]);


  // Compute and split items into pending and done 
  const { pendingItems, doneItems } = useMemo(() => {
    const filtered = userId
      ? items.filter(item => item.user_id == userId)
      : [...items];

    const sorted = filtered.sort((a, b) => {
      const nameA = (users.find(u => u.userId == a.user_id)?.name || 'Sin asignar').toLowerCase();
      const nameB = (users.find(u => u.userId == b.user_id)?.name || 'Sin asignar').toLowerCase();
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
  const downloadExcel = () => {
    const pendingData = pendingItems.map(item => ({
      Task: item.description,
      Sprint: item.sprint_id,
      Status: 'Pending',
      'Assigned Member': getUserName(item.user_id),
      Deadline: item.deadline,
    }));

    const doneData = doneItems.map(item => ({
      Task: item.description,
      Sprint: item.sprint_id,
      Status: 'Done',
      Deadline: item.deadline,
      'Completion Date': item.completion_date,
    }));

    const workbook = XLSX.utils.book_new();

    const pendingSheet = XLSX.utils.json_to_sheet(pendingData);
    XLSX.utils.book_append_sheet(workbook, pendingSheet, 'Pending Tasks');

    const doneSheet = XLSX.utils.json_to_sheet(doneData);
    XLSX.utils.book_append_sheet(workbook, doneSheet, 'Done Tasks');

    const buffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    const blob = new Blob([buffer], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    });

    const filename = userId ? `tasks_user_${userId}.xlsx` : 'all_tasks.xlsx';
    saveAs(blob, filename);
  };


  return (
    <div>
      {/* Download Button */}
      <div style={{ textAlign: 'right', marginBottom: '10px' }}>
        <button className='download-button' onClick={downloadExcel}>Download Tasks as Excel</button>
      </div>

      {/* Accordion for Pending Tasks */}
      <Accordion sx={{ backgroundColor: 'transparent', boxShadow: 'none' }}>
        <AccordionSummary
          expandIcon={
            <ExpandMoreIcon sx={{ fontSize: 32 }} />
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
            <ExpandMoreIcon sx={{ fontSize: 32 }} />
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
    </div>
  );
}
