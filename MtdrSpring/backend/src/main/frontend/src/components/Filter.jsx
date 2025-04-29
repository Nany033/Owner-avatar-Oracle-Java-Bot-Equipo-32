import React, { useState } from 'react';

const Filter = () => {
    const [selectedOption, setSelectedOption] = useState('');

    const handleChange = (event) => {
        setSelectedOption(event.target.value);
    };

    return (
        <div>
            <label htmlFor="filter-dropdown">Filter Options:</label>
            <select
                id="filter-dropdown"
                value={selectedOption}
                onChange={handleChange}
            >
                <option value="">Select an option</option>
                <option value="option1">Option 1</option>
                <option value="option2">Option 2</option>
                <option value="option3">Option 3</option>
            </select>
            {selectedOption && <p>You selected: {selectedOption}</p>}
        </div>
    );
};

export default Filter;