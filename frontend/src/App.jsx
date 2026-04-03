import { useState } from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css'
import Code_editor from './pages/Dashboard';

function App() {
 

  return (
    <>
    <Router> 
    
      <Routes>
        <Route path="/" element={<Code_editor />} />
      
      </Routes>
    </Router>
  );
    </>
  )
}

export default App
