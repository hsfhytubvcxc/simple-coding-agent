export interface ChatSummary {
  id: string;
  title: string;
  updatedAt: string;
  running: boolean;
}

export interface ChatMessage {
  id: string;
  role: 'USER' | 'ASSISTANT' | 'TOOL';
  content: string;
  toolName: string | null;
  toolArguments: string | null;
  status: 'RUNNING' | 'COMPLETED' | 'FAILED';
  exitCode: number | null;
  createdAt: string;
}

export interface ChatDetail {
  id: string;
  title: string;
  port: number;
  running: boolean;
  messages: ChatMessage[];
}

export type ChatStreamEvent =
  | { type: 'text'; data: { text: string } }
  | { type: 'message'; data: ChatMessage }
  | { type: 'complete'; data: { chatId: string } };
