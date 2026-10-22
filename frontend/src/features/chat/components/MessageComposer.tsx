import { ArrowUp, Loader2 } from 'lucide-react';

interface Props {
  value: string;
  disabled: boolean;
  onChange: (value: string) => void;
  onSend: () => void;
}

export function MessageComposer({ value, disabled, onChange, onSend }: Props) {
  return (
    <form className="composer" onSubmit={event => { event.preventDefault(); onSend(); }}>
      <div className="composer-input">
        <textarea aria-label="Сообщение агенту" placeholder="Опишите приложение или изменения…"
          rows={3} value={value} disabled={disabled}
          onChange={event => onChange(event.target.value)}
          onKeyDown={event => {
            if (event.key === 'Enter' && !event.shiftKey && !event.nativeEvent.isComposing) {
              event.preventDefault();
              onSend();
            }
          }}
        />
        <button className="send-button" type="submit" aria-label="Отправить"
          disabled={disabled || !value.trim()}>
          {disabled ? <Loader2 size={19} className="spin" /> : <ArrowUp size={20} />}
        </button>
      </div>
      <div className="composer-hint">Enter — отправить<span>Shift + Enter — новая строка</span></div>
    </form>
  );
}
