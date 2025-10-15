FROM mcr.microsoft.com/devcontainers/java:21

# Install tools
USER root

RUN apt-get update && apt-get install -y \
    curl \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

# Install Node.js using NodeSource repository
RUN curl -fsSL https://deb.nodesource.com/setup_22.x | bash - && \
    apt-get install -y nodejs

# Docker CLI inside the container
RUN apt-get update && apt-get install -y docker.io

# Install Prettier globally
RUN npm install -g prettier prettier-plugin-java

# Install aws cli
RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
    unzip awscliv2.zip && \
    sudo ./aws/install && \
    rm -rf awscliv2.zip aws

# Create a workspace folder
RUN mkdir -p /workspace
WORKDIR /workspace

# Ensure vscode user has ownership of the workspace directory
RUN chown -R vscode:vscode /workspace

# Switch back to non-root user if preferred
USER vscode