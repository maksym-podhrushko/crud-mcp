# CRUD Spring MCP

## Overview

This project is a simple CRUD (Create, Read, Update, Delete) application designed to demonstrate integration with the Model Context Protocol (MCP) for AI-powered features. The goal is to provide a modern, extensible example of how to connect a standard CRUD backend and frontend with an AI context protocol server, enabling advanced context-aware operations and chat capabilities.

**_ In development: IaaC with OpenTofu scripts for AWS will be added _**

## Features

- Full-stack CRUD app (Java Spring Boot backend, Vue.js frontend)
- Integration with Model Context Protocol (MCP) for AI context and chat
- Dockerized microservices architecture
- RESTful API for data operations
- Example chat sidebar powered by MCP

## Getting Started

### Prerequisites

- Docker & Docker Compose
- Java 21
- Node.js & npm

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/maksym-podhrushko/crud-mcp.git
   cd crud-mcp
   ```
2. Build and start all services:
   ```bash
   docker-compose -f docker-compose.yml up
   ```
3. Access the frontend at [http://localhost:8080](http://localhost:8080)

### Manual Development

- Backend: See `/backend/demo/` for Spring Boot app. Run with `./mvnw spring-boot:run`.
- Frontend: See `/frontend/`. Run with `npm install && npm run serve`.
- MCP Server/Client: See `/mcp-server/` and `/mcp-client/` for context protocol integration.

## Usage

Interact with the CRUD app via the web UI. The chat sidebar demonstrates AI context integration using MCP. You can extend the backend and frontend to support more advanced context-aware features.

## Project Structure

- `backend/` - Spring Boot CRUD API
- `frontend/` - Vue.js SPA
- `mcp-server/` - Model Context Protocol server
- `mcp-client/` - MCP client integration

## Contributing

Contributions are welcome! Please open issues or pull requests for improvements, bug fixes, or new features. Follow best practices for code style and documentation.

## License

This project is licensed under the MIT License.

## Authors & Acknowledgments

- Maksym Podhrushko
- Inspired by open source AI and CRUD app communities

## Roadmap

- Add more AI-powered features via MCP
- Improve UI/UX for chat and context
- Expand documentation and examples

## Support

For help or questions, open an issue on GitHub.
