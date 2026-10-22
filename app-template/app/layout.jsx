import './globals.css';

export const metadata = { title: 'Моё приложение' };

export default function RootLayout({ children }) {
  return (
    <html lang="ru">
      <body>{children}</body>
    </html>
  );
}
