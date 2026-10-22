import { Code2, MessageSquare, Plus } from 'lucide-react';
import type { ChatSummary } from '../types';

interface Props {
  chats: ChatSummary[];
  activeId?: string;
  disabled: boolean;
  onNew: () => void;
  onOpen: (id: string) => void;
}

export function ChatSidebar({ chats, activeId, disabled, onNew, onOpen }: Props) {
  return (
    <aside className="sidebar">
      <div className="brand"><span className="brand-icon"><Code2 size={22} /></span>
        <div><strong>Coding Agent</strong><span>Ваше рабочее пространство</span></div>
      </div>
      <button className="new-chat" disabled={disabled} onClick={onNew}>
        <Plus size={18} /> Новое приложение
      </button>
      <div className="sidebar-label">Приложения <span>{chats.length}</span></div>
      <nav className="chat-list" aria-label="История приложений">
        {chats.map(chat => (
          <button key={chat.id} className={'chat-link ' + (chat.id === activeId ? 'active' : '')}
            disabled={disabled} onClick={() => onOpen(chat.id)}>
            <MessageSquare size={17} />
            <span>{chat.title}</span>
            {chat.running && <i className="activity-dot" />}
          </button>
        ))}
        {!chats.length && <p className="sidebar-empty">Созданные приложения появятся здесь.</p>}
      </nav>
    </aside>
  );
}
