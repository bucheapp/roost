import React from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import "./UserSidebar.css";

type Props = {
  isOpen: boolean;
  isMobile: boolean;
  onClose: () => void;
};

const UserSidebar: React.FC<Props> = ({ isOpen, isMobile, onClose }) => {
  const navigate = useNavigate();
  const { publicId } = useParams();
  const location = useLocation();

  const isMe = !publicId;

  const menu = [
    { label: "プロフィール", path: isMe ? "/user/me/profile" : `/user/${publicId}/profile` },
    { label: "所属コミュニティ", path: isMe ? "/user/me/community" : `/user/${publicId}/community` }
  ];

  return (
    <div className={`sidebar ${isOpen ? "open" : ""}`}>
      {(isMobile && isOpen) && (
        <button className="close-btn" onClick={onClose}>×</button>
      )}

      <div className="sidebar-header">ユーザー</div>

      <div className="sidebar-menu">
        {menu.map((item) => (
          <div
            key={item.label}
            className={`sidebar-item ${
              location.pathname === item.path ? "active" : ""
            }`}
            onClick={() => {
              navigate(item.path);
              if (isMobile) onClose();
            }}
          >
            {item.label}
          </div>
        ))}
      </div>
    </div>
  );
};

export default UserSidebar;