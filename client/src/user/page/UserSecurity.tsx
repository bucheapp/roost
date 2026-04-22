import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../../AuthContext";
import axios from "axios";
import styles from "./UserSecurity.module.css";
import "./UserSecurity.css";

const baseURL = import.meta.env.VITE_API_URL;

const UserSecurity: React.FC = () => {
	const navigate = useNavigate();
	const auth = useContext(AuthContext);

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
			<div className={styles.content}>
				<h2>セキュリティ設定</h2>

				<div className="form-group">
					<label>パスワード</label>
					<button className={styles.action_btn} onClick={handlePasswordChange}>
						パスワードを変更
					</button>
				</div>

				<div className="form-group">
					<label>ログアウト</label>
					<button className="logout-btn" onClick={handleLogout}>
						ログアウト
					</button>
				</div>
			</div>
		</div>
	);
};

export default UserSecurity;