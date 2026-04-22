import React, { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../../AuthContext";
import axios from "axios";
import UserSidebar from "../components/UserSidebar";
import { useIsMobile } from "../../utils/useIsMobile";

import styles from "./UserSecurity.module.css";
import "./UserSecurity.css";

const baseURL = import.meta.env.VITE_API_URL;

const UserSecurity: React.FC = () => {
	const navigate = useNavigate();
	const auth = useContext(AuthContext);

	const isMobile = useIsMobile();
	const [isOpen, setIsOpen] = useState(false);

	const handlePasswordChange = () => {
		navigate("/user/me/password/edit");
	};

	const handleLogout = async () => {
		try {
			await axios.post(
				baseURL + "/api/auth/logout",
				{},
				{ withCredentials: true }
			);

			if (auth) auth.setAccessToken("");

			navigate("/");
		} catch (err) {
			console.error(err);
			alert("ログアウトに失敗しました");
		}
	};

	return (
		<div className={styles.layout}>
			<UserSidebar
				isOpen={isOpen}
				isMobile={isMobile}
				onClose={() => setIsOpen(false)}
			/>

			{isOpen && isMobile && (
				<div className={styles.overlay} onClick={() => setIsOpen(false)} />
			)}

			<div className={styles.main}>
				{isMobile && !isOpen && (
					<button
						className="open-sidebar-btn"
						onClick={() => setIsOpen(true)}
					>
						☰
					</button>
				)}

				<div className={styles.content}>
					<h2 className={styles.title}>セキュリティ設定</h2>

					<div className={styles.card}>
						<div className={styles.row}>
							<div>
								<h3>パスワード</h3>
								<p>アカウントのパスワードを変更できます</p>
							</div>

							<button
								className={styles.primaryBtn}
								onClick={handlePasswordChange}
							>
								変更
							</button>
						</div>
					</div>

					<div className={`${styles.card} ${styles.dangerCard}`}>
						<div className={styles.row}>
							<div>
								<h3>ログアウト</h3>
								<p>この端末からログアウトします</p>
							</div>

							<button
								className={styles.dangerBtn}
								onClick={handleLogout}
							>
								ログアウト
							</button>
						</div>
					</div>

				</div>
			</div>
		</div>
	);
};

export default UserSecurity;