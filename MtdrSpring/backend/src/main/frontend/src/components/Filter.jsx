import React, { useState } from 'react';

const Filter = ({ options, onSelect }) => {
    const [selectedOption, setSelectedOption] = useState('');

    const handleChange = (event) => {
        const value = event.target.value;
        setSelectedOption(value);
        onSelect(value); // Notify parent
    };

    return (
        <div className='Filter'>
            <label htmlFor="filter-dropdown">Filter Options:</label>
            <select
                id="filter-dropdown"
                value={selectedOption}
                onChange={handleChange}
            >
                <option value="">Select an option</option>
                {options.map(option => (
                    <option key={option.userId} value={option.userId}>
                        {option.name}
                    </option>
                ))}
            </select>
            {selectedOption && <p>You selected: {selectedOption}</p>}
        </div>
    );
};

export default Filter;
