import './chat.scss';

import React from 'react';

const ChatPage = () => (
  <div>
    <iframe
      src="../chat-ui/index.html"
      title="Chat UI"
      seamless
      style={{ width: '100%', minHeight: '80vh', border: 'none' }}
      data-cy="chat-frame"
    />
  </div>
);

export default ChatPage;
