import React from "react";

type Props = {
	roomPublicId?: string;
};

const ChatArea: React.FC<Props> = ({ roomPublicId }) => {
	return (
		<div className="chat-area">
			チャットエリア
			{roomPublicId && (
				<div>Room: {roomPublicId}</div>
			)}
		</div>
	);
};

export default ChatArea;