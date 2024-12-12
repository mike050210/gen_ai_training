# Gen-AI Training for Java Developers

This project aims to help Java developers to understand the basic concepts of
Generative AI and how to implement solutions using the Java language.

# Project Setup
1. Clone the repository.
2. Edit your run configurations to add the following environment variables:
   ```
   OPEN_AI_KEY=<Your OpenAPI Key>
   OPEN_AI_ENDPOINT=<Your OpenAPI Endpoint>
   OPEN_AI_DEPLOYMENT_NAME=<The GPT Model >
   ```
3. Run the SpringBoot Application. The application would start on the port 8080 by default.

**Note:** You can modify the Gen-AI Settings like the temperature or the maximum
    tokens within the `application.properties` file.

## REST Endpoints
### Simple Prompt
_Request_
```
POST http://localhost:8080/api/chat-bot/simple-prompt
{
    "input": "What is the capital of France?"
}
```
_Response_
```json
{
    "response": "The capital of France is Paris.",
    "model": "Mixtral-8x7B-Instruct-v0.1"
}
```

### Chat History Prompt
_Request 1_
```
POST http://localhost:8080/api/chat-bot/prompt-with-history
{
    "input": "Tell me a few advantages of using Java",
    "model": "ai21.j2-jumbo-instruct"
}
```
_Response 1_
```json
{
    "response": "Sure! Here are a few advantages of using Java:\n\n1. Platform independence: Java programs are compiled into bytecode, which can run on any platform with a Java Virtual Machine (JVM). This cross-platform compatibility makes Java a popular choice for building enterprise-level applications.\n\n2. Object-oriented programming: Java follows the object-oriented programming paradigm, which promotes modular and reusable code. This helps in building robust and scalable applications.\n\n3. Rich API: Java provides a vast collection of pre-built libraries and APIs, ranging from graphics rendering to networking and database connectivity. These APIs save development time and effort by providing ready-to-use solutions.\n\n4. Memory management: Java uses automatic garbage collection, which helps in managing memory allocation and deallocation. Developers don't need to worry about manual memory management, improving efficiency and reducing the likelihood of memory leaks.\n\n5. Strong community support: Java has a large and active community of developers, which means you can find extensive documentation, forums, and resources to learn, troubleshoot, and collaborate on Java projects.\n\n6. Security: Java has built-in security features, such as sandboxing, that protect against malicious activities. It also supports cryptography and secure socket layers (SSL) for secure communication.\n\nThese advantages are just a glimpse of what Java offers. It is a versatile language used in a variety of domains, including web development, mobile app development, and enterprise software."
}
```

_Request 2_
```
POST http://localhost:8080/api/chat-bot/prompt-with-history
{
    "input": "Summarize it and convert your response into HTML",
    "model": "ai21.j2-jumbo-instruct"
}
```
_Response 2_
```json
{
   "response": "Certainly! Here's the HTML representation of the advantages of using Java:\n\n```html\n<h3>Advantages of Using Java:</h3>\n<ol>\n  <li><strong>Platform independence:</strong> Java programs can run on any platform with a JVM.</li>\n  <li><strong>Object-oriented programming:</strong> Java promotes modular and reusable code through its OOP paradigm.</li>\n  <li><strong>Rich API:</strong> Java provides a wide range of pre-built libraries and APIs.</li>\n  <li><strong>Memory management:</strong> Java uses automatic garbage collection, reducing the need for manual memory management.</li>\n  <li><strong>Strong community support:</strong> Java has an active community of developers, offering extensive resources and support.</li>\n  <li><strong>Security:</strong> Java has built-in security features, such as sandboxing and support for encryption.</li>\n</ol>\n```\n\nFeel free to modify or style the HTML code as necessary."
}
```

# Generate Images from text
_Request_
```
POST http://localhost:8080/api/ai-images
{
    "input": "A yellow dragonfly eating a watermelon close to a river"
}
```
_Response_
![image](images/image_example.png)


# Plugins

- Inventory plugin: Allows to manage an inventory stock.

_Request 1_
```
POST http://localhost:8080/api/inventory-bot
{
    "input": "Show me the current inventory",
    "model": "gpt-35-turbo"
}
```
_Response 1_
```json
{
    "response": "Here are the current items in the inventory:\n\n1. Laptop HP - Current Stock: 10, Maximum Stock: 50\n2. Mouse Logitech - Current Stock: 105, Maximum Stock: 200\n3. Monitor Asus - Current Stock: 15, Maximum Stock: 40"
}
```

_Request 2_
```
POST http://localhost:8080/api/inventory-bot
{
    "input": "Increase 10 units to the Logitech mouse",
    "model": "gpt-35-turbo"
}
```
_Response 2_
```json
{
   "response": "10 units have been added to the stock of Logitech Mouse. The current stock of Logitech Mouse is now 115 units."
}
```

- Countries Information

_Request_
```
POST http://localhost:8080/api/country-info
{
    "input": "Give me information about Mauritius",
    "model": "gpt-35-turbo"
}
```
_Response_
```json
{
    "response": "Mauritius, officially known as the Republic of Mauritius, is an island nation located in the Eastern Africa region of Africa. Here is some information about Mauritius:\n\n- Common Name: Mauritius\n- Official Name: Republic of Mauritius\n- Native Name: Maurice (French), Moris (Mauritian Creole)\n- Capital: Port Louis\n- Region: Africa\n- Subregion: Eastern Africa\n- Population: Approximately 1,265,740\n- Area: 2,040 square kilometers\n- Language: The official languages are English and French, with Mauritian Creole being widely spoken.\n- Currency: Mauritian Rupee (MUR)\n- Timezone: UTC+04:00\n- Calling Code: +230\n- Internet TLD: .mu\n- Flag: 🇲🇺\n\nIf you need more specific information or have any other questions, feel free to ask!"
}
```