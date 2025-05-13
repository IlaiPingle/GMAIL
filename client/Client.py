import sys
import socket

def main(argv):
    if len(argv) != 3:
       return
    dest_ip = argv[1]
    dest_port = int(argv[2])

    try: 
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client:
            client.connect((dest_ip, dest_port))
            while True:
                UserInput = input()
                client.sendall((UserInput+ "\n").encode)
                response = b''
                while True:

                    chunk = client.recv(1024)
                    
                    if not chunk:
                        break
                    
                    response += chunk
                    if not response.startswith(b'200'):
                        if response.endswith(b'\n'):
                            break
                    elif b'\n\n' in response and response.endswith(b'\n') and not response.endswith(b'\n\n'):
                        break

                print(response.decode(), end="")

    except Exception as e:
        print(f"connection error: {e}")
    
if __name__ == "__main__":
    main(sys.argv)    
        