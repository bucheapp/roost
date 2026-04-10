import React, { useEffect, useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../AuthContext";
import { fetchWithAuth } from "../utils/fetchWithAuth";
import "./UserProfileEdit.css";

type Profile = {
	bio: string;
	iconUrl: string;
	gender: string;
	dateOfBirth: string;
	address: string;
	githubUrl: string;
};

const baseURL = import.meta.env.VITE_API_URL;

const UserProfileEdit: React.FC = () => {
	const navigate = useNavigate();
	const auth = useContext(AuthContext);

	const [profile, setProfile] = useState<Profile | null>(null);

	const [bio, setBio] = useState("");
	const [iconFile, setIconFile] = useState<File | null>(null);
	const [gender, setGender] = useState("");
	const [dateOfBirth, setDateOfBirth] = useState("");
	const [address, setAddress] = useState("");
	const [githubUrl, setGithubUrl] = useState("");

	const [initial, setInitial] = useState<Profile | null>(null);

	useEffect(() => {
		if (!auth) return;

		fetchWithAuth(baseURL + "/api/users/me/profile", {}, auth.accessToken, auth.setAccessToken)
			.then(res => res.json())
			.then(data => {
				setProfile(data);
				setInitial(data);

				setBio(data.bio || "");
				setGender(data.gender || "");
				setDateOfBirth(data.dateOfBirth || "");
				setAddress(data.address || "");
				setGithubUrl(data.githubUrl || "");
			})
			.catch(err => {
				console.error(err);
				navigate("/login");
			});
	}, [auth, navigate]);

	const isChanged = () => {
		if (!initial) return false;
		return (
			bio !== (initial.bio || "") ||
			gender !== (initial.gender || "") ||
			dateOfBirth !== (initial.dateOfBirth || "") ||
			address !== (initial.address || "") ||
			githubUrl !== (initial.githubUrl || "") ||
			iconFile !== null
		);
	};

	const handleClose = () => {
		if (isChanged()) {
			const ok = window.confirm("保存されていない変更があります。閉じますか？");
			if (!ok) return;
		}
		navigate(-1);
	};

	const handleSubmit = async () => {
		if (!auth || !initial) return;

		const formData = new FormData();

		if (bio !== (initial.bio || "")) formData.append("bio", bio);
		if (gender !== (initial.gender || "")) formData.append("gender", gender);
		if (dateOfBirth !== (initial.dateOfBirth || "")) formData.append("dateOfBirth", dateOfBirth);
		if (address !== (initial.address || "")) formData.append("address", address);
		if (githubUrl !== (initial.githubUrl || "")) formData.append("githubUrl", githubUrl);

		if (iconFile) {
			formData.append("iconFile", iconFile);
		}

		try {
			const res = await fetchWithAuth(
				baseURL + "/api/users/me/profile",
				{
					method: "PATCH",
					body: formData,
				},
				auth.accessToken,
				auth.setAccessToken
			);

			if (!res.ok) throw new Error();

			navigate("/user/me/profile");
		} catch (err) {
			console.error(err);
			alert("保存に失敗しました");
		}
	};

	if (!profile) return <div>Loading...</div>;

	return (
		<div className="edit-layout">
			<div className="edit-content">
				<h2>プロフィール編集 {isChanged() && <span className="edited">*</span>}</h2>

				<div className="form-group">
					<label>アイコン {iconFile && <span className="edited">●</span>}</label>
					<input
						type="file"
						accept="image/*"
						onChange={e => setIconFile(e.target.files?.[0] || null)}
					/>
				</div>

				<div className="form-group">
					<label>自己紹介 {bio !== (initial?.bio || "") && <span className="edited">●</span>}</label>
					<textarea value={bio} onChange={e => setBio(e.target.value)} />
				</div>

				<div className="form-group">
					<label>性別 {gender !== (initial?.gender || "") && <span className="edited">●</span>}</label>
					<select value={gender} onChange={e => setGender(e.target.value)}>
						<option value="">未設定</option>
						<option value="MALE">男性</option>
						<option value="FEMALE">女性</option>
						<option value="OTHER">その他</option>
					</select>
				</div>

				<div className="form-group">
					<label>誕生日 {dateOfBirth !== (initial?.dateOfBirth || "") && <span className="edited">●</span>}</label>
					<input
						type="date"
						value={dateOfBirth ? dateOfBirth.substring(0, 10) : ""}
						onChange={e => setDateOfBirth(e.target.value)}
					/>
				</div>

				<div className="form-group">
					<label>住所 {address !== (initial?.address || "") && <span className="edited">●</span>}</label>
					<input
						type="text"
						value={address}
						onChange={e => setAddress(e.target.value)}
					/>
				</div>

				<div className="form-group">
					<label>Github {githubUrl !== (initial?.githubUrl || "") && <span className="edited">●</span>}</label>
					<input
						type="text"
						value={githubUrl}
						onChange={e => setGithubUrl(e.target.value)}
					/>
				</div>

				<div className="button-group">
					<button className="save-btn" onClick={handleSubmit}>
						保存
					</button>
					<button className="cancel-btn" onClick={handleClose}>
						閉じる
					</button>
				</div>
			</div>
		</div>
	);
};

export default UserProfileEdit;