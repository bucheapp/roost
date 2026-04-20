import React from "react";
import "./App.css";
import Home from "./home/Home";
import Signup from "./signup/Signup";
import Login from "./login/Login";
import UserProfile from "./user/UserProfile";
import UserProfileEdit from "./user/UserProfileEdit";
import UserPasswordEdit from "./user/UserPasswordEdit";
import UserEdit from "./user/UserEdit";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import UserManagement from "./user/UserManagement";
import UserCreate from "./user/UserCreate";
import UserCommunity from "./user/UserCommunity";

const App: React.FC = () => {
	return (
		<BrowserRouter>
			<Routes>
				<Route path="/" element={<Home />} />
				<Route path="/signup" element={<Signup />} />
				<Route path="/login" element={<Login />} />
				<Route path="/user">
					<Route path="me">
						<Route path="profile" element={<UserProfile />} />
						<Route path="edit" element={<UserEdit />} />
						<Route path="profile/edit" element={<UserProfileEdit />} />
						<Route path="password/edit" element={<UserPasswordEdit />} />
						<Route path="community" element={<UserCommunity />} />
					</Route>
					<Route path="create" element={<UserCreate />} />

					<Route path=":publicId">
						<Route path="profile" element={<UserProfile />} />
						<Route path="management" element={<UserManagement />} />
						<Route path="community" element={<UserCommunity />} />
					</Route>
				</Route>
			</Routes>
		</BrowserRouter>
	);
};


export default App;