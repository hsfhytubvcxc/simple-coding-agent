import { useEffect, useRef } from 'react';
import { Bot, Loader2, Sparkles, User } from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import type { ChatMessage } from '../types';
import { ToolExecutionCard } from './ToolExecutionCard';

interface Props {
  messages: ChatMessage[];
  partialText: string;
  busy: boolean;
  onSuggestion: (text: string) => void;
}

const suggestions = [
  ['Список задач', 'Создай список задач с добавлением, выполнением и удалением задач'],
  ['Форма заявки', 'Создай красивую форму заявки: имя, email, описание и подтверждение отправки'],
  ['Каталог товаров', 'Создай каталог товаров с поиском и карточками товаров на тестовых данных'],
];

export function MessageList({ messages, partialText, busy, onSuggestion }: Props) {
  const end = useRef<HTMLDivElement>(null);
  useEffect(() => {
    end.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, partialText, busy]);

  return (
    <div className="messages">
      {!messages.length && !busy && <div className="chat-empty">
        <div className="empty-icon"><Sparkles size={24} /></div>
        <h1>Что вы хотите создать?</h1>
        <p>Опишите идею. Агент напишет код, проверит его<br />и откроет приложение рядом с чатом.</p>
        <div className="suggestions">
          {suggestions.map(([label, text]) => (
            <button key={label} onClick={() => onSuggestion(text)}>{label}</button>
          ))}
        </div>
      </div>}
      {messages.map(message => message.role === 'TOOL'
        ? <ToolExecutionCard key={message.id} message={message} />
        : <article key={message.id} className={'message ' + message.role.toLowerCase()}>
          <div className="message-avatar">{message.role === 'USER' ? <User size={16} /> : <Bot size={17} />}</div>
          <div className="message-body">
            <div className="message-author">{message.role === 'USER' ? 'Вы' : 'Агент'}</div>
            <div className="markdown"><ReactMarkdown>{message.content}</ReactMarkdown></div>
          </div>
        </article>)}
      {partialText && <article className="message assistant">
        <div className="message-avatar"><Bot size={17} /></div>
        <div className="message-body">
          <div className="message-author">Агент</div>
          <div className="markdown"><ReactMarkdown>{partialText}</ReactMarkdown><span className="typing-cursor" /></div>
        </div>
      </article>}
      {busy && !partialText && <div className="agent-working"><Loader2 size={15} className="spin" /> Агент работает…</div>}
      <div ref={end} />
    </div>
  );
}
