export async function sendChat(message) {
  const CHAT_API_BASE_URL =
    process.env.VUE_CHAT_API_BASE_URL ||
    "http://localhost:8086/api/chat/message";

  // Timeout in milliseconds (e.g., 10 minutes = 600,000 ms)
  const TIMEOUT_MS = 10 * 60 * 1000;

  const fetchPromise = fetch(CHAT_API_BASE_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ message }),
  });

  const timeoutPromise = new Promise((_, reject) =>
    setTimeout(() => reject(new Error("Request timed out")), TIMEOUT_MS)
  );

  const res = await Promise.race([fetchPromise, timeoutPromise]);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return await res.json(); // { sessionId, reply }
}
