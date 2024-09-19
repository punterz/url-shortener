# URL Shortener
A Dockerized Spring Boot URL shortener built using Redis as the data store. This project serves as a demonstration of how to use the Llama 8B model locally as a coding copilot. It's purely for exploration and not an official implementation. Have fun coding, with less effort!

# Demo
URL shortner Demo Service is available under domain `https://zypp.lol` 
- Healthcheck request
  
   ```bash
   curl -i https://zypp.lol/ping
   ```
- URL shortener endpoint sample request
  
   ```bash
   curl -X POST https://zypp.lol/shorten 
   -H "Content-Type: application/json" 
   -d '{"longUrl": "https://www.example.com"}'
   ```  

# Getting Started
Before we start the development, lets set up the LLM on local machine and integrate it with the choice of IDE/editor so that the LLM helps you like a copilot while you are coding

## Setting up Llama (LLM by Meta) on local machine
To install and use Llama on your local desktop, we will use Ollama to get it set up. Follow these steps:

1. **Install Ollama**:
   - Visit [Ollama's website](https://ollama.com/) and download the installer for your operating system.
   - Follow the installation instructions to set up Ollama on your local machine.

2. **Download Llama**:
   - Open Ollama and use it to download the latest Llama model [Llama 3.1](https://ollama.com/library/llama3.1). This is typically done via the Ollama interface or command-line options provided by Ollama.
   - For our set up, I am using 8B parameter model. To install Llama 3.1:8b model on your local machine, use following command

     ```bash
     ollama pull llama3.1:8b
     ```
4. **Setting up and running Llama in Ollama**:
   - After downloading, configure Llama within Ollama. This usually involves setting up paths or selecting the model in the Ollama interface.
   - For our set up, use the following command to run the model.

      ```bash
      ollama run llama3.1:8b
      ```
6. **Use Llama as Copilot**:
   - You can now use Llama as your local AI assistant. Depending on Ollama’s integration options, you might need to configure VS Code or other development environments to use Llama.

7. **Verify Llama Installation**:
   - Open Ollama and check that Llama is listed and properly configured.
   - Test Llama with a simple query or example to ensure it is working as expected.

## Setting Up Dev environment - Copilot (powered by local Llama) with VS Code

To enhance your development experience with Copilot in Visual Studio Code, follow these steps:

1. **Install Visual Studio Code**:
   - Download and install Visual Studio Code from [here](https://code.visualstudio.com/).

2. **Install the Continue Copilot Extension**:
   - Open VS Code.
   - Go to the Extensions view by clicking on the Extensions icon in the Activity Bar on the side of the window.
   - Search for "Continue" and click "Install" on the GitHub Copilot extension. You may also check the official site of Continue [here](https://docs.continue.dev/)

3. **Configure Continue Copilot Extension**:
   - Continue Copilot Extension will get autoconfigured after discovering a local running copy of Ollama.
   - For advance settings, you can configure Copilot settings by going to `Code > Settings > Settings` and searching for "Continue".
   - Adjust settings as needed to fit your development preferences.

## Getting Started with the Project

### Checkout the Code

1. **Clone the Repository**:
   - Open your terminal and run the following command to clone the repository:

     ```bash
     git clone https://github.com/punterz/url-shortener.git
     cd url-shortener
     ```

### Build with Maven and Run with Docker Compose

1. **Build the Project Using Maven**:
   - Run the following Maven command to build the project:

     ```bash
     ./mvnw clean package
     ```

2. **Run the Application Using Docker Compose**:
   - For ease, ensure Docker Desktop is installed and running on your local machine.
   - Use the following command to start the services defined in the `docker-compose.yml` file:

     ```bash
     docker-compose up --build
     ```

3. **Access the Application**:
   - Once the services are running, open your browser and navigate to `http://localhost` to access the application.

4. **Shut Down the Services**:
   - To stop the running services, use:

     ```bash
     docker-compose down
     ```

## Testing the APIs with curl Commands

1. **Shorten a URL**:
   - Use the following `curl` command to test the URL shortening endpoint:

     ```bash
     curl -X POST http://localhost/shorten \
       -H "Content-Type: application/json" \
       -d '{"longUrl": "https://www.example.com"}'
     ```
   - If the server is running fine, the response will look like this:

     ```bash
     {"shortUrl":"http://localhost/e149be"}
     ```


2. **Redirect to Long URL**:
   - Use the following `curl` command to test the URL redirection endpoint:

     ```bash
     curl -I http://localhost:/e149be
     ```
   - If the server is running fine, the response will look like this:

     ```bash
     HTTP/1.1 302 
     Location: https://www.example.com
     Content-Length: 0
     Date: E, dd MMM yyyy HH:mm:ss z
     ```
    - Alternatively, open the url `http://localhost/e149be` [this](http://localhost/e149be) in a  browser and it will be redirected to `https://www.example.com` [here](https://www.example.com).

3. **Test Not Found Case**:
   - Use the following `curl` command to test a not found case:

     ```bash
     curl -I http://localhost/invalidKey
     ```
   - If the server is running fine, the response will look like this:

     ```bash
     HTTP/1.1 404 
     Content-Length: 0
     Date: E, dd MMM yyyy HH:mm:ss z
     ```
    
    - Alternatively, open the url `http://localhost:/invalidKey` [this](http://localhost:/invalidKey) in a  browser and it will throw a `page can't be found` error with error code `HTTP ERROR 404`.
