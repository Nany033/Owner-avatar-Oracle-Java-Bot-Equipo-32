import React, { useEffect, useState } from 'react';
import axios from 'axios';
import API from '../../API';
import SprintBarChart from '../charts/SprintBarChart';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

const SprintHoursForUser = ({ userId, userName, tasks }) => {
  const [sprintData, setSprintData] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!userId) return;

    setLoading(true);

    axios.get(`${API.KPI}/hours-per-sprint/${userId}`, {
      withCredentials: true
    })
      .then(response => {
        setSprintData(response.data);
        console.log('Sprint data for user:', response.data);
        setLoading(false);
      })
      .catch(error => {
        setLoading(false);
        if (axios.isAxiosError(error) && error.response) {
          if (error.response.status === 401 || error.response.status === 403) {
            console.warn('Session expired. Redirecting to login...');
            window.location.href = '/'; // or use `navigate('/')`
            return;
          }
        }
        console.error('Error fetching sprint hours for user:', error);
      });
  }, [userId]);

  const downloadExcel = () => {
    const tableData = sprintData.map(sprint => ({
      Sprint: sprint.sprintName ?? `Sprint ${sprint.sprintId}`,
      'Estimated Hours': sprint.estimated_hours ?? '',
      'Actual Hours': sprint.totalHours ?? '',
    }));

    const worksheet = XLSX.utils.json_to_sheet(tableData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Sprint Hours');

    const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    const blob = new Blob([excelBuffer], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    });

    saveAs(blob, `sprint_hours_${userName}.xlsx`);
  };

  if (!userId) return <p>Select a user to view their sprint hours.</p>;
  if (loading) return <p>Loading sprint data for {userName}...</p>;

  return (
    <div>
      <h2>Sprint Hours for {userName} <span>({userId})</span></h2>

      <div style={{ textAlign: 'right', marginBottom: '10px' }}>
        <button className='download-button' onClick={downloadExcel}>Download as Excel</button>
      </div>

      {sprintData.length === 0 ? (
        <p>No sprint data available for this user.</p>
      ) : (
        <>
          <table className='table'>
            <thead>
              <tr>
                <th>Sprint Name</th>
                <th>Estimated Hours</th>
                <th>Actual Hours</th>
                <th>Tasks Completed</th>
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

          <SprintBarChart data={sprintData} />
        </>
      )}
    </div>
  );
};

export default SprintHoursForUser;
