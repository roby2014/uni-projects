/* 
 * Computer Networks Project - Phase 1
 * webclient.c - establish a TCP connection to a web server and send/receive message(s).
 * 02 April 2022
 */

#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <string.h>

#define HTTP_RESPONSE_SIZE 10000

#define HTTP_PORT 80

// message that will be sent to the server
const char msg_template[] =
    "GET /%s HTTP/1.1\r\n"
    "Host: %s\r\n"
    "Connection: keep-alive\r\n"
    "User-Agent: WebClient Console Application written in C\r\n"
    "Accept: text/html,application/xhtml+xml\r\n"
    "Accept-Language: en-US,en;q=0.9\r\n"
    "Acccept-Encoding: gzip, deflate, br\r\n"
    "\r\n";

/// creates a tcp socket
int create_tcp_socket();

/// opens the connection to the server via tcp socket
void connect_to_server(int socket_fd, const char* host, int port);
 
/// handle message (let client pick if it should be printed to terminal or file) 
void handle_http_response(const char* http_response);

int main(int argc, char* argv[]) {
    if (argc != 2) {
        printf("Usage: ./webclient <URL> \n");
        return EXIT_FAILURE;
    }

    // get arguments
    char* url = argv[1];
    const char* host = strtok(url, "/");
    char* path = strtok(NULL, "");
    if (path == NULL) {
        path = malloc(1);
        strcpy(path, "");
    }

    char* msg = malloc(sizeof(msg_template) + strlen(path) + strlen(host));
    sprintf(msg, msg_template, path, host);

    // create tcp socket
    int socket_fd = create_tcp_socket();
    printf("-> Socket created successfully\n");
    
    // connect to server
    connect_to_server(socket_fd, host, HTTP_PORT);
    printf("-> Server connection made successfully (%s:%d)\n", host, HTTP_PORT);

    // send message
    if(send(socket_fd, msg, strlen(msg), 0) == -1) {
        perror("ERROR: Unable to send message to server\nREASON");
        return EXIT_FAILURE;
    }
    printf("-> Message sent: \n%s", msg);

    // receive and store server response
    char response[HTTP_RESPONSE_SIZE];
    if(recv(socket_fd, response, sizeof(response), 0) == -1) {
        perror("ERROR: Unable to receive message from server\nREASON");
        return EXIT_FAILURE;
    }
    printf("-> Response received \n");

    // let client pick where to print the response msg
    handle_http_response(response);

    // close socket
    printf("-> Closing socket...\n");
    close(socket_fd);
    free(msg);

    return EXIT_SUCCESS;
}

/// creates a tcp socket
int create_tcp_socket() {
    int socket_fd = socket(AF_INET, SOCK_STREAM, 0);
    if(socket_fd == -1){
        perror("ERROR: Unable to create socket\nREASON");
        exit(EXIT_FAILURE);
    }
    return socket_fd;
}

/// opens the connection to the server via tcp socket
void connect_to_server(int socket_fd, const char* host, int port) {
    // specify server info
    struct sockaddr_in server_addr;
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(port);
    server_addr.sin_addr.s_addr = inet_addr(host);
    
    // connect
    if(connect(socket_fd, (struct sockaddr*)&server_addr, sizeof(server_addr)) == -1) {
        printf("ERROR: Unable to connect to %s:%d\n", host, port);
        perror("REASON");
        exit(EXIT_FAILURE);
    }
}

/// handle message (let client pick if it should be printed to terminal or file) 
void handle_http_response(const char* http_response) {
    int opc;
    do {
        printf("Display http response in:\n 1. Current terminal\n 2. Separate file\n 3. Exit \n  > ");
        scanf("%d", &opc);
        switch(opc) {
            case 1: {
                printf("-> Message received:\n%s", http_response);
                break;
            }
            case 2: {
                FILE* fp = fopen("response.txt", "w");
                fprintf(fp, http_response);
                fclose(fp);
                printf("-> Message printed to \"response.txt\"\n");
                break;
            }
            case 3: {
                break;
            }
            default: {
                printf("Invalid option! \n\n");
            }
        }
    } while (opc != 1 && opc != 2 && opc != 3);
}
