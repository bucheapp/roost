import React from "react";
import "./RoomSidebar.css";

type Room = {
	name: string;
	publicId: number;
};

type Props = {
	rooms: Room[];
	selectedRoomId?: string;
	onSelect: (roomId: number) => void;
};

const RoomSidebar: React.FC<Props> = ({
	rooms,
	selectedRoomId,
	onSelect
}) => {
	return (
		<div className="room-sidebar">
			{rooms.map(room => {
				const isActive = String(room.publicId) === selectedRoomId;

				return (
					<div
						key={room.publicId}
						className={`room-item ${isActive ? "active" : ""}`}
						onClick={() => onSelect(room.publicId)}
					>
						{room.name}
					</div>
				);
			})}
		</div>
	);
};

export default RoomSidebar;