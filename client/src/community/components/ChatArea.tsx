import React, { useEffect, useRef, useState } from "react";
import "./ChatArea.css";
import { fetchWithAuth } from "../../utils/fetchWithAuth";

const baseURL = import.meta.env.VITE_API_URL;

type MediaType = "IMAGE" | "VIDEO" | "AUDIO";

type MediaContent = {
	mediaContentUUID: string;
	type: MediaType;
};

type ChatType = "TEXT" | "APPROVAL";

type ChatResponse = {
	type: ChatType;
	publicId: number;
	roomId: number;
	creatorId: number;
};

type TextChatResponse = ChatResponse & {
	content: string;
	mediaContent?: MediaContent;
	edited: boolean;
};

type ApprovalChatResponse = ChatResponse & {
	targetId: number;
	approverId?: number;
};

type Props = {
	roomPublicId?: string;
	communityPublicId?: string;
	community?: any;
	accessToken?: string;
	setAccessToken: (t: string | null) => void;
};

const PAGE_SIZE = 20;

const ChatArea: React.FC<Props> = ({
	roomPublicId,
	communityPublicId,
	community,
	accessToken,
	setAccessToken,
}) => {
	const [chats, setChats] = useState<ChatResponse[]>([]);
	const [page, setPage] = useState(0);
	const [hasMore, setHasMore] = useState(true);
	const [loading, setLoading] = useState(false);

	const [me, setMe] = useState<any>(null);
	const [input, setInput] = useState("");

	const containerRef = useRef<HTMLDivElement | null>(null);

	useEffect(() => {
		fetch(baseURL + "/api/users/me")
			.then(res => res.json())
			.then(setMe);
	}, []);

	const fetchChats = async (pageNum: number) => {
		if (!roomPublicId || loading) return;

		setLoading(true);

		const res = await fetchWithAuth(
			`${baseURL}/api/rooms/${roomPublicId}/chats?page=${pageNum}&size=${PAGE_SIZE}`,
			{},
			accessToken ?? null,
			setAccessToken
		);

		const data = await res.json();
		const newChats: ChatResponse[] = data.chats || [];

		if (newChats.length < PAGE_SIZE) setHasMore(false);

		setChats(prev => [...newChats, ...prev]);
		setPage(pageNum);
		setLoading(false);
	};

	useEffect(() => {
		if (!roomPublicId) return;
		setChats([]);
		setPage(0);
		setHasMore(true);
		fetchChats(0);
	}, [roomPublicId]);

	const onScroll = async () => {
		const el = containerRef.current;
		if (!el) return;

		if (el.scrollTop === 0 && hasMore && !loading) {
			const prevHeight = el.scrollHeight;

			await fetchChats(page + 1);

			requestAnimationFrame(() => {
				if (!containerRef.current) return;
				containerRef.current.scrollTop =
					containerRef.current.scrollHeight - prevHeight;
			});
		}
	};

	const sendChat = async () => {
		if (!input.trim() || !roomPublicId) return;

		const form = new FormData();
		form.append("type", "TEXT");
		form.append("content", input);

		await fetchWithAuth(
			`${baseURL}/api/chats/${roomPublicId}`,
			{
				method: "POST",
				body: form,
			},
			accessToken ?? null,
			setAccessToken
		);

		setInput("");
		setChats([]);
		fetchChats(0);
	};

	const approve = async (targetUserId: number) => {
		if (!communityPublicId || !accessToken || !setAccessToken) return;

		await fetchWithAuth(
			`${baseURL}/api/community/${communityPublicId}/members/${targetUserId}/approve`,
			{
				method: "POST",
			},
			accessToken,
			setAccessToken
		);

		setChats([]);
		fetchChats(0);
	};

	const join = async () => {
		if (!me || !communityPublicId) return;

		await fetchWithAuth(
			`${baseURL}/api/communities/${communityPublicId}/members`,
			{
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify({ publicId: me.publicId }),
			},
			accessToken ?? null,
			setAccessToken
		);
	};

	const renderMedia = (m?: MediaContent) => {
		if (!m) return null;

		const url =
			m.type === "IMAGE"
				? `/image/${m.mediaContentUUID}`
				: m.type === "VIDEO"
					? `/video/${m.mediaContentUUID}`
					: `/audio/${m.mediaContentUUID}`;

		if (m.type === "IMAGE") return <img src={url} className="media" />;
		if (m.type === "VIDEO") return <video src={url} controls className="media" />;
		if (m.type === "AUDIO") return <audio src={url} controls className="media" />;
	};

	const getUserIcon = (creatorId: number) =>
		`${baseURL}/api/users/${creatorId}/profile`;

	return (
		<div className="chat-wrapper">
			<div className="chat-list" ref={containerRef} onScroll={onScroll}>
				{chats.map((c) => (
					<div key={c.publicId} className="chat-item">
						<img className="icon" src={getUserIcon(c.creatorId)} />

						<div className="body">
							{/* TEXT */}
							{c.type === "TEXT" && (
								<>
									<div className="text">
										{(c as TextChatResponse).content}
									</div>
									{renderMedia((c as TextChatResponse).mediaContent)}
								</>
							)}

							{/* APPROVAL */}
							{c.type === "APPROVAL" && (() => {
								const chat = c as ApprovalChatResponse;

								return (
									<div className="approval">
										{chat.approverId ? (
											<>
												<div>承認済み</div>
												<div>
													申請者: {chat.targetId} / 承認者: {chat.approverId}
												</div>
											</>
										) : (
											<button onClick={() => approve(chat.targetId)}>
												承認する
											</button>
										)}
									</div>
								);
							})()}
						</div>
					</div>
				))}
			</div>

			{/* 入力 */}
			<div className="chat-input">
				<input
					value={input}
					onChange={(e) => setInput(e.target.value)}
					placeholder="メッセージ..."
				/>
				<button onClick={sendChat}>送信</button>
			</div>

			{/* 権限UI */}
			<div className="chat-actions">
				{community?.type === "OPEN" && (
					<button onClick={join}>メンバーになる</button>
				)}

				{community && !["FREE", "OPEN"].includes(community.type) && (
					<button onClick={join}>メンバーを申請する</button>
				)}
			</div>
		</div>
	);
};

export default ChatArea;