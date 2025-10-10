
# MurlanLive
**MurlanLive** is an open-source game based on *Murlan*, a playing cards game that first originated in communist Albania from Chinese advisors/migrants. **MurlanLive** provides a UI (based on the Godot game engine), a game server, a user management service and a database.

# Our vision
Unlike some existing free and closed-source games that promote micro-transactions and purchases for in-game currency, we strive to bring a fair experience for all players. This means that every skin, every goal and every achievement in the game will be retrieved only based on the total score (and sometimes luck) of the player.

We also noticed that some alternatives already exist, although not many. The alternatives are clearly made with profit in mind by introducing purchases for in-game assets. On the other side, we bring an open-source alternative, which means:
- Anyone can create a server for MurlanLive (either *local* or *public*).
- Anyone can modify MurlanLive and deploy their own customized version.

# Project Technologies
- Java (21+)
- SpringBoot Framework
- WebSocket Protocol
- PostgreSQL
- Godot Game Engine

# Build Requirements
### Backend (UM, DB, GameServer)
Steps to build the backend:
- Install Docker Compose.
- ```./build_backend.sh```.

### UI (Godot)
