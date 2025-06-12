import { useEffect, useState } from 'react';
import axios from 'axios';
import API from '../../API';
import SprintBarChart from '../charts/SprintBarChart';
import TaskCompletion from '../charts/TasksCompleted';
import HoursPerDev from '../charts/HoursPerDev';

import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

const SprintHours = ({ tasks }) => {
  const [sprintData, setSprintData] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [sprintRes, usersRes] = await Promise.all([
          axios.get(`${API.KPI}/hours-per-sprint`, { withCredentials: true }),
          axios.get(API.USERS, { withCredentials: true })
        ]);

        setSprintData(sprintRes.data);
        setUsers(usersRes.data);
        setLoading(false);
      } catch (error) {
        setLoading(false);

        // Check for unauthorized session
        if (axios.isAxiosError(error) && error.response) {
          if (error.response.status === 401 || error.response.status === 403) {
            console.warn('Session expired. Redirecting to login...');
            window.location.href = '/'; // or use navigate('/')
            return;
          }
        }

        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, []);

  const downloadExcel = () => {
    const tableData = sprintData.map(sprint => ({
      Sprint: sprint.sprintName ?? `Sprint ${sprint.sprintId}`,
      'Estimated Hours': sprint.estimated_hours ?? '',
      'Actual Hours': sprint.totalHours ?? '',
      'Tasks Completed': tasks.filter(task => task.sprintId === sprint.sprintId).length,
    }));

    const worksheet = XLSX.utils.json_to_sheet(tableData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Sprint Hours');

    const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    const blob = new Blob([excelBuffer], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    });

    saveAs(blob, 'sprint_hours.xlsx');
  };

  if (loading) return <p>Loading sprint data...</p>;

  return (
    <div>
      <h2>Sprint Hours (All Users)</h2>

      <div style={{ textAlign: 'right', marginBottom: '10px' }}>
        <button className='download-button' onClick={downloadExcel}>Download as Excel</button>
      </div>

      <table className="table">
        <thead>
          <tr>
            <th>Sprint</th>
            <th>Estimated Hours</th>
            <th>Actual Hours</th>
            <th>Number of Tasks Completed</th>
          </tr>
        </thead>
        <tbody>
          {sprintData.map((sprint, index) => (
            <tr key={index}>
              <td>{sprint.sprintName ?? `Sprint ${sprint.sprintId}`}</td>
              <td>{sprint.estimated_hours ?? '–'}</td>
              <td>{sprint.totalHours ?? '–'}</td>
              <td>{tasks.filter(task => task.sprint_id === sprint.sprintId).length}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Chart views */}
      <SprintBarChart  data={sprintData} />
      <TaskCompletion  data={sprintData} tasks={tasks} isLoading={loading} />
      <HoursPerDev  tasks={tasks} users={users} isLoading={loading} />
    </div>
  );
};

export default SprintHours;
