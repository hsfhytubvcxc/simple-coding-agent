import { useEffect, useState } from 'react';
import { chatApi } from '../api/chatApi';
import type { ChatDetail, ChatStreamEvent, ChatSummary } from '../types';

export function useChat() {
  const [chats, setChats] = useState<ChatSummary[]>([]);
  const [chat, setChat] = useState<ChatDetail | null>(null);
  const [draft, setDraft] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const [streaming, setStreaming] = useState(false);
  const [partialText, setPartialText] = useState('');
  const [previewVersion, setPreviewVersion] = useState(0);

  useEffect(() => {
    async function load() {
      try {
        const items = await chatApi.list();
        setChats(items);
        if (items.length) setChat(await chatApi.get(items[0].id));
      } catch (e) {
        setError((e as Error).message);
      } finally {
        setLoading(false);
      }
    }
    void load();
  }, []);

  useEffect(() => {
    if (!chat?.running || streaming) return;
    const id = chat.id;
    const timer = window.setInterval(() => {
      chatApi.get(id)
        .then(next => setChat(current => current?.id === id ? next : current))
        .catch(e => setError(e.message));
    }, 1500);
    return () => window.clearInterval(timer);
  }, [chat?.id, chat?.running, streaming]);

  async function openChat(id: string) {
    setLoading(true);
    setError('');
    setPartialText('');
    try {
      setChat(await chatApi.get(id));
      setDraft('');
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setLoading(false);
    }
  }

  function newChat() {
    setChat(null);
    setDraft('');
    setPartialText('');
    setError('');
  }

  function receive(event: ChatStreamEvent) {
    if (event.type === 'text') {
      setPartialText(current => current + event.data.text);
    } else if (event.type === 'message') {
      const message = event.data;
      setChat(current => {
        if (!current) return current;
        const exists = current.messages.some(item => item.id === message.id);
        return {
          ...current,
          running: true,
          messages: exists
            ? current.messages.map(item => item.id === message.id ? message : item)
            : [...current.messages, message],
        };
      });
      if (message.role === 'ASSISTANT') setPartialText('');
    }
  }

  async function send() {
    const text = draft.trim();
    if (!text || loading || streaming || chat?.running) return;
    setDraft('');
    setError('');
    setPartialText('');
    setStreaming(true);
    let current = chat;
    try {
      current ??= await chatApi.create();
      setChat(current);
      await chatApi.send(current.id, text, receive);
      setPreviewVersion(version => version + 1);
    } catch (e) {
      setError((e as Error).message);
      if (!current) setDraft(text);
    } finally {
      if (current) {
        const saved = await chatApi.get(current.id).catch(() => null);
        if (saved) setChat(saved);
      }
      const items = await chatApi.list().catch(() => null);
      if (items) setChats(items);
      setPartialText('');
      setStreaming(false);
    }
  }

  async function deleteChat() {
    if (!chat || !window.confirm('Удалить приложение, его файлы и историю чата?')) return;
    setLoading(true);
    setError('');
    try {
      await chatApi.remove(chat.id);
      newChat();
      setChats(await chatApi.list());
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setLoading(false);
    }
  }

  return {
    chats, chat, draft, setDraft, error, partialText, previewVersion,
    loading, streaming, busy: loading || streaming || Boolean(chat?.running),
    openChat, newChat, send, deleteChat,
    refreshPreview: () => setPreviewVersion(version => version + 1),
  };
}
