import { Code2, ExternalLink, Globe, RefreshCw } from 'lucide-react';

interface Props {
  chatId?: string;
  port?: number;
  version: number;
  onRefresh: () => void;
}

export function PreviewPanel({ chatId, port, version, onRefresh }: Props) {
  const url = port ? 'http://' + window.location.hostname + ':' + port : null;
  return (
    <section className="preview-panel">
      <header className="panel-header">
        <div className="preview-heading"><Globe size={17} /><strong>Предпросмотр</strong></div>
        {url && <div className="preview-actions">
          <button className="icon-button" aria-label="Обновить предпросмотр" title="Обновить" onClick={onRefresh}>
            <RefreshCw size={16} />
          </button>
          <a className="icon-button" href={url} target="_blank" rel="noreferrer"
            aria-label="Открыть приложение" title="Открыть в новой вкладке"><ExternalLink size={16} /></a>
        </div>}
      </header>
      {url ? <>
        <div className="preview-address"><span className="activity-dot" />{url}</div>
        <iframe key={chatId + '-' + version} title="Созданное приложение" src={url} />
      </> : <div className="preview-empty">
        <div className="preview-placeholder"><Code2 size={36} strokeWidth={1.3} /></div>
        <h2>Здесь появится приложение</h2>
        <p>Начните с сообщения агенту.<br />Результат можно будет сразу проверить.</p>
      </div>}
    </section>
  );
}
