# SOCIAL NETWORK APPLICATION DEVELOPMENT BASED ON MICROSERVICES ARCHITECTURE WITH INTEGRATED MACHINE LEARNING FOR AUTOMATED IMAGE MODERATION

The social network application system is deployed based on Microservices architecture, using the following technologies:

## 1. Back-end
* Implemented based on microservices architecture
* Main framework is Java Spring Boot
* Spring Cloud (gateway, eureka,...)
* Open Feign: communication between services
* Redis: for caching and token management (refresh, revoke token)
* Kafka: message broker for asynchronous communication between services
* Sight Engine API: machine learning model for image and video moderation
* Elasticsearch: search and search suggestions (auto complete)
* Websocket: handling realtime tasks
* Firebase Cloud Messaging: push notifications to users
* Cloudinary: cloud file storage support

## 2. Front-end
* ReactJS
* Tanstack Query
* TailwindCSS
* Ant Design
* SocketIO

## 3. Database
* MongoDB
* MySQL

The application ensures providing an online space for users to interact through features such as:

## 4. Some features
### Authentication
* User authentication using JWT
* Password change, forgot password
* Send OTP email for password-related tasks and personal information

### Personal Posts
* Users can create personal posts on their profile including text, images, videos, or combinations
* Set privacy settings for posts (Public, Friends, Only Me)
* Tag other users in posts
* Tagged posts appear on user's profile

### News Feed
* Display latest posts from self, friends, groups
* Ensure posts display according to user privacy settings

### Post Interactions
* Users can react, comment, or share others' posts

### Post Sharing
* Users can share posts on their timeline, in groups, or with specific users

### Content Moderation
* Automatic moderation of images and videos using machine learning through Sight Engine API

### Friends Management
* Send friend requests
* Friend suggestions
* Manage and approve friend requests
* Unfriend users

### Account Management
* Update personal information
* Manage security and privacy settings
* View, manage, and update posts

### Messaging
* Send private messages, images, videos to friends using WebSocket

### Notifications
* Realtime notifications for interactions, shares, messages, friend requests, and group invites

### Search
* Search for users, posts, groups, and other information
* Advanced search and auto-complete using Elasticsearch

### Favorites Management
* Manage favorite lists including adding/removing posts and deleting lists

### Groups
* Create groups, invite others
* Post and interact within groups
* Set group privacy, moderate members and posts

### Stories
* Create and manage personal stories
* View and react to others' stories
* Set story privacy settings
* Ensure stories are retrieved according to privacy settings
* Manage story archives

## 5. Some application screenshots
### News feed
![home page image](https://raw.githubusercontent.com/VietTran8/resources/refs/heads/master/social_network/home.png)

### Creating post
![creating post](https://raw.githubusercontent.com/VietTran8/resources/refs/heads/master/social_network/create_post.png)

### Post comment
![post comment](https://raw.githubusercontent.com/VietTran8/resources/refs/heads/master/social_network/comment.png)

### Create story
![create story](https://raw.githubusercontent.com/VietTran8/resources/refs/heads/master/social_network/create_story.png)

### View my stories
![view my story](https://raw.githubusercontent.com/VietTran8/resources/refs/heads/master/social_network/view_my_story.png)

### View others stories
![view others stories](https://raw.githubusercontent.com/VietTran8/resources/refs/heads/master/social_network/view_story_others.png)

### Profile
![profile](https://raw.githubusercontent.com/VietTran8/resources/refs/heads/master/social_network/profile.png)

### Settings
![settings](https://raw.githubusercontent.com/VietTran8/resources/refs/heads/master/social_network/settings.png)

### Chatting
![chatting](https://raw.githubusercontent.com/VietTran8/resources/refs/heads/master/social_network/chat.png)

### Group list
![group list](https://raw.githubusercontent.com/VietTran8/resources/refs/heads/master/social_network/group_list.png)

### Group
![group](https://raw.githubusercontent.com/VietTran8/resources/refs/heads/master/social_network/group.png)

### Friend request
![friend request](https://raw.githubusercontent.com/VietTran8/resources/refs/heads/master/social_network/friend_requests.png)

### Favourite
![favourite](https://raw.githubusercontent.com/VietTran8/resources/refs/heads/master/social_network/favourite.png)

### Saved
![saved](https://raw.githubusercontent.com/VietTran8/resources/refs/heads/master/social_network/saved.png)


And many more pages...

