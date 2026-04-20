import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../../AuthContext";
import { fetchWithAuth } from "../../utils/fetchWithAuth";
import { useUnsavedChanges } from "../../utils/useUnsavedChanges"
import "./UserProfileEdit.css";

const baseURL = import.meta.env.VITE_API_URL;

const UserPasswordEdit: React.FC = () => {
	const navigate = useNavigate();
	const auth = useContext(AuthContext);

	const [currentPassword, setCurrentPassword] = useState("");
	const [newPassword, setNewPassword] = useState("");
	const [confirmPassword, setConfirmPassword] = useState("");

	const isChanged = () => {
		return currentPassword !== "" || newPassword !== "" || confirmPassword !== "";
	};

	const handleClose = () => {
		const { confirmClose } = useUnsavedChanges(isChanged);
		if (!confirmClose()) return;
		navigate(-1);
	};

	const handleSubmit = async () => {
		if (!auth) return;

		if (!currentPassword || !newPassword || !confirmPassword) {
			alert("すべて入力してください");
			return;
		}

		if (newPassword !== confirmPassword) {
			alert("新しいパスワードが一致しません");
			return;
		}

		try {
			const res = await fetchWithAuth(
				baseURL + "/api/users/me/password",
				{
					method: "PATCH",
					headers: {
						"Content-Type": "application/json",
					},
					body: JSON.stringify({
						currentPassword,
						newPassword,
					}),
				},
				auth.accessToken,
				auth.setAccessToken
			);

			if (!res.ok) {
				if (res.status === 400 || res.status === 401) {
					alert("現在のパスワードが違います");
					return;
				}
				throw new Error();
			}

			alert("パスワードを変更しました");
			navigate("/user/me/profile");
		} catch (err) {
			console.error(err);
			alert("変更に失敗しました");
		}
	};

	return (
		<div className="edit-layout">
			<div className="edit-content">
				<h2>パスワード変更 {isChanged() && <span className="edited">*</span>}</h2>

				<div className="form-group">
					<label>現在のパスワード</label>
					<input
						type="password"
						value={currentPassword}
						onChange={e => setCurrentPassword(e.target.value)}
					/>
				</div>

				<div className="form-group">
					<label>新しいパスワード</label>
					<input
						type="password"
						value={newPassword}
						onChange={e => setNewPassword(e.target.value)}
					/>
				</div>

				<div className="form-group">
					<label>新しいパスワード（確認）</label>
					<input
						type="password"
						value={confirmPassword}
						onChange={e => setConfirmPassword(e.target.value)}
					/>
				</div>

				<div className="button-group">
					<button
						className="save-btn"
						onClick={handleSubmit}
						disabled={!isChanged()}
					>
						変更
					</button>
					<button className="cancel-btn" onClick={handleClose}>
						閉じる
					</button>
				</div>
			</div>
		</div>
	);
};

export default UserPasswordEdit;