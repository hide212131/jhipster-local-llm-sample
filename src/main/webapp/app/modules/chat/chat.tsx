import './chat.scss';

import React from 'react';

const ChatPage = () => (
  <div>
    <iframe
      src="../chat-ui/index.html"
      width="100vw"
      height="100vh"
      title="Chat UI"
      seamless
      style={{ border: 'none' }}
      data-cy="chat-frame"
    />
  </div>
);

export default ChatPage;
