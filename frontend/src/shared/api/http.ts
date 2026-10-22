export async function apiError(response: Response): Promise<Error> {
  const body = await response.json().catch(() => ({}));
  return new Error(body.detail || body.error || body.message || 'Ошибка запроса: ' + response.status);
}

export async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch('/api' + path, {
    ...options,
    headers: { 'Content-Type': 'application/json' },
  });
  if (!response.ok) throw await apiError(response);
  return response.status === 204 ? undefined as T : response.json();
}
