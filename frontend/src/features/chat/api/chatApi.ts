import { apiError, request } from '../../../shared/api/http';
import type { ChatDetail, ChatStreamEvent, ChatSummary } from '../types';

export const chatApi = {
  list: () => request<ChatSummary[]>('/chats'),
  create: () => request<ChatDetail>('/chats', { method: 'POST' }),
  get: (id: string) => request<ChatDetail>('/chats/' + id),
  remove: (id: string) => request<void>('/chats/' + id, { method: 'DELETE' }),

  async send(id: string, text: string, onEvent: (event: ChatStreamEvent) => void) {
    const response = await fetch('/api/chats/' + id + '/messages', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Accept: 'text/event-stream' },
      body: JSON.stringify({ text }),
    });
    if (!response.ok) throw await apiError(response);
    if (!response.body) throw new Error('Сервер не вернул поток ответа');

    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let buffer = '';
    let completed = false;
    try {
      while (true) {
        const { value, done } = await reader.read();
        if (done) break;
        buffer = (buffer + decoder.decode(value, { stream: true })).replace(/\r\n/g, '\n');
        let boundary;
        while ((boundary = buffer.indexOf('\n\n')) !== -1) {
          const frame = buffer.slice(0, boundary);
          buffer = buffer.slice(boundary + 2);
          const lines = frame.split('\n');
          const type = lines.find(line => line.startsWith('event:'))?.slice(6).trim();
          const data = lines.filter(line => line.startsWith('data:'))
            .map(line => line.slice(5).trimStart()).join('\n');
          if (!type || !data) continue;
          const payload = JSON.parse(data);
          if (type === 'error') throw new Error(payload.message);
          if (type === 'complete') completed = true;
          onEvent({ type, data: payload } as ChatStreamEvent);
        }
      }
      if (!completed) throw new Error('Соединение прервалось. История работы сохранена.');
    } finally {
      await reader.cancel();
    }
  },
};
