import { ChatPanel } from '../features/chat/components/ChatPanel';
import { ChatSidebar } from '../features/chat/components/ChatSidebar';
import { useChat } from '../features/chat/hooks/useChat';
import { PreviewPanel } from '../features/preview/PreviewPanel';

export function App() {
  const workspace = useChat();
  return (
    <div className="workspace">
      <ChatSidebar chats={workspace.chats} activeId={workspace.chat?.id}
        disabled={workspace.busy} onNew={workspace.newChat} onOpen={workspace.openChat} />
      <ChatPanel chat={workspace.chat} draft={workspace.draft} partialText={workspace.partialText}
        error={workspace.error} busy={workspace.busy} onDraft={workspace.setDraft}
        onSend={workspace.send} onDelete={workspace.deleteChat} />
      <PreviewPanel chatId={workspace.chat?.id} port={workspace.chat?.port}
        version={workspace.previewVersion} onRefresh={workspace.refreshPreview} />
    </div>
  );
}
