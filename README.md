## Handshake ##
An app to help people with social anxiety engaging in networking events.

*This app is developed as my USC ITP341 Final Project: Develop App for Social Good.*

<img src="https://github.com/HaomeiLiu/Handshake/blob/master/github.handshake.store/ic_launcher-web.png" width="200">

### Rationale ###
People with social anxiety always find it hard to enjoy a networking event. We hope that an online chat app can break the barrier and serve as a platform to help them establish a connection with other participants of the event.

### APP DEMO ###
- Login (Register/Use Facebook), browse through available events

![Login](https://github.com/HaomeiLiu/Handshake/blob/master/github.handshake.store/login.gif)

- Search an event in database, chat page, private message

![Search](https://github.com/HaomeiLiu/Handshake/blob/master/github.handshake.store/search.gif)

- Send a message

![Message](https://github.com/HaomeiLiu/Handshake/blob/master/github.handshake.store/send_message.gif)

- Send a real-time location

![Location](https://github.com/HaomeiLiu/Handshake/blob/master/github.handshake.store/send_location.gif)

### APP Details ###
- Persistent storage: achieved with Firestore
- Shared Preference: user information, log in status
- Main functionalities:
  - Log in/out
  - View events
  - Add an event
  - Search for an event
  - Join an event for messages
  - View user of interest
  - Private chat with user
  - Share real time location
- Layout Design: Bottom Navigation View with fragments, listview and custom view
- Model: Event, UserInfo (integrated from JMessage), MyLocation
- Third Party API:
  - **JMessage & JPush** (JChat as a demo is the most important reference for the app)
  - Firestore (store events and location)
  - Google Maps
  - Google Location Services
  - Facebook
- Additional tech items:
  - SpinKit (Custom Dialog)
  - Camera and photo gallery (implemented, but not uploaded)
  - Permission
  - Notification (Sound & Vibration)


