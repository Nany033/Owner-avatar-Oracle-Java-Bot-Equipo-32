import React from 'react';
import ToDoItems from '../components/ToDoItems';

export default function ToDoList () {
    return (
        <div className='todo-list'>
            <h1>To-Do List</h1>
            <p>Welcome to your To-Do List. Start adding your tasks!</p>
            <ToDoItems />
        </div>
    );
};
