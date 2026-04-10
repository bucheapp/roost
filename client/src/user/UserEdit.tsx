import React, { useEffect, useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../AuthContext";
import { fetchWithAuth } from "../utils/fetchWithAuth";
import "./UserEdit.css";

type User = {
    name: string;
    email: string;
};

const baseURL = import.meta.env.VITE_API_URL;

const UserEdit: React.FC = () => {
    const navigate = useNavigate();
    const auth = useContext(AuthContext);

    const [user, setUser] = useState<User | null>(null);
    const [initial, setInitial] = useState<User | null>(null);

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");

    useEffect(() => {
        if (!auth) return;

        fetchWithAuth(baseURL + "/api/users/me", {}, auth.accessToken, auth.setAccessToken)
            .then(res => res.json())
            .then(data => {
                setUser(data);
                setInitial(data);
                setName(data.name || "");
                setEmail(data.email || "");
            })
            .catch(err => {
                console.error(err);
                navigate("/login");
            });
    }, [auth, navigate]);

    const isChanged = () => {
        if (!initial) return false;
        return (
            name !== (initial.name || "") ||
            email !== (initial.email || "")
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

        try {
            const res = await fetchWithAuth(
                baseURL + "/api/users/me",
                {
                    method: "PATCH",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        name: name !== initial.name ? name : null,
                        email: email !== initial.email ? email : null,
                    }),
                },
                auth.accessToken,
                auth.setAccessToken
            );

            if (!res.ok) throw new Error();

            navigate("/user/me/profile");
        } catch (err) {
            console.error(err);
            alert("変更に失敗しました");
        }
    };

    if (!user) return <div>Loading...</div>;

    return (
        <div className="edit-layout">
            <div className="edit-content">
                <h2>ユーザー編集 {isChanged() && <span className="edited">*</span>}</h2>

                <div className="form-group">
                    <label>
                        ユーザ名 {name !== (initial?.name || "") && <span className="edited">●</span>}
                    </label>
                    <input
                        type="text"
                        value={name}
                        onChange={e => setName(e.target.value)}
                    />
                </div>

                <div className="form-group">
                    <label>
                        Email {email !== (initial?.email || "") && <span className="edited">●</span>}
                    </label>
                    <input
                        type="email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                    />
                </div>

                <div className="form-group">
                    <label>password</label>
                    <div className="form-group password-center">
                        <button
                            className="link-btn"
                            onClick={() => navigate("/user/me/password/edit")}
                        >
                            パスワード変更
                        </button>
                    </div>
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

export default UserEdit;