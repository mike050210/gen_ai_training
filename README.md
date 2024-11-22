# Gen-AI Training for Java Developers

This project aims to help Java developers to understand the basic concepts of
Generative AI and how to implement solutions using the Java language.

## Project Setup
1. Clone the repository.
2. Edit your run configurations to add the following environment variables:
   ```
   OPEN_AI_KEY=<Your OpenAPI Key>
   OPEN_AI_ENDPOINT=<Your OpenAPI Endpoint>
   OPEN_AI_DEPLOYMENT_NAME=<The GPT Model >
   ```
3. Run the SpringBoot Application. The application would start on the port 8080 by default.

### REST Endpoints
- http://localhost:8080/api/chat-bot/simple-prompt?input=<Your-prompt>
- http://localhost:8080/api/chat-bot/prompt-with-history?input=<Your-prompt>