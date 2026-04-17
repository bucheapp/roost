import React, { useState, useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import "./Home.css";
import Sidebar from "./Sidebar";
import { AuthContext } from "../AuthContext";
import { fetchWithAuth } from "../utils/fetchWithAuth";

function useIsMobile() {
	const [isMobile, setIsMobile] = useState(
		window.matchMedia("(max-width: 768px)").matches
	);

	useEffect(() => {
		const media = window.matchMedia("(max-width: 768px)");
		const listener = (e: MediaQueryListEvent) => setIsMobile(e.matches);
		media.addEventListener("change", listener);
		return () => media.removeEventListener("change", listener);
	}, []);

	return isMobile;
}

const baseURL = import.meta.env.VITE_API_URL;

const Home: React.FC = () => {
	const [isOpen, setIsOpen] = useState(false);
	const isMobile = useIsMobile();
	const navigate = useNavigate();
	const auth = useContext(AuthContext);

	const [iconUrl, setIconUrl] = useState<string>(
		baseURL + "/icons/default_icon.jpg"
	);
	const [menuOpen, setMenuOpen] = useState(false);

	const [isLoggedIn, setIsLoggedIn] = useState<boolean | null>(null);

	useEffect(() => {
		const checkAuth = async () => {
			try {
				const res = await fetch(baseURL + "/api/auth/me", {
					method: "GET",
					credentials: "include",
				});

				if (!res.ok) {
					setIsLoggedIn(false);
					return;
				}

				const text = await res.text();
				const data = text ? JSON.parse(text) : null;

				if(data == null) {
					setIsLoggedIn(false);
				} else {
					setIsLoggedIn(true);
				}
			} catch (err) {
				console.error(err);
				setIsLoggedIn(false);
			}
		};

		checkAuth();
	}, [auth]);

	useEffect(() => {
		if (!auth || !isLoggedIn) return;

		const fetcher = (url: string) =>
			fetchWithAuth(url, {}, auth.accessToken, auth.setAccessToken);

		fetcher(baseURL + "/api/users/me/profile")
			.then(res => {
				if (res.status === 404) return null;
				return res.json();
			})
			.then(data => {
				if (!data) return;
				setIconUrl(baseURL + "/icons/" + data.iconUrl + ".jpg");
			})
			.catch(err => console.error(err));
	}, [auth, isLoggedIn]);

	if (isLoggedIn === null) {
		return <div>Loading...</div>;
	}

	return (
		<div className="layout">
			<Sidebar
				isOpen={isOpen}
				isMobile={isMobile}
				onClose={() => setIsOpen(false)}
			/>

			{isOpen && isMobile && (
				<div className="overlay" onClick={() => setIsOpen(false)} />
			)}

			<div className="main">
				<button
					className="open-btn"
					onClick={() => setIsOpen(true)}
					style={{ display: isMobile && !isOpen ? "block" : "none" }}
				>
					☰
				</button>

				<div className="content">中央コンテンツ</div>

				<div
					className="user-icon-wrapper"
					onMouseEnter={() => isLoggedIn && setMenuOpen(true)}
					onMouseLeave={() => setMenuOpen(false)}
				>
					<img
						src={iconUrl}
						className="icon"
						alt="user icon"
						onClick={() => {
							if (!isLoggedIn) {
								navigate("/login");
							}
						}}
					/>

					{menuOpen && isLoggedIn && (
						<div className="dropdown">
							<div onClick={() => navigate("/user/me/profile")}>
								プロフィール
							</div>
							<div onClick={() => navigate("/user/me/community")}>
								コミュニティ
							</div>
						</div>
					)}
				</div>
			</div>
		</div>
	);
};

export default Home;