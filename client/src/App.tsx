import React from "react";
import "./App.css";
import Home from "./home/Home";
import Signup from "./signup/Signup";
import Login from "./login/Login";
import User from "./user/User";
import { BrowserRouter, Routes, Route } from "react-router-dom";

const App: React.FC = () => {
	return (
		<BrowserRouter>
			<Routes>
				<Route path="/" element={<Home />} />
				<Route path="/signup" element={<Signup />} />
				<Route path="/login" element={<Login />} />
				<Route path="/user" element={<User />} />
			</Routes>
		</BrowserRouter>
	);
};


export default App;