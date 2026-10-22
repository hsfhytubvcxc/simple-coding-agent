import { CircleAlert, Trash2 } from 'lucide-react';
import type { ChatDetail } from '../types';
import { MessageComposer } from './MessageComposer';
import { MessageList } from './MessageList';

interface Props {
  chat: ChatDetail | null;
  draft: string;
  partialText: string;
  error: string;
  busy: boolean;
  onDraft: (value: string) => void;
  onSend: () => void;
  onDelete: () => void;
}

export function ChatPanel({ chat, draft, partialText, error, busy, onDraft, onSend, onDelete }: Props) {
  return (
    <main className="chat-panel">
      <header className="panel-header">
        <div className="panel-heading"><strong>{chat?.title || 'Новое приложение'}</strong>
          <span>{busy ? 'Агент выполняет задачу' : 'Опишите, что нужно сделать'}</span>
        </div>
        {chat && <button className="icon-button danger" aria-label="Удалить приложение"
          title="Удалить приложение" disabled={busy} onClick={onDelete}><Trash2 size={17} /></button>}
      </header>
      <MessageList messages={chat?.messages || []} partialText={partialText}
        busy={busy} onSuggestion={onDraft} />
      {error && <div role="alert" className="error-banner"><CircleAlert size={17} /><span>{error}</span></div>}
      <MessageComposer value={draft} disabled={busy} onChange={onDraft} onSend={onSend} />
    </main>
  );
}
