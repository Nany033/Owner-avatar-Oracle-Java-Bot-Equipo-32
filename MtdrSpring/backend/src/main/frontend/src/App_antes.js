          /*
## MyToDoReact version 1.0.
##
## Copyright (c) 2022 Oracle, Inc.
## Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl/
*/
/*
 * This is the application main React component. We're using "function"
 * components in this application. No "class" components should be used for
 * consistency.
 * @author  jean.de.lavarene@oracle.com
 */
import React, { useState, useEffect } from 'react';
import NewItem from './NewItem';
import API_LIST from './API';
import DeleteIcon from '@mui/icons-material/Delete';
import { Button, TableBody, CircularProgress } from '@mui/material';
import Moment from 'react-moment';

/* In this application we're using Function Components with the State Hooks
 * to manage the states. See the doc: https://reactjs.org/docs/hooks-state.html
 * This App component represents the entire app. It renders a NewItem component
 * and two tables: one that lists the todo items that are to be done and another
 * one with the items that are already done.
 */
function App() {
    // isLoading is true while waiting for the backend to return the list
    // of items. We use this state to display a spinning circle:
    const [isLoading, setLoading] = useState(false);
    // Similar to isLoading, isInserting is true while waiting for the backend
    // to insert a new item:
    const [isInserting, setInserting] = useState(false);
    // The list of todo items is stored in this state. It includes the "done"
    // "not-done" items:
    const [items, setItems] = useState([]);
    // In case of an error during the API call:
    const [error, setError] = useState();

    function deleteItem(deleteId) {
      // console.log("deleteItem("+deleteId+")")
      fetch(API_LIST+"/"+deleteId, {
        method: 'DELETE',
      })
      
      .then(response => {
        // console.log("response=");
        // console.log(response);
        if (response.ok) {
          // console.log("deleteItem FETCH call is ok");
          return response;
        } else {
          throw new Error('Something went wrong ...');
        }
      })
      .then(
        (result) => {
          const remainingItems = items.filter(item => item.id !== deleteId);
          setItems(remainingItems);
        },
        (error) => {
          setError(error);
        }
      );
    }
    function toggleDone(event, id, description, done, deadline) {
      event.preventDefault();
      modifyItem(id, description, done, deadline).then(
        (result) => { reloadOneIteam(id); },
        (error) => { setError(error); }
      );
    }
    
    function reloadOneIteam(id){
      fetch(API_LIST+"/"+id)
        .then(response => {
          if (response.ok) {
            return response.json();
          } else {
            throw new Error('Something went wrong ...');
          }
        })
        .then(
          (result) => {
            const items2 = items.map(
              x => (x.id === id ? {
                 ...x,
                 'description':result.description,
                 'done': result.done
                } : x));
            setItems(items2);
          },
          (error) => {
            setError(error);
          });
    }
    function modifyItem(id, description, done, deadline) {
      var data = {
        "description": description, 
        "done": done,
        "deadline": deadline  // Incluimos la deadline en la actualización
      };
      
      return fetch(API_LIST+"/"+id, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      })
      .then(response => {
        if (response.ok) {
          return response;
        } else {
          throw new Error('Something went wrong ...');
        }
      });
    }
    /*
    To simulate slow network, call sleep before making API calls.
    const sleep = (milliseconds) => {
      return new Promise(resolve => setTimeout(resolve, milliseconds))
    }
    */
    useEffect(() => {
      setLoading(true);
      // sleep(5000).then(() => {
      fetch(API_LIST)
        .then(response => {
          if (response.ok) {
            return response.json();
          } else {
            throw new Error('Something went wrong ...');
          }
        })
        .then(
          (result) => {
            setLoading(false);
            setItems(result);
          },
          (error) => {
            setLoading(false);
            setError(error);
          });

      //})
    },
    // https://en.reactjs.org/docs/faq-ajax.html
    [] // empty deps array [] means
       // this useEffect will run once
       // similar to componentDidMount()
    );
    function addItem(text, deadlineStr) {
      console.log("addItem(" + text + ", " + deadlineStr + ")");
      setInserting(true);
      
      // Preparamos los datos básicos para la creación de la tarea
      var data = {
        description: text
      };
      
      // Primero creamos el ítem
      fetch(API_LIST, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data),
      })
      .then((response) => {
        if (response.ok) {
          return response;
        } else {
          throw new Error('Error creating task');
        }
      })
      .then((result) => {
        const id = result.headers.get('location');
        
        // Si hay fecha límite, hacemos una segunda llamada para configurarla
        if (deadlineStr && deadlineStr.trim() !== '') {
          return fetch(`${API_LIST}/${id}/deadline`, {
            method: 'POST',
            headers: {
              'Content-Type': 'text/plain' // El backend espera un string simple
            },
            body: deadlineStr
          })
          .then(response => {
            if (response.ok) {
              return response.json();
            } else {
              throw new Error('Error setting deadline');
            }
          })
          .then(updatedItem => {
            // Una vez configurada la fecha límite, recargamos la lista completa
            return fetch(API_LIST);
          })
          .then(response => response.json())
          .then(allItems => {
            setItems(allItems);
            setInserting(false);
          });
        } else {
          // Si no hay fecha límite, simplemente recargamos la lista
          return fetch(API_LIST)
            .then(response => response.json())
            .then(allItems => {
              setItems(allItems);
              setInserting(false);
            });
        }
      })
      .catch(error => {
        console.error('Error:', error);
        setInserting(false);
        setError(error);
      });
    }
    return (
      <div className="App">
        <h1>MY TODO LIST</h1>
        <NewItem addItem={addItem} isInserting={isInserting}/>
        
        { error &&
          <p>Error: {error.message}</p>
        }
        
        { isLoading ? (
          <div id="maincontent">
            <h2 id="activelist" className="section-header">
              Items
            </h2>
            <CircularProgress />
          </div>
        ) : (
          <div id="maincontent">
            <h2 id="activelist" className="section-header">
              Items
            </h2>
            
            {/* TAREAS PENDIENTES - Solo mostrar las que NO están completadas */}
            <table id="itemlistNotDone" className="itemlist">
              <thead>
                <tr>
                  <th className="task-header">Task</th>
                  <th className="deadline-header">Deadline</th>
                  <th className="action-header"></th> 
                </tr>
              </thead>
              <TableBody>
              {items.filter(item => !item.done).map(item => (
                <tr key={item.id}>
                  <td className="description">{item.description}</td>
                  <td className="deadline">
                    {item.deadline && (
                      <Moment format="MMM Do YYYY">{item.deadline}</Moment>
                    )}
                  </td>
                  <td className="action-cell">
                  <Button variant="contained" className="DoneButton"
                    onClick={(event) => toggleDone(event, item.id, item.description, true, item.deadline)}
                    size="small">
                    Done
                  </Button>
                  </td>
                </tr>
              ))}
              </TableBody>
            </table>
            
            <h2 id="donelist" className="section-header">
              Done items
            </h2>
            
            {/* TAREAS COMPLETADAS - Solo mostrar las que están completadas */}
            <table id="itemlistDone" className="itemlist">
              <thead>
                <tr>
                  <th className="task-header">Task</th>
                  <th className="deadline-header">Deadline</th>
                  <th className="action-header" colSpan="2"></th>
                </tr>
              </thead>
              <TableBody>
              {items.filter(item => item.done).map(item => (
                <tr key={item.id}>
                  <td className="description">{item.description}</td>
                  <td className="deadline">
                    {item.deadline && (
                      <Moment format="MMM Do YYYY">{item.deadline}</Moment>
                    )}
                  </td>
                  <td className="action-cell">
                  <Button variant="contained" className="DoneButton"
                    onClick={(event) => toggleDone(event, item.id, item.description, false, item.deadline)}
                    size="small">
                    Undo
                  </Button>
                  </td>
                  <td className="action-cell">
                    <Button startIcon={<DeleteIcon />} variant="contained"
                      className="DeleteButton" onClick={() => deleteItem(item.id)}
                      size="small">
                      Delete
                    </Button>
                  </td>
                </tr>
              ))}
              </TableBody>
            </table>
          </div>
        )}
      </div>
    );
 }
    
        export default App;