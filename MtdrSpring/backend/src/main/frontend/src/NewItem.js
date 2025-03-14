/*
## MyToDoReact version 1.0.
##
## Copyright (c) 2022 Oracle, Inc.
## Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl/
*/
/*
 * Component that supports creating a new todo item.
 * @author  jean.de.lavarene@oracle.com
 */

import React, { useState } from "react";
import Button from '@mui/material/Button';


function NewItem(props) {
  const [item, setItem] = useState('');
  const [deadline, setDeadline] = useState('');
  
  function handleSubmit(e) {
    e.preventDefault();
    if (!item.trim()) {
      return;
    }
    
    props.addItem(item, deadline);
    setItem("");
    setDeadline("");
  }
  
  function handleChange(e) {
    setItem(e.target.value);
  }
  
  function handleDeadlineChange(e) {
    setDeadline(e.target.value);
  }
  
  return (
    <div id="newinputform">
        <table className="table-input">
            <tr>
                <th className="primera-columna">Task description</th>
                <th className="segunda-columna">Deadline</th>
                <th className="tercera-columna"></th>
            </tr>
            <tr>
                <td className="primera-columna">
                  <input
                    id="newiteminput"
                    placeholder="New item"
                    type="text"
                    autoComplete="off"
                    value={item}
                    onChange={handleChange}
                    onKeyDown={event => {
                      if (event.key === 'Enter') {
                        handleSubmit(event);
                      }
                    }}
                  />
                </td>
                <td className="segunda-columna">
                  <input
                    id="deadlineinput"
                    type="date"
                    value={deadline}
                    onChange={handleDeadlineChange}
                  />
                </td>
                <td className="tercera-columna">  
                  <Button
                    className="AddButton"
                    variant="contained"
                    disabled={props.isInserting}
                    onClick={!props.isInserting ? handleSubmit : null}
                    size="small"
                  >
                    {props.isInserting ? 'Adding…' : 'Add'}
                  </Button>
                </td>
            </tr>
        </table>
    </div>
  );
}

export default NewItem;