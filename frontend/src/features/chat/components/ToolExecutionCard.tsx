import { Check, ChevronDown, CircleAlert, Loader2, Terminal } from 'lucide-react';
import type { ChatMessage } from '../types';

function command(argumentsJson: string | null): string {
  if (!argumentsJson) return '';
  try {
    return JSON.parse(argumentsJson).command;
  } catch {
    return argumentsJson;
  }
}

export function ToolExecutionCard({ message }: { message: ChatMessage }) {
  const running = message.status === 'RUNNING';
  const failed = message.status === 'FAILED';
  return (
    <details className={'tool-card ' + message.status.toLowerCase()} open={running ? true : undefined}>
      <summary>
        <Terminal size={15} />
        <span>bash</span>
        <span className="tool-status">
          {running ? <><Loader2 size={13} className="spin" /> Выполняется</>
            : failed ? <><CircleAlert size={13} /> Код {message.exitCode}</>
              : <><Check size={13} /> Завершено</>}
        </span>
        <ChevronDown size={14} className="tool-chevron" />
      </summary>
      <div className="tool-body">
        <pre className="tool-command">{command(message.toolArguments)}</pre>
        {message.content && <pre className="tool-output">{message.content}</pre>}
      </div>
    </details>
  );
}
