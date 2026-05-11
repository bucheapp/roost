import React from "react";
import "./App.css";
import Home from "./home/page/Home";
import Signup from "./signup/page/Signup";
import Login from "./login/page/Login";
import UserProfile from "./user/page/UserProfile";
import UserProfileEdit from "./user/page/UserProfileEdit";
import UserPasswordEdit from "./user/page/UserPasswordEdit";
import UserEdit from "./user/page/UserEdit";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import UserManagement from "./user/page/UserManagement";
import UserCreate from "./user/page/UserCreate";
import UserCommunity from "./user/page/UserCommunity";
import UserSecurity from "./user/page/UserSecurity"
import Community from "./community/page/Community"

const App: React.FC = () => {
	return (
		<BrowserRouter>
			<Routes>
				<Route path="/" element={<Home />} />
				<Route path="/community">
					<Route path=":communityPublicId">
						<Route path="room">
							<Route path=":roomPublicId" element={<Community />} />
						</Route>
					</Route>
				</Route>
				<Route path="/signup" element={<Signup />} />
				<Route path="/login" element={<Login />} />
				<Route path="/user">
					<Route path="me">
						<Route path="profile" element={<UserProfile />} />
						<Route path="edit" element={<UserEdit />} />
						<Route path="profile/edit" element={<UserProfileEdit />} />
						<Route path="password/edit" element={<UserPasswordEdit />} />
						<Route path="community" element={<UserCommunity />} />
						<Route path="security" element={<UserSecurity />} />
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